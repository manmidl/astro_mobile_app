package com.example.astro.app.auth.auth

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.astro.R
import com.example.astro.app.databases.databaseSP.SPFunctions
import com.example.astro.app.listdata.ListDataRepository
import com.example.astro.app.mainui.MainActivity
import com.example.astro.databinding.FragmentAddInfBinding


class AddInfFragment : Fragment() {
    private lateinit var binding : FragmentAddInfBinding

    private lateinit var adapterItems : ArrayAdapter<String>
    private var zodiac = ListDataRepository.signsZodiac[0]

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAddInfBinding.inflate(inflater, container, false)
        selectZodiacSign()

        binding.buttonDone.setOnClickListener {
            val name = binding.editTextTextPersonName.text.toString().trim()
            val signZodiac = zodiac

            if(name.isNotEmpty()) {
                SPFunctions(requireContext()).dataEntryInSharedPreferences(name, signZodiac)
                SPFunctions(requireContext()).authorizationMethod("no auth")

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
        @JvmStatic
        fun newInstance() = AddInfFragment()
    }
}