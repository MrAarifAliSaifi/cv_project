package com.example.cvproject.activites.activity.bottomSheet

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cvproject.activites.activity.constant.BlinkitConstants
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pixplicity.easyprefs.library.Prefs
import cvproject.blinkit.R
import cvproject.blinkit.activites.activity.ui.home.HomeFragment
import cvproject.blinkit.databinding.FragmentItemListDialogListDialogBinding

class ItemListDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentItemListDialogListDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentItemListDialogListDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            buttonRetry.setOnClickListener {

            }
            val addressList = listOf(Prefs.getString("location"))
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = AddressAdapter(addressList) { address ->
                val parentFragment = parentFragment as? HomeFragment
                parentFragment?.let {
                    it.updateLocationText(address)
                    Prefs.putString(BlinkitConstants.LOCATION, address)
                }
                dismiss()
            }
        }
    }

    class AddressAdapter(
        private val addressList: List<String>, private val itemClickListener: (String) -> Unit
    ) : RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {

        inner class AddressViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val nameTextView: TextView = view.findViewById(R.id.text_view_address)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_location, parent, false)
            return AddressViewHolder(view)
        }

        override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
            val address = addressList[position]
            holder.nameTextView.text = address
            holder.itemView.setOnClickListener {
                itemClickListener(address)
            }
        }

        override fun getItemCount(): Int = addressList.size
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}