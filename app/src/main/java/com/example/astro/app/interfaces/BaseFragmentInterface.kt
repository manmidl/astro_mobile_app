package com.example.astro.app.interfaces

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

interface BaseFragmentInterface {
    //метод добавления поверх теекущего, не удаляя из стека вызовов.
    // Вызов фргамента из активити
    fun addFragment(supportFragmentManager: FragmentManager, containerId: Int, fragment: Fragment){
        supportFragmentManager
            .beginTransaction()
            .add(containerId, fragment)
            .addToBackStack(null)
            .commit()
    }
    //метод замещения текущего фрагмента, на новый фрагмент и удаляет текущий из
    //стека вызовов. Вызов фрагмента из фрагментов
    fun replaceFragment(transaction: FragmentTransaction, containerId: Int, fragment: Fragment){
        transaction?.replace(containerId, fragment) // заменяем текущий фрагмент на новый
        transaction?.addToBackStack(null) // добавляем новый фрагмент в стек
        transaction?.commit()
    }
}