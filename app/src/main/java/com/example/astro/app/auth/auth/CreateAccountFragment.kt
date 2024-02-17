package com.example.astro.app.auth.auth

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.example.astro.R
import com.example.astro.app.databases.databaseSP.SPFunctions
import com.example.astro.app.databases.firebase.firebaseAuth.AuthFunctions
import com.example.astro.app.databases.firebase.firebaseDB.FirebaseDBFunctions
import com.example.astro.app.fragments_work.ActionsFragments
import com.example.astro.app.listdata.ListDataRepository
import com.example.astro.app.passcorrect.PasswordCorrect
import com.example.astro.databinding.FragmentCreateAccountBinding
import kotlinx.coroutines.launch


class CreateAccountFragment : Fragment() {
    private lateinit var binding : FragmentCreateAccountBinding

    private lateinit var adapterItems : ArrayAdapter<String>
    private var zodiac : String = ListDataRepository.signsZodiac[0]

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateAccountBinding.inflate(inflater, container, false)
        selectZodiacSign()
        buttonActionsCreateAccountFragment()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        selectZodiacSign()
        buttonActionsCreateAccountFragment()
    }

    private suspend fun createAccountAndSendEmail(name:String, zodiacSign: String, email: String, password: String, transaction : FragmentTransaction){
        if(email.isEmpty() || password.isEmpty() || name.isEmpty() || zodiacSign.isEmpty()){ //не трогать
            binding.textViewError.text = "Поля не заполненны!"
            binding.editTextTextPersonName.setHintTextColor(Color.RED)
            binding.editTextEnterEmail.setHintTextColor(Color.RED)
            binding.editTextPassowrd.setHintTextColor(Color.RED)
        }
        else if(!PasswordCorrect().isStrongPassword(password)){ //перевести функцию в отдельный класс
            binding.textViewError.text = "Введённые пароль должен состоять из цифр и букв, и должен быть из 10 или более символов"
            binding.editTextPassowrd.setTextColor(Color.RED)
        }
        else {
            val successCNUIFirebaseAuth = AuthFunctions().createNewUserInFirebaseAuth(email, password, requireActivity())
            if (successCNUIFirebaseAuth) {
                val successSConEmailByNUser = AuthFunctions().sendConfirmationEmailByNewUser()
                if (successSConEmailByNUser) {
                            binding.buttonResume.isEnabled = false

                            FirebaseDBFunctions().setDataInRealtimeDatabase(email, name, zodiacSign)
                            SPFunctions(requireContext()).authorizationMethod("auth") //функция метода авторизации

                            ActionsFragments().replaceFragment(transaction!!, R.id.fragment_holder, ActionsFragments().fragmentInfoForCheckEmailFragment(email))
                        } else {
                            binding.textViewError.text = "Ошибка отправки письма для подтверждения email"
                        }
            } else {
                binding.textViewError.text = "Ошибка регистрации! Возможно этот email уже используется или веденны некорректные данные!"
                binding.editTextEnterEmail.setHintTextColor(Color.RED)
            }
        }
    }

    private fun buttonActionsCreateAccountFragment(){
        val transaction = fragmentManager?.beginTransaction()

        binding.buttonResume.setOnClickListener {
            val name = binding.editTextTextPersonName.text.toString().trim()
            val zodiacSign = zodiac
            val email = binding.editTextEnterEmail.text.toString().trim()
            val password = binding.editTextPassowrd.text.toString().trim()

            lifecycleScope.launch {
                createAccountAndSendEmail(name, zodiacSign, email, password, transaction!!)
            }
        }
    }
    private fun selectZodiacSign(){
        adapterItems = ArrayAdapter<String>(requireContext(), R.layout.list_item_website, ListDataRepository.signsZodiac)
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