package com.example.inventoryexpirytracker.checkhistory

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inventoryexpirytracker.MainActivity
import com.example.inventoryexpirytracker.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_check_history.rv_checkhistory

class CheckHistoryFragment : Fragment(R.layout.fragment_check_history), CheckHistoryAdapter.OnItemClickListener {

    private val args: CheckHistoryFragmentArgs by navArgs()
    private lateinit var database : DatabaseReference
    private lateinit var databaseApp : DatabaseReference
    private lateinit var checkList : ArrayList<CheckHistoryCheck>
    private lateinit var adapter : CheckHistoryAdapter
    private val listener = this

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //sets the text of the button depending on location
        (activity as MainActivity).supportActionBar?.title = args.location

        //This function deletes the temporary table. This is for when a user is navigated here after confirming a check.
        //If a user comes here from inventory fragment, this function will execute but won't do anything.
        DeleteTempTable()

        //pulls the data from the check history table and puts it into the recycler view.
        //must stay in onViewCreated
        val list = ArrayList<CheckHistoryCheck>() //creating our list to add data to recycler view
        //reference to database
        database = Firebase.database.getReference("App").child("checkhistory")
        //event listener to database
        database.addValueEventListener(object: ValueEventListener {
            //real time pull of data function, not only on data change
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = ArrayList<CheckHistoryCheck>()
                val children = snapshot!!.children
                //opening the firebase snapshot
                children.forEach {
                    if (it.child("checklocation").value.toString() == args.location) {
                        //putting the children into variables
                        val checkID = it.child("checkid").value
                        val date = it.child("checkdate").value
                        val stafffname = it.child("checkstafffname").value
                        val stafflname = it.child("checkstafflname").value
                        val mylocation = it.child("checklocation").value
                        list.add(
                            CheckHistoryCheck(
                                checkID.toString(),
                                date.toString(),
                                stafffname.toString() + " " + stafflname.toString(),
                                mylocation.toString()
                            )
                        )
                    }
                }
                checkList = list
                adapter = CheckHistoryAdapter(checkList, listener)

                if (this@CheckHistoryFragment.isResumed) {
                    rv_checkhistory.adapter = adapter
                    rv_checkhistory.layoutManager = LinearLayoutManager(context)
                    rv_checkhistory.setHasFixedSize(true)
                }
                database.removeEventListener(this)
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
            }
        })
    }
    override fun onItemClick(position: Int) {
        //navigates to check history details fragment if an item is clicked in the recycler view
        val checkID = checkList[position].checkID
        val action = CheckHistoryFragmentDirections.actionCheckHistoryFragmentToCheckHistoryDetailsFragment(checkID)
        findNavController().navigate(action)
    }

    fun DeleteTempTable() {
        //deletes the whole temporary table
        databaseApp = FirebaseDatabase.getInstance().getReference("App")
        databaseApp.child("temptable").removeValue()
    }
}