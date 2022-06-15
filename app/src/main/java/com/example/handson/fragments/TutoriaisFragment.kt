package com.example.handson

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import com.example.handson.databinding.CardBinding
import com.example.handson.databinding.FragmentTutoriaisBinding
import com.example.handson.databinding.NovoTutorialBinding
import com.example.handson.model.Tutorial
import com.example.handson.model.TutorialSalvo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.HashMap

class TutoriaisFragment : Fragment() {

    lateinit var binding : FragmentTutoriaisBinding
    lateinit var database: DatabaseReference
    val usuario = FirebaseAuth.getInstance().currentUser
    lateinit var inflaterTest: LayoutInflater
    lateinit var listener: ValueEventListener
    companion object maxSalvos{
        var valor: Int = 0
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        inflaterTest = inflater
        binding = FragmentTutoriaisBinding.inflate(inflaterTest)

        configurarBase()

        binding.fab3.setOnClickListener(){
            inserirTut()
        }

        return binding.root
    }


    fun inserirTut(){
        val novoTutorial = NovoTutorialBinding.inflate(inflaterTest)


        AlertDialog.Builder(requireContext())
            .setView(novoTutorial.root)

            .setPositiveButton("Inserir") {_, _ ->
                val tutNomeDesc = Tutorial(nome = novoTutorial.editNome.text.toString(),
                    des = novoTutorial.editDesc.text.toString(),
                    idUsuario = usuario?.uid)
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

        if(usuario != null){
            database = FirebaseDatabase.getInstance()
                .reference

            //verificando se teve alguém(alguma função) que alterou algum valor na base e caso tenha sido alterado
            //ele cria uma lista com os novos valores(editados,excluido ou inseridos)
            listener = object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val listTutorial = arrayListOf<Tutorial>()
                    val listTutorialSalvo = arrayListOf<TutorialSalvo>()


                    snapshot.child("Tutoriais").children.forEach {

                        val map = it.value as HashMap<String, Any>

                        val id = it.key
                        val nome = map["nome"] as String
                        val des = map["des"] as String
                        val idUsuario = map["idUsuario"] as String

                        val tutorial = Tutorial(id,nome,des,idUsuario)
                        listTutorial.add(tutorial)
                    }

                    snapshot.child(usuario.uid).children.forEach {
                        val map = it.value as HashMap<String, Any>

                        listTutorial.forEach{ tutorial ->
                            if (tutorial.id == it.key){
                                val id = tutorial.id
                                val salvo = map["salvo"] as Boolean
                                val tutorialSalvo = TutorialSalvo(id,tutorial, salvo)
                                listTutorialSalvo.add(tutorialSalvo)
                            }
                        }
                    }


                    refreshUi(listTutorial, listTutorialSalvo)
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
            database.addValueEventListener(listener)
        }
    }

    override fun onDetach() {
        super.onDetach()

        database.removeEventListener(listener)
    }

    //para atualizar todos os cards, quando tem mudança na base
    fun refreshUi(listTutorial: List<Tutorial>, listTutorialSalvo: List<TutorialSalvo>){
        binding.container.removeAllViews()

        listTutorial.forEach(){

            var cardBinding = CardBinding.inflate(inflaterTest)

            cardBinding.nome.text = it.nome
            cardBinding.des.text = it.des

            listTutorialSalvo.forEach { tutorialSalvo ->
                if (tutorialSalvo.id == it.id){
                    maxSalvos.valor +=1
                    cardBinding.checkSalvo.isChecked = tutorialSalvo.salvo

                }else{
                    maxSalvos.valor -=1
                }
            }

            cardBinding.checkSalvo.setOnCheckedChangeListener{ checkbox, isChecked ->

                if (isChecked && usuario != null && maxSalvos.valor <= 3){
                    maxSalvos.valor +=1
                    it.id?.let { it1 -> database.child(usuario.uid).child(it1).child("salvo").setValue(isChecked) }
                    it.id?.let { it1 -> database.child(usuario.uid).child(it1).child("id").setValue(it1) }
                    it.id?.let { it1 -> database.child(usuario.uid).child(it1).child("des").setValue(it.des) }
                    it.id?.let { it1 -> database.child(usuario.uid).child(it1).child("nome").setValue(it.nome) }
                    it.id?.let { it1 -> database.child(usuario.uid).child(it1).child("idUsuario").setValue(it.idUsuario) }

                    val intent = Intent(requireContext(), AdActivity::class.java)
                    startActivity(intent)
                }else{
                    if(usuario != null)
                        it.id?.let { it1 -> database.child(usuario.uid).child(it1).removeValue() }
                        maxSalvos.valor -=1
                }

            }

            binding.container.addView(cardBinding.root)
        }


    }


}