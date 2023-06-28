package com.example.inventoryexpirytracker.staffdetails

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inventoryexpirytracker.R
import com.example.inventoryexpirytracker.checkhistory.CheckHistoryCheck
import com.example.inventoryexpirytracker.databinding.FragmentStaffDetailBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_staff_detail.rv_nursedetail
import kotlinx.android.synthetic.main.fragment_staff_detail.tv_first_name
import kotlinx.android.synthetic.main.fragment_staff_detail.tv_last_name
import kotlinx.android.synthetic.main.fragment_staff_detail.tv_user_name
import kotlinx.android.synthetic.main.staffcard.tv_account

class StaffDetailFragment : Fragment(R.layout.fragment_staff_detail), StaffDetailsAdapter.OnItemClickListener {

    private val args: StaffDetailFragmentArgs by navArgs()
    private lateinit var database: DatabaseReference
    private lateinit var checkList : ArrayList<CheckHistoryCheck>
    private lateinit var adapter : StaffDetailsAdapter
    private val listener = this
    private var compareusername = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //binding firebase data to textviews etc to display in the fragment
        val binding = FragmentStaffDetailBinding.bind(view)

        binding.apply {
            val thisNursefname : String = args.nursefname
            val thisNurselname : String = args.nurselname
            compareusername = thisNursefname.substring(0, 1).toLowerCase() + thisNurselname.toLowerCase()
            database = FirebaseDatabase.getInstance().getReference("App").child("staff")
            database.child(compareusername).get().addOnSuccessListener {
                val fName = it.child("fname").value
                val lName = it.child("lname").value
                val userName = it.child("username").value
                val account = it.child("account").value
                tv_first_name.text = fName.toString()
                tv_last_name.text = lName.toString()
                tv_user_name.text = userName.toString()
                tv_account.text = account.toString()
            }.addOnFailureListener {
                 Toast.makeText(context, "Failed", Toast.LENGTH_LONG).show()
            }
        }

        //pulls all checks from check history table that have been done by the nurse selected and puts them into recycler view
        //must stay in onViewCreated
        //reference to database
        database = Firebase.database.getReference("App").child("checkhistory")
        //event listener to database
        database.addValueEventListener(object: ValueEventListener {
            //real time pull of data function, not only on data change
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = ArrayList<CheckHistoryCheck>() //creating our list to add data to recycler view
                val children = snapshot!!.children
                //opening the firebase snapshot
                children.forEach {
                    //putting the children into variables
                    if (it.child("checkstaffuser").value.toString() == compareusername) {
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
                adapter = StaffDetailsAdapter(checkList, listener)

                // stops app from crashing when moving to new screen before data loads
                // stops the UI update from taking place
                if (this@StaffDetailFragment.isResumed){
                    rv_nursedetail.adapter = adapter
                    rv_nursedetail.layoutManager = LinearLayoutManager(context)
                    rv_nursedetail.setHasFixedSize(true)
                }
                database.removeEventListener(this)
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
            }
        })
    }
    override fun onItemClick(position: Int) {
        //if item is clicked in recycler view, navigate to check history details fragment
        val checkID = checkList[position].checkID
        val action = StaffDetailFragmentDirections.actionNurseDetailFragmentToCheckHistoryDetailsFragment(checkID)
        findNavController().navigate(action)
    }
}