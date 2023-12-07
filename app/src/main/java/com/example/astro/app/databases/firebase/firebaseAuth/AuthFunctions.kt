package com.example.astro.app.databases.firebase.firebaseAuth

import com.google.firebase.auth.FirebaseAuth

class AuthFunctions {

    fun userLoginMethodAuth(mAuth : FirebaseAuth, email: String, password: String) : Boolean{
        var resultTask : Boolean = false
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) { //вход произведён успешно
                    resultTask = true
                }
                /*else{
                    resultTask = false
                }*/
            }
        return resultTask
    }
}