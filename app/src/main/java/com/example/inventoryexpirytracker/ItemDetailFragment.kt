package com.example.inventoryexpirytracker

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.inventoryexpirytracker.databinding.FragmentItemDetailBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_item_detail.tv_item_name
import kotlinx.android.synthetic.main.fragment_item_detail.tv_id
import kotlinx.android.synthetic.main.fragment_item_detail.tv_location
import kotlinx.android.synthetic.main.fragment_item_detail.tv_status
import kotlinx.android.synthetic.main.fragment_item_detail.tv_type

class ItemDetailFragment : Fragment(R.layout.fragment_item_detail) {

    private val args: ItemDetailFragmentArgs by navArgs()
    private lateinit var database : DatabaseReference

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //binding of data from firebase to variables and text views to display in fragment
        val binding = FragmentItemDetailBinding.bind(view)
        binding.apply {
            val thisItem : String = args.drug
            database = FirebaseDatabase.getInstance().getReference("App").child("item")
            database.child(thisItem).get().addOnSuccessListener {
                    val itemid = it.child("itemid").value
                    val name = it.child("itemname").value
                    val type = it.child("itemtype").value
                    val location = it.child("itemlocation").value
                    val status = it.child("itemstatus").value
                    tv_id.text = itemid.toString()
                    tv_item_name.text = name.toString()
                    tv_type.text = type.toString()
                    tv_location.text = location.toString()
                    tv_status.text = status.toString()
                }.addOnFailureListener {
                    Toast.makeText(context, "Failed", Toast.LENGTH_LONG).show()
                }
        }
    }
}
