package com.example.handson

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.handson.databinding.CardBinding
import com.example.handson.databinding.FragmentSalvosBinding
import com.example.handson.databinding.FragmentTutoriaisBinding
import com.example.handson.model.Tutorial
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.HashMap

class SalvosFragment : Fragment() {
    lateinit var binding : FragmentSalvosBinding
    lateinit var database: DatabaseReference
    val usuario = FirebaseAuth.getInstance().currentUser
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSalvosBinding.inflate(inflater)

        carregarcards()

        return binding.root
    }

    fun carregarcards(){


        if(usuario != null){
            database = FirebaseDatabase.getInstance()
                .reference

            val valueEventListener = object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = arrayListOf<Tutorial>()
                    snapshot.child("Tutoriais").children.forEach {
                        val map = it.value as HashMap<String, Any>

                        val id = it.key
                        val nome = map["nome"] as String
                        val des = map["des"] as String
                        val salvo = map["salvo"] as Boolean
                        val idUsuario = map["idUsuario"] as String

                        val tutorial = Tutorial(id,nome,des,salvo,idUsuario)
                        list.add(tutorial)
                    }
                    refreshUi(list)
                }


                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        requireContext(),
                        "Não foi possivel acessar o servidor",
                        Toast.LENGTH_LONG)
                        .show()

                    Log.e("TutoriaisFragment", "onCancelled", error.toException())
                }

            }
            database.addValueEventListener(valueEventListener)
        }
    }


    //para atualizar todos os cards, quando tem mudança na base
    fun refreshUi(list: List<Tutorial>){
        binding.container.removeAllViews()


        list.forEach(){
            if (it.salvo) {
                val cardBinding = CardBinding.inflate(layoutInflater)

                cardBinding.nome.text = it.nome
                cardBinding.des.text = it.des
                cardBinding.checkSalvo.isChecked = it.salvo

                cardBinding.checkSalvo.setOnCheckedChangeListener { checkbox, isChecked ->

                    val noTutorial = it.id?.let { it1 ->
                        database.child(usuario.uid).child(it1)
                    }

                    noTutorial?.child("salvo")?.setValue(isChecked)
                }

                binding.container.addView(cardBinding.root)
            }
        }
    }
}