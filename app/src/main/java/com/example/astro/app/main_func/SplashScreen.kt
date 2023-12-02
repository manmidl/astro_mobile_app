package com.example.astro.app.main_func

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.astro.R
import com.example.astro.app.auth.auth_classes.EntrActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SplashScreen : AppCompatActivity(){

    private lateinit var mAuth : FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        checkMethodLogIn()
    }

    override fun onRestart() {
        super.onRestart()
        checkMethodLogIn()
    }


    private fun checkMethodLogIn(){
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        mAuth = FirebaseAuth.getInstance()

        val currentUser = mAuth.currentUser

        val updatedUser: FirebaseUser? = mAuth.currentUser
        val isEmailVerified = updatedUser?.isEmailVerified

        Handler().postDelayed({
            if(sharedPreferences.contains("name")
                && sharedPreferences.contains("sign_zodiac")){

                val editor = sharedPreferences.edit()
                editor.putString("login_method", "no auth")
                editor.apply()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

            else if(currentUser != null){ //сделать проверку на подверждение email, вот сюда, если почта не подверждена, то  на экран входа пускать пользователя

                if (isEmailVerified == true) {
                    val editor = sharedPreferences.edit()
                    editor.putString("login_method", "auth")
                    editor.apply()

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    val intent = Intent(this, EntrActivity::class.java)
                    startActivity(intent)
                }
            }
            else{
                val intent = Intent(this, EntrActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 3000) //внесены изменения
    }
}