package ra.project_api.service;

import ra.project_api.constrants.OrderStatus;
import ra.project_api.dto.request.CheckoutRequestDTO;
import ra.project_api.dto.request.UpdateOrderStatusDTO;
import ra.project_api.dto.response.OrderDetailsResponseDTO;
import ra.project_api.dto.response.OrderHistoryResponse;
import ra.project_api.model.Order;
import ra.project_api.model.User;

import java.util.List;

public interface IOrderService {
    void placeOrder(CheckoutRequestDTO checkoutRequest, String username);
    List<Order> getAllOrders();
    OrderDetailsResponseDTO getOrderById(Long orderId);
    List<Order> getOrdersByStatus(OrderStatus orderStatus);
    Order updateOrderStatus(Long userId, UpdateOrderStatusDTO updateOrderStatusDTO);
    List<OrderHistoryResponse> getOrderHistoryByUser(User user);
    List<OrderHistoryResponse> getOrderHistoryByStatusAndUser(OrderStatus orderStatus, String username);
    OrderDetailsResponseDTO getOrderDetailsBySerialNumber(String serialNumber, String username);
    void cancelOrder(Long orderId, String username);
}
