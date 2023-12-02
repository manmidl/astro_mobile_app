package com.example.astro.app.interfaces

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

interface DeleteAccountInterface : BaseFragmentInterface {

    fun deleteAcc(mAuth : FirebaseAuth){
         //cоздание экземпляра аутентификатора Firebase

        val user: FirebaseUser? =
            mAuth.currentUser //возможно заменить параметры которые получет функция
        user?.delete()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                }
                else {
                }
            }

    }
}

//val logInFragment = LogInFragment()
//                    replaceFragment(transaction!!, R.id.fragment_holder, logInFragment)
//