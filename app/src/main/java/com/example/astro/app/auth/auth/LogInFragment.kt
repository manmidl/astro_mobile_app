package com.example.astro.app.auth.auth

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.example.astro.R
import com.example.astro.app.databases.databaseSP.SPFunctions
import com.example.astro.app.databases.firebase.firebaseAuth.AuthFunctions
import com.example.astro.app.fragments_work.ActionsFragments
import com.example.astro.app.mainui.MainActivity
import com.example.astro.databinding.FragmentLogInBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch


class LogInFragment : Fragment() {

    private lateinit var binding : FragmentLogInBinding
    private lateinit var mAuth : FirebaseAuth
    private lateinit var transaction : FragmentTransaction
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLogInBinding.inflate(inflater, container, false)
        transaction = fragmentManager?.beginTransaction()!!
        mAuth = FirebaseAuth.getInstance()
        return binding.root
    }
    override fun onResume() {
        super.onResume()
        buttonsActionsLoginFragment()
    }
    suspend fun signInUser(email: String, password: String){
        val successLoginMethod = AuthFunctions().loginMethodAccountFirebaseAuth(email, password)
        if (successLoginMethod) {
            val successEmailConfirmed = AuthFunctions().isEmailConfirmedFirebaseAuth()
            if(successEmailConfirmed){
                val intent = Intent(activity, MainActivity::class.java)

                SPFunctions(requireContext()).authorizationMethod("auth")

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }else {
                binding.textViewError.text = "Почта не подтверждена!"
                binding.editTextEnterEmail.setHintTextColor(Color.RED)

                AuthFunctions().sendAgainСonfirmationEmail()
                ActionsFragments().replaceFragment(
                    transaction!!,
                    R.id.fragment_holder,
                    ActionsFragments().fragmentInfoForCheckEmailFragment(email))
            }
        }
        else {
            binding.textViewError.text = "Неправильные имя пользователя или пароль!"
            binding.editTextEnterEmail.setHintTextColor(Color.RED)
            binding.editTextEnterPassword.setHintTextColor(Color.RED)
        }
    }
    private fun buttonsActionsLoginFragment(){
        binding.buttonLogIn.setOnClickListener { //кнопка входа в уч.запись
            val email = binding.editTextEnterEmail.text.toString().trim()
            val password = binding.editTextEnterPassword.text.toString().trim()

            if(email.isNotEmpty() || password.isNotEmpty()) {
                //запуск корутины для выполнения входа юзера асинхронно (в фоне с ожиданием выполнения)
                lifecycleScope.launch {
                    signInUser(email, password)
                }
            }else{
                binding.textViewError.text = "Поля не заполненны!"
                binding.editTextEnterEmail.setHintTextColor(Color.RED)
                binding.editTextEnterPassword.setHintTextColor(Color.RED)
            }
        }
        binding.textViewResetPass.setOnClickListener {//кнопка сброса пароля
            ActionsFragments().replaceFragment(transaction!!, R.id.fragment_holder, ResetPasswordFragment())
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = LogInFragment()
    }
}

