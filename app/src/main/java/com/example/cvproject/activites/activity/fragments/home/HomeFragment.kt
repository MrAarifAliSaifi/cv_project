package cvproject.blinkit.activites.activity.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.cvproject.activites.activity.adapters.HomeItemsAdapter
import com.example.cvproject.activites.activity.dataclass.ItemDataClass
import com.example.cvproject.activites.activity.utilities.Utils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.pixplicity.easyprefs.library.Prefs
import cvproject.blinkit.R
import cvproject.blinkit.databinding.FragmentHomeBinding
import java.io.IOException
import java.util.Locale
import kotlin.random.Random

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var adapter: HomeItemsAdapter
    private val itemList = mutableListOf<ItemDataClass>()
    private val filteredList = mutableListOf<ItemDataClass>()
    private lateinit var databaseReference: DatabaseReference
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1000

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding?.apply {

            setupRecyclerView()
            fetchItemsFromDatabase()

            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
            checkLocationPermission()

            swipeRefreshLayout.setOnRefreshListener {
                binding.swipeRefreshLayout.isRefreshing = true
                fetchItemsFromDatabase()
                Handler().postDelayed({
                    binding.swipeRefreshLayout.isRefreshing = false
                }, 4000)
                swipeRefreshLayout.setColorSchemeColors(
                    resources.getColor(R.color.red),
                    resources.getColor(R.color.yellow),
                    resources.getColor(R.color.green_1B7938),
                    resources.getColor(R.color.blue_037CBF)
                )
            }

            setupSearchView()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                requireActivity().window.statusBarColor =
                    ContextCompat.getColor(requireContext(), R.color.yellow_F7E0B4)
            }

            val time = Random.nextInt(10, 21)
            welcomeText.text = getString(R.string.minutes, time)
        }
    }

    private fun setupRecyclerView() {
        adapter = HomeItemsAdapter(requireContext(), filteredList)
        binding.recyclerView.adapter = adapter

        val staggeredGridLayoutManager =
            StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        binding.recyclerView.layoutManager = staggeredGridLayoutManager
        binding.recyclerView.isNestedScrollingEnabled = true
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                filterList(query)
                return true
            }
        })
    }

    private fun filterList(query: String?) {
        if (query.isNullOrEmpty()) {
            filteredList.clear()
            filteredList.addAll(itemList)
        } else {
            val searchQuery = query.lowercase()
            filteredList.clear()
            filteredList.addAll(itemList.filter {
                it.name!!.lowercase().contains(searchQuery)
            })
        }
        adapter.notifyDataSetChanged()
    }

    private fun fetchItemsFromDatabase() {
        itemList.clear()
        databaseReference = FirebaseDatabase.getInstance().getReference("BlinkitItems")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                itemList.clear()
                for (itemSnapshot in snapshot.children) {
                    val item = itemSnapshot.getValue(ItemDataClass::class.java)
                    if (item != null) {
                        itemList.add(item)
                        Log.e("TAG", "HomeFragment Fetched item: ${item.name}")
                    }
                }
                filteredList.clear()
                filteredList.addAll(itemList)
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Utils.showToast(requireActivity(), error.message)
            }
        })
    }


    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            getCurrentLocation()
        }
    }


    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val latitude = it.latitude
                    val longitude = it.longitude
                    getAddressFromLocation(latitude, longitude)
                } ?: run {
                    Utils.showToast(requireContext(), "Unable to get location")
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getCurrentLocation()
            } else {
                Utils.showToast(requireContext(), "Location permission denied")
            }
        }
    }

    private fun getAddressFromLocation(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0]
                val addressString = address.getAddressLine(0)
                binding.tvLocation.text = addressString
                Prefs.putString("location", addressString)
            } else {
                Utils.showToast(requireContext(), "No address found")
            }
        } catch (e: IOException) {
            Utils.showToast(requireContext(), e.localizedMessage)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}