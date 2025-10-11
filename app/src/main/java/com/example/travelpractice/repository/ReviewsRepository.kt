package com.example.travelpractice.repository

import com.example.travelpractice.data.Review
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.util.Date

class ReviewsRepository {
    private val db = FirebaseFirestore.getInstance()
    private val reviewsCollection = db.collection("reviews")
    
    suspend fun getAllReviews(): List<Review> {
        return try {
            val snapshot = reviewsCollection
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            
            snapshot.documents.mapNotNull { document ->
                try {
                    document.toObject(Review::class.java)?.copy(id = document.id)
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            throw Exception("Failed to fetch reviews: ${e.message}")
        }
    }
    
    suspend fun addReview(review: Review): String {
        return try {
            val reviewData = review.copy(
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )

            val docRef = reviewsCollection.add(reviewData).await()
            docRef.id
        } catch (e: Exception) {
            throw Exception("Failed to add review: ${e.message}")
        }
    }
    
    suspend fun updateReview(review: Review) {
        try {
            val reviewData = review.copy(updatedAt = System.currentTimeMillis())
            reviewsCollection.document(review.id).set(reviewData).await()
        } catch (e: Exception) {
            throw Exception("Failed to update review: ${e.message}")
        }
    }
    
    suspend fun deleteReview(reviewId: String) {
        try {
            reviewsCollection.document(reviewId).delete().await()
        } catch (e: Exception) {
            throw Exception("Failed to delete review: ${e.message}")
        }
    }
    
    suspend fun getReviewsByDestination(destination: String): List<Review> {
        return try {
            val snapshot = reviewsCollection
                .whereEqualTo("destination", destination)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            
            snapshot.documents.mapNotNull { document ->
                try {
                    document.toObject(Review::class.java)?.copy(id = document.id)
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            throw Exception("Failed to fetch reviews by destination: ${e.message}")
        }
    }
    
    suspend fun getReviewsByUser(userId: String): List<Review> {
        return try {
            val snapshot = reviewsCollection
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            
            snapshot.documents.mapNotNull { document ->
                try {
                    document.toObject(Review::class.java)?.copy(id = document.id)
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            throw Exception("Failed to fetch user reviews: ${e.message}")
        }
    }
}
