package com.example.astro.app.auth.auth

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import com.example.astro.R
import com.example.astro.app.databases.databaseSP.SPFunctions
import com.example.astro.app.main_func.MainActivity
import com.example.astro.app.interfaces.BaseFragmentInterface
import com.example.astro.databinding.FragmentLogInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class LogInFragment : Fragment(), BaseFragmentInterface {

    private lateinit var binding : FragmentLogInBinding
    private lateinit var mAuth : FirebaseAuth
    private lateinit var transaction : FragmentTransaction
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLogInBinding.inflate(inflater, container, false)
        transaction = fragmentManager?.beginTransaction()!!
        mAuth = FirebaseAuth.getInstance()

        //кнопка сброса пароля
        binding.textViewResetPass.setOnClickListener {
            replaceFragment(transaction!!, R.id.fragment_holder, ResetPasswordFragment())
        }
        //кнопка входа в уч.запись
        binding.buttonLogIn.setOnClickListener {
            val email = binding.editTextEnterEmail.text.toString().trim()
            val password = binding.editTextEnterPassword.text.toString().trim()

            if(email.isNotEmpty() || password.isNotEmpty()) {
                signInUser(mAuth, email, password)
            }else{
                binding.textViewError.text = "Поля не заполненны!"
                binding.editTextEnterEmail.setHintTextColor(Color.RED)
                binding.editTextEnterPassword.setHintTextColor(Color.RED)
            }

        }
        return binding.root
    }
    companion object {
        @JvmStatic
        fun newInstance() = LogInFragment()
    }
    fun signInUser(mAuth: FirebaseAuth, email: String, password: String){
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) { //вход произведён успешно

                    ////////////////////////
                    //Проверка подтверждения email
                    val user: FirebaseUser? = mAuth.currentUser
                    user?.reload()?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val updatedUser: FirebaseUser? = mAuth.currentUser
                            val isEmailVerified = updatedUser?.isEmailVerified

                            if (isEmailVerified == true) {
                                //инт
                                val intent = Intent(activity, MainActivity::class.java)

                                //способ входа юзера добавлен 12:06 01.12.2023
                                SPFunctions(requireContext()).authorizationMethod("auth")

                                //интерфейсная хуйня
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                            }else{
                                binding.textViewError.text = "Почта не подтверждена!" //интерфейсаня хуйн
                                binding.editTextEnterEmail.setHintTextColor(Color.RED)

                                user?.sendEmailVerification() //повторная отправка письма подтверждения

                                val checkEmailFragment = fragmentInfo(email) //интерфейсная хуня
                                replaceFragment(transaction!!, R.id.fragment_holder, checkEmailFragment)
                            }
                        }
                    }
                }
                //////////////////
                else {
                    //интерфейсная хуйня
                    binding.textViewError.text = "Неправильные имя пользователя или пароль!" //интерфейсная хуйняя
                    binding.editTextEnterEmail.setHintTextColor(Color.RED)
                    binding.editTextEnterPassword.setHintTextColor(Color.RED)
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
}

