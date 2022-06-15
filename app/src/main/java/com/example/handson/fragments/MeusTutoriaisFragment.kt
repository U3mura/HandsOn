package com.example.handson.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.handson.R
import com.example.handson.databinding.*
import com.example.handson.model.Tutorial
import com.example.handson.model.TutorialSalvo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.HashMap

class MeusTutoriaisFragment : Fragment() {

    lateinit var binding : FragmentMeusTutoriaisBinding
    lateinit var database: DatabaseReference
    val usuario = FirebaseAuth.getInstance().currentUser
    lateinit var listener : ValueEventListener
    lateinit var inflaterTest: LayoutInflater


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        inflaterTest = inflater
        binding = FragmentMeusTutoriaisBinding.inflate(inflaterTest)

        carregarcards()
        return binding.root
    }

    fun carregarcards(){

        if(usuario != null){
            database = FirebaseDatabase.getInstance()
                .reference

            listener = object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = arrayListOf<Tutorial>()
                    snapshot.child("Tutoriais").children.forEach {

                            val map = it.value as HashMap<String, Any>

                            val id = it.key
                            val nome = map["nome"] as String
                            val des = map["des"] as String
                            val idUsuario = map["idUsuario"] as String

                            val tutorial = Tutorial(id, nome, des, idUsuario)
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

                    Log.e("SalvosFragment", "onCancelled", error.toException())
                }

            }
            database.addValueEventListener(listener)
        }
    }

    override fun onDetach() {
        super.onDetach()

        database.removeEventListener(listener)
    }


    //para atualizar todos os cards, quando tem mudança na base
    fun refreshUi(list: List<Tutorial>){
        binding.container.removeAllViews()


        list.forEach(){
            if (it.idUsuario == usuario.uid){


                val cardBinding = CardBinding.inflate(inflaterTest)

                cardBinding.nome.text = it.nome
                cardBinding.des.text = it.des
                cardBinding.checkSalvo.isChecked = true

                cardBinding.checkSalvo.setOnCheckedChangeListener { checkbox, isChecked ->
                    if (!isChecked && usuario!= null){
                        it.id?.let { it1 -> database.child("Tutoriais").child(it1).removeValue() }
                    }
                }

                binding.container.addView(cardBinding.root)
            }
        }
    }

}