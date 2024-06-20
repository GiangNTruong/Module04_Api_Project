package ra.project_api.service.impl;

;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ra.project_api.dto.request.CategoryDTO;
import ra.project_api.model.Category;
import ra.project_api.repository.CategoryRepository;
import ra.project_api.service.CategoryService;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public Category findById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }


    @Override
    public List<Category> getSoldCategories() {
        return categoryRepository.findSoldCategories();
    }
    @Override
    public void deleteCategoryById(Long categoryId) {
        if (categoryRepository.existsProductByCategoryId(categoryId)) {
            throw new IllegalStateException("Cannot delete category because it has associated products");
        }
        categoryRepository.deleteById(categoryId);
    }
    @Override
    public Category update(Long categoryId, CategoryDTO categoryDTO) {
        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NoSuchElementException("Update failed because the ID does not exist"));

        modelMapper.map(categoryDTO, existingCategory);
        return categoryRepository.save(existingCategory);
    }


    @Override
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public Page<Category> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

}
