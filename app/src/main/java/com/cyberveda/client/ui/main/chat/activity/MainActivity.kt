//package com.cyberveda.client
//
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import androidx.fragment.app.Fragment
//import com.cyberveda.client.fragment.MyChatProfileFragment
//import com.cyberveda.client.fragment.PeopleFragment
//
//class MainActivity : AppCompatActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        replaceFragment(PeopleFragment())
//
//        navigation.setOnNavigationItemSelectedListener {
//            when (it.itemId) {
//                R.id.navigation_people -> {
//                    replaceFragment(PeopleFragment())
//                    true
//                }
//                R.id.navigation_my_account -> {
//                    replaceFragment(MyChatProfileFragment())
//                    true
//                }
//                else -> false
//            }
//        }
//    }
//
//    private fun replaceFragment(fragment: Fragment) {
//        supportFragmentManager.beginTransaction()
//                .replace(R.id.fragment_layout, fragment)
//                .commit()
//    }
//}
