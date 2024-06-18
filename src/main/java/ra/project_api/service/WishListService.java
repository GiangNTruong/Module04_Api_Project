package ra.project_api.service;

import ra.project_api.dto.response.WishListResponse;
import ra.project_api.model.User;
import ra.project_api.model.WishList;

import java.util.List;

public interface WishListService {
    List<WishListResponse> getWishListByUser(User user);
    void addToWishList(User user, Long productId);
    void removeFromWishList(Long wishListId);

}
