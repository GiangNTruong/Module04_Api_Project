package ra.project_api.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ra.project_api.model.Category;
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
