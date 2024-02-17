package com.example.astro.app.databases.databaseSP

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.ContentProviderCompat.requireContext

class SPFunctions(val context: Context) {
    private val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    fun authorizationMethod(auth_method : String){
        val editor = sharedPreferences.edit()
        editor.putString("login_method", auth_method)
        editor.apply()
    }
    fun dataEntryInSharedPreferences(name : String, signZodiac : String){
        val editor = sharedPreferences.edit()
        editor.putString("name", name)
        editor.putString("sign_zodiac", signZodiac)
        editor.apply()
    }
}