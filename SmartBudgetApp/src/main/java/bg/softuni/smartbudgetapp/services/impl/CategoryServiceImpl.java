package bg.softuni.smartbudgetapp.services.impl;

import bg.softuni.smartbudgetapp.models.CategoryEntity;
import bg.softuni.smartbudgetapp.models.CurrencyEntity;
import bg.softuni.smartbudgetapp.repositories.CategoryRepository;
import bg.softuni.smartbudgetapp.services.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;


    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<CategoryEntity> getAllCategories() {
        return categoryRepository.findAll();
    }

}
