package ra.project_api.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ra.project_api.model.Category;
import ra.project_api.model.Product;
import ra.project_api.repository.ProductRepository;
import ra.project_api.service.ProductService;

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
    public Page<Product> findSoldProducts(int page, int size, String sortBy, String sortDir) {
        Pageable pageable = PageRequest.of(page, size,
                sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending());
        return productRepository.findSoldProducts(pageable);
    }


    @Override
    public List<Product> searchProductsByNameOrDescription(String keyword) {
        return productRepository.searchByNameOrDescription(keyword);
    }

    @Override
    public List<Product> findTop3BestSellingProducts() {
        return productRepository.findTop3BestSellingProducts(PageRequest.of(0, 3));
    }

}
