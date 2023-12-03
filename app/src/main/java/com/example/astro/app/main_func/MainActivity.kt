package com.example.astro.app.main_func

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import com.example.astro.R
import com.example.astro.app.interfaces.BaseFragmentInterface
import com.example.astro.app.interfaces.WorkWithDatabase
import com.example.astro.app.setting_user.UserSettingsActivity
import com.example.astro.databinding.ActivityMainBinding
import org.jsoup.Jsoup
import java.io.IOException
import java.util.Calendar


class MainActivity : AppCompatActivity(), WorkWithDatabase, BaseFragmentInterface {

    private lateinit var binding : ActivityMainBinding
    val items = arrayListOf<String>("horo.mail.ru", "goroskop365.ru", "1001goroskop.ru")
    private lateinit var adapterItems : ArrayAdapter<String>
    private var colorNeonBlue = 0
    private lateinit var signZodiac : String
    private var websiteMain : String = "horo.mail.ru"
    private lateinit var loginMethod: String

    //второстепенный поток
    private lateinit var secThread: Thread
    private lateinit var runnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        colorNeonBlue = ContextCompat.getColor(this, R.color.neon_blue)

        dataDisplay()
        selectWebSite()

        binding.imageViewButtonUserSettings.setOnClickListener {
            val intent = Intent(this, UserSettingsActivity::class.java)
            startActivity(intent)
        }
        clickOnImageButton()
        binding.autoCompleteTxt.setText("horo.mail.ru", false)
    }
    override fun onStart() {
        super.onStart()
        dataDisplay()
    }
    override fun onResume() {
        super.onResume()
        dataDisplay()
        selectWebSite()
    }
    override fun onRestart() {
        super.onRestart()
        dataDisplay()
    }

    private fun selectWebSite(){ //Пользовательские функции (работа с интерфейсом)
        adapterItems = ArrayAdapter<String>(this, R.layout.list_item_website, items)
        binding.autoCompleteTxt.setAdapter(adapterItems)

        binding.autoCompleteTxt.setOnItemClickListener { parent, view, position, id ->
            val item = parent.getItemAtPosition(position).toString()
            websiteMain = item

            initGetForecast()
        }
        binding.autoCompleteTxt.isActivated = true
        binding.autoCompleteTxt.requestFocus()
    }
    private fun dataDisplay(){
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        loginMethod = sharedPreferences.getString("login_method", "default value")!!

        //перекрашивание текста
        val header_signs = "Гороскопы для других знаков:"
        val spannableHeader = SpannableString(header_signs)
        val s = 14
        val e = header_signs.length - 1
        val colorSp = ForegroundColorSpan(colorNeonBlue)
        spannableHeader.setSpan(colorSp, s, e, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        binding.textViewHeaderSign1.text = spannableHeader
        //------------

        var name = ""
        //если пользователь вошёл с учётной записью

        if (loginMethod == "auth"){
            getDataRealtimeDatabase { mutMap ->
                name = mutMap["personName"]!!
                signZodiac = mutMap["zodiacSign"]!!
                initGetForecast()
                timeOfDayDisplay(name)
            }
        }
        //если пользователь вошёл без уч.записи
        else if (loginMethod == "no auth"){
            name = sharedPreferences.getString("name", "")!!
            signZodiac = sharedPreferences.getString("sign_zodiac", "")!!
            initGetForecast()
            timeOfDayDisplay(name)
        }
    }

    //получение данных для отображения гороскопов
    private fun initGetForecast(){
        runnable = Runnable {
            getWebShortForecast()
        }
        secThread = Thread(runnable)
        secThread.start()
    }
    private fun getWebShortForecast() {
        try {
            var text_forecast = ""

            when(signZodiac){ // тут уизменения
                "Овен" -> {
                    when(websiteMain){
                        "horo.mail.ru" -> {
                            val doc = Jsoup.connect("https://horo.mail.ru/prediction/aries/today/").get()
                            val element = doc.getElementsByClass("article__text")
                            text_forecast = element.text()
                        }
                        "goroskop365.ru" -> {
                            val doc = Jsoup.connect("https://goroskop365.ru/aries/").get()
                            val element = doc.select("#content_wrapper > p")
                            text_forecast = element.text()
                        }
                        "1001goroskop.ru" -> {
                            val doc = Jsoup.connect("https://1001goroskop.ru/?znak=aries").get()
                            val element = doc.select("#eje_text > tbody > tr > td > div:nth-child(2) > p")
                            text_forecast = element.text()
                        }
                    }
                }
                "Лев" -> {
                    when(websiteMain){
                        "horo.mail.ru" -> {
                            val doc = Jsoup.connect("https://horo.mail.ru/prediction/leo/today/").get()
                            val element = doc.getElementsByClass("article__text")
                            text_forecast = element.text()
                        }
                        "goroskop365.ru" -> {
                            val doc = Jsoup.connect("https://goroskop365.ru/leo/").get()
                            val element = doc.select("#content_wrapper > p")
                            text_forecast = element.text()
                        }
                        "1001goroskop.ru" -> {
                            val doc = Jsoup.connect("https://1001goroskop.ru/?znak=leo").get()
                            val element = doc.select("#eje_text > tbody > tr > td > div:nth-child(2) > p")
                            text_forecast = element.text()
                        }
                    }
                }
                "Стрелец" -> {
                    when(websiteMain){
                        "horo.mail.ru" -> {
                            val doc = Jsoup.connect("https://horo.mail.ru/prediction/sagittarius/today/").get()
                            val element = doc.getElementsByClass("article__text")
                            text_forecast = element.text()
                        }
                        "goroskop365.ru" -> {
                            val doc = Jsoup.connect("https://goroskop365.ru/sagittarius/").get()
                            val element = doc.select("#content_wrapper > p")
                            text_forecast = element.text()
                        }
                        "1001goroskop.ru" -> {
                            val doc = Jsoup.connect("https://1001goroskop.ru/?znak=sagittarius").get()
                            val element = doc.select("#eje_text > tbody > tr > td > div:nth-child(2) > p")
                            text_forecast = element.text()
                        }
                    }
                }

                "Телец" -> {
                    when(websiteMain){
                        "horo.mail.ru" -> {
                            val doc = Jsoup.connect("https://horo.mail.ru/prediction/taurus/today/").get()
                            val element = doc.getElementsByClass("article__text")
                            text_forecast = element.text()
                        }
                        "goroskop365.ru" -> {
                            val doc = Jsoup.connect("https://goroskop365.ru/taurus/").get()
                            val element = doc.select("#content_wrapper > p")
                            text_forecast = element.text()
                        }
                        "1001goroskop.ru" -> {
                            val doc = Jsoup.connect("https://1001goroskop.ru/?znak=taurus").get()
                            val element = doc.select("#eje_text > tbody > tr > td > div:nth-child(2) > p")
                            text_forecast = element.text()
                        }
                    }
                }
                "Дева" -> {
                    when(websiteMain){
                        "horo.mail.ru" -> {
                            val doc = Jsoup.connect("https://horo.mail.ru/prediction/virgo/today/").get()
                            val element = doc.getElementsByClass("article__text")
                            text_forecast = element.text()
                        }
                        "goroskop365.ru" -> {
                            val doc = Jsoup.connect("https://goroskop365.ru/virgo/").get()
                            val element = doc.select("#content_wrapper > p")
                            text_forecast = element.text()
                        }
                        "1001goroskop.ru" -> {
                            val doc = Jsoup.connect("https://1001goroskop.ru/?znak=virgo").get()
                            val element = doc.select("#eje_text > tbody > tr > td > div:nth-child(2) > p")
                            text_forecast = element.text()
                        }
                    }
                }
                "Козерог" -> {
                    when(websiteMain){
                        "horo.mail.ru" -> {
                            val doc = Jsoup.connect("https://horo.mail.ru/prediction/capricorn/today/").get()
                            val element = doc.getElementsByClass("article__text")
                            text_forecast = element.text()
                        }
                        "goroskop365.ru" -> {
                            val doc = Jsoup.connect("https://goroskop365.ru/capricorn/").get()
                            val element = doc.select("#content_wrapper > p")
                            text_forecast = element.text()
                        }
                        "1001goroskop.ru" -> {
                            val doc = Jsoup.connect("https://1001goroskop.ru/?znak=capricorn").get()
                            val element = doc.select("#eje_text > tbody > tr > td > div:nth-child(2) > p")
                            text_forecast = element.text()
                        }
                    }
                }

                "Близнецы" -> {
                    when(websiteMain){
                        "horo.mail.ru" -> {
                            val doc = Jsoup.connect("https://horo.mail.ru/prediction/gemini/today/").get()
                            val element = doc.getElementsByClass("article__text")
                            text_forecast = element.text()
                        }
                        "goroskop365.ru" -> {
                            val doc = Jsoup.connect("https://goroskop365.ru/gemini/").get()
                            val element = doc.select("#content_wrapper > p")
                            text_forecast = element.text()
                        }
                        "1001goroskop.ru" -> {
                            val doc = Jsoup.connect("https://1001goroskop.ru/?znak=gemini").get()
                            val element = doc.select("#eje_text > tbody > tr > td > div:nth-child(2) > p")
                            text_forecast = element.text()
                        }
                    }
                }
                "Весы" -> {
                    when(websiteMain){
                        "horo.mail.ru" -> {
                            val doc = Jsoup.connect("https://horo.mail.ru/prediction/libra/today/").get()
                            val element = doc.getElementsByClass("article__text")
                            text_forecast = element.text()
                        }
                        "goroskop365.ru" -> {
                            val doc = Jsoup.connect("https://goroskop365.ru/libra/").get()
                            val element = doc.select("#content_wrapper > p")
                            text_forecast = element.text()
                        }
                        "1001goroskop.ru" -> {
                            val doc = Jsoup.connect("https://1001goroskop.ru/?znak=libra").get()
                            val element = doc.select("#eje_text > tbody > tr > td > div:nth-child(2) > p")
                            text_forecast = element.text()
                        }
                    }
                }
                "Водолей" -> {
                    when(websiteMain){
                        "horo.mail.ru" -> {
                            val doc = Jsoup.connect("https://horo.mail.ru/prediction/aquarius/today/").get()
                            val element = doc.getElementsByClass("article__text")
                            text_forecast = element.text()
                        }
                        "goroskop365.ru" -> {
                            val doc = Jsoup.connect("https://goroskop365.ru/aquarius/").get()
                            val element = doc.select("#content_wrapper > p")
                            text_forecast = element.text()
                        }
                        "1001goroskop.ru" -> {
                            val doc = Jsoup.connect("https://1001goroskop.ru/?znak=aquarius").get()
                            val element = doc.select("#eje_text > tbody > tr > td > div:nth-child(2) > p")
                            text_forecast = element.text()
                        }
                    }
                }

                "Рак" -> {
                    when(websiteMain){
                        "horo.mail.ru" -> {
                            val doc = Jsoup.connect("https://horo.mail.ru/prediction/cancer/today/").get()
                            val element = doc.getElementsByClass("article__text")
                            text_forecast = element.text()
                        }
                        "goroskop365.ru" -> {
                            val doc = Jsoup.connect("https://goroskop365.ru/cancer/").get()
                            val element = doc.select("#content_wrapper > p")
                            text_forecast = element.text()
                        }
                        "1001goroskop.ru" -> {
                            val doc = Jsoup.connect("https://1001goroskop.ru/?znak=cancer").get()
                            val element = doc.select("#eje_text > tbody > tr > td > div:nth-child(2) > p")
                            text_forecast = element.text()
                        }
                    }
                }
                "Скорпион" -> {
                    when(websiteMain){
                        "horo.mail.ru" -> {
                            val doc = Jsoup.connect("https://horo.mail.ru/prediction/scorpio/today/").get()
                            val element = doc.getElementsByClass("article__text")
                            text_forecast = element.text()
                        }
                        "goroskop365.ru" -> {
                            val doc = Jsoup.connect("https://goroskop365.ru/scorpio/").get()
                            val element = doc.select("#content_wrapper > p")
                            text_forecast = element.text()
                        }
                        "1001goroskop.ru" -> {
                            val doc = Jsoup.connect("https://1001goroskop.ru/?znak=scorpio").get()
                            val element = doc.select("#eje_text > tbody > tr > td > div:nth-child(2) > p")
                            text_forecast = element.text()
                        }
                    }
                }
                "Рыбы" -> {
                    when(websiteMain){
                        "horo.mail.ru" -> {
                            val doc = Jsoup.connect("https://horo.mail.ru/prediction/pisces/today/").get()
                            val element = doc.getElementsByClass("article__text")
                            text_forecast = element.text()
                        }
                        "goroskop365.ru" -> {
                            val doc = Jsoup.connect("https://goroskop365.ru/pisces/").get()
                            val element = doc.select("#content_wrapper > p")
                            text_forecast = element.text()
                        }
                        "1001goroskop.ru" -> {
                            val doc = Jsoup.connect("https://1001goroskop.ru/?znak=pisces").get()
                            val element = doc.select("#eje_text > tbody > tr > td > div:nth-child(2) > p")
                            text_forecast = element.text()
                        }
                    }
                }
            }
            runOnUiThread {
                binding.textViewDescriptionSign1.text = text_forecast
            }

        }catch (e : IOException){
            Log.d("MainLog", e.toString())
        }
    }

    //Функции отображения приветственного текста и заставки --------- УСПЕШНО ЗАКОНЧЕНЫ
    fun timeOfDayDisplay(name: String){
        val calendar = Calendar.getInstance()
        val timeOfDay = calendar.get(Calendar.HOUR_OF_DAY)

        when(timeOfDay){
            //утро
            in 6..11 -> {
                binding.imageViewScreensaver.setImageResource(R.drawable.morning)
                colorWelcomeText("Доброе утро $name, ваш краткий гороскоп на сегодня:", name)
            }
            //день
            in 12..17 -> {
                binding.imageViewScreensaver.setImageResource(R.drawable.daytime)
                colorWelcomeText("Добрый день $name, ваш краткий гороскоп на сегодня:", name)
            }
            //вечер
            in 18..21 -> {
                binding.imageViewScreensaver.setImageResource(R.drawable.evening)
                colorWelcomeText("Добрый вечер $name, ваш краткий гороскоп на сегодня:", name)
            } else -> { //ночь
            binding.imageViewScreensaver.setImageResource(R.drawable.night_screensaver)
            colorWelcomeText("Доброй ночи $name, ваш краткий гороскоп на сегодня:", name)
        }
        }
    }
    fun colorWelcomeText(string_output: String, name : String){ //тут просто троку добавлять
        //Заполнение поля для tw - welcom_user и его окрашивание!---------------------------
        val str_welcome = string_output
        val spannable = SpannableStringBuilder(str_welcome)
        val start = str_welcome.indexOf(name!!)
        val end = start + name.length
        val colorSpan = ForegroundColorSpan(colorNeonBlue)
        spannable.setSpan(colorSpan, start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        binding.textViewWelcomeUser.text = spannable
        //----------------------------------------------------------------------------------
    }
    //Программные функциии ------------ УСПЕШНО ЗАКОНЧЕНЫ
    private fun clickOnImageButton(){
        binding.imageViewButtonUserSettings.setOnClickListener {
            val intent = Intent(this, UserSettingsActivity::class.java)
            startActivity(intent)
        }

        binding.imageViewButtonFire.setOnClickListener {
            SingletonParametersDisplay.element = "fire"
            actionLaunchFragments()
            addFragment(supportFragmentManager, R.id.fragment_holder_info_signs, InformationZodiacSignsFragment())
        }
        binding.imageViewButtonWater.setOnClickListener {
            SingletonParametersDisplay.element = "water"
            actionLaunchFragments()
            addFragment(supportFragmentManager, R.id.fragment_holder_info_signs, InformationZodiacSignsFragment())
        }
        binding.imageViewButtonGround.setOnClickListener {
            SingletonParametersDisplay.element = "ground"
            actionLaunchFragments()
            addFragment(supportFragmentManager, R.id.fragment_holder_info_signs, InformationZodiacSignsFragment())
        }
        binding.imageViewButtonAir.setOnClickListener {
            SingletonParametersDisplay.element = "air"
            actionLaunchFragments()
            addFragment(supportFragmentManager, R.id.fragment_holder_info_signs, InformationZodiacSignsFragment())
        }
    }
    override fun onBackPressed() {
        backToActivity()
        super.onBackPressed()
    } //переопределённый метод для программной кнопки назад
    private fun backToActivity(){
        val count = supportFragmentManager.backStackEntryCount
        if(count == 1){
            elementsActive(true)
        }
    }
    private fun actionLaunchFragments(){ //действия над активити при запуске фргаментов
        binding.fragmentHolderInfoSigns.bringToFront() //перевод на передний план frame-layout
        elementsActive(false) //заблокировать элементы активити при запуске фрагмента(ов)
    }
    private fun elementsActive(bl : Boolean){
        binding.textInputLayout.isClickable = bl
        binding.autoCompleteTxt.isClickable = bl
        binding.imageViewButtonUserSettings.isClickable = bl

        binding.imageViewButtonFire.isClickable = bl
        binding.imageViewButtonAir.isClickable = bl
        binding.imageViewButtonGround.isClickable = bl
        binding.imageViewButtonWater.isClickable = bl
    } //деактивация или активация элементов на активити
}
