package com.example.inventoryexpirytracker


import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged

import androidx.fragment.app.Fragment
import com.example.inventoryexpirytracker.data.Staff
import com.example.inventoryexpirytracker.databinding.FragmentAddStaffBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_add_staff.et_firstname
import kotlinx.android.synthetic.main.fragment_add_staff.et_lastname
import kotlinx.android.synthetic.main.fragment_add_staff.et_pin
import kotlinx.android.synthetic.main.fragment_add_staff.spinner_account
import kotlinx.android.synthetic.main.fragment_add_staff.tv_username


class AddStaffFragment : Fragment(R.layout.fragment_add_staff) {

    private lateinit var database: DatabaseReference
    private lateinit var accountSpinner: Spinner
    private lateinit var accountSpinnerAdapter: ArrayAdapter<String>
    var accountvar = ""
    var firstName = ""
    var lastName = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //code for the account type drop down selection
        accountSpinner = spinner_account

        //array of selections for the dropdown
        val selection = arrayListOf("Administrator", "User")

        //creates drop down adapter
        accountSpinnerAdapter =
            context?.let {
                ArrayAdapter(
                    it,
                    android.R.layout.simple_spinner_item,
                    selection
                )
            }!!

        //sets drop down adapter, layout and listener
        accountSpinner.adapter = accountSpinnerAdapter
        accountSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        accountSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (parent != null) {
                    accountvar = parent.getItemAtPosition(position).toString()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        //binding the the text views to variables to send to firebase
        val binding = FragmentAddStaffBinding.bind(view)

        binding.apply {
            btnDone.setOnClickListener {
                val firstName = et_firstname.text.toString()
                val lastName = et_lastname.text.toString()
                val userName = tv_username.text.toString()
                val account = accountvar
                val pin = et_pin.text.toString()

                //if statement here as validation of data. User must have a username to generate a new nurse
                //As long as the username isn't empty it will generate a new database item rather than delete all items.
                if (userName.isNotEmpty()) {
                    //connection to database
                    database = FirebaseDatabase.getInstance().getReference("App").child("staff")
                    //set text items as a new object
                    val Staff = Staff(userName, firstName, lastName, account, pin)

                    //send object to nurse table in firebase
                    database.child(userName).setValue(Staff).addOnSuccessListener {

                        et_firstname.text.clear()
                        et_lastname.text.clear()
                        et_pin.text.clear()

                        Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {

                        Toast.makeText(context, "Fail", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "No info entered", Toast.LENGTH_SHORT).show()
                }

            }
            //auto generate the user ID when the user enters their first and last name
            et_firstname.doAfterTextChanged {
                firstName = et_firstname.text.toString()
                if (firstName.isNotEmpty()) {
                    firstName = firstName[0].toString()
                    tv_username.text = firstName.lowercase() + lastName.lowercase()
                }
            }
            et_lastname.doAfterTextChanged {
                lastName = et_lastname.text.toString()
                tv_username.text = firstName.lowercase() + lastName.lowercase()
            }
        }
    }
}