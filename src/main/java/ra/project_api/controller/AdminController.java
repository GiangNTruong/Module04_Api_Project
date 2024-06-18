package ra.project_api.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ra.project_api.constrants.EHttpStatus;
import ra.project_api.constrants.OrderStatus;
import ra.project_api.dto.request.UpdateOrderStatusDTO;
import ra.project_api.dto.response.OrderDetailsResponseDTO;
import ra.project_api.dto.response.ResponseWrapper;
import ra.project_api.dto.response.UserResponseDTO;
import ra.project_api.model.*;
import ra.project_api.service.CategoryService;
import ra.project_api.service.IOrderService;
import ra.project_api.service.IUserService;
import ra.project_api.service.ProductService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api.myservice.com/v1/admin")
public class AdminController {

    private final IUserService userService;
    private final CategoryService categoryService;
    private final ModelMapper modelMapper;
    private final ProductService productService;
    private final IOrderService orderService;
    @GetMapping("/users")
    public ResponseEntity<ResponseWrapper<Page<UserResponseDTO>>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "userId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        // Đunúng asc thì sắp xếp tăng dần còn không thì giảm dần
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<User> usersPage = userService.getUsers(pageable);
// Chuyển đổi danh sách các user thành danh sách các UserResponseDTO
        List<UserResponseDTO> userResponseDTOs = usersPage.getContent().stream()
                .map(user -> modelMapper.map(user, UserResponseDTO.class))
                .collect(Collectors.toList());// Thu thập kết quả thành một danh sách

        Page<UserResponseDTO> userResponseDTOPage = new PageImpl<>(userResponseDTOs, pageable, usersPage.getTotalElements());

        ResponseWrapper<Page<UserResponseDTO>> responseWrapper = ResponseWrapper.<Page<UserResponseDTO>>builder()
                .eHttpStatus(EHttpStatus.SUCCESS)
                .statusCode(HttpStatus.OK.value())
                .data(userResponseDTOPage)
                .build();

        return ResponseEntity.ok(responseWrapper);
    }

    @GetMapping("/users/search")
    public ResponseEntity<Page<User>> searchUsers(
            @RequestParam String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "userId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<User> users = userService.searchUsersByUsername(username, pageable);

        return ResponseEntity.ok(users);
    }

    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getRoles() {
        List<Role> roles = userService.getRoles();
        return ResponseEntity.ok(roles);
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<ResponseWrapper<UserResponseDTO>> updateUserStatus(@PathVariable Long userId) {
        User updatedUser = userService.updateUserStatus(userId);
        UserResponseDTO userResponseDTO = modelMapper.map(updatedUser, UserResponseDTO.class);
        ResponseWrapper<UserResponseDTO> responseWrapper = ResponseWrapper.<UserResponseDTO>builder()
                .eHttpStatus(EHttpStatus.SUCCESS)
                .statusCode(HttpStatus.OK.value())
                .data(userResponseDTO)
                .build();
        return ResponseEntity.ok(responseWrapper);
    }


    //Chức năng danh muc


    @DeleteMapping("/categories/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategoryById(categoryId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @PutMapping("/categories/{categoryId}")
    public ResponseEntity<Category> updateCategory(@RequestBody Category category){
        return new ResponseEntity<>(categoryService.update(category),HttpStatus.OK);
    }

    @PostMapping("/categories")
    public ResponseEntity<ResponseWrapper<Category>> addCategory(@RequestBody Category category) {
        Category savedCategory = categoryService.save(category);
        ResponseWrapper<Category> responseWrapper = ResponseWrapper.<Category>builder()
                .eHttpStatus(EHttpStatus.SUCCESS)
                .statusCode(HttpStatus.CREATED.value())
                .data(savedCategory)
                .build();
        return new ResponseEntity<>(responseWrapper, HttpStatus.CREATED);
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<ResponseWrapper<Category>> getCategoryById(@PathVariable Long id) {
        Category category = categoryService.findById(id);
        ResponseWrapper<Category> responseWrapper = ResponseWrapper.<Category>builder()
                .eHttpStatus(EHttpStatus.SUCCESS)
                .statusCode(HttpStatus.OK.value())
                .data(category)
                .build();
        return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
    }

//    @GetMapping("/categories")
//    public ResponseEntity<Page<Category>> getAllCategories(){
//        Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.ASC, "categoryId"));
//        Page<Category> categories = categoryService.findAll(pageable);
//        return new ResponseEntity<>(categories, HttpStatus.OK);
//    }

    @GetMapping("/categories")
    public ResponseEntity<ResponseWrapper<Page<Category>>> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "categoryId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDir), sortBy));
        Page<Category> categories = categoryService.findAll(pageable);
        ResponseWrapper<Page<Category>> responseWrapper = ResponseWrapper.<Page<Category>>builder()
                .eHttpStatus(EHttpStatus.SUCCESS)
                .statusCode(HttpStatus.OK.value())
                .data(categories)
                .build();
        return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
    }



    //CHức năng sản phẩm
    @DeleteMapping("/products/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId){
     productService.deleteProduct(productId);
     return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/products")
    public ResponseEntity<ResponseWrapper<Product>> addProduct(@RequestBody Product product){
        Product savedProduct = productService.saveProduct(product);
        ResponseWrapper<Product> responseWrapper = ResponseWrapper.<Product>builder()
                .eHttpStatus(EHttpStatus.SUCCESS)
                .statusCode(HttpStatus.CREATED.value())
                .data(savedProduct)
                .build();
        return new ResponseEntity<>(responseWrapper, HttpStatus.CREATED);
    }

    @PutMapping("/products/{productId}")
    public ResponseEntity<Product> updateProduct(@RequestBody Product product){
        return new ResponseEntity<>(productService.update(product),HttpStatus.OK);
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable Long productId) {
        Product product = productService.findProductById(productId);
        if (product != null) {
            return new ResponseEntity<>(product, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("/products")
    public ResponseEntity<ResponseWrapper<Page<Product>>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "productId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Pageable pageable = PageRequest.of(page,size,Sort.by(Sort.Direction.fromString(sortDir),sortBy));
        Page<Product> products = productService.findAll(pageable);
        ResponseWrapper<Page<Product>> responseWrapper = ResponseWrapper.<Page<Product>>builder()
                .eHttpStatus(EHttpStatus.SUCCESS)
                .statusCode(HttpStatus.OK.value())
                .data(products)
                .build();
        return new ResponseEntity<>(responseWrapper,HttpStatus.OK);
    }


    // Đơn hàng
    //Danh sách tất cả đơn hàng - Bắt buộc
    @GetMapping("/orders")
    public ResponseEntity<ResponseWrapper<List<Order>>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        ResponseWrapper<List<Order>> responseWrapper = ResponseWrapper.<List<Order>>builder()
                .eHttpStatus(EHttpStatus.SUCCESS)
                .statusCode(HttpStatus.OK.value())
                .data(orders)
                .build();
        return ResponseEntity.ok(responseWrapper);
    }

    //Chi tiết đơn hàng
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<ResponseWrapper<OrderDetailsResponseDTO>> getOrderById(@PathVariable Long orderId) {
        OrderDetailsResponseDTO orderDetails = orderService.getOrderById(orderId);
        ResponseWrapper<OrderDetailsResponseDTO> responseWrapper = ResponseWrapper.<OrderDetailsResponseDTO>builder()
                .eHttpStatus(EHttpStatus.SUCCESS)
                .statusCode(HttpStatus.OK.value())
                .data(orderDetails)
                .build();
        return ResponseEntity.ok(responseWrapper);
    }

//Danh sách đơn hàng theo trạng thái - Bắt buộc
    @GetMapping("/orders/status/{orderStatus}")
    public ResponseEntity<ResponseWrapper<List<Order>>> getOrdersByStatus(@PathVariable OrderStatus orderStatus) {
        List<Order> orders = orderService.getOrdersByStatus(orderStatus);
        ResponseWrapper<List<Order>> responseWrapper = ResponseWrapper.<List<Order>>builder()
                .eHttpStatus(EHttpStatus.SUCCESS)
                .statusCode(HttpStatus.OK.value())
                .data(orders)
                .build();
        return ResponseEntity.ok(responseWrapper);
    }


// Cập nhật trạng thái đơn hàng (payload : orderStatus) - Bắt buộc
    @PutMapping("/orders/{orderId}/status")
    public ResponseEntity<ResponseWrapper<Order>> updateOrderStatus(@PathVariable Long orderId, @RequestBody UpdateOrderStatusDTO updateOrderStatusDTO) {
        Order updatedOrder = orderService.updateOrderStatus(orderId, updateOrderStatusDTO);
        ResponseWrapper<Order> responseWrapper = ResponseWrapper.<Order>builder()
                .eHttpStatus(EHttpStatus.SUCCESS)
                .statusCode(HttpStatus.OK.value())
                .data(updatedOrder)
                .build();
        return ResponseEntity.ok(responseWrapper);
    }

}
