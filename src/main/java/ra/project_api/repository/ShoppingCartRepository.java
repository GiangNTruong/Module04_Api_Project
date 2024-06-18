package ra.project_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ra.project_api.model.Product;
import ra.project_api.model.ShoppingCart;
import ra.project_api.model.User;

import java.util.List;
import java.util.Optional;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Integer> {
    List<ShoppingCart> findByUser(User user);
    Optional<ShoppingCart> findByUserAndProduct(User user, Product product);

    @Query("SELECT sc FROM ShoppingCart sc WHERE sc.shoppingCartId = :cartItemId AND sc.user = :user")
    Optional<ShoppingCart> findByIdAndUser(@Param("cartItemId") Long cartItemId, @Param("user") User user);

}
