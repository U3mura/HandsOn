package com.example.handson

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.handson.databinding.ActivityMainBinding
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tratarLogin()

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

    fun tratarLogin(){
        if (FirebaseAuth.getInstance().currentUser != null) {
            Toast.makeText(this, "Entrou", Toast.LENGTH_SHORT).show()
            configurarBase()
        } else{
            val providers = arrayListOf(
                AuthUI.IdpConfig.EmailBuilder().build())

            val i = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build()

            startActivityForResult(i, 1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == RESULT_OK){
            Toast.makeText(this, "Autenticado", Toast.LENGTH_SHORT).show()
            configurarBase()
        } else {
            finishAffinity()
        }
    }

    fun configurarBase(){

        val usuario = FirebaseAuth.getInstance().currentUser

        if(usuario != null){

            database = FirebaseDatabase.getInstance()
                .reference.child(usuario.uid)

        }

    }

}