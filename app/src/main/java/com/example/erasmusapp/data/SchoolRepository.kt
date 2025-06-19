package com.example.erasmusapp.data

import com.example.erasmusapp.model.Comment
import com.example.erasmusapp.model.School
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.Query

class SchoolRepository {

    private val db = FirebaseFirestore.getInstance()
    private val schoolsCollection = db.collection("schools")

    suspend fun getSchoolById(id: String): School? {
        return try {
            val doc = schoolsCollection.document(id).get().await()
            if (doc.exists()) {
                doc.toObject(School::class.java)?.copy(id = doc.id)
            } else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getCommentsForSchool(schoolId: String): List<Comment> {
        return try {
            schoolsCollection.document(schoolId)
                .collection("comments")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()
                .mapNotNull { snapshot ->
                    snapshot.toObject(Comment::class.java)
                }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addCommentForSchool(schoolId: String, comment: Comment) {
        try {
            val schoolRef = schoolsCollection.document(schoolId)

            // Yorum ekle
            schoolRef.collection("comments").add(comment).await()

            // Mevcut verileri oku
            val snapshot = schoolRef.get().await()
            val school = snapshot.toObject(School::class.java)

            if (school != null) {
                val newCount = (school.ratingCount ?: 0) + 1
                val newAvg = ((school.avgRating ?: 0.0) * (school.ratingCount ?: 0) + comment.rating) / newCount

                // Güncelle
                schoolRef.update(
                    mapOf(
                        "avgRating" to newAvg,
                        "ratingCount" to newCount
                    )
                ).await()
            }

        } catch (_: Exception) {
            // Gerekirse hata loglanır
        }
    }
}