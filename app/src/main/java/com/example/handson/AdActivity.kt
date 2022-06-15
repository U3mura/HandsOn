package com.example.handson

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.handson.databinding.ActivityAdBinding
import com.google.firebase.auth.FirebaseAuth


class AdActivity : AppCompatActivity() {

    lateinit var binding: ActivityAdBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button2.setOnClickListener {
            finish()
        }

    }


}