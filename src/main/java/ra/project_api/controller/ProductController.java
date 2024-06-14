package ra.project_api.controller;


import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ra.project_api.model.Category;
import ra.project_api.model.Product;
import ra.project_api.service.CategoryService;
import ra.project_api.service.ProductService;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api.myservice.com/v1/public/products")
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;


    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return productService.findProductById(id);
    }

    @GetMapping("/categories/{categoryId}")
    @ResponseStatus(HttpStatus.OK)
    public List<Product> getProductsByCategory(@PathVariable("categoryId") Long categoryId) {
        Category category = categoryService.findById(categoryId);
        return productService.findProductsByCategory(category);
    }
    @GetMapping("/latest")
    @PermitAll
    @ResponseStatus(HttpStatus.OK)
    public List<Product> getLatestProducts() {
        return productService.findLatestProducts();
    }

    @GetMapping("/available")
    @PermitAll
    @ResponseStatus(HttpStatus.OK)
    public Page<Product> getAvailableProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        return productService.findAvailableProducts(page, size, sortBy, sortDir);
    }

    @GetMapping("/search")
    @PermitAll
    @ResponseStatus(HttpStatus.OK)
    public List<Product> searchProducts(
            @RequestParam String keyword) {
        return productService.searchProductsByNameOrDescription(keyword);
    }
}