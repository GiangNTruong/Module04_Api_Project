package ra.project_api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import ra.project_api.constrants.EHttpStatus;
import ra.project_api.constrants.OrderStatus;
import ra.project_api.dto.request.AddToCartRequestDTO;
import ra.project_api.dto.request.ChangePasswordRequest;
import ra.project_api.dto.request.CheckoutRequestDTO;
import ra.project_api.dto.request.UserRequestDTO;
import ra.project_api.dto.response.*;
import ra.project_api.model.Address;
import ra.project_api.model.User;
import ra.project_api.model.WishList;
import ra.project_api.security.jwt.JwtProvider;
import ra.project_api.service.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/api.myservice.com/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;
    private final JwtProvider jwtProvider;
    private final ModelMapper modelMapper;
    private final IShoppingCartService shoppingCartService;
    private final IOrderService orderService;
    private final WishListService wishListService;
    private final AddressService addressService;

    @PutMapping("/account/change-password")
    public ResponseEntity<ResponseWrapper<String>> changePassword(@RequestParam Long userId, @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        userService.changePassword(userId, changePasswordRequest);
        ResponseWrapper<String> responseWrapper = ResponseWrapper.<String>builder()
                .eHttpStatus(EHttpStatus.SUCCESS)
                .statusCode(HttpStatus.OK.value())
                .data("Password changed successfully")
                .build();
        return ResponseEntity.ok(responseWrapper);
    }

    @GetMapping("/account")
    public ResponseEntity<UserResponseDTO> getUserAccount(@RequestHeader("Authorization") String token) {
        // Xóa bỏ "Bearer " từ token
        String jwt = token.substring(7);
        // Lấy username từ token
        String username = jwtProvider.getUserNameFromToken(jwt);
        // Lấy thông tin người dùng từ username
        User user = userService.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        // Chuyển đổi thông tin người dùng sang DTO để trả về
        UserResponseDTO userResponse = modelMapper.map(user, UserResponseDTO.class);
        return ResponseEntity.ok(userResponse);
    }
    @PutMapping("/account")
    public ResponseEntity<UserResponseDTO> updateUserAccount(@RequestHeader("Authorization") String token, @RequestBody UserRequestDTO updateUserRequest) {
        // Xóa bỏ "Bearer " từ token
        String jwt = token.substring(7);
        // Lấy username từ token
        String username = jwtProvider.getUserNameFromToken(jwt);
        // Lấy thông tin người dùng từ username
        User user = userService.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        // Cập nhật thông tin người dùng từ updateUserRequest
        user.setFullname(updateUserRequest.getFullname());
        user.setPhone(updateUserRequest.getPhone());
        user.setAddress(updateUserRequest.getAddress());
        // Nếu email cũng cần cập nhật, thêm dòng dưới đây
        // user.setEmail(updateUserRequest.getEmail());
        // Lưu thông tin người dùng đã cập nhật
        User updatedUser = userService.updateUser(user);
        // Chuyển đổi thông tin người dùng sang DTO để trả về
        UserResponseDTO userResponse = modelMapper.map(updatedUser, UserResponseDTO.class);
        return ResponseEntity.ok(userResponse);
    }


    //gio hàng
    @GetMapping("/cart/list")
    public ResponseEntity<ResponseWrapper<List<ShoppingCartItemDTO>>> getCartItems(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7);
        String username = jwtProvider.getUserNameFromToken(jwt);
        List<ShoppingCartItemDTO> cartItems = shoppingCartService.getCartItemsByUser(username);

        ResponseWrapper<List<ShoppingCartItemDTO>> responseWrapper = ResponseWrapper.<List<ShoppingCartItemDTO>>builder()
                .eHttpStatus(EHttpStatus.SUCCESS)
                .statusCode(HttpStatus.OK.value())
                .data(cartItems)
                .build();

        return ResponseEntity.ok(responseWrapper);
    }

    @PostMapping("/cart/add")
    public ResponseEntity<ResponseWrapper<String>> addToCart(@RequestHeader("Authorization") String token,
                                                             @RequestBody AddToCartRequestDTO requestDTO) {
        String jwt = token.substring(7);
        String username = jwtProvider.getUserNameFromToken(jwt);

        shoppingCartService.addToCart(username, requestDTO);

        ResponseWrapper<String> responseWrapper = ResponseWrapper.<String>builder()
                .eHttpStatus(EHttpStatus.SUCCESS)
                .statusCode(HttpStatus.OK.value())
                .data("Thêm sản phẩm vào giỏ hàng thành công")
                .build();

        return ResponseEntity.ok(responseWrapper);
    }

    @PutMapping("/cart/items/{cartItemId}")
    public ResponseEntity<ResponseWrapper<String>> updateCartItem(@PathVariable Long cartItemId,
                                                                @RequestParam Integer quantity,
                                                                @RequestHeader("Authorization") String token) {
        String jwt = token.substring(7);
        String username = jwtProvider.getUserNameFromToken(jwt);

        shoppingCartService.updateCartItem(cartItemId, username, quantity);

        ResponseWrapper<String> responseWrapper = ResponseWrapper.<String>builder()
                .eHttpStatus(EHttpStatus.SUCCESS)
                .statusCode(HttpStatus.OK.value())
                .data("Cập nhật số lượng sản phẩm trong giỏ hàng thành công")
                .build();

        return ResponseEntity.ok(responseWrapper);
    }

    //Xóa 1 sản phẩm trong giả hàng
    @DeleteMapping("/cart/items/{cartItemId}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable Long cartItemId,
                                               @RequestHeader("Authorization") String token) {
        // Xóa bỏ "Bearer " từ token
        String jwt = token.substring(7);
        // Lấy username từ token
        String username = jwtProvider.getUserNameFromToken(jwt);
        // Thực hiện xóa sản phẩm trong giỏ hàng
        shoppingCartService.deleteCartItem(cartItemId, username);
        return ResponseEntity.noContent().build();
    }


    //Xóa toàn bộ sản phẩm
    @DeleteMapping("/cart/clear")
    public ResponseEntity<Void> clearCart(@RequestHeader("Authorization") String token) {
        // Xóa bỏ "Bearer " từ token
        String jwt = token.substring(7);
        // Lấy username từ token
        String username = jwtProvider.getUserNameFromToken(jwt);
        // Thực hiện xóa toàn bộ sản phẩm trong giỏ hàng
        shoppingCartService.clearCart(username);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/cart/checkout")
    public ResponseEntity<ResponseWrapper<String>> checkout(@RequestBody CheckoutRequestDTO checkoutRequest,
                                                          @RequestHeader("Authorization") String token) {
        String jwt = token.substring(7);
        String username = jwtProvider.getUserNameFromToken(jwt);

        orderService.placeOrder(checkoutRequest, username);

        ResponseWrapper<String> responseWrapper = ResponseWrapper.<String>builder()
                .eHttpStatus(EHttpStatus.SUCCESS)
                .statusCode(HttpStatus.OK.value())
                .data("Đặt hàng thành công")
                .build();

        return ResponseEntity.ok(responseWrapper);
    }


    //danh sách trang yêu thích

    @GetMapping("/wish-list")
    public ResponseEntity<List<WishListResponse>> getWishList(@RequestHeader("Authorization") String token) {
        // Remove "Bearer " from token
        String jwt = token.substring(7);
        // Get username from token
        String username = jwtProvider.getUserNameFromToken(jwt);
        // Find the user
        User user = userService.findByUsernameOrEmail(username, username).orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
        // Get the wishlist
        List<WishListResponse> wishList = wishListService.getWishListByUser(user);
        return ResponseEntity.ok(wishList);
    }

    @PostMapping("/wish-list")
    public ResponseEntity<?> addToWishList(@RequestHeader("Authorization") String token, @RequestBody Map<String, Long> payload) {
        // Remove "Bearer " from token
        String jwt = token.substring(7);
        // Get username from token
        String username = jwtProvider.getUserNameFromToken(jwt);
        // Find the user
        User user = userService.findByUsernameOrEmail(username, username).orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
        // Add product to wishlist
        wishListService.addToWishList(user, payload.get("productId"));
        return ResponseEntity.ok("Product added to wishlist successfully");
    }

    @DeleteMapping("/wish-list/{wishListId}")
    public ResponseEntity<?> removeFromWishList(@PathVariable Long wishListId) {
        wishListService.removeFromWishList(wishListId);
        return ResponseEntity.ok("Product removed from wishlist successfully");
    }



//Lịch sử mua hàng
    @GetMapping("/history")
    public ResponseEntity<List<OrderHistoryResponse>> getOrderHistory(@RequestHeader("Authorization") String token) {
        // Remove "Bearer " from token
        String jwt = token.substring(7);
        // Get username from token
        String username = jwtProvider.getUserNameFromToken(jwt);
        // Find the user
        User user = userService.findByUsernameOrEmail(username, username).orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
        // Get the order history
        List<OrderHistoryResponse> orderHistory = orderService.getOrderHistoryByUser(user);
        return ResponseEntity.ok(orderHistory);
    }

    @GetMapping("/history/status/{orderStatus}")
    public ResponseEntity<List<OrderHistoryResponse>> getOrderHistoryByStatus(
            @PathVariable OrderStatus orderStatus,
            Principal principal) {

        String username = principal.getName();
        List<OrderHistoryResponse> orderHistory = orderService.getOrderHistoryByStatusAndUser(orderStatus, username);
        return ResponseEntity.ok(orderHistory);
    }

    @GetMapping("/history/{serialNumber}")
    public ResponseEntity<ResponseWrapper<OrderDetailsResponseDTO>> getOrderDetailsBySerialNumber(
            @PathVariable String serialNumber,
            Principal principal) {

        String username = principal.getName();
        OrderDetailsResponseDTO orderDetails = orderService.getOrderDetailsBySerialNumber(serialNumber, username);
        ResponseWrapper<OrderDetailsResponseDTO> responseWrapper = ResponseWrapper.<OrderDetailsResponseDTO>builder()
                .eHttpStatus(EHttpStatus.SUCCESS)
                .statusCode(HttpStatus.OK.value())
                .data(orderDetails)
                .build();
        return ResponseEntity.ok(responseWrapper);
    }

    @PutMapping("/history/{orderId}/cancel")

    public ResponseEntity<String> cancelOrder(
            @PathVariable Long orderId,
            Principal principal) {

        String username = principal.getName();
        orderService.cancelOrder(orderId, username);

        return ResponseEntity.ok("Order cancelled successfully");
    }



    //Địa chỉ
    @GetMapping("/account/addresses")
    public ResponseEntity<List<Address>> getUserAddresses(
            @RequestHeader("Authorization") String token) {

        // Extract username from token
        String username = extractUsernameFromToken(token);

        // Retrieve addresses by username
        List<Address> addresses = addressService.getAddressesByUsername(username);

        return ResponseEntity.ok(addresses);
    }

    private String extractUsernameFromToken(String token) {
        String jwt = token.substring(7);
        return jwtProvider.getUserNameFromToken(jwt);
    }

    @GetMapping("/account/addresses/{addressId}")
    public ResponseEntity<Address> getAddressById(
            @RequestHeader("Authorization") String token,
            @PathVariable Long addressId) {

        // Remove "Bearer " from token
        String jwt = token.substring(7);

        // Get username from token
        String username = jwtProvider.getUserNameFromToken(jwt);

        // Find the user by username
        User user = userService.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        // Retrieve address by addressId using the service method
        try {
            Address address = addressService.getAddressById(addressId);

            // Check if the retrieved address belongs to the authenticated user
            if (!address.getUser().getUsername().equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // Return 403 Forbidden if the address does not belong to the user
            }

            return ResponseEntity.ok(address);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/account/addresses")
    public ResponseEntity<Address> addAddress(
            @RequestHeader("Authorization") String token,
            @RequestBody AddressDTO addressDTO) {

        // Lấy username từ token
        String jwt = token.substring(7);
        String username = jwtProvider.getUserNameFromToken(jwt);

        // Thêm địa chỉ mới
        Address newAddress = addressService.addAddress(username, addressDTO);

        return ResponseEntity.ok(newAddress);
    }

    @DeleteMapping("/account/addresses/{addressId}")
    public ResponseEntity<?> deleteAddress(@PathVariable Long addressId){
        addressService.deleteAddress(addressId);
        return ResponseEntity.ok("Address removed from wishlist successfully");
    }
}