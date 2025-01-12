package ru.modgy.room.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.modgy.exception.NotFoundException;
import ru.modgy.room.category.dto.CategoryDto;
import ru.modgy.room.category.dto.NewCategoryDto;
import ru.modgy.room.category.dto.UpdateCategoryDto;
import ru.modgy.room.category.dto.mapper.CategoryMapper;
import ru.modgy.room.category.model.Category;
import ru.modgy.room.category.repository.CategoryRepository;
import ru.modgy.utility.EntityService;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final EntityService entityService;

    @Transactional
    @Override
    public CategoryDto addCategory(Long userId, NewCategoryDto newCategoryDto) {
        Category newCategory = categoryMapper.toCategory(newCategoryDto);
        Category addedCategory = categoryRepository.save(newCategory);
        log.info("CategoryService: addCategory, userId={}, newCategoryDto={}", userId, newCategoryDto);
        return categoryMapper.toCategoryDto(addedCategory);
    }

    @Transactional
    @Override
    public CategoryDto updateCategoryById(Long userId, Long catId, UpdateCategoryDto updateCategoryDto) {
        Category oldCategory = entityService.getCategoryIfExists(catId);
        Category newCategory = categoryMapper.toCategory(updateCategoryDto);
        newCategory.setId(oldCategory.getId());

        if (Objects.isNull(newCategory.getName())) {
            newCategory.setName(oldCategory.getName());
        }

        if (Objects.isNull(newCategory.getDescription())) {
            newCategory.setDescription(oldCategory.getDescription());
        }

        Category updatedCategory = categoryRepository.save(newCategory);
        log.info("CategoryService: updateCategoryById, userId={}, catId={}, updateCategoryDto={}",
                userId, catId, updateCategoryDto);

        return categoryMapper.toCategoryDto(updatedCategory);
    }

    @Transactional(readOnly = true)
    @Override
    public CategoryDto getCategoryById(Long userId, Long catId) {
        Category category = entityService.getCategoryIfExists(catId);
        log.info("CategoryService: getCategoryById, userId={}, catId={}", userId, catId);
        return categoryMapper.toCategoryDto(category);
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<CategoryDto> getAllCategories(Long userId) {
        List<Category> allCategories = categoryRepository.findAllOrderByNameAsc().orElse(Collections.emptyList());
        log.info("CategoryService: getAllCategories, userId={}, list size={}", userId, allCategories.size());
        return categoryMapper.toCategoryDto(allCategories);
    }

    @Transactional
    @Override
    public void deleteCategoryById(Long userId, Long catId) {
         int result = categoryRepository.deleteCategoryById(catId);

        if (result == 0) {
            throw new NotFoundException(String.format("category with id=%d not found", catId));
        }

        log.info("CategoryService: deleteCategoryById, userId={}, catId={}", userId, catId);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean checkUniqueCategoryName(Long userId, String categoryName) {
        boolean isUnique = false;
        int  result = categoryRepository.countAllByName(categoryName);

        if (result == 0) {
            isUnique = true;
        }

        log.info("CategoryService: checkUniqueCategoryName, userId={}, categoryName={}, isUnique={}", userId, categoryName, isUnique);
        return isUnique;
    }
}
