package ra.project_api.service;

import ra.project_api.model.Category;

import java.util.List;

public interface CategoryService {
    List<Category> findAll();

    Category findById(Long id);
}
