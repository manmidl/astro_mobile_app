package com.example.astro.app.databases.firebase.firebaseAuth

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import com.example.astro.app.mainui.MainActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AuthFunctions {
    var mAuth = FirebaseAuth.getInstance()

    //проверка есть ли юзер в системе Firebase Auth
    suspend fun loginMethodAccountFirebaseAuth(
        email: String,
        password: String
    ): Boolean { //функция создания аккаунта с помощью Firebase Auth
        return try {
            mAuth.signInWithEmailAndPassword(email, password).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    //проверка подтверждения Email
    suspend fun isEmailConfirmedFirebaseAuth(): Boolean {
        val user: FirebaseUser? = mAuth.currentUser
        var success = false
        return try {
            user?.reload()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val updatedUser: FirebaseUser? = mAuth.currentUser
                    val isEmailVerified = updatedUser?.isEmailVerified
                    success = isEmailVerified == true
                }
            }?.await()
            success
        } catch (e: Exception) {
            false
        }
    }

    //метод создание нового пользователя
    suspend fun createNewUserInFirebaseAuth(
        email: String,
        password: String,
        activity: Activity
    ): Boolean = suspendCoroutine { continuation ->

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    continuation.resume(true)
                } else {
                    continuation.resume(false)
                }
            }
    }
    //отправить письмо с подтверждением Email при создании нового пользователя
    suspend fun sendConfirmationEmailByNewUser(): Boolean {
        val user: FirebaseUser? = mAuth.currentUser
        var success = false
        return try {
            user?.sendEmailVerification()
                ?.addOnCompleteListener { verificationTask ->
                    success = verificationTask.isSuccessful
                }?.await()
            success
        } catch (e: Exception) {
            false
        }
    }

    //повторно отправить письмо с подтверждением Email
    fun sendAgainСonfirmationEmail() {
        val user: FirebaseUser? = mAuth.currentUser
        user?.sendEmailVerification() //повторная отправка письма подтверждения
    }
}