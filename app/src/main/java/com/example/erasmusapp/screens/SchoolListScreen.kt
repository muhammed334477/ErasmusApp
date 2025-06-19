package com.example.erasmusapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import java.math.RoundingMode

data class School(
    val id: String = "",
    val name: String = "",
    val country: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val avgRating: Double = 0.0,
    val ratingCount: Int = 0
)

@Composable
fun SchoolListScreen(navController: NavController, country: String = "Tümü") {
    val firestore = FirebaseFirestore.getInstance()
    var schools by remember { mutableStateOf<List<School>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(country) {
        val query = if (country == "Tümü") {
            firestore.collection("schools")
        } else {
            firestore.collection("schools").whereEqualTo("country", country)
        }

        query.get().addOnSuccessListener { result ->
            schools = result.map { doc ->
                val school = doc.toObject<School>()
                school.copy(id = doc.id)
            }
            isLoading = false
        }.addOnFailureListener {
            isLoading = false
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(schools) { school ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            navController.navigate("school_detail/${school.id}")
                        }
                ) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        Image(
                            painter = rememberAsyncImagePainter(school.imageUrl),
                            contentDescription = school.name,
                            modifier = Modifier.size(80.dp),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = school.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = school.country,
                                style = MaterialTheme.typography.bodyMedium
                            )

                            val roundedRating = school.avgRating.toBigDecimal().setScale(1, RoundingMode.DOWN)
                            Text(
                                text = "Rating: $roundedRating (${school.ratingCount})",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}