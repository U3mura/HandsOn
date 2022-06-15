package com.example.handson

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.handson.databinding.FragmentFAQBinding

class FaqFragment : Fragment() {

    lateinit var binding: FragmentFAQBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_f_a_q, container, false)
    }
}