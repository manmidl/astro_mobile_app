package com.example.astro.app.interfaces

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

interface WorkWithDatabase {
    fun getDataRealtimeDatabase(callback: (MutableMap<String, String>) ->
    Unit){
        val userId = Firebase.auth.currentUser?.uid
        val myRef = FirebaseDatabase.getInstance().getReference("users").child(userId!!)
        val mutableMap = mutableMapOf<String, String>()

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
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