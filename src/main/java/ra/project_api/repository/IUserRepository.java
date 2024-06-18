package ra.project_api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ra.project_api.model.User;

import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User,Long> {
//    @Query("select u from User u where u.username=?1 or u.email=?1")
//   User loadByUsername(String username);
Optional<User> findByUsername(String username);
    Optional<User> findByUsernameOrEmail(String username, String email);
    Page<User> findAll(Pageable pageable);
    Page<User> findByUsernameContainingIgnoreCase(String username, Pageable pageable);

    //check trùng
    boolean existsByUsername(String username);

}
