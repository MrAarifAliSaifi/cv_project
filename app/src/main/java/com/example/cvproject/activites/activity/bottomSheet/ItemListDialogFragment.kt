package com.example.cvproject.activites.activity.bottomSheet

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cvproject.activites.activity.dataBase.SavedAddresses
import com.example.cvproject.activites.activity.utilities.Utils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import cvproject.blinkit.R
import cvproject.blinkit.activites.activity.ui.home.HomeFragment
import cvproject.blinkit.activites.activity.ui.home.HomeViewModel
import cvproject.blinkit.databinding.FragmentItemListDialogListDialogBinding

class ItemListDialogFragment(private val homeViewModel: HomeViewModel) :
    BottomSheetDialogFragment() {

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
        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let { sheet ->
                val behavior = BottomSheetBehavior.from(sheet)
                behavior.isDraggable = false
            }
        }
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            homeViewModel.fetchAllSavedAddress()
            buttonRetry.setOnClickListener {
                val parentFragment = parentFragment as? HomeFragment
                parentFragment?.let {
                    it.checkLocationPermission()
                }
                dismiss()
            }
            setUpRecyclerView()

            btnAddManualAddress.setOnClickListener {
                if (editTextLocation.text.toString().trim().isEmpty()) {
                    Utils.showToast(
                        requireContext(), getString(R.string.please_enter_location_first)
                    )
                } else {
                    btnAddManualAddress.visibility = View.INVISIBLE
                    progressBar.visibility = View.VISIBLE
                    val enteredAddress = editTextLocation.text.toString().trim()
                    homeViewModel.insertAddress(enteredAddress)
                    homeViewModel.setCurrentAddress(enteredAddress)
                    updateTextAtHomeFragment(enteredAddress)
                }
            }
        }
    }

    class AddressAdapter(
        private val addressList: List<SavedAddresses>,
        private val itemClickListener: (String) -> Unit
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
            val item = addressList[position]
            holder.nameTextView.text = item.address
            holder.itemView.setOnClickListener {
                itemClickListener(item.address!!)
            }
        }

        override fun getItemCount(): Int = addressList.size
    }

    private fun setUpRecyclerView() {
        binding.apply {
            homeViewModel.saveAddress.observe(viewLifecycleOwner) { addressList ->
                if (addressList.isNotEmpty()) {
                    hideFieldsToEnterAddress()
                    recyclerView.layoutManager = LinearLayoutManager(requireContext())
                    recyclerView.adapter = AddressAdapter(addressList) { address ->
                        homeViewModel.insertAddress(address)
                        homeViewModel.setCurrentAddress(address)
                        updateTextAtHomeFragment(address)
                    }
                } else {
                    showFieldsToEnterAddress()
                }
            }
        }
    }

    private fun showFieldsToEnterAddress() {
        binding.apply {
            textInputLayout.visibility = View.VISIBLE
            btnAddManualAddress.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            tvSavedAddresses.visibility = View.GONE
        }
    }

    private fun hideFieldsToEnterAddress() {
        binding.apply {
            textInputLayout.visibility = View.GONE
            btnAddManualAddress.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            tvSavedAddresses.visibility = View.VISIBLE
        }
    }

    private fun updateTextAtHomeFragment(address: String) {
        val parentFragment = parentFragment as? HomeFragment
        parentFragment?.let {
            it.updateLocationText(address)
        }
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}