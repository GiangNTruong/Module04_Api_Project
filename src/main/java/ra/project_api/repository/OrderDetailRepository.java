package ra.project_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ra.project_api.model.Order;
import ra.project_api.model.OrderDetail;
import ra.project_api.model.OrderDetailCompositeKey;
import ra.project_api.model.User;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, OrderDetailCompositeKey> {
    List<OrderDetail> findByCompositeKeyOrder(Order order);
    List<OrderDetail> findByCompositeKey_Order_User(User user);
}