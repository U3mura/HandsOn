package com.example.handson

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.example.handson.databinding.FragmentTutoriaisBinding
import com.example.handson.databinding.NovoTutorialBinding
import com.example.handson.model.Tutorial
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class TutoriaisFragment : Fragment() {

    lateinit var binding : FragmentTutoriaisBinding
    lateinit var database: DatabaseReference

     override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentTutoriaisBinding.inflate(inflater)

        configurarBase()

         binding.fab3.setOnClickListener(){
             inserirTut()
         }
        return binding.root
    }


    fun inserirTut(){
        val novoTutorial = NovoTutorialBinding.inflate(layoutInflater)

        AlertDialog.Builder(requireContext())
            .setTitle("Inserir título do tutorial")
            .setView(novoTutorial.root)

            .setPositiveButton("Inserir") {_, _ ->
                val tutNomeDesc = Tutorial(nome = novoTutorial.editNome.text.toString(), des = novoTutorial.editDesc.text.toString())
                val newNode = database.child("Tutoriais").push()

                tutNomeDesc.id = newNode.key
                newNode.setValue(tutNomeDesc)
            }

            .setNegativeButton("Cancelar", null)
            .create()
            .show()
    }

    fun configurarBase(){

        val usuario = FirebaseAuth.getInstance().currentUser

        if(usuario != null){
            database = FirebaseDatabase.getInstance()
                .reference.child(usuario.uid)
        }
    }

}