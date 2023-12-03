package com.example.astro.app.setting_user

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
import com.example.astro.app.interfaces.WorkWithDatabase
import com.example.astro.app.main_func.MainActivity
import com.example.astro.databinding.ActivityUserSettingsBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database


class UserSettingsActivity : AppCompatActivity(), WorkWithDatabase {

    private lateinit var binding : ActivityUserSettingsBinding

    val itemsSign = arrayListOf<String>("Овен", "Лев", "Стрелец", "Телец", "Дева", "Козерог", "Близнецы", "Весы", "Водолей", "Рак", "Скорпион", "Рыбы")
    private lateinit var adapterItemsSign : ArrayAdapter<String>

    private lateinit var login_method: String
    private lateinit var sharedPreferences : SharedPreferences

    private var name : String = ""
    private var sign_zodiac: String = ""

    private lateinit var mAuth : FirebaseAuth
    private lateinit var database: FirebaseDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        login_method = sharedPreferences.getString("login_method", "default value")!!

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

            if(login_method == "auth"){
                val userId = com.google.firebase.ktx.Firebase.auth.currentUser?.uid
                val myRef = database.getReference("users/$userId")

                name = binding.editTextTextPersonName.text.toString().trim()
                var oldPassword = binding.editTextOldPassword.text.toString().trim()
                var newPassword = binding.editTextNewPassword.text.toString().trim()

                if(name.isNotEmpty()) {
                    val updates = hashMapOf<String, Any>("personName" to name)
                    myRef.updateChildren(updates)

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                if(sign_zodiac.isNotEmpty()) {
                    val updates = hashMapOf<String, Any>("zodiacSign" to sign_zodiac)
                    myRef.updateChildren(updates)

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                else if(oldPassword.isNotEmpty() && newPassword.isNotEmpty()) {
                    if (!isStrongPassword(newPassword)) {
                        binding.textViewErr.text = "Введённые пароль должен состоять из цифр и букв, и должен быть из 10 или более символов"
                    } else {
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

            else if(login_method == "no auth"){
                val editor = sharedPreferences.edit()
                name = binding.editTextTextPersonName.text.toString().trim()
                if(name.isNotEmpty()) {
                    editor.putString("name", name)
                }
                else if(sign_zodiac.isNotEmpty()){
                    editor.putString("sign_zodiac", sign_zodiac) //изменить
                }
                editor.apply()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }
    //отобразить данные пользователя
    private fun dataDisplayUser(){
        if(login_method == "auth"){
            getDataRealtimeDatabase { mutMap ->
                val namePerson = mutMap["personName"]!!
                val zodiacSign = mutMap["zodiacSign"]!! //проверить правильно ли называется

                binding.editTextTextPersonName.hint = namePerson
                binding.autoCompleteTxtZodiacSign.setText(zodiacSign, false)
            }
        }
        else if(login_method == "no auth"){
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
        adapterItemsSign = ArrayAdapter<String>(this, R.layout.list_item_website, itemsSign)
        binding.autoCompleteTxtZodiacSign.setAdapter(adapterItemsSign)

        binding.autoCompleteTxtZodiacSign.setOnItemClickListener { parent, view, position, id ->
            val item = parent.getItemAtPosition(position).toString()
            sign_zodiac = item
        }
    }
    private fun isStrongPassword(password: String): Boolean {
        val digitRegex = ".*\\d.*"
        val letterRegex = ".*[a-zA-Z].*"

        return password.length >= 10 && password.matches(digitRegex.toRegex()) && password.matches(letterRegex.toRegex())
    }
    private fun logOutOfYourAccount(){
        if(login_method == "auth"){
            mAuth.signOut()
            val intent = Intent(this, EntrActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
        else if(login_method == "no auth"){
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