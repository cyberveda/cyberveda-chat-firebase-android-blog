package com.cyberveda.client.models.chat


data class User(val name: String,
                val bio: String,
                val weight: String,
                val height: String,
                val maritalStatus: String,
                val education: String,
                val profession: String,
                val age: String,
                val gender: String,
                val eatingHabits: String,
                val sleepingHabits: String,
                val profilePicturePath: String?, // because profile picture is not set by default.
                val registrationTokens: MutableList<String>) {


    // because firestore needs a parameter less constructor.

    constructor(): this("", "", "","","","","","","","","",null, mutableListOf())

}