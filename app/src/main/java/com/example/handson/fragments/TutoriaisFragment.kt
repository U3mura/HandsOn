package com.example.handson

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.example.handson.databinding.CardBinding
import com.example.handson.databinding.FragmentTutoriaisBinding
import com.example.handson.databinding.NovoTutorialBinding
import com.example.handson.model.Tutorial
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.HashMap

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
            .setTitle("Insira o título e descrição do tutorial")
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

            val valueEventListener = object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = arrayListOf<Tutorial>()
                    snapshot.child("Tutoriais").children.forEach {
                        val map = it.value as HashMap<String, Any>

                        val id = it.key
                        val nome = map["nome"] as String
                        val des = map["des"] as String

                        val tutorial = Tutorial(id,nome,des)
                        list.add(tutorial)
                    }
                    refreshUi(list)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            }
            database.addValueEventListener(valueEventListener)
        }
    }

    fun refreshUi(list: List<Tutorial>){
        binding.container.removeAllViews()


        list.forEach(){
            val cardBinding = CardBinding.inflate(layoutInflater)

            cardBinding.nome.text = it.nome
            cardBinding.des.text = it.des

            binding.container.addView(cardBinding.root)
        }
    }


}