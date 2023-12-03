package com.example.astro.app.main_func

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.astro.R
import com.example.astro.app.setting_user.UserSettingsActivity
import com.example.astro.databinding.FragmentInformationZodiacSignsBinding
import org.jsoup.Jsoup
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


//возможно дело в веб-сайте

class InformationZodiacSignsFragment : Fragment(){
    private lateinit var binding : FragmentInformationZodiacSignsBinding
    //второстепенный поток
    private lateinit var secThread: Thread
    private lateinit var runnable: Runnable

    val items = arrayListOf<String>("horo.mail.ru", "goroskop365.ru", "1001goroskop.ru")
    private lateinit var adapterItems : ArrayAdapter<String>

    private var websiteFragment : String = "horo.mail.ru"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentInformationZodiacSignsBinding.inflate(inflater, container, false)
        binding.textViewDate.text = getCurrentDate()

        selectWebSite()
        initGetForecast()

        binding.imageViewButtonUserSettings2.setOnClickListener {
            val intent = Intent(activity, UserSettingsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        selectWebSite()
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
            var header1 = ""
            var header2 = ""
            var header3 = ""

            var description1 = ""
            var description2 = ""
            var description3 = ""

            when (SingletonParametersDisplay.element) { //отрабатывает
                "fire" -> {
                    header1 = "Oвен"
                    header2 = "Лев"
                    header3 = "Стрелец"
                    when (websiteFragment) {
                        "horo.mail.ru" -> {
                            val doc1 =
                                Jsoup.connect("https://horo.mail.ru/prediction/aries/today/").get()
                            description1 = doc1.getElementsByClass("article__text").text()

                            val doc2 =
                                Jsoup.connect("https://horo.mail.ru/prediction/leo/today/").get()
                            description2 = doc2.getElementsByClass("article__text").text()

                            val doc3 =
                                Jsoup.connect("https://horo.mail.ru/prediction/sagittarius/today/")
                                    .get()
                            description3 = doc3.getElementsByClass("article__text").text()
                        }

                        "goroskop365.ru" -> {
                            val doc1 = Jsoup.connect("https://goroskop365.ru/aries/").get()
                            description1 = doc1.select("#content_wrapper > p").text()

                            val doc2 = Jsoup.connect("https://goroskop365.ru/leo/").get()
                            description2 = doc2.select("#content_wrapper > p").text()

                            val doc3 = Jsoup.connect("https://goroskop365.ru/sagittarius/").get()
                            description3 = doc3.select("#content_wrapper > p").text()
                        }

                        "1001goroskop.ru" -> {
                            val doc1 = Jsoup.connect("https://1001goroskop.ru/?znak=aries/").get()
                            description1 =
                                doc1.select("#eje_text > tbody > tr > td > div:nth-child(2) > p")
                                    .text()

                            val doc2 = Jsoup.connect("https://1001goroskop.ru/?znak=leo").get()
                            description2 =
                                doc2.select("#eje_text > tbody > tr > td > div:nth-child(2) > p")
                                    .text()

                            val doc3 =
                                Jsoup.connect("https://1001goroskop.ru/?znak=sagittarius").get()
                            description3 =
                                doc3.select("#eje_text > tbody > tr > td > div:nth-child(2) > p")
                                    .text()
                        }
                    }
                }
                "ground" -> {
                    header1 = "Телец"
                    header2 = "Дева"
                    header3 = "Козерог"
                    when (websiteFragment) {
                        "horo.mail.ru" -> {
                            val doc1 =
                                Jsoup.connect("https://horo.mail.ru/prediction/taurus/today/").get()
                            description1 = doc1.getElementsByClass("article__text").text()

                            val doc2 =
                                Jsoup.connect("https://horo.mail.ru/prediction/virgo/today/").get()
                            description2 = doc2.getElementsByClass("article__text").text()

                            val doc3 =
                                Jsoup.connect("https://horo.mail.ru/prediction/capricorn/today/")
                                    .get()
                            description3 = doc3.getElementsByClass("article__text").text()
                        }

                        "goroskop365.ru" -> {
                            val doc1 = Jsoup.connect("https://goroskop365.ru/taurus/").get()
                            description1 = doc1.select("#content_wrapper > p").text()

                            val doc2 = Jsoup.connect("https://goroskop365.ru/virgo/").get()
                            description2 = doc2.select("#content_wrapper > p").text()

                            val doc3 = Jsoup.connect("https://goroskop365.ru/capricorn/").get()
                            description3 = doc3.select("#content_wrapper > p").text()
                        }

                        "1001goroskop.ru" -> {
                            val doc1 = Jsoup.connect("https://1001goroskop.ru/?znak=taurus").get()
                            description1 =
                                doc1.select("#eje_text > tbody > tr > td > div:nth-child(2) > p")
                                    .text()

                            val doc2 = Jsoup.connect("https://1001goroskop.ru/?znak=virgo").get()
                            description2 =
                                doc2.select("#eje_text > tbody > tr > td > div:nth-child(2) > p")
                                    .text()

                            val doc3 =
                                Jsoup.connect("https://1001goroskop.ru/?znak=capricorn").get()
                            description3 =
                                doc3.select("#eje_text > tbody > tr > td > div:nth-child(2) > p")
                                    .text()
                        }
                    }
                }
                "air" -> {
                    header1 = "Близнецы"
                    header2 = "Весы"
                    header3 = "Водолей"
                    when (websiteFragment) {
                        "horo.mail.ru" -> {
                            val doc1 =
                                Jsoup.connect("https://horo.mail.ru/prediction/gemini/today/").get()
                            description1 = doc1.getElementsByClass("article__text").text()

                            val doc2 =
                                Jsoup.connect("https://horo.mail.ru/prediction/libra/today/").get()
                            description2 = doc2.getElementsByClass("article__text").text()

                            val doc3 =
                                Jsoup.connect("https://horo.mail.ru/prediction/aquarius/today/")
                                    .get()
                            description3 = doc3.getElementsByClass("article__text").text()
                        }

                        "goroskop365.ru" -> {
                            val doc1 = Jsoup.connect("https://goroskop365.ru/gemini/").get()
                            description1 = doc1.select("#content_wrapper > p").text()

                            val doc2 = Jsoup.connect("https://goroskop365.ru/libra/").get()
                            description2 = doc2.select("#content_wrapper > p").text()

                            val doc3 = Jsoup.connect("https://goroskop365.ru/aquarius/").get()
                            description3 = doc3.select("#content_wrapper > p").text()
                        }

                        "1001goroskop.ru" -> {
                            val doc1 = Jsoup.connect("https://1001goroskop.ru/?znak=gemini").get()
                            description1 =
                                doc1.select("#eje_text > tbody > tr > td > div:nth-child(2) > p")
                                    .text()

                            val doc2 = Jsoup.connect("https://1001goroskop.ru/?znak=libra").get()
                            description2 =
                                doc2.select("#eje_text > tbody > tr > td > div:nth-child(2) > p")
                                    .text()

                            val doc3 = Jsoup.connect("https://1001goroskop.ru/?znak=aquarius").get()
                            description3 =
                                doc3.select("#eje_text > tbody > tr > td > div:nth-child(2) > p")
                                    .text()
                        }
                    }
                }
                "water" -> {
                    header1 = "Рак"
                    header2 = "Скорпион"
                    header3 = "Рыбы"
                    when (websiteFragment) {
                        "horo.mail.ru" -> {
                            val doc1 =
                                Jsoup.connect("https://horo.mail.ru/prediction/cancer/today/").get()
                            description1 = doc1.getElementsByClass("article__text").text()

                            val doc2 =
                                Jsoup.connect("https://horo.mail.ru/prediction/scorpio/today/")
                                    .get()
                            description2 = doc2.getElementsByClass("article__text").text()

                            val doc3 =
                                Jsoup.connect("https://horo.mail.ru/prediction/pisces/today/").get()
                            description3 = doc3.getElementsByClass("article__text").text()
                        }

                        "goroskop365.ru" -> {
                            val doc1 = Jsoup.connect("https://goroskop365.ru/cancer/").get()
                            description1 = doc1.select("#content_wrapper > p").text()

                            val doc2 = Jsoup.connect("https://goroskop365.ru/scorpio/").get()
                            description2 = doc2.select("#content_wrapper > p").text()

                            val doc3 = Jsoup.connect("https://goroskop365.ru/pisces/").get()
                            description3 = doc3.select("#content_wrapper > p").text()
                        }

                        "1001goroskop.ru" -> {
                            val doc1 = Jsoup.connect("https://1001goroskop.ru/?znak=cancer").get()
                            description1 =
                                doc1.select("#eje_text > tbody > tr > td > div:nth-child(2) > p")
                                    .text()

                            val doc2 = Jsoup.connect("https://1001goroskop.ru/?znak=scorpio").get()
                            description2 =
                                doc2.select("#eje_text > tbody > tr > td > div:nth-child(2) > p")
                                    .text()

                            val doc3 = Jsoup.connect("https://1001goroskop.ru/?znak=pisces").get()
                            description3 =
                                doc3.select("#eje_text > tbody > tr > td > div:nth-child(2) > p")
                                    .text()
                        }
                    }
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



    private fun selectWebSite(){ //Пользовательские функции (работа с интерфейсом)
        adapterItems = ArrayAdapter<String>(requireContext(), R.layout.list_item_website, items)
        binding.autoCompleteTxt.setAdapter(adapterItems)

        binding.autoCompleteTxt.setOnItemClickListener { parent, view, position, id ->
            val item = parent.getItemAtPosition(position).toString()
            websiteFragment = item
            initGetForecast()

            binding.textViewHeaderSign1.text = "Знак зодиака загрузка..."
            binding.textViewHeaderSign2.text = "Знак зодиака загрузка..."
            binding.textViewHeaderSigns3.text = "Знак зодиака загрузка..."

            binding.textViewDescriptionSign1.text = "Загрузка гороскопа..."
            binding.textViewDescriptionSign2.text = "Загрузка гороскопа..."
            binding.textViewDescriptionSign3.text = "Загрузка гороскопа..."
        }
        binding.autoCompleteTxt.isActivated = true
        binding.autoCompleteTxt.setText(adapterItems.getItem(0), false)
        binding.autoCompleteTxt.requestFocus()

    }

    fun getCurrentDate(): String {
        val currentDate = Date()
        val dateFormat = SimpleDateFormat("d MMMM", Locale("ru"))

        return dateFormat.format(currentDate)
    }

    companion object {
        fun newInstance() = InformationZodiacSignsFragment()
    }
}
