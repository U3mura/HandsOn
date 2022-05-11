package com.example.handson

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.handson.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.tutoriais -> {
                    val frag = TutoriaisFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.container, frag).commit()
                }
                R.id.salvos -> {
                    val frag = SalvosFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.container, frag).commit()
                }
                R.id.faq -> {
                    val frag = FaqFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.container, frag).commit()
                }
//                R.id.perfil -> {
//                    val frag = SalvosFragment()
//                    supportFragmentManager.beginTransaction().replace(R.id.container, frag).commit()
//                }

            }
            true
        }
    }
}