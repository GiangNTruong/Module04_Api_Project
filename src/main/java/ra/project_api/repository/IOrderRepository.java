package ra.project_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ra.project_api.constrants.OrderStatus;
import ra.project_api.model.Order;
import ra.project_api.model.User;

import java.util.List;
import java.util.Optional;


public interface IOrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByStatusAndUser(OrderStatus status, User user);
    Optional<Order> findBySerialNumberAndUser(String serialNumber, User user);
    Optional<Order> findByOrderIdAndUser_Username(Long orderId, String username);
}