package ra.project_api.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ra.project_api.dto.response.ListProductSoldResponse;
import ra.project_api.model.Category;
import ra.project_api.model.Product;
import ra.project_api.repository.ProductRepository;
import ra.project_api.service.ProductService;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Product findProductById(Long id) {
        return productRepository.findById(id).orElseThrow(()->new NoSuchElementException("Khong ton tai Id "+id));
    }

    @Override
    public List<Product> findProductsByCategory(Category category) {
        return productRepository.findByCategory(category);
    }

    @Override
    public List<Product> findLatestProducts() {
        return productRepository.findTop5ByOrderByCreatedAtDesc();
    }

    @Override
    public ListProductSoldResponse findSoldProducts(int page, int size, String sortBy, String sortDir) {
        Pageable pageable = PageRequest.of(page, size,
                sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending());

        Page<Product> productPage = productRepository.findSoldProducts(pageable);

        ListProductSoldResponse response = ListProductSoldResponse.builder()
                .content(productPage.getContent())
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .size(productPage.getSize())
                .number(productPage.getNumber())
                .sort(productPage.getSort())
                .build();

        return response;
    }


    @Override
    public List<Product> searchProductsByNameOrDescription(String keyword) {
        return productRepository.searchByNameOrDescription(keyword);
    }

    @Override
    public List<Product> findTop3BestSellingProducts() {
        return productRepository.findTop3BestSellingProducts(PageRequest.of(0, 3));
    }

    @Override
    public void deleteProduct(Long productId) {
        productRepository.deleteById(productId);
    }

    @Override
    public Product update(Product product) {
        productRepository.findById(product.getProductId()).orElseThrow(()->new NoSuchElementException("Khong ton tai Id "));
        return productRepository.save(product);
    }

    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    public List<Product> getTopFeaturedProducts(int limit) {
        Pageable pageable = PageRequest.of(0,limit);
        return productRepository.findTopProductsByRevenue(pageable);
    }

    @Override
    public List<Product> getTopBestSellingProducts(Date from, Date to, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return productRepository.findTopBestSellingProductsBetweenDates(from, to, pageable);
    }
}
