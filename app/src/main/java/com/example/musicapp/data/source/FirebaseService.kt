package com.example.musicapp.data.source

import com.example.musicapp.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FirebaseService {

    private val auth: FirebaseAuth = Firebase.auth
    private val db: FirebaseFirestore = Firebase.firestore

    suspend fun login(email: String, password: String): String {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        return result.user?.uid ?: throw IllegalStateException("Firebase User es nulo después del login.")
    }

    suspend fun signUp(user: User, password: String): String {
        val result = auth.createUserWithEmailAndPassword(user.email, password).await()
        val firebaseUser = result.user ?: throw IllegalStateException("Firebase User es nulo después del registro.")

        db.collection("users").document(firebaseUser.uid).set(user).await()

        return firebaseUser.uid
    }

    fun logout() {
        auth.signOut()
    }

    suspend fun getUserData(userId: String): User? {
        val documentSnapshot = db.collection("users").document(userId).get().await()
        return documentSnapshot.toObject(User::class.java)
    }

    suspend fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser ?: return null
        return getUserData(firebaseUser.uid)
    }

    suspend fun updateUserField(fieldPath: String, value: Any): Boolean {
        val userId = auth.currentUser?.uid ?: return false
        return try {
            db.collection("users").document(userId)
                .update(fieldPath, value)
                .await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}