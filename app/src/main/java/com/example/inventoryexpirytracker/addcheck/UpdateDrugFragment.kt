package com.example.inventoryexpirytracker.addcheck

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.inventoryexpirytracker.R
import com.example.inventoryexpirytracker.data.CheckItem
import com.example.inventoryexpirytracker.databinding.FragmentUpdateItemBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_update_item.btn_confirm
import kotlinx.android.synthetic.main.fragment_update_item.spinner_status
import kotlinx.android.synthetic.main.fragment_update_item.tv_item_name
import kotlinx.android.synthetic.main.fragment_update_item.tv_id
import kotlinx.android.synthetic.main.fragment_update_item.tv_location
import kotlinx.android.synthetic.main.fragment_update_item.tv_type

class UpdateDrugFragment() : Fragment(R.layout.fragment_update_item) {

    private val args: UpdateDrugFragmentArgs by navArgs()
    private lateinit var databaseTempTable : DatabaseReference
    private lateinit var databaseCheckItem : DatabaseReference
    private lateinit var statusSpinner: Spinner
    private lateinit var statusSpinnerAdapter : ArrayAdapter<String>
    var statusStatus = ""


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //code for drop down box for changing status
        statusSpinner = spinner_status
        //arrays of values to put in the drop down box
        val statusSelectionsKnox = arrayListOf("Valid", "Expiring", "Removed")
        val statusSelectionsOthers = arrayListOf("Valid", "Expiring", "Disposed")

        //select array based on location of check
        if (args.location == "Knox Wing Safe") {
            statusSpinnerAdapter =
                context?.let {
                    ArrayAdapter(
                        it,
                        android.R.layout.simple_spinner_item,
                        statusSelectionsKnox
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
        //set the adapter, layout and listener for the dropdown box
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

        //binding of firebase data to variables and text views to display data in fragment
        val binding = FragmentUpdateItemBinding.bind(view)

        binding.apply {
            val thisDrug: String = args.drug
            databaseTempTable = FirebaseDatabase.getInstance().getReference("App").child("temptable")
            databaseTempTable.child(thisDrug).get().addOnSuccessListener {

                val tempid = it.child("tempid").value
                val name = it.child("tempname").value
                val type = it.child("temptype").value
                val location = it.child("templocation").value
                val status = it.child("tempstatus").value
                tv_id.text = tempid.toString()
                tv_item_name.text = name.toString()
                tv_type.text = type.toString()
                tv_location.text = location.toString()

                //set the selection of the spinner
                if (status.toString() == "Valid") {
                    statusSpinner.setSelection(0)
                } else {
                    statusSpinner.setSelection(1)
                }

            }.addOnFailureListener {
                Toast.makeText(context, "Failed", Toast.LENGTH_LONG).show()
            }
            btn_confirm.setOnClickListener {

                //collate data into variables
                val checkitemid = tv_id.text.toString()
                val checkitemname = tv_item_name.text.toString()
                val checkitemtype = tv_type.text.toString()
                val checkitemlocation = tv_location.text.toString()
                val checkitemstatus = statusStatus
                val checkID = args.newCheckID

                AddCheckedItem(checkitemid, checkitemname, checkitemtype, checkitemlocation, checkitemstatus, checkID)

                UpdateTempTable(checkitemid, checkitemstatus)

                //navigate to Add check fragment
                val action = UpdateDrugFragmentDirections.actionUpdateDrugFragmentToAddCheckFragment(args.location, args.firstname, args.lastname)
                findNavController().navigate(action)

            }
        }
    }

    fun AddCheckedItem(checkitemid : String, checkitemname : String, checkitemtype : String, checkitemlocation : String, checkitemstatus : String, checkID : String ) {

        //create concatonated ID for a unique ID for the check drug table
        val concatID = checkitemid + "-" + checkID

        //build the check drug object to add to firebase
        val checkitem = CheckItem(concatID, checkitemid, checkitemname, checkitemtype, checkitemlocation, checkitemstatus, checkID)

        //insert object into firebase
        databaseCheckItem = FirebaseDatabase.getInstance().getReference("App").child("checkitem")
        databaseCheckItem.child(concatID).setValue(checkitem)
    }

    fun UpdateTempTable(checkitemid : String, checkitemstatus: String) {

        //create value to update
        val updateTempItem = mapOf<String, String>(
            "tempstatus" to checkitemstatus
        )

        //update status of temporary table object
        databaseTempTable = FirebaseDatabase.getInstance().getReference("App").child("temptable")
        databaseTempTable.child(checkitemid).updateChildren(updateTempItem)
    }
}