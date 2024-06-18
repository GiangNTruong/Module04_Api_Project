package ra.project_api.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ra.project_api.model.Category;
import ra.project_api.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/api.myservice.com/v1/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;


    @GetMapping("/sold-categories")
    @ResponseStatus(HttpStatus.OK)
    public List<Category> getSoldCategories() {
        return categoryService.getSoldCategories();
    }

}