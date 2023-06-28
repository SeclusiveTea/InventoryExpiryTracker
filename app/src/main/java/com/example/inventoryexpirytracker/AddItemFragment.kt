package com.example.inventoryexpirytracker

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.inventoryexpirytracker.data.Item
import com.example.inventoryexpirytracker.databinding.FragmentAddItemBinding
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_add_item.*

class AddItemFragment : Fragment(R.layout.fragment_add_item) {

    private val args: AddItemFragmentArgs by navArgs()
    private lateinit var database: DatabaseReference
    private lateinit var itemNameSpinner: Spinner
    private lateinit var itemNameSpinnerAdapter: ArrayAdapter<String>
    private lateinit var statusSpinner: Spinner
    private lateinit var statusSpinnerAdapter: ArrayAdapter<String>
    var itemName = ""
    var statusStatus = ""


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //sets the text of the button depending on location
        (activity as MainActivity).supportActionBar?.title = args.location

        //sets drug type based on location
        tv_location.text = args.location

        if (args.location == "Location 1") {
            tv_type.text = "Cans"
        } else {
            tv_type.text = "Packets"
        }

        //code for drug name selection drop down menu
        itemNameSpinner = spinner_name

        //arrays of options for the spinner
        val itemSelectionsOthers = arrayListOf("Oreos", "Popcorn", "Muesli Bars", "Chocolate")
        val itemSelectionsLocation1 = arrayListOf("Baked Beans", "Spaghetti", "Creamed Corn", "Tomatoes")

        //selects the array based on location
        if (args.location == "Location 1") {
            itemNameSpinnerAdapter =
                context?.let {
                    ArrayAdapter(
                        it,
                        android.R.layout.simple_spinner_item,
                        itemSelectionsLocation1
                    )
                }!!
        } else {
            itemNameSpinnerAdapter =
                context?.let {
                    ArrayAdapter(
                        it,
                        android.R.layout.simple_spinner_item,
                        itemSelectionsOthers
                    )
                }!!
        }
        //sets adapter, layout and listener
        itemNameSpinner.adapter = itemNameSpinnerAdapter
        itemNameSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        itemNameSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (parent != null) {
                    itemName = parent.getItemAtPosition(position).toString()

                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        //code for drug status selection drop down menu
        statusSpinner = spinner_status

        //arrays of options for the drop down menu
        val statusSelectionsLocation1 = arrayListOf("Valid", "Expiring", "Removed")
        val statusSelectionsOthers = arrayListOf("Valid", "Expiring", "Disposed")

        //selects the array based on location
        if (args.location == "Location 1") {
            statusSpinnerAdapter =
                context?.let {
                    ArrayAdapter(
                        it,
                        android.R.layout.simple_spinner_item,
                        statusSelectionsLocation1
                    )
                }!!
        } else {
            statusSpinnerAdapter =
                context?.let {
                    ArrayAdapter(
                        it,
                        android.R.layout.simple_spinner_item,
                        statusSelectionsOthers
                    )
                }!!
        }
        //sets adapter, layout and listener
        statusSpinner.adapter = statusSpinnerAdapter
        statusSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        statusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (parent != null) {
                    statusStatus = parent.getItemAtPosition(position).toString()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }


        val binding = FragmentAddItemBinding.bind(view)
        binding.apply {

            //when the done button is pressed
            btnDone.setOnClickListener {
                val itemid = autoTvId.text.toString()
                val name = itemName
                val type = tv_type.text.toString()
                val location = tv_location.text.toString()
                val status = statusStatus
                //if statement here as temporary validation of data. This will stop us accidentally deleting every value in the database.
                //As long as the ID isn't empty it will generate a new database item rather than delete all.

                //connection to database
                database = FirebaseDatabase.getInstance().getReference("App")

                //set text items as a new object
                val item = Item(itemid, name, type, location, status)

                //sends the object to be stored in drug table in firebase
                database.child("item").child(itemid).setValue(item).addOnSuccessListener {

                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {

                    Toast.makeText(context, "Fail", Toast.LENGTH_SHORT).show()
                }

                // generate new id when we click done
                setID()
            }
        }
        // generate new id when we enter the fragment
        setID()
    }

    private fun setID() {
        // generating a key based on the count of items in the database to be used
        // as a unique id
        fun setKey(key: Long) {
            auto_tv_id.text = key.toString()
        }

        database = FirebaseDatabase.getInstance().getReference("App").child("item")
        var key: Long
        database.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                key = snapshot.childrenCount
                setKey(key)
                database.removeEventListener(this)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

