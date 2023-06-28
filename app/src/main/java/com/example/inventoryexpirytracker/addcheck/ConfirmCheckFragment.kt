package com.example.inventoryexpirytracker.addcheck

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.inventoryexpirytracker.R
import com.example.inventoryexpirytracker.data.Check
import com.example.inventoryexpirytracker.data.Item
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_confirm.*
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Base64
import java.util.Date

class ConfirmCheckFragment : Fragment(R.layout.fragment_confirm) {

    private val args: ConfirmCheckFragmentArgs by navArgs()
    private lateinit var databaseItem: DatabaseReference
    private lateinit var databaseCheckHistory: DatabaseReference
    private lateinit var databaseTempTable: DatabaseReference

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_clear_signature.setOnClickListener {
            signature_view.clearCanvas()
        }

        btn_confirm.setOnClickListener {

            //check that signature isn't empty
            if (!signature_view.isBitmapEmpty){

                AddCheckToFirebase()
                //short pause to prevent issues with firebase
                Thread.sleep(500)

                UpdateItemTable()

                //short pause to prevent issues with firebase
                Thread.sleep(500)

                //navigate to check history fragment
                val action =
                    ConfirmCheckFragmentDirections.actionConfirmCheckFragmentToCheckHistoryFragment(
                        args.location, args.firstname, args.lastname
                        )
                findNavController().navigate(action)
            } else {
                //displays this toast if the signature is empty.
                Toast.makeText(context, "Signature must not be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun AddCheckToFirebase() {
        // filler byte to initialise array
        //https://medium.com/@reddytintaya/note-1-android-bitmap-to-base64-string-with-kotlin-552890c56b04
        // converting signature to base64 to store in firebase, and retrieve to rebuild in check history
        var sigByteArray = ByteArrayOutputStream(0x48)
        signature_view.signatureBitmap.compress(Bitmap.CompressFormat.JPEG, 100, sigByteArray)
        val sbg = sigByteArray.toByteArray()


        //collate check data into variables
        val signatureB64 = Base64.getEncoder().encodeToString(sbg)
        val checkid = args.checkid
        val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy")
        val date = simpleDateFormat.format(Date())
        val staffuser = args.firstname[0].toLowerCase() + args.lastname.toLowerCase()
        val stafffname = args.firstname
        val stafflname = args.lastname
        val location = args.location

        //build check object to store in firebase check history table
        val check = Check(checkid, date, staffuser, stafffname, stafflname, location, signatureB64)

        //save check history object to firebase
        databaseCheckHistory =
            FirebaseDatabase.getInstance().getReference("App").child("checkhistory")
        databaseCheckHistory.child(checkid).setValue(check)
    }
    fun UpdateItemTable() {
        //update the item table with the status changes that were made during the check that updated the temporary table
        databaseTempTable =
            FirebaseDatabase.getInstance().getReference("App").child("temptable")
        databaseTempTable.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val children = snapshot!!.children
                children.forEach {

                    val tempid = it.child("tempid").value.toString()
                    val name = it.child("tempname").value.toString()
                    val type = it.child("temptype").value.toString()
                    val location = it.child("templocation").value.toString()
                    val status = it.child("tempstatus").value.toString()
                    val item = Item(tempid, name, type, location, status)

                    databaseItem = FirebaseDatabase.getInstance().getReference("App").child("drug")
                    databaseItem.child(tempid).setValue(item)

                }
                //detach listener
                databaseTempTable.removeEventListener(this)
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
            }
        })
    }
}