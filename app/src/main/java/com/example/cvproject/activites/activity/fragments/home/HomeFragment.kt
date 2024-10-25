package cvproject.blinkit.activites.activity.ui.home

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.basicmvvmapp.MainActivity
import com.example.cvproject.activites.activity.adapters.HomeItemsAdapter
import com.example.cvproject.activites.activity.bottomSheet.ItemListDialogFragment
import com.example.cvproject.activites.activity.constant.BlinkitConstants
import com.example.cvproject.activites.activity.dataBase.BlinkitDatabase
import com.example.cvproject.activites.activity.dataclass.ItemDataClass
import com.example.cvproject.activites.activity.utilities.Utils
import com.example.cvproject.activites.activity.viewmodeles.MainActivityVM
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
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
    private val viewModel: MainActivityVM by activityViewModels()
    private lateinit var adapter: HomeItemsAdapter
    private val itemList = mutableListOf<ItemDataClass>()
    private val filteredList = mutableListOf<ItemDataClass>()
    private lateinit var databaseReference: DatabaseReference
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1000

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val database = BlinkitDatabase.getDatabase(requireContext())
        val homePageItemsDao = database.blinkitDao()
//        homeViewModel = HomeViewModel(homePageItemsDao)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding?.apply {

            setupRecyclerView()
            fetchItemsFromDatabase()
            setupUserdetail()

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
            tvDeliveryTime.text = getString(R.string.minutes, time)
        }
    }

    private fun setupRecyclerView() {
        binding.apply {
            adapter = HomeItemsAdapter(requireContext(), filteredList, homeViewModel)
            recyclerView.adapter = adapter

            val staggeredGridLayoutManager =
                StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
            recyclerView.layoutManager = staggeredGridLayoutManager
            recyclerView.isNestedScrollingEnabled = true

            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0) {
                        // Scrolling down
                        (context as MainActivity).hideBottomNavAndCart()
                    } else if (dy < 0) {
                        (context as MainActivity).showBottomNavAndCart()
                        // Scrolling up
                    }
                }
            })
        }
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
            turnOnLocation()
        }
    }

    private fun turnOnLocation() {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 10000 // 10 seconds
            fastestInterval = 5000 // 5 seconds
        }

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
            .setAlwaysShow(true)

        val settingsClient = LocationServices.getSettingsClient(requireActivity())
        val task = settingsClient.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            // Location settings are already satisfied, you can get the current location
            getCurrentLocation()
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    exception.startResolutionForResult(
                        requireActivity(), LOCATION_PERMISSION_REQUEST_CODE
                    )
                    showNoAddressFoundDialog()
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.e("TAG", "Error turning on location: ${sendEx.localizedMessage}")
                }
            }
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
                    showNoAddressFoundDialog()
                    Log.e("TAG", "Unable to get location")
                }
            }.addOnFailureListener { e ->
                showNoAddressFoundDialog()
                Log.e("TAG", "Failed to get location: ${e.message}")
            }
        } else {
            showNoAddressFoundDialog()
        }
    }

    private fun getAddressFromLocation(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val addressString = address.getAddressLine(0)
                binding.tvLocation.text = TextUtils.concat(
                    Utils.styleStrings(getString(R.string.title_home)), " - ", addressString
                )
                Prefs.putString(BlinkitConstants.LOCATION, addressString)
            } else {
                showNoAddressFoundDialog()
            }
        } catch (e: IOException) {
            showNoAddressFoundDialog()
            Log.e("TAG", e.localizedMessage)
        }
    }

    private fun showNoAddressFoundDialog() {
        val bottomSheet = ItemListDialogFragment()
        bottomSheet.show(childFragmentManager, bottomSheet.tag)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                turnOnLocation()
            } else {
                showNoAddressFoundDialog()
                Log.e("TAG", "Location permission denied")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                getCurrentLocation()
            } else {
                showNoAddressFoundDialog()
                Log.e("TAG", "Location services are not enabled")
            }
        }
    }

    fun updateLocationText(location: String) {
        binding.tvLocation.text = location
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupUserdetail(){
        viewModel.userInfo.observe(viewLifecycleOwner) { data ->
            if (data != null) {
                Toast.makeText(requireContext(), "${data.name}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
