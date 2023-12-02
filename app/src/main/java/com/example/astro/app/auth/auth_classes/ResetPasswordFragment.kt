package com.example.astro.app.auth.auth_classes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import com.example.astro.R
import com.example.astro.app.interfaces.BaseFragmentInterface
import com.example.astro.databinding.FragmentResetPasswordBinding
import com.google.firebase.auth.FirebaseAuth


class ResetPasswordFragment : Fragment(), BaseFragmentInterface {
    private lateinit var binding : FragmentResetPasswordBinding
    private lateinit var mAuth : FirebaseAuth
    private lateinit var transaction : FragmentTransaction

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentResetPasswordBinding.inflate(inflater, container, false)
        transaction = fragmentManager?.beginTransaction()!!

        mAuth = FirebaseAuth.getInstance()
        val logInFragment = LogInFragment()

        binding.buttonResume.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser
            val email = binding.editTextEnterReset.text.toString().trim()

            user?.reload()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    FirebaseAuth.getInstance().fetchSignInMethodsForEmail(email)
                        .addOnCompleteListener { signInMethodsTask ->
                            if (signInMethodsTask.isSuccessful) {
                                mAuth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(requireContext(), "На ваш email отправлена ссылка для восстановления пароля", Toast.LENGTH_LONG).show()
                                            replaceFragment(transaction!!, R.id.fragment_holder, logInFragment)
                                        } else {
                                            Toast.makeText(requireContext(), "Произошла ошибка при отправке ссылки на сброс пароля.", Toast.LENGTH_LONG).show()
                                        }
                                    }
                            } else {
                                Toast.makeText(requireContext(), "Извините что-то пошло не так...", Toast.LENGTH_LONG).show()
                            }
                        }
                }
            }
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = ResetPasswordFragment()
    }
}