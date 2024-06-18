package ra.project_api.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ra.project_api.dto.response.WishListResponse;
import ra.project_api.model.Product;
import ra.project_api.model.User;
import ra.project_api.model.WishList;
import ra.project_api.repository.ProductRepository;
import ra.project_api.repository.WishListRepository;
import ra.project_api.service.ProductService;
import ra.project_api.service.WishListService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class WishListServiceImpl implements WishListService {
    @Autowired
    private WishListRepository wishListRepository;
    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<WishListResponse> getWishListByUser(User user) {
        List<WishList> wishLists = wishListRepository.findByUser(user);
        return wishLists.stream()
                .map(wishList -> WishListResponse.builder()
                        .wishListId(wishList.getWishListId())
                        .productId(wishList.getProduct().getProductId())
                        .productName(wishList.getProduct().getProductName())
                        .description(wishList.getProduct().getDescription())
                        .unitPrice(wishList.getProduct().getUnitPrice())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void addToWishList(User user, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("không tồn tại id Product"));
        WishList wishList = new WishList();
        wishList.setUser(user);
        wishList.setProduct(product);
        wishListRepository.save(wishList);
    }

    @Override
    public void removeFromWishList(Long wishListId) {
        WishList wishList = wishListRepository.findById(wishListId)
                .orElseThrow(() -> new NoSuchElementException("Không tồn tai id "));
        wishListRepository.delete(wishList);
    }

}
