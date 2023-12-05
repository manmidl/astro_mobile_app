package com.example.astro.app.auth.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.astro.R
import com.example.astro.app.main_func.MainActivity
import com.example.astro.databinding.FragmentAddInfBinding


class AddInfFragment : Fragment() {
    private lateinit var binding : FragmentAddInfBinding
    //
    private val signsZodiac = listOf("Овен", "Лев", "Стрелец", "Телец", "Дева", "Козерог", "Близнецы", "Весы", "Водолей", "Рак", "Скорпион", "Рыбы")
    private lateinit var adapterItems : ArrayAdapter<String>
    private var zodiac = "Овен"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddInfBinding.inflate(inflater, container, false)
        selectZodiacSign()


        binding.buttonDone.setOnClickListener {

            val name = binding.editTextTextPersonName.text.toString().trim()
            val signZodiac = zodiac  //переменная знака зодиака
            val loginMethod = "no auth"

            //Ввод данных в локальную БД - SharedPreferences
            val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            if(name.isNotEmpty()) {
                editor.putString("name", name)
                editor.putString("sign_zodiac", signZodiac)
                editor.putString("login_method", loginMethod)
                editor.apply()

                val intent = Intent(activity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }else{
                binding.textViewError.text = "Поле имени не должно быть пустым!"
            }
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        selectZodiacSign()
    }

    private fun selectZodiacSign(){
        adapterItems = ArrayAdapter<String>(requireContext(), R.layout.list_item_website, signsZodiac)
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
        @JvmStatic
        fun newInstance() = AddInfFragment()
    }
}