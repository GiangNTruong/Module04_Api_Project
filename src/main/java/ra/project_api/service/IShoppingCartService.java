package ra.project_api.service;

import ra.project_api.dto.request.AddToCartRequestDTO;
import ra.project_api.dto.response.ShoppingCartItemDTO;
import ra.project_api.model.Product;
import ra.project_api.model.User;

import java.util.List;

public interface IShoppingCartService {
    List<ShoppingCartItemDTO> getCartItemsByUser(String username);
    void addToCart(String username, AddToCartRequestDTO addToCartRequestDTO);
    void updateCartItem(Long cartItemId, String username, Integer quantity);
    void deleteCartItem(Long cartItemId, String username);
    void clearCart(String username);
}
