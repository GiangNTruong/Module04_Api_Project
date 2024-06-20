package ra.project_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ra.project_api.model.User;
import ra.project_api.model.WishList;

import java.util.Date;
import java.util.List;

public interface WishListRepository extends JpaRepository<WishList,Long> {
    List<WishList> findByUser(User user);
//    @Query("SELECT w.product, COUNT(w) AS likeCount " +
//                  "FROM WishList w " +
//                  "WHERE w.createdAt BETWEEN :from AND :to " +
//                  "GROUP BY w.product " +
//                  "ORDER BY likeCount DESC")
//    List<Object[]> findMostLikedProductsBetweenDates(@Param("from") Date from, @Param("to") Date to);

}
