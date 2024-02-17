package com.example.astro.app.mainui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.astro.R
import com.example.astro.app.listdata.ListDataRepository
import com.example.astro.app.mainfunc.SingletonParametersDisplay
import com.example.astro.app.parsing.ParsingData
import com.example.astro.app.settingsui.UserSettingsActivity
import com.example.astro.databinding.FragmentInformationZodiacSignsBinding
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import java.util.Locale


//возможно дело в веб-сайте

class InformationZodiacSignsFragment : Fragment(){
    private lateinit var binding : FragmentInformationZodiacSignsBinding
    //второстепенный поток
    private lateinit var secThread: Thread
    private lateinit var runnable: Runnable

    private lateinit var adapterItems : ArrayAdapter<String>
    private var websiteFragment : String = ListDataRepository.listWebSite[0]

    private lateinit var arrayListHeaderAwait : ArrayList<CharSequence>
    private lateinit var arrayListDescriptionAwait : ArrayList<CharSequence>

    private lateinit var header1 : String
    private lateinit var header2 : String
    private lateinit var header3 : String
    private lateinit var description1 : String
    private lateinit var description2 : String
    private lateinit var description3 : String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentInformationZodiacSignsBinding.inflate(inflater, container, false)
        binding.textViewDate.text = getCurrentDate()

        arrayListHeaderAwait = arrayListOf(binding.textViewHeaderSign1.text, binding.textViewHeaderSign2.text, binding.textViewHeaderSigns3.text)
        arrayListDescriptionAwait = arrayListOf(binding.textViewDescriptionSign1.text,  binding.textViewDescriptionSign2.text, binding.textViewDescriptionSign3.text)

        selectWebSite()
        initGetForecast()
        buttonUserSettings()
        return binding.root
    }
    override fun onResume() {
        super.onResume()
        focusAutoCompileTxt()
    }
    //получение данных
    private fun initGetForecast(){
        runnable = Runnable {
            getWebForecast()
        }
        secThread = Thread(runnable)
        secThread.start()
    }
    private fun getWebForecast() {
        try {
            when (SingletonParametersDisplay.element) { //отрабатывает
                "fire" -> {
                    setHeaders("Oвен", "Лев", "Стрелец")
                    setDescriptions("aries", "leo", "sagittarius")
                }
                "ground" -> {
                    setHeaders("Телец", "Дева", "Козерог")
                    setDescriptions("taurus","virgo", "capricorn" )
                }
                "air" -> {
                    setHeaders("Близнецы","Весы", "Водолей")
                    setDescriptions("gemini", "libra", "aquarius")
                }
                "water" -> {
                    setHeaders("Рак", "Скорпион", "Рыбы")
                    setDescriptions("cancer","scorpio", "pisces")
                }
            }
            activity?.runOnUiThread {
                binding.textViewHeaderSign1.text = header1
                binding.textViewHeaderSign2.text = header2
                binding.textViewHeaderSigns3.text = header3

                binding.textViewDescriptionSign1.text = description1
                binding.textViewDescriptionSign2.text = description2
                binding.textViewDescriptionSign3.text = description3
            }

        }catch (e: IOException){
            Log.d("FragmentLog", e.toString())
        }
    }
    private fun setDescriptions(sign1 : String, sign2 : String, sign3 : String){
        description1 = ParsingData().getPersonUserHoroscope(sign1, websiteFragment)
        description2 = ParsingData().getPersonUserHoroscope(sign2, websiteFragment)
        description3 = ParsingData().getPersonUserHoroscope(sign3, websiteFragment)
    }
    private fun setHeaders(h1 : String, h2 : String, h3 : String){
        header1 = h1
        header2 = h2
        header3 = h3
    }
    private fun selectWebSite(){ //Пользовательские функции (работа с интерфейсом)
        adapterItems = ArrayAdapter<String>(requireContext(), R.layout.list_item_website, ListDataRepository.listWebSite)
        binding.autoCompleteTxt.setAdapter(adapterItems)

        binding.autoCompleteTxt.setOnItemClickListener { parent, view, position, id ->
            val item = parent.getItemAtPosition(position).toString()
            websiteFragment = item
            initGetForecast()
            for (i in 0 until arrayListHeaderAwait.size) arrayListHeaderAwait[i] = "Знак зодиака загрузка..."
            for (i in 0 until arrayListDescriptionAwait.size) arrayListDescriptionAwait[i] = "Загрузка гороскопа..."
        }
        binding.autoCompleteTxt.setText(adapterItems.getItem(0), false)
        focusAutoCompileTxt()
    }
    private fun focusAutoCompileTxt(){
        binding.autoCompleteTxt.isActivated = true
        binding.autoCompleteTxt.requestFocus()
    }
    fun getCurrentDate(): String {
        val currentDate = Date()
        val dateFormat = SimpleDateFormat("d MMMM", Locale("ru"))

        return dateFormat.format(currentDate)
    }
    fun buttonUserSettings(){
        binding.imageViewButtonUserSettings2.setOnClickListener {
            val intent = Intent(activity, UserSettingsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
    companion object {
        fun newInstance() = InformationZodiacSignsFragment()
    }
}
