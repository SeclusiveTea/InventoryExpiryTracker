package com.example.inventoryexpirytracker

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.inventoryexpirytracker.databinding.FragmentLoginBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_login.btn_login

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var databaseNurse : DatabaseReference

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // hide the nav bar when navigating from drawer menu
        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        actionBar?.let {
            actionBar.hide()
        }

        val binding = FragmentLoginBinding.bind(view)

        binding.apply {



            btn_login.setOnClickListener {

                val userName : String = etUsername.text.toString()
                val pin : String = etPin.text.toString()

                val firstName = "Example"
                val lastName = "Name"


                val action = LoginFragmentDirections.actionLoginFragmentToHomeFragment(firstName, lastName)
                findNavController().navigate(action)



            }
        }


    }
    override fun onResume() {
        super.onResume()
        // Disable the back button and overrides it!
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            // Do nothing or show a message if desired
        }
    }
}