package com.example.inventoryexpirytracker.checkhistorydetails

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionManager
import com.example.inventoryexpirytracker.R

import com.example.inventoryexpirytracker.databinding.FragmentCheckHistoryDetailsBinding
import com.example.inventoryexpirytracker.inventory.InventoryItem

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_check_history_details.*

import kotlinx.android.synthetic.main.fragment_inventory.btn_allitems
import kotlinx.android.synthetic.main.fragment_inventory.btn_expiring
import java.util.*
import kotlin.collections.ArrayList


class CheckHistoryDetailsFragment : Fragment(R.layout.fragment_check_history_details) {

    private val args: CheckHistoryDetailsFragmentArgs by navArgs()
    private lateinit var databaseCheckHistory: DatabaseReference
    private lateinit var databaseCheckItem: DatabaseReference
    private lateinit var itemList: ArrayList<InventoryItem>
    private lateinit var adapter: CheckHistoryDetailsAdapter

    private lateinit var highlightedTab: View
    private var highlightedTabBottomConstraint: Int = 0
    private var highlightedTabStartConstraint: Int = 0
    private var highlightedTabEndConstraint: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //code for xml animations
        // Initialize the highlightedTab view
        highlightedTab = view.findViewById(R.id.highlightedTab)

        // Store the initial constraints of the highlightedTab view
        highlightedTabBottomConstraint = (highlightedTab.layoutParams as ConstraintLayout.LayoutParams).bottomToBottom
        highlightedTabStartConstraint = (highlightedTab.layoutParams as ConstraintLayout.LayoutParams).startToStart
        highlightedTabEndConstraint = (highlightedTab.layoutParams as ConstraintLayout.LayoutParams).endToEnd

        //binding firebase data to variables to display in fragment
        val binding = FragmentCheckHistoryDetailsBinding.bind(view)

        binding.apply {

            val thisCheck : String = args.checkID
            databaseCheckHistory = FirebaseDatabase.getInstance().getReference("App").child("checkhistory")
            databaseCheckHistory.child(thisCheck).get().addOnSuccessListener {

                // retrieve and convert signature to bitmap
                fun retrieveSignature(sig: String):Bitmap {
                    // https://stackoverflow.com/questions/58955434/how-to-convert-base64-string-into-image-in-kotlin-android
                    val imageBytes = Base64.getDecoder().decode(sig)
                    val image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    return image
                }

                val checkID = it.child("checkid").value
                val date = it.child("checkdate").value
                val location = it.child("checklocation").value
                val stafffname = it.child("checkstafffname").value
                val stafflname = it.child("checkstafflname").value
                val staff = stafffname.toString() + " " + stafflname.toString()
                val signatureB64 = it.child("checkSignature").value.toString()
                val signature = retrieveSignature(signatureB64)
                tv_check_id.text = checkID.toString()
                tv_date.text = date.toString()
                tv_nurse.text = staff
                tv_location.text = location.toString()
                iv_signature.setImageBitmap(signature)
            }.addOnFailureListener {
                Toast.makeText(context, "Failed", Toast.LENGTH_LONG).show()
            }
        }

        //filter tab "All Items"
        //This button is clicked on create
        btn_allitems.setOnClickListener {

            //animation code
            moveHighlightedTabToButton(btn_allitems)

            //pulls data from check item table and puts into recycler view
            //must stay in onViewCreated
            databaseCheckItem = Firebase.database.getReference("App").child("checkitem")
            //event listener to database
            databaseCheckItem.addValueEventListener(object: ValueEventListener {
                //real time pull of data function, not only on data change
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list =
                        ArrayList<InventoryItem>() //creating our list to add data to recycler view
                    val children = snapshot!!.children
                    //opening the firebase snapshot
                    children.forEach {
                            //putting the children into variables
                        if (it.child("checkitemcheckid").value.toString() == args.checkID) {
                            val itemid = it.child("checkitemitemid").value
                            val name = it.child("checkitemname").value
                            val status = it.child("checkitemstatus").value
                            //if type is controlled display a red rectangle, if simple display a blue rectangle and add to list
                            if (it.child("checkitemtype").value.toString() == "Controlled") {
                                list.add(
                                    InventoryItem(
                                        R.drawable.redrectangle,
                                        itemid.toString(),
                                        name.toString(),
                                        status.toString()
                                    )
                                )
                            } else {
                                list.add(
                                    InventoryItem(
                                        R.drawable.bluerectangle,
                                        itemid.toString(),
                                        name.toString(),
                                        status.toString()
                                    )
                                )
                            }
                        }
                    }//sending the compiled list to the recycler view.
                    itemList = list
                    adapter = CheckHistoryDetailsAdapter(itemList)
                    // stops app from crashing when moving to new screen before data loads
                    // stops the UI update from taking place
                    if (this@CheckHistoryDetailsFragment.isResumed) {
                        rv_checkhistorydetail.adapter = adapter
                        rv_checkhistorydetail.layoutManager = LinearLayoutManager(context)
                        rv_checkhistorydetail.setHasFixedSize(true)
                    }
                    // detach listener
                    databaseCheckItem.removeEventListener(this)
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
                }
            })
        }
        btn_allitems.callOnClick()

        //filter tab "Expiring Items"
        btn_expiring.setOnClickListener {

            //animation code
            moveHighlightedTabToButton(btn_expiring)

            //pulls data from check items table and puts into recycler view
            //must stay in onViewCreated
            databaseCheckItem = Firebase.database.getReference("App").child("checkitem")
            //event listener to database
            databaseCheckItem.addValueEventListener(object: ValueEventListener {
                //real time pull of data function, not only on data change
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list =
                        ArrayList<InventoryItem>() //creating our list to add data to recycler view
                    val children = snapshot!!.children
                    //opening the firebase snapshot
                    children.forEach {
                        if (it.child("checkitemcheckid").value.toString() == args.checkID) {
                            if (it.child("checkitemstatus").value.toString() == "Expiring") {
                                //putting the children into variables
                                val itemid = it.child("checkitemitemid").value
                                val name = it.child("checkitemname").value
                                val status = it.child("checkitemstatus").value
                                //if type is cans display a red rectangle, if simple display a blue rectangle and add to list
                                if (it.child("checkitemtype").value.toString() == "Cans") {
                                    list.add(
                                        InventoryItem(
                                            R.drawable.redrectangle,
                                            itemid.toString(),
                                            name.toString(),
                                            status.toString()
                                        )
                                    )
                                } else {
                                    list.add(
                                        InventoryItem(
                                            R.drawable.bluerectangle,
                                            itemid.toString(),
                                            name.toString(),
                                            status.toString()
                                        )
                                    )
                                }
                            }
                        }
                    }//sending the compiled list to the recycler view.
                    itemList = list
                    adapter = CheckHistoryDetailsAdapter(itemList)
                    // stops app from crashing when moving to new screen before data loads
                    // stops the UI update from taking place
                    if (this@CheckHistoryDetailsFragment.isResumed) {
                        rv_checkhistorydetail.adapter = adapter
                        rv_checkhistorydetail.layoutManager = LinearLayoutManager(context)
                        rv_checkhistorydetail.setHasFixedSize(true)
                    }
                    // detach listener
                    databaseCheckItem.removeEventListener(this)
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
    private fun moveHighlightedTabToButton(button: Button) {
        // Get the constraints of the parent layout
        val parentLayout = highlightedTab.parent as ConstraintLayout
        val constraints = ConstraintSet()
        constraints.clone(parentLayout)

        // Update the constraints of the highlightedTab view to match the clicked button
        constraints.connect(highlightedTab.id, ConstraintSet.BOTTOM, button.id, ConstraintSet.BOTTOM)
        constraints.connect(highlightedTab.id, ConstraintSet.START, button.id, ConstraintSet.START)
        constraints.connect(highlightedTab.id, ConstraintSet.END, button.id, ConstraintSet.END)

        // Apply the updated constraints with animation transition
        TransitionManager.beginDelayedTransition(parentLayout)
        constraints.applyTo(parentLayout)
    }
}