package ra.project_api.service;



import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import ra.project_api.model.Category;
import ra.project_api.model.Product;

import java.util.List;

public interface ProductService {
    Product findProductById(Long id);
    List<Product> findProductsByCategory(Category category);
    List<Product> findLatestProducts();
    Page<Product> findSoldProducts(int page, int size, String sortBy, String sortDir);
    List<Product> searchProductsByNameOrDescription(String keyword);
    List<Product> findTop3BestSellingProducts();
    void deleteProduct(Long productId);
    Product update(Product product);
    Product saveProduct(Product product);
    Page<Product> findAll(Pageable pageable);
    List<Product> getTopFeaturedProducts (int limit);
}

