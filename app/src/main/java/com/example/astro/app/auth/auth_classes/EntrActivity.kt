package com.example.astro.app.auth.auth_classes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.astro.R
import com.example.astro.app.interfaces.BaseFragmentInterface
import com.example.astro.app.interfaces.DeleteAccountInterface
import com.example.astro.databinding.ActivityEntrBinding


class EntrActivity : AppCompatActivity(), BaseFragmentInterface, DeleteAccountInterface {
    private lateinit var binding : ActivityEntrBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEntrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        buttonActions()
        //проврека на то, что фрагменты отсутствуют и поэтому не показывать в начале кнопку назад
        if (getSupportFragmentManager().getFragments().isEmpty()) {
            binding.buttonBackArrow.isVisible = false
        }
    }


    private fun buttonActions(){
        //кнопка назад
        binding.buttonBackArrow.setOnClickListener {
            supportFragmentManager.popBackStack()
            backToActivity() //проверка не возвращает ли кнопка "назад" обратно на активити
        }
        binding.buttonLogIn.setOnClickListener {
            actionLaunchFragments()
            addFragment(supportFragmentManager, R.id.fragment_holder, LogInFragment.newInstance())
        }
        //кнопка продожить без регистрации
        binding.buttonNextStep.setOnClickListener {
            actionLaunchFragments()
            addFragment(supportFragmentManager, R.id.fragment_holder, AddInfFragment.newInstance())
        }
        binding.buttonRegistred.setOnClickListener {
            actionLaunchFragments()
            addFragment(supportFragmentManager,
                R.id.fragment_holder,
                CreateAccountFragment.newInstance()
            )
        }
    }


    //действия над активити при запуске фргаментов
    private fun actionLaunchFragments(){
        binding.fragmentHolder.bringToFront() //перевод на передний план frame-layout
        binding.buttonBackArrow.bringToFront() //кнопку возврата на передний фон перекрывая, чтобы она была видна на фрагменте
        binding.buttonBackArrow.isVisible = true //отображаем кнопку назад
        elementsActive(false) //заблокировать элементы активити при запуске фрагмента(ов)
    }

    //возврат из фрагментов на активити
    private fun backToActivity(){
        val count = supportFragmentManager.backStackEntryCount
        if(count == 1){ //если фрагмент переходит на активити, то скрываем кнопку вернуться и активируем элементы
            binding.buttonBackArrow.isVisible = false
            elementsActive(true)
        }
    }
    //деактивация или активация элементов на активити
    private fun elementsActive(bl : Boolean){
        binding.buttonLogIn.isClickable = bl
        binding.buttonNextStep.isClickable = bl
        binding.buttonRegistred.isClickable = bl
    }
    //переопределённый метод для программной кнопки назад
    override fun onBackPressed() {
        backToActivity()
        super.onBackPressed()
    }
}