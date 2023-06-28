package com.example.inventoryexpirytracker

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_disclaimer.btn_accept

class DisclaimerFragment : Fragment(R.layout.fragment_disclaimer) {



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //hide toolbar
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()

        btn_accept.setOnClickListener {

            //navigate to login fragment when accept button pressed
            val action = DisclaimerFragmentDirections.actionDisclaimerFragmentToLoginFragment()
            findNavController().navigate(action)
        }
    }
}