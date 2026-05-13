package com.example.domain.usecase.category

import com.example.domain.repository.category.CategoryRepository
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke() = categoryRepository.fetchCategories()
}
