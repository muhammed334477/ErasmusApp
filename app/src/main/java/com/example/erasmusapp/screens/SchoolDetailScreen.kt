package com.example.erasmusapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.erasmusapp.data.SchoolRepository
import com.example.erasmusapp.model.Comment
import com.example.erasmusapp.model.School
import kotlinx.coroutines.launch
import java.math.RoundingMode

@Composable
fun SchoolDetailScreen(navController: NavController, schoolId: String) {
    val repo = remember { SchoolRepository() }
    val scope = rememberCoroutineScope()

    var school by remember { mutableStateOf<School?>(null) }
    var comments by remember { mutableStateOf(listOf<Comment>()) }

    var newComment by remember { mutableStateOf(TextFieldValue("")) }
    var newRating by remember { mutableStateOf(3f) }
    var userName by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(schoolId) {
        school = repo.getSchoolById(schoolId)
        comments = repo.getCommentsForSchool(schoolId)
    }

    school?.let { schoolData ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            item {
                Image(
                    painter = rememberAsyncImagePainter(schoolData.imageUrl),
                    contentDescription = "Okul Görseli",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = schoolData.name,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )

                Text(text = "Ülke: ${schoolData.country}")
                Text(text = "Açıklama: ${schoolData.description}")
                val roundedRating = schoolData.avgRating.toBigDecimal().setScale(1, RoundingMode.DOWN)
                Text(text = "Puan: $roundedRating (${schoolData.ratingCount} oy)")
                
                Spacer(modifier = Modifier.height(24.dp))

                Text("Yorum Yap", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = userName,
                    onValueChange = { userName = it },
                    label = { Text("Adınız") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = newComment,
                    onValueChange = { newComment = it },
                    label = { Text("Yorum") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Puan: ${newRating.toInt()}")
                Slider(
                    value = newRating,
                    onValueChange = { newRating = it },
                    valueRange = 1f..5f,
                    steps = 3
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(onClick = {
                    if (userName.text.isNotBlank() && newComment.text.isNotBlank()) {
                        scope.launch {
                            repo.addCommentForSchool(
                                schoolId,
                                Comment(
                                    user = userName.text,
                                    comment = newComment.text,
                                    rating = newRating.toDouble()
                                )
                            )
                            // yorumları yeniden al
                            comments = repo.getCommentsForSchool(schoolId)
                            school = repo.getSchoolById(schoolId)

                            // sıfırla
                            newComment = TextFieldValue("")
                            newRating = 3f
                            userName = TextFieldValue("")
                        }
                    }
                }) {
                    Text("Gönder")
                }

                Spacer(modifier = Modifier.height(24.dp))
                Divider(thickness = 2.dp, color = Color.Gray)
                Spacer(modifier = Modifier.height(12.dp))

                Text("Yorumlar", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))
            }

            items(comments) { comment ->
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(text = comment.user, fontWeight = FontWeight.Bold)
                    Text(text = comment.comment)
                    Text(text = "Puan: ${comment.rating}")
                }
                Divider(thickness = 1.dp, color = Color.LightGray)
            }
        }
    } ?: run {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}