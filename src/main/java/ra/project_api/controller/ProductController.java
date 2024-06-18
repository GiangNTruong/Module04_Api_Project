package ra.project_api.controller;


import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ra.project_api.constrants.EHttpStatus;
import ra.project_api.dto.response.ResponseWrapper;
import ra.project_api.model.Category;
import ra.project_api.model.Product;
import ra.project_api.service.CategoryService;
import ra.project_api.service.ProductService;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api.myservice.com/v1/products")
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;



    @GetMapping("/categories/{categoryId}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable("categoryId") Long categoryId) {
        Category category = categoryService.findById(categoryId);
        if (category != null) {
            List<Product> products = productService.findProductsByCategory(category);
            return new ResponseEntity<>(products, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/new-products")
    public ResponseEntity<List<Product>> getLatestProducts() {
        List<Product> products = productService.findLatestProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/sold")
    public ResponseEntity<Page<Product>> getSoldProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Page<Product> products = productService.findSoldProducts(page, size, sortBy, sortDir);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // top 3 danh sách sản phẩm bán chạy nhất lấy số lượng quanlyti nhiều nhất trong orderDetail
    @GetMapping("/best-seller-products")
    public ResponseEntity<List<Product>> getBestSellerProducts() {
        List<Product> bestSellerProducts = productService.findTop3BestSellingProducts();
        return new ResponseEntity<>(bestSellerProducts, HttpStatus.OK);
    }


    @GetMapping("/search")
    public ResponseEntity<ResponseWrapper<List<Product>>> searchProducts(@RequestParam("keyword") String keyword) {
        List<Product> products = productService.searchProductsByNameOrDescription(keyword);
        ResponseWrapper<List<Product>> responseWrapper = ResponseWrapper.<List<Product>>builder()
                .eHttpStatus(EHttpStatus.SUCCESS)
                .statusCode(HttpStatus.OK.value())
                .data(products)
                .build();
        return ResponseEntity.ok(responseWrapper);
    }

    @GetMapping("/featured-products")
    public ResponseEntity<List<Product>> getFeaturedProduct(){
        List<Product> featuredProducts = productService.getTopFeaturedProducts(3);
        return ResponseEntity.ok(featuredProducts);
    }
}
