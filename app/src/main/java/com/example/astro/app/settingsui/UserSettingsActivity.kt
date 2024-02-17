package com.example.astro.app.settingsui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.astro.R
import com.example.astro.app.auth.auth.EntrActivity
import com.example.astro.app.databases.firebase.firebaseAuth.AuthFunctions
import com.example.astro.app.interfaces.WorkWithDatabase
import com.example.astro.app.listdata.ListDataRepository
import com.example.astro.app.mainui.MainActivity
import com.example.astro.databinding.ActivityUserSettingsBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database


class UserSettingsActivity : AppCompatActivity(), WorkWithDatabase {

    private lateinit var binding : ActivityUserSettingsBinding

    private lateinit var adapterItemsSign : ArrayAdapter<String>

    private lateinit var loginMethod: String
    private lateinit var sharedPreferences : SharedPreferences

    private var name : String = ""
    private var signZodiac: String = ""

    private lateinit var mAuth : FirebaseAuth
    private lateinit var database: FirebaseDatabase

    //переделать некоторые элементы
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        loginMethod = sharedPreferences.getString("login_method", "default value")!!

        mAuth = FirebaseAuth.getInstance()
        database = com.google.firebase.ktx.Firebase.database

        binding.textViewButtonLogOut.setOnClickListener {
            logOutOfYourAccount()
        }
        binding.buttonSupport.setOnClickListener {
            contactSupport()
        }

        dataDisplayUser()
        changeZodiacSign()
        saveСhanges()
    }

    override fun onResume() {
        super.onResume()
        changeZodiacSign()
    }

    //сохранить изменения
    private fun saveСhanges(){

        binding.buttonSave.setOnClickListener {

            if(loginMethod == "auth"){
                val userId = com.google.firebase.ktx.Firebase.auth.currentUser?.uid
                val myRef = database.getReference("users/$userId")

                name = binding.editTextTextPersonName.text.toString().trim()
                var oldPassword = binding.editTextOldPassword.text.toString().trim()
                var newPassword = binding.editTextNewPassword.text.toString().trim()

                if(name.isNotEmpty()) { // порядок
                    val updates = hashMapOf<String, Any>("personName" to name)
                    myRef.updateChildren(updates)

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                if(signZodiac.isNotEmpty()) { //порядок
                    val updates = hashMapOf<String, Any>("zodiacSign" to signZodiac)
                    myRef.updateChildren(updates)

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                else if(oldPassword.isNotEmpty() && newPassword.isNotEmpty()) { //не порядок
                    if (!isStrongPassword(newPassword)) {
                        binding.textViewErr.text = R.string.attetion_char_pass.toString()
                    } else { //----------------------------
                        val user = mAuth.currentUser
                        val credential = EmailAuthProvider.getCredential(user?.email!!, oldPassword)

                        //подтверждение старого пароля
                        user.reauthenticate(credential)
                            .addOnCompleteListener { reAuthResult ->
                                if (reAuthResult.isSuccessful) {
                                    user.updatePassword(newPassword)
                                        .addOnCompleteListener { updatePasswordResult ->
                                            if (updatePasswordResult.isSuccessful) {
                                                Toast.makeText(
                                                    this,
                                                    "Пароль успешно изменён :)",
                                                    Toast.LENGTH_LONG
                                                ).show()

                                                val intent = Intent(this, MainActivity::class.java)
                                                startActivity(intent)
                                            } else {
                                                binding.textViewErr.text = "Ошибка при изменении пароля :("
                                            }
                                        }
                                } else {
                                    binding.textViewErr.text = "Старый пароль неверный!"
                                }
                            }

                    }
                }
            }

            else if(loginMethod == "no auth"){
                val editor = sharedPreferences.edit()
                name = binding.editTextTextPersonName.text.toString().trim()
                if(name.isNotEmpty()) {
                    editor.putString("name", name)
                }
                else if(signZodiac.isNotEmpty()){
                    editor.putString("sign_zodiac", signZodiac) //изменить
                }
                editor.apply()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }
    //отобразить данные пользователя
    private fun dataDisplayUser(){
        if(loginMethod == "auth"){
            getDataInRealtimeDatabase { mutMap ->
                val namePerson = mutMap["personName"]!!
                val zodiacSign = mutMap["zodiacSign"]!! //проверить правильно ли называется

                binding.editTextTextPersonName.hint = namePerson
                binding.autoCompleteTxtZodiacSign.setText(zodiacSign, false)
            }
        }
        else if(loginMethod == "no auth"){
            val namePerson = sharedPreferences.getString("name", "default_value")
            val zodiacSigns = sharedPreferences.getString("sign_zodiac", "default_value")

            binding.editTextTextPersonName.hint = namePerson
            binding.autoCompleteTxtZodiacSign.setText(zodiacSigns, false)

            binding.editTextOldPassword.isVisible = false
            binding.editTextNewPassword.isVisible = false
            binding.textViewDescription3.isVisible = false
        }
    }
    //сменить знак зодиака
    private fun changeZodiacSign(){
        adapterItemsSign = ArrayAdapter<String>(this, R.layout.list_item_website, ListDataRepository.signsZodiac)
        binding.autoCompleteTxtZodiacSign.setAdapter(adapterItemsSign)

        binding.autoCompleteTxtZodiacSign.setOnItemClickListener { parent, view, position, id ->
            val item = parent.getItemAtPosition(position).toString()
            signZodiac = item
        }
    }
    private fun isStrongPassword(password: String): Boolean {
        val digitRegex = ".*\\d.*"
        val letterRegex = ".*[a-zA-Z].*"

        return password.length >= 10 && password.matches(digitRegex.toRegex()) && password.matches(letterRegex.toRegex())
    }
    private fun logOutOfYourAccount(){
        if(loginMethod == "auth"){
            mAuth.signOut()
            val intent = Intent(this, EntrActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
        else if(loginMethod == "no auth"){
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()

            val intent = Intent(this, EntrActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
    fun contactSupport() {
        val emailIntent = Intent(
            Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "mionaki9@gmail.com", null
            )
        )
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Поддержка")
        startActivity(Intent.createChooser(emailIntent, "Отправить письмо..."))
    }
}