package com.example.astro.app.auth.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import com.example.astro.R
import com.example.astro.app.interfaces.BaseFragmentInterface
import com.example.astro.app.interfaces.DeleteAccountInterface
import com.example.astro.databinding.FragmentCheckEmailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class CheckEmailFragment : Fragment(), BaseFragmentInterface, DeleteAccountInterface {

    private lateinit var binding : FragmentCheckEmailBinding
    private lateinit var mAuth : FirebaseAuth
    private lateinit var email: String
    private lateinit var transaction : FragmentTransaction

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCheckEmailBinding.inflate(inflater, container, false)
        transaction = fragmentManager?.beginTransaction()!!
        mAuth = FirebaseAuth.getInstance()

        email = arguments?.getString("emailUser")!! //Получение данных из CreateAccountFragment или LoginFragment.
        binding.buttonResume.text = "Я подтвердил email" //смена текста на заголовке и на кнопке
        binding.textView.text = "На ваш адрес эл.почты $email выслано письмо с ссылкой для подтверждения вашего email, перейдите по ней и нажмите кнопку \"Я подтвердил email\", а потом войдите в свой аккаунт на странице входа"


        binding.buttonResume.setOnClickListener {
            val user: FirebaseUser? = mAuth.currentUser
            user?.reload()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val updatedUser: FirebaseUser? = mAuth.currentUser
                    val isEmailVerified = updatedUser?.isEmailVerified

                    if (isEmailVerified == true) {
                        Toast.makeText(requireContext(), "Почта успешно подтверждена!", Toast.LENGTH_SHORT).show()
                        val logInFragment = LogInFragment()
                        replaceFragment(transaction!!, R.id.fragment_holder, logInFragment)
                    }else{
                        Toast.makeText(requireContext(), "Почта не подтверждена!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        return binding.root
    }
    companion object {
        @JvmStatic
        fun newInstance() = CheckEmailFragment()
    }
}
