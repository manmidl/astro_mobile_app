package com.example.astro.app.auth.auth_classes

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.astro.R
import com.example.astro.app.interfaces.BaseFragmentInterface
import com.example.astro.databinding.FragmentCreateAccountBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database


class CreateAccountFragment : Fragment(), BaseFragmentInterface {
    private lateinit var binding : FragmentCreateAccountBinding
    private lateinit var mAuth : FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var myRef : DatabaseReference

    val items = arrayListOf<String>("Овен", "Лев", "Стрелец", "Телец", "Дева", "Козерог", "Близнецы", "Весы", "Водолей", "Рак", "Скорпион", "Рыбы")
    private lateinit var adapterItems : ArrayAdapter<String>
    private var zodiac : String = "Овен"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateAccountBinding.inflate(inflater, container, false)
        val transaction = fragmentManager?.beginTransaction() //вызов другого фрагмента
        selectZodiacSign()

        mAuth = FirebaseAuth.getInstance() //cоздание экземпляра аутентификатора Firebase
        database = com.google.firebase.ktx.Firebase.database
        myRef = database.reference


        binding.buttonResume.setOnClickListener {
            val name = binding.editTextTextPersonName.text.toString().trim()
            val zodiac_sign = zodiac

            val email = binding.editTextEnterEmail.text.toString().trim()
            val password = binding.editTextPassowrd.text.toString().trim()
            createAccountandSendEmail(name, zodiac_sign, email, password, transaction!!)
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        selectZodiacSign()
    }

    private fun createAccountandSendEmail(name:String, zodiac_sign: String, email: String, password: String, transaction : FragmentTransaction){
        if(email.isEmpty() || password.isEmpty() || name.isEmpty() || zodiac_sign.isEmpty()){
            binding.textViewError.text = "Поля не заполненны!"
            binding.editTextTextPersonName.setHintTextColor(Color.RED)
            binding.editTextEnterEmail.setHintTextColor(Color.RED)
            binding.editTextPassowrd.setHintTextColor(Color.RED)
        }
        else if(!isStrongPassword(password)){
            binding.textViewError.text = "Введённые пароль должен состоять из цифр и букв, и должен быть из 10 или более символов"
            binding.editTextPassowrd.setTextColor(Color.RED)
        }
        else {
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) {
                    if (it.isSuccessful) {
                        val user = mAuth.currentUser

                        user?.sendEmailVerification()
                            ?.addOnCompleteListener { verificationTask ->
                                if (verificationTask.isSuccessful) {
                                    binding.buttonResume.isEnabled = false

                                    val userId = com.google.firebase.ktx.Firebase.auth.currentUser?.uid
                                    val user = hashMapOf( //заполнение БД
                                        "email" to email,
                                        "personName" to name,
                                        "zodiacSign" to zodiac_sign,
                                        "web_site_default" to "horo.mail.ru"
                                    )
                                    myRef.child("users").child(userId!!).setValue(user) //обращение к узлу и размещение информации

                                    val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                                    val editor = sharedPreferences.edit()
                                    val login_method = "auth"
                                    editor.putString("login_method", login_method)
                                    editor.apply()

                                    val checkEmail = fragmentInfo(email)
                                    replaceFragment(transaction!!, R.id.fragment_holder, checkEmail)
                                } else {
                                    binding.textViewError.text = "Ошибка отправки письма для подтверждения email"
                                }
                            }
                    } else {
                        binding.textViewError.text = "Ошибка регистрации! Возможно этот email уже используется или веденны некорректные данные!"
                        binding.editTextEnterEmail.setHintTextColor(Color.RED)
                    }
                }
        }
    }
    private fun fragmentInfo(email: String) : CheckEmailFragment {
        val bundle = Bundle()
        bundle.putString("emailUser", email)
        val checkEmail = CheckEmailFragment()
        checkEmail.arguments = bundle
        return checkEmail
    }

    fun isStrongPassword(password: String): Boolean {
        val digitRegex = ".*\\d.*"
        val letterRegex = ".*[a-zA-Z].*"

        return password.length >= 10 && password.matches(digitRegex.toRegex()) && password.matches(letterRegex.toRegex())
    }

    private fun selectZodiacSign(){
        adapterItems = ArrayAdapter<String>(requireContext(), R.layout.list_item_website, items)
        binding.autoCompleteTxtZodiacSign.setAdapter(adapterItems)

        binding.autoCompleteTxtZodiacSign.setOnItemClickListener { parent, view, position, id ->
            val item = parent.getItemAtPosition(position).toString()
            zodiac = item
        }
        binding.autoCompleteTxtZodiacSign.isActivated = true
        binding.autoCompleteTxtZodiacSign.setText(adapterItems.getItem(0), false)
        binding.autoCompleteTxtZodiacSign.requestFocus()
    }
    companion object {
        fun newInstance() = CreateAccountFragment()
    }
}