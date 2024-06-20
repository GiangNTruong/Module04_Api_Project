package ra.project_api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ra.project_api.model.Category;
import ra.project_api.model.Product;

import java.util.Date;
import java.util.List;
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategory(Category category);
    List<Product> findTop5ByOrderByCreatedAtDesc();
    @Query("SELECT DISTINCT p FROM Product p JOIN OrderDetail od ON p = od.compositeKey.product")
    Page<Product> findSoldProducts(Pageable pageable);
    @Query("SELECT p FROM Product p WHERE LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> searchByNameOrDescription(String keyword);


    //danh sách bán chạy nhất
    @Query("SELECT p FROM Product p JOIN OrderDetail od ON p = od.compositeKey.product GROUP BY p ORDER BY SUM(od.orderQuantity) DESC")
    List<Product> findTop3BestSellingProducts(Pageable pageable);
    Page<Product> findAll(Pageable pageable);

    //dách sách sản phẩm nổi bật là thu nhập cao nhất dùng giá product*quantity
    @Query("select p from Product p join  OrderDetail od on p.productId=od.compositeKey.product.productId"+
    " group by p.productId"+" order by SUM(od.unitPrice * od.orderQuantity) DESC")
    List<Product> findTopProductsByRevenue(Pageable pageable);

    //top 10 sản phẩm bán chạy theo thời gian
    @Query("SELECT p FROM Product p JOIN OrderDetail od ON p.productId = od.compositeKey.product.productId " +
            "JOIN Order o ON od.compositeKey.order.orderId = o.orderId " +
            "WHERE o.createdAt BETWEEN :from AND :to " +
            "GROUP BY p.productId " +
            "ORDER BY SUM(od.orderQuantity) DESC")
    List<Product> findTopBestSellingProductsBetweenDates(@Param("from") Date from, @Param("to") Date to, Pageable pageable);



}
