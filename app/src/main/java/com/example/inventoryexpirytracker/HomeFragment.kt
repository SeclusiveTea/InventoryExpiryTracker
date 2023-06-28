package com.example.inventoryexpirytracker

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.fragment_home.btn_location3
import kotlinx.android.synthetic.main.fragment_home.btn_location1
import kotlinx.android.synthetic.main.fragment_home.btn_location2


class HomeFragment : Fragment(R.layout.fragment_home) {

    private val args : HomeFragmentArgs by navArgs()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity?)!!.supportActionBar!!.show()

        btn_location1.setOnClickListener {

            val label = "Location 1"

            //navigate to Inventory Fragment filter by Knox Wing Safe
            val action = HomeFragmentDirections.actionHomeFragmentToInventoryFragment(label, args.firstname, args.lastname)
            findNavController().navigate(action)
        }
        btn_location2.setOnClickListener {

            val label = "Location 2"

            //navigate to Inventory Fragment filter by Ward Office Trolley
            val action = HomeFragmentDirections.actionHomeFragmentToInventoryFragment(label, args.firstname, args.lastname)
            findNavController().navigate(action)
        }
        btn_location3.setOnClickListener {

            val label = "Location 3"

            //navigate to Inventory Fragment filter by Day Stay Station
            val action = HomeFragmentDirections.actionHomeFragmentToInventoryFragment(label, args.firstname, args.lastname)
            findNavController().navigate(action)
        }

    }
}