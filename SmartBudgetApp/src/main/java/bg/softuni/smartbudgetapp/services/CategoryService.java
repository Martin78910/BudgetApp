package bg.softuni.smartbudgetapp.services;

import bg.softuni.smartbudgetapp.models.CategoryEntity;
import bg.softuni.smartbudgetapp.models.CurrencyEntity;

import java.util.List;

public interface CategoryService {

    List<CategoryEntity> getAllCategories();
}
