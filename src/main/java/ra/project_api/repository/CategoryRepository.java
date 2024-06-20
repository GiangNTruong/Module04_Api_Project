package ra.project_api.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ra.project_api.model.Category;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT DISTINCT c FROM Category c " +
            "JOIN Product p ON c.categoryId = p.category.categoryId " +
            "JOIN OrderDetail od ON p.productId = od.compositeKey.product.productId")
    List<Category> findSoldCategories();

    Page<Category> findAll(Pageable pageable);

    @Query("SELECT COUNT(p) > 0 FROM Product p WHERE p.category.categoryId = :categoryId")
    boolean existsProductByCategoryId(@Param("categoryId") Long categoryId);
}
