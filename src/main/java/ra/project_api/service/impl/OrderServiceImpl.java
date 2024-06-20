package ra.project_api.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ra.project_api.constrants.OrderStatus;
import ra.project_api.dto.request.CheckoutRequestDTO;
import ra.project_api.dto.request.OrderDetailRequestDTO;
import ra.project_api.dto.request.UpdateOrderStatusDTO;
import ra.project_api.dto.response.OrderDetailDTO;
import ra.project_api.dto.response.OrderDetailsResponseDTO;
import ra.project_api.dto.response.OrderHistoryResponse;
import ra.project_api.exception.InsufficientStockException;
import ra.project_api.model.*;
import ra.project_api.repository.IOrderRepository;
import ra.project_api.repository.IUserRepository;
import ra.project_api.repository.OrderDetailRepository;
import ra.project_api.repository.ProductRepository;
import ra.project_api.service.IOrderService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrderService {

    private final IOrderRepository orderRepository;
    private final IUserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ModelMapper modelMapper;

    @Override
    public void placeOrder(CheckoutRequestDTO checkoutRequest, String username) {
        User user = userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        // Sử dụng ModelMapper để ánh xạ từ CheckoutRequestDTO sang Order
        Order order = modelMapper.map(checkoutRequest, Order.class);
        order.setUser(user);
        order.setStatus(OrderStatus.WAITING);
        Order savedOrder = orderRepository.save(order);

        // Tạo và lưu các đối tượng OrderDetail từ checkoutRequest
        for (OrderDetailRequestDTO item : checkoutRequest.getOrderDetails()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new NoSuchElementException("Product not found"));


            if (item.getQuantity() > product.getStockQuantity()) {
                throw new InsufficientStockException("Not enough stock for product: " + product.getProductName()+" and  still: "+product.getStockQuantity());
            }

            OrderDetail orderDetail = OrderDetail.builder()
                    .compositeKey(new OrderDetailCompositeKey(savedOrder, product))
                    .name(product.getProductName())
                    .unitPrice(product.getUnitPrice())
                    .orderQuantity(item.getQuantity())
                    .build();

            orderDetailRepository.save(orderDetail);

            // giảm quantity sản phẩm
            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            productRepository.save(product);
        }
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public OrderDetailsResponseDTO getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found with id: " + orderId));

        // Tìm các chi tiết đơn hàng của đơn hàng này
        List<OrderDetail> orderDetails = orderDetailRepository.findByCompositeKeyOrder(order);

        List<OrderDetailDTO> orderDetailDTOs = mapOrderDetailsToDTOs(orderDetails);

        // Xây dựng đối tượng OrderDetailsResponseDTO để trả về
        return OrderDetailsResponseDTO.builder()
                .order(order)
                .orderDetails(orderDetailDTOs)
                .build();
    }
    @Override
    public List<Order> getOrdersByStatus(OrderStatus orderStatus) {
        return orderRepository.findByStatus(orderStatus);
    }

    @Override
    public Order updateOrderStatus(Long orderId, UpdateOrderStatusDTO updateOrderStatusDTO) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found"));
        //don hàng hien tại
        OrderStatus currentStatus = order.getStatus();

      // hàng mới
        OrderStatus newStatus = updateOrderStatusDTO.getOrderStatus();

        if (newStatus == OrderStatus.CANCEL || newStatus == OrderStatus.DENIED) {

            if (currentStatus != OrderStatus.CANCEL && currentStatus != OrderStatus.DENIED) {
                // cộng lại số lượng sản phẩm vào kho
                updateProductStock(order, 1);
            }
        } else {
            if (currentStatus == OrderStatus.CANCEL || currentStatus == OrderStatus.DENIED) {
                // Trừ đi số lượng sản phẩm từ kho
                updateProductStock(order, -1);
            }
        }


        order.setStatus(newStatus);
        return orderRepository.save(order);
    }


    @Override
    public List<OrderHistoryResponse> getOrderHistoryByUser(User user) {
        return orderDetailRepository.findByCompositeKey_Order_User(user).stream()
                .map(orderDetail -> OrderHistoryResponse.builder()
                        .username(user.getUsername())
                        .productName(orderDetail.getCompositeKey().getProduct().getProductName())
                        .productPrice(orderDetail.getUnitPrice())
                        .quantity(orderDetail.getOrderQuantity())
                        .purchaseDate(orderDetail.getCompositeKey().getOrder().getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderHistoryResponse> getOrderHistoryByStatusAndUser(OrderStatus orderStatus, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        List<Order> orders = orderRepository.findByStatusAndUser(orderStatus, user);

        List<OrderHistoryResponse> orderHistoryResponses = new ArrayList<>();
        for (Order order : orders) {
            List<OrderDetail> orderDetails = orderDetailRepository.findByCompositeKeyOrder(order);
            for (OrderDetail orderDetail : orderDetails) {
                OrderHistoryResponse response = OrderHistoryResponse.builder()
                        .username(username)
                        .productName(orderDetail.getName())
                        .productPrice(orderDetail.getUnitPrice())
                        .quantity(orderDetail.getOrderQuantity())
                        .purchaseDate(order.getCreatedAt())
                        .build();
                orderHistoryResponses.add(response);
            }
        }
        return orderHistoryResponses;
    }
    @Override
    public OrderDetailsResponseDTO getOrderDetailsBySerialNumber(String serialNumber, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        Order order = orderRepository.findBySerialNumberAndUser(serialNumber, user)
                .orElseThrow(() -> new NoSuchElementException("Order not found"));

        List<OrderDetail> orderDetails = orderDetailRepository.findByCompositeKeyOrder(order);

        List<OrderDetailDTO> orderDetailDTOs = mapOrderDetailsToDTOs(orderDetails);

        return OrderDetailsResponseDTO.builder()
                .order(order)
                .orderDetails(orderDetailDTOs)
                .build();
    }

    @Override
    public void cancelOrder(Long orderId, String username) {
        // Fetch order by orderId and username
        Order order = orderRepository.findByOrderIdAndUser_Username(orderId, username)
                .orElseThrow(() -> new NoSuchElementException("Order not found"));

        // Check if order is already cancelled
        if (order.getStatus() == OrderStatus.CANCEL) {
            throw new IllegalStateException("Order is already cancelled");
        }

        // Check if order status is WAITING
        if (order.getStatus() == OrderStatus.WAITING) {
            // Update order status to cancelled
            order.setStatus(OrderStatus.CANCEL);

            // Update product stock
            updateProductStock(order, 1);

            // Save updated order
            orderRepository.save(order);
        } else {
            throw new IllegalStateException("Order can only be cancelled if it is in WAITING status");
        }
    }



    private void updateProductStock(Order order, int quantityModifier) {

        List<OrderDetail> orderDetails = orderDetailRepository.findByCompositeKeyOrder(order);

        // duyệt qua từng chi tiết đơn hàng để cập nhật số lượng sản phẩm
        for (OrderDetail orderDetail : orderDetails) {
            Product product = orderDetail.getCompositeKey().getProduct();
            int quantityChange = orderDetail.getOrderQuantity() * quantityModifier;

            // Cập nhật stockQuantity của sản phẩm
            product.setStockQuantity(product.getStockQuantity() + quantityChange);
            productRepository.save(product);
        }
    }

   @Override
    public Double getTotalRevenueBetweenDates(Date from, Date to) {
        // Gọi phương thức từ repository để tính tổng doanh thu
        return orderRepository.calculateTotalRevenueBetweenDates(from, to);
    }


    private List<OrderDetailDTO> mapOrderDetailsToDTOs(List<OrderDetail> orderDetails) {
        return orderDetails.stream()
                .map(orderDetail -> new OrderDetailDTO(
                        orderDetail.getCompositeKey().getProduct().getProductName(),
                        orderDetail.getUnitPrice(),
                        orderDetail.getOrderQuantity()
                ))
                .collect(Collectors.toList());
    }
}
