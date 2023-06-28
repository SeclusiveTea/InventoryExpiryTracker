package com.example.inventoryexpirytracker.managestaff

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inventoryexpirytracker.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.manage_staff_fragment.fab_addnurse
import kotlinx.android.synthetic.main.manage_staff_fragment.rv_managenurses

class ManageStaffFragment : Fragment(R.layout.manage_staff_fragment), ManageStaffAdapter.OnItemClickListener {

    private lateinit var database : DatabaseReference
    private lateinit var staffList : ArrayList<ManageStaffStaff>
    private lateinit var adapter : ManageStaffAdapter
    private val listener = this
    var argusername = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //pulls all nurse from nurse table and puts them into recycler view
        //must stay in onViewCreated
        val list = ArrayList<ManageStaffStaff>() //creating our list to add data to recycler view
        //reference to database
        database = Firebase.database.getReference("App").child("staff")
        //event listener to database
        database.addValueEventListener(object: ValueEventListener {
            //real time pull of data function, not only on data change
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = ArrayList<ManageStaffStaff>()
                val children = snapshot!!.children
                //opening the firebase snapshot
                children.forEach{
                    //putting the children into variables
                    val fName = it.child("fname").value
                    val lName = it.child("lname").value
                    val account = it.child("account").value

                    list.add(ManageStaffStaff(fName.toString(), lName.toString(), account.toString()))
                }
                staffList = list
                adapter = ManageStaffAdapter(staffList, listener)

                // stops app from crashing when moving to new screen before data loads
                // stops the UI update from taking place
                if (this@ManageStaffFragment.isResumed){
                    rv_managenurses.adapter = adapter
                    rv_managenurses.layoutManager = LinearLayoutManager(context)
                    rv_managenurses.setHasFixedSize(true)
                }
                database.removeEventListener(this)
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
            }
        })



        fab_addnurse.setOnClickListener {
            //navigate to add nurse fragment
            val action = ManageStaffFragmentDirections.actionManageNursesFragmentToAddNurseFragment()
            findNavController().navigate(action)
        }
    }

    override fun onItemClick(position: Int) {
        //if item in recycler view is clicked, navigate to nurse details.
        val fName = staffList[position].fName
        val lName = staffList[position].lName
        val action = ManageStaffFragmentDirections.actionManageNursesFragmentToNurseDetailFragment(fName, lName)
        findNavController().navigate(action)
    }
}