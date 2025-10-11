package com.example.travelpractice.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelpractice.data.Review
import com.example.travelpractice.repository.ReviewsRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReviewsViewModel : ViewModel() {
    private val repository = ReviewsRepository()
    private val auth = FirebaseAuth.getInstance()
    
    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    init {
        loadReviews()
    }
    
    fun loadReviews() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val reviewsList = repository.getAllReviews()
                _reviews.value = reviewsList
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load reviews: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun addReview(review: Review) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val reviewId = repository.addReview(review)
                loadReviews() // Refresh the list
            } catch (e: Exception) {
                _errorMessage.value = "Failed to add review: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun updateReview(review: Review) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.updateReview(review)
                loadReviews() // Refresh the list
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update review: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun deleteReview(reviewId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.deleteReview(reviewId)
                loadReviews() // Refresh the list
            } catch (e: Exception) {
                _errorMessage.value = "Failed to delete review: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun getReviewsByDestination(destination: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val reviewsList = repository.getReviewsByDestination(destination)
                _reviews.value = reviewsList
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load reviews: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
}
