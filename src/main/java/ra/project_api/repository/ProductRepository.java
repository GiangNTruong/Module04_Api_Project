package ra.project_api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ra.project_api.model.Category;
import ra.project_api.model.Product;

import java.util.List;
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategory(Category category);
    List<Product> findTop5ByOrderByCreatedAtDesc();
    @Query("SELECT DISTINCT p FROM Product p JOIN OrderDetail od ON p = od.compositeKey.product")
    Page<Product> findSoldProducts(Pageable pageable);
    @Query("SELECT p FROM Product p WHERE LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> searchByNameOrDescription(String keyword);

    @Query("SELECT p FROM Product p JOIN OrderDetail od ON p = od.compositeKey.product GROUP BY p ORDER BY SUM(od.orderQuantity) DESC")
    List<Product> findTop3BestSellingProducts(Pageable pageable);
    Page<Product> findAll(Pageable pageable);
}
