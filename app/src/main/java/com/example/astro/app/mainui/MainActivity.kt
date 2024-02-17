package com.example.astro.app.mainui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import com.example.astro.R
import com.example.astro.app.databases.firebase.firebaseDB.FirebaseDBFunctions
import com.example.astro.app.interfaces.WorkWithDatabase
import com.example.astro.app.listdata.ListDataRepository
import com.example.astro.app.settingsui.UserSettingsActivity
import com.example.astro.databinding.ActivityMainBinding
import com.example.astro.app.mainfunc.MainFunctions
import com.example.astro.app.mainfunc.textsettings.SettingsTextMainActivity
import com.example.astro.app.parsing.ListSigns
import com.example.astro.app.parsing.ParsingData
import java.io.IOException


class MainActivity : AppCompatActivity(), WorkWithDatabase{
    private lateinit var binding : ActivityMainBinding

    private lateinit var adapterItems : ArrayAdapter<String>
    private lateinit var signZodiac : String
    private var websiteMain : String = ListDataRepository.listWebSite[0]
    private lateinit var loginMethod: String

    private lateinit var secThread: Thread
    private lateinit var runnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataDisplay()
        selectWebSite()

        clickOnImageButton()
        clickOnImageViewButtonUserSettings()
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

    private fun dataDisplay(){
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        loginMethod = sharedPreferences.getString("login_method", "default value")!!
        SettingsTextMainActivity(binding).settingsTextMainActivity(this)

        var name = ""
        //если пользователь вошёл с учётной записью
        if (loginMethod == "auth"){
            FirebaseDBFunctions().getDataInRealtimeDatabase { mutMap ->
                name = mutMap["personName"]!!
                signZodiac = mutMap["zodiacSign"]!!

                initGetForecast()
                MainFunctions(binding).timeOfDayDisplay(name, this)
            }
        }
        //если пользователь вошёл без уч.записи
        else if (loginMethod == "no auth"){
            name = sharedPreferences.getString("name", "")!!
            signZodiac = sharedPreferences.getString("sign_zodiac", "")!!

            initGetForecast()
            MainFunctions(binding).timeOfDayDisplay(name, this)
        }
    }
    private fun initGetForecast(){
        runnable = Runnable {
            getWebShortForecast()
        }
        secThread = Thread(runnable)
        secThread.start()
    }
    //метод получения данных из гороскопов
    private fun getWebShortForecast() {
        try {
            var textForecast = ParsingData().getPersonUserHoroscope(ListSigns.mapZodiacSigns.get(signZodiac)!!, websiteMain)
            runOnUiThread {
                binding.textViewDescriptionSign1.text = textForecast
            }
        }catch (e : IOException){
            Log.d("MainLog", e.toString())
        }
    }
    private fun clickOnImageViewButtonUserSettings(){
        binding.imageViewButtonUserSettings.setOnClickListener {
            val intent = Intent(this, UserSettingsActivity::class.java)
            startActivity(intent)
        }
    }
    private fun clickOnImageButton(){
        binding.imageViewButtonUserSettings.setOnClickListener {
            val intent = Intent(this, UserSettingsActivity::class.java)
            startActivity(intent)
        }
        binding.imageViewButtonFire.setOnClickListener {
            MainFunctions(binding).setElement("fire", supportFragmentManager)
        }
        binding.imageViewButtonWater.setOnClickListener {
            MainFunctions(binding).setElement("water", supportFragmentManager)
        }
        binding.imageViewButtonGround.setOnClickListener {
            MainFunctions(binding).setElement("ground", supportFragmentManager)
        }
        binding.imageViewButtonAir.setOnClickListener {
            MainFunctions(binding).setElement("air", supportFragmentManager)
        }
    }
    private fun backToActivity(){
        val count = supportFragmentManager.backStackEntryCount
        if(count == 1){
            MainFunctions(binding).elementsActive(true)
        }
    }
    override fun onBackPressed() {
        backToActivity()
        super.onBackPressed()
    }
    private fun selectWebSite(){ //Пользовательские функции (работа с интерфейсом)
        adapterItems = ArrayAdapter<String>(this, R.layout.list_item_website, ListDataRepository.listWebSite)
        binding.autoCompleteTxt.setAdapter(adapterItems)

        binding.autoCompleteTxt.setOnItemClickListener { parent, view, position, id ->
            val item = parent.getItemAtPosition(position).toString()
            websiteMain = item

            initGetForecast()
        }
        binding.autoCompleteTxt.isActivated = true
        binding.autoCompleteTxt.requestFocus()
    }
}
