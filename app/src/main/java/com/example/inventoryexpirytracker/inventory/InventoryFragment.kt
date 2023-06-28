package com.example.inventoryexpirytracker.inventory

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionManager
import com.example.inventoryexpirytracker.MainActivity
import com.example.inventoryexpirytracker.R
import com.example.inventoryexpirytracker.data.TempTable
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_inventory.*

class InventoryFragment : Fragment(R.layout.fragment_inventory), InventoryAdapter.OnItemClickListener {

    private val args: InventoryFragmentArgs by navArgs()
    private lateinit var databaseItem : DatabaseReference
    private lateinit var databaseTempTable : DatabaseReference
    private lateinit var inventoryList : ArrayList<InventoryItem>
    private lateinit var adapter : InventoryAdapter
    private val listener = this

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


        //sets the text of the button depending on location
        (activity as MainActivity).supportActionBar?.title = args.label
        if (args.label == "Location 1") {
            btn_removed.text = "Removed"
        } else {
            btn_removed.text = "Disposed"
        }

        //filter tab "Current inventory"
        //This button is clicked on create
        btn_allitems.setOnClickListener {

            //animation code
            moveHighlightedTabToButton(btn_allitems)

            //pulls all drugs from drug table by location that are valid or expiring and puts them into recycler view
            //must stay in onViewCreated
            databaseItem = Firebase.database.getReference("App").child("item")
            //event listener to database
            databaseItem.addValueEventListener(object: ValueEventListener {
                //real time pull of data function, not only on data change
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list =
                        ArrayList<InventoryItem>() //creating our list to add data to recycler view
                    val children = snapshot!!.children
                    //opening the firebase snapshot
                    children.forEach {
                        if(it.child("itemlocation").value.toString() == args.label && (it.child("itemstatus").value.toString() == "Valid" || it.child("itemstatus").value.toString() == "Expiring")) {
                            //putting the children into variables
                            val itemid = it.child("itemid").value
                            val name = it.child("itemname").value
                            val status = it.child("itemstatus").value
                            //if type is cans display a red rectangle, if simple display a blue rectangle and add to list
                            if (it.child("itemtype").value.toString() == "Cans") {
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
                    inventoryList = list
                    adapter = InventoryAdapter(inventoryList, listener)
                    // stops app from crashing when moving to new screen before data loads
                    // stops the UI update from taking place
                    if (this@InventoryFragment.isResumed) {
                        rv_inventory.adapter = adapter
                        rv_inventory.layoutManager = LinearLayoutManager(context)
                        rv_inventory.setHasFixedSize(true)
                    }
                    // detach listener
                    databaseItem.removeEventListener(this)
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
                }
            })
        }
        btn_allitems.callOnClick()

        //filter tab "Expiring"
        btn_expiring.setOnClickListener {

            //animation code
            moveHighlightedTabToButton(btn_expiring)

            //pulls all drugs from drug table by location that are expiring and puts them into recycler view
            //must stay in onViewCreated
            databaseItem = Firebase.database.getReference("App").child("item")
            //event listener to database
            databaseItem.addValueEventListener(object: ValueEventListener {
                //real time pull of data function, not only on data change
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list =
                        ArrayList<InventoryItem>() //creating our list to add data to recycler view
                    val children = snapshot!!.children
                    //opening the firebase snapshot
                    children.forEach {
                        if(it.child("itemlocation").value.toString() == args.label) {
                            if (it.child("itemstatus").value.toString() == "Expiring") {
                                //putting the children into variables
                                val itemid = it.child("itemid").value
                                val name = it.child("itemname").value
                                val status = it.child("itemstatus").value
                                //if type is cans display a red rectangle, if simple display a blue rectangle and add to list
                                if (it.child("itemtype").value.toString() == "Cans") {
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
                    inventoryList = list
                    adapter = InventoryAdapter(inventoryList, listener)
                    // stops app from crashing when moving to new screen before data loads
                    // stops the UI update from taking place
                    if (this@InventoryFragment.isResumed) {
                        rv_inventory.adapter = adapter
                        rv_inventory.layoutManager = LinearLayoutManager(context)
                        rv_inventory.setHasFixedSize(true)
                    }
                    // detach listener
                    databaseItem.removeEventListener(this)
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
                }
            })
        }

        btn_removed.setOnClickListener {

            //animation code
            moveHighlightedTabToButton(btn_removed)

            //pulls all drugs from drug table by location that are removed or expired and puts them into recycler view
            //must stay in onViewCreated
            databaseItem = Firebase.database.getReference("App").child("item")
            //event listener to database
            databaseItem.addValueEventListener(object: ValueEventListener {
                //real time pull of data function, not only on data change
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list =
                        ArrayList<InventoryItem>() //creating our list to add data to recycler view
                    val children = snapshot!!.children
                    //opening the firebase snapshot
                    children.forEach {
                        if(it.child("itemlocation").value.toString() == args.label) {
                            if (it.child("itemstatus").value.toString() == "Removed" || it.child("itemstatus").value.toString() == "Disposed") {
                                //putting the children into variables
                                val itemid = it.child("itemid").value
                                val name = it.child("itemname").value
                                val status = it.child("itemstatus").value
                                //if type is cans display a red rectangle, if simple display a blue rectangle and add to list
                                if (it.child("itemtype").value.toString() == "Cans") {
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
                    inventoryList = list
                    adapter = InventoryAdapter(inventoryList, listener)
                    // stops app from crashing when moving to new screen before data loads
                    // stops the UI update from taking place
                    if (this@InventoryFragment.isResumed) {
                        rv_inventory.adapter = adapter
                        rv_inventory.layoutManager = LinearLayoutManager(context)
                        rv_inventory.setHasFixedSize(true)
                    }
                    // detach listener
                    databaseItem.removeEventListener(this)
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
                }
            })
        }

        //floating action button to goes to add check fragment
        fab_adddrug.setOnClickListener {

            //function to generate the temp table ready to add a check
            GenerateTempTable()

            //short pause to prevent issues with firebase
            Thread.sleep(500)

            //navigate to add check fragment
            val action = InventoryFragmentDirections.actionInventoryFragmentToAddCheckFragment(args.label, args.firstname, args.lastname)
            findNavController().navigate(action)
            Log.i("mytag", "nav from inventory to add check")
        }
        setHasOptionsMenu(true);
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.options_inventory, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.addItemFragment -> {
                findNavController().navigate(InventoryFragmentDirections.actionInventoryFragmentToAddDrugFragment(args.label))
                true
            }
            R.id.checkHistoryFragment -> {
                findNavController().navigate(InventoryFragmentDirections.actionInventoryFragmentToCheckHistoryFragment(args.label, args.firstname, args.lastname))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onItemClick(position: Int) {
        //navigates to the drug detail fragment if an item in the recycler view is clicked
        val drug = inventoryList[position].itemId
        val action = InventoryFragmentDirections.actionInventoryFragmentToDrugDetailFragment(drug)
        findNavController().navigate(action)
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

    fun GenerateTempTable() {
        databaseItem = FirebaseDatabase.getInstance().getReference("App").child("item")
        databaseItem.addValueEventListener(object: ValueEventListener {

            //real time pull of data function, not only on data change
            override fun onDataChange(snapshot: DataSnapshot) {
                val children = snapshot!!.children
                //opening the firebase snapshot
                children.forEach {

                    if(it.child("itemlocation").value.toString() == args.label && (it.child("itemstatus").value.toString() == "Valid" || it.child("itemstatus").value.toString() == "Expiring")) {
                        val id = it.child("itemid").value
                        val name = it.child("itemname").value
                        val type = it.child("itemtype").value
                        val location = it.child("itemlocation").value
                        val status = it.child("itemstatus").value
                        val checkID = "0"
                        val temp = TempTable(id.toString(), name.toString(), type.toString(), location.toString(), status.toString(), checkID)
                        databaseTempTable = FirebaseDatabase.getInstance().getReference("App").child("temptable")
                        databaseTempTable.child(id.toString()).setValue(temp)
                        Log.i("mytag", "Temp table generates")

                    }
                }
                // detach listener
                databaseItem.removeEventListener(this)
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
            }
        })
    }
}