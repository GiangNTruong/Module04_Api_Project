package ra.project_api.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ra.project_api.model.Category;


import java.util.List;

public interface CategoryService {
    List<Category> findAll();

    Category findById(Long id);
    List<Category> getSoldCategories();
    void deleteCategoryById(Long categoryId);
    Category update(Category category);
    Category save(Category category);
    Page<Category> findAll(Pageable pageable);
}
