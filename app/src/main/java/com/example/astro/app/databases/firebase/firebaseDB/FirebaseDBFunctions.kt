package com.example.astro.app.databases.firebase.firebaseDB

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FirebaseDBFunctions {
    //внос новых данных в RealtimeDatabase
    fun setDataInRealtimeDatabase(email : String, name : String, zodiacSign : String) {
        val database = com.google.firebase.ktx.Firebase.database
        val myRef = database.reference
        val userId = com.google.firebase.ktx.Firebase.auth.currentUser?.uid

        val user = hashMapOf(
            "email" to email,
            "personName" to name,
            "zodiacSign" to zodiacSign,
            "web_site_default" to "horo.mail.ru"
        )
        myRef.child("users").child(userId!!)
            .setValue(user) //обращение к узлу и размещение информации
    }

    fun getDataInRealtimeDatabase(callback: (Map<String, String>) ->
    Unit){
        val userId = Firebase.auth.currentUser?.uid
        val myRef = FirebaseDatabase.getInstance().getReference("users").child(userId!!)
        val mutableMap = mutableMapOf<String, String>()

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //создать доп доп функцию чтобы избавиться от копипасты
                var email = dataSnapshot.child("email").getValue(String::class.java)!!
                var personName = dataSnapshot.child("personName").getValue(String::class.java)!!
                var zodiacSign = dataSnapshot.child("zodiacSign").getValue(String::class.java)!!
                var websiteDefault = dataSnapshot.child("web_site_default").getValue(String::class.java)!!

                mutableMap["email"] = email
                mutableMap["personName"] = personName
                mutableMap["zodiacSign"] = zodiacSign
                mutableMap["websiteDefault"] = websiteDefault

                callback(mutableMap)
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}