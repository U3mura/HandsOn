package com.example.handson

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.example.handson.databinding.FragmentLoginBinding
import com.example.handson.databinding.FragmentTutoriaisBinding
import com.example.handson.fragments.MeusTutoriaisFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class LoginFragment : Fragment() {

    lateinit var binding : FragmentLoginBinding
    val usuario = FirebaseAuth.getInstance().currentUser
    lateinit var listener: ValueEventListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLoginBinding.inflate(inflater)

        configurarBase()

        binding.mTutoriais.setOnClickListener {
           var fragMan : FragmentManager? = super.getActivity()?.supportFragmentManager
            val frag = MeusTutoriaisFragment()

            fragMan?.beginTransaction()?.replace(R.id.container, frag)?.commit()

        }
        return binding.root
    }

    fun configurarBase (){
        binding.boxEmail.setText(usuario.email)
        binding.boxNome.setText(usuario.displayName)
    }
}