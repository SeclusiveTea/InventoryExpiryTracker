package com.example.inventoryexpirytracker.addcheck

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inventoryexpirytracker.MainActivity
import com.example.inventoryexpirytracker.R
import com.example.inventoryexpirytracker.inventory.InventoryItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_add_check.*
import kotlinx.android.synthetic.main.fragment_add_check.btn_done


class AddCheckFragment : Fragment (R.layout.fragment_add_check), AddCheckAdapter.OnItemClickListener {

    private val args: AddCheckFragmentArgs by navArgs()
    private lateinit var inventoryList : ArrayList<InventoryItem>
    private lateinit var databaseCheckHistory: DatabaseReference
    private lateinit var databaseTempTable: DatabaseReference
    private lateinit var databaseCheckItem: DatabaseReference
    private lateinit var adapter : AddCheckAdapter
    private val listener = this
    var newCheckID = ""


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //sets the text of the button depending on location
        (activity as MainActivity).supportActionBar?.title = "           " + args.location

        //Hide back button on action bar
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(false)

        //generate the key for the new check
        setID()

        //put the temporary table drugs into the recycler view
        //must stay in onViewCreated
        databaseTempTable = FirebaseDatabase.getInstance().getReference("App").child("temptable")
        databaseTempTable.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = ArrayList<InventoryItem>()
                val children = snapshot!!.children
                children.forEach {

                        val tempid = it.child("tempid").value
                        val name = it.child("tempname").value
                        val status = it.child("tempstatus").value
                        if (it.child("temptype").value.toString() == "Controlled") {
                            list.add(
                                InventoryItem(
                                    R.drawable.redrectangle,
                                    tempid.toString(),
                                    name.toString(),
                                    status.toString()
                                )
                            )
                        } else {
                            list.add(
                                InventoryItem(
                                    R.drawable.bluerectangle,
                                    tempid.toString(),
                                    name.toString(),
                                    status.toString()
                                )
                            )
                        }
                    }

                inventoryList = list
                adapter = AddCheckAdapter(inventoryList, listener)

                if (this@AddCheckFragment.isResumed) {
                    rv_addcheck.adapter = adapter
                    Log.i("mytag", "Temp table Displayed")
                    rv_addcheck.layoutManager = LinearLayoutManager(context)
                    rv_addcheck.setHasFixedSize(true)
                }
                // detach listener
                databaseTempTable.removeEventListener(this)
            }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
                }
        })

        btn_done.setOnClickListener {
            //navigate to Confirm Check Fragment
            val action = AddCheckFragmentDirections.actionAddCheckFragmentToConfirmCheckFragment(args.location, newCheckID, args.firstname, args.lastname)
            findNavController().navigate(action)
        }
        btn_cancel.setOnClickListener {
            //cancel check
            onCancelPressedAlert()
        }
    }
    private fun setID() {
        // generating a key based on the count of drugs in the database to be used
        // as a unique id
        fun setKey(key: Long) {
            tv_check_title.text = "Add Check ID: $key"
            newCheckID = key.toString()
        }

        databaseCheckHistory =
            FirebaseDatabase.getInstance().getReference("App").child("checkhistory")
        var key: Long
        databaseCheckHistory.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                key = snapshot.childrenCount
                setKey(key)
                databaseCheckHistory.removeEventListener(this)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onItemClick(position: Int) {
        //clicking an item in the recycler view navigates you to update drug fragment
        val drug = inventoryList[position].itemId
        val action = AddCheckFragmentDirections.actionAddCheckFragmentToUpdateDrugFragment(drug, args.location, newCheckID, args.firstname, args.lastname)
        findNavController().navigate(action)
    }
    override fun onResume() {
        super.onResume()
        // hide app bar
        //(activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        // Disable the back button and overrides it!
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            // Do nothing or show a message if desired
        }
    }


    private fun onCancelPressedAlert() {
        //pop up when you press cancel
        val alert = AlertDialog.Builder(this.context)
        alert.setTitle("Cancel Check")
        alert.setMessage("Are you sure you'd like to cancel your check?")
        alert.setPositiveButton("Yes") { _, _ -> cancelCheck() }
        alert.setNegativeButton("No", null)
        alert.setCancelable(false)
        alert.show()
    }

    private fun cancelCheck() {
        //removes the temporary table generated when a new check is started
        databaseTempTable = FirebaseDatabase.getInstance().getReference("App")
        databaseTempTable.child("temptable").removeValue()

        //removes all checked drugs that were created in this session by removing all items from the check drug table
        //that match the check ID of that session.
        // https://stackoverflow.com/questions/37390864/how-to-delete-from-firebase-realtime-database

        //create the query to grab all items that match the check ID of this session
        databaseCheckItem = FirebaseDatabase.getInstance().getReference("App").child("checkitem")
        val query = databaseCheckItem.orderByChild("checkitemcheckid").equalTo(newCheckID)

        //iterate through all items and remove
        query.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val children = snapshot!!.children
                children.forEach {
                    it.ref.removeValue()
                    //detach listener
                    databaseCheckItem.removeEventListener(this)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
            }
        })

        //navigate back to the inventory fragment
        val action = AddCheckFragmentDirections.actionAddCheckFragmentToInventoryFragment(args.location, args.firstname, args.lastname)
        findNavController().navigate(action)
    }
    //override fun onDestroyView() {
    //    super.onDestroyView()
    //    (requireActivity() as AppCompatActivity).supportActionBar?.show()
    //}
}