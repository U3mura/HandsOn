package com.example.handson

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.allViews
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
        val usuario = FirebaseAuth.getInstance().currentUser

        AlertDialog.Builder(requireContext())
            .setTitle("Insira o título e descrição do tutorial")
            .setView(novoTutorial.root)

            .setPositiveButton("Inserir") {_, _ ->
                val tutNomeDesc = Tutorial(nome = novoTutorial.editNome.text.toString(),
                    des = novoTutorial.editDesc.text.toString(),
                    idUsuario = usuario.uid)
                val newNode = database.child("Tutoriais").push()

                newNode.key?.let{
                    tutNomeDesc.id = it
                }
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

            //verificando se teve alguém(alguma função) que alterou algum valor na base e caso tenha sido alterado
            //ele cria uma lista com os novos valores(editados,excluido ou inseridos)
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
            val cardBinding = CardBinding.inflate(layoutInflater)

            cardBinding.nome.text = it.nome
            cardBinding.des.text = it.des
            cardBinding.checkSalvo.isChecked = it.salvo

            cardBinding.checkSalvo.setOnCheckedChangeListener{ checkbox, isChecked ->
                val noTutorial = it.id?.let { it1 ->
                    database.child("Tutoriais").child(it1)
                }

                noTutorial?.child("salvo")?.setValue(isChecked)
            }

            binding.container.addView(cardBinding.root)
        }


    }


}