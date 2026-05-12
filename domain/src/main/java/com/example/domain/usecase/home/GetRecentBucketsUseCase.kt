package com.example.domain.usecase.home

import com.example.domain.repository.home.HomeRepository
import javax.inject.Inject

class GetRecentBucketsUseCase @Inject constructor(
    private val homeRepository: HomeRepository
) {
    suspend operator fun invoke() = homeRepository.fetchRecentBuckets()
}
