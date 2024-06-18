package ra.project_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ra.project_api.model.User;
import ra.project_api.model.WishList;

import java.util.List;

public interface WishListRepository extends JpaRepository<WishList,Long> {
    List<WishList> findByUser(User user);
}
