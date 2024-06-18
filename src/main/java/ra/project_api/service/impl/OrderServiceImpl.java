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

        // Lưu đơn hàng vào cơ sở dữ liệu
        Order savedOrder = orderRepository.save(order);

        // Tạo và lưu các đối tượng OrderDetail từ checkoutRequest
        for (OrderDetailRequestDTO item : checkoutRequest.getOrderDetails()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new NoSuchElementException("Product not found"));

            // Kiểm tra số lượng tồn kho
            if (item.getQuantity() > product.getStockQuantity()) {
                throw new InsufficientStockException("Not enough stock for product: " + product.getProductName()+" and  still: "+product.getStockQuantity());
            }

            // Sử dụng @Builder để khởi tạo OrderDetail
            OrderDetail orderDetail = OrderDetail.builder()
                    .compositeKey(new OrderDetailCompositeKey(savedOrder, product))
                    .name(product.getProductName())
                    .unitPrice(product.getUnitPrice())
                    .orderQuantity(item.getQuantity())
                    .build();

            // Lưu chi tiết đơn hàng vào cơ sở dữ liệu
            orderDetailRepository.save(orderDetail);

            // Giảm số lượng tồn kho của sản phẩm
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

        List<OrderDetail> orderDetails = orderDetailRepository.findByCompositeKeyOrder(order);

        List<OrderDetailDTO> orderDetailDTOs = mapOrderDetailsToDTOs(orderDetails);

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

        order.setStatus(updateOrderStatusDTO.getOrderStatus());

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

        // Check if order status is pending confirmation
        if (order.getStatus() == OrderStatus.WAITING) {
            // Update order status to cancelled
            order.setStatus(OrderStatus.CANCEL);
            // Save updated order
            orderRepository.save(order);
        } else {
            throw new IllegalStateException("Order cannot be cancelled at this time");
        }
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
