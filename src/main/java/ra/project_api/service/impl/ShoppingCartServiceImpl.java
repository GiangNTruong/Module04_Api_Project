package ra.project_api.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ra.project_api.dto.request.AddToCartRequestDTO;
import ra.project_api.dto.response.ShoppingCartItemDTO;
import ra.project_api.model.Product;
import ra.project_api.model.ShoppingCart;
import ra.project_api.model.User;
import ra.project_api.repository.IUserRepository;
import ra.project_api.repository.ProductRepository;
import ra.project_api.repository.ShoppingCartRepository;
import ra.project_api.service.IShoppingCartService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShoppingCartServiceImpl implements IShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ProductRepository productRepository;
    private final IUserRepository userRepository;
    private final ModelMapper modelMapper;
    @Override
    public List<ShoppingCartItemDTO> getCartItemsByUser(String username) {
        User user = userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        List<ShoppingCart> cartItems =shoppingCartRepository.findByUser(user);
        return cartItems.stream()
                .map(cartItem -> {
                    Product product = cartItem.getProduct();
                    return ShoppingCartItemDTO.builder()
                            .productId(product.getProductId())
                            .productName(product.getProductName())
                            .description(product.getDescription())
                            .unitPrice(product.getUnitPrice())
                            .orderQuantity(cartItem.getOrderQuantity())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public void addToCart(String username, AddToCartRequestDTO requestDTO) {
        User user = userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        Product product = productRepository.findById(requestDTO.getProductId())
                .orElseThrow(() -> new NoSuchElementException("Product not found"));

        // Kiểm tra số lượng sản phẩm trong kho
        if (requestDTO.getQuantity() > product.getStockQuantity()) {
            throw new IllegalArgumentException("Quantity exceeds available stock");
        }

        Optional<ShoppingCart> existingCartItem = shoppingCartRepository.findByUserAndProduct(user, product);
        if (existingCartItem.isPresent()) {
            // Nếu sản phẩm đã có trong giỏ hàng, cập nhật số lượng
            ShoppingCart cartItem = existingCartItem.get();
            int newQuantity = cartItem.getOrderQuantity() + requestDTO.getQuantity();

            // Kiểm tra số lượng mới với số lượng trong kho
            if (newQuantity > product.getStockQuantity()) {
                throw new IllegalArgumentException("Quantity exceeds available stock");
            }

            cartItem.setOrderQuantity(newQuantity);
            shoppingCartRepository.save(cartItem);
        } else {
            // Nếu sản phẩm chưa có trong giỏ hàng, tạo mới
            ShoppingCart newCartItem = ShoppingCart.builder()
                    .user(user)
                    .product(product)
                    .orderQuantity(requestDTO.getQuantity())
                    .build();
            shoppingCartRepository.save(newCartItem);
        }
    }

    @Override

    public void updateCartItem(Long cartItemId, String username, Integer quantity) {
        User user = userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        Optional<ShoppingCart> optionalCartItem = shoppingCartRepository.findByIdAndUser(cartItemId, user);
        if (optionalCartItem.isPresent()) {
            ShoppingCart cartItem = optionalCartItem.get();
            cartItem.setOrderQuantity(quantity);
            shoppingCartRepository.save(cartItem);
        } else {
            throw new EntityNotFoundException("Cart item not found for user");
        }
    }

    @Override
    public void deleteCartItem(Long cartItemId, String username) {
        User user = userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        // Kiểm tra xem cart item có tồn tại và thuộc về người dùng không
        ShoppingCart cartItem = shoppingCartRepository.findByIdAndUser(cartItemId, user)
                .orElseThrow(() -> new NoSuchElementException("Cart item not found"));

        shoppingCartRepository.delete(cartItem);
    }

    @Override
    public void clearCart(String username) {
        User user = userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<ShoppingCart> cartItems = shoppingCartRepository.findByUser(user);
        shoppingCartRepository.deleteAll(cartItems);
    }
}
