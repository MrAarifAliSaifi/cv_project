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
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.example.basicmvvmapp.MainActivity
import com.example.cvproject.activites.activity.adapters.HomeItemsAdapter
import com.example.cvproject.activites.activity.bottomSheet.ItemListDialogFragment
import com.example.cvproject.activites.activity.constant.BlinkitConstants
import com.example.cvproject.activites.activity.dataBase.BlinkitDatabase
import com.example.cvproject.activites.activity.dataclass.ItemDataClass
import com.example.cvproject.activites.activity.utilities.Utils
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.firebase.database.FirebaseDatabase
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
    private val filteredList = mutableListOf<ItemDataClass>()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1000
    private val itemNameList = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        initializeViewModel()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel.fetchUserInfo()
        homeViewModel.fetchItemsFromDatabase()
        observeHomeListItems()
        setRandomDeliveryTime()
        setupSwipeRefreshLayout()
        setLocation()
        setStatusBarColor()
        setupUserDetail()
        Utils.callPolicyFunction()
    }

    private fun setLocation() {
        if (Prefs.getString(BlinkitConstants.LOCATION).isNotEmpty()) {
            binding.tvLocation.text = TextUtils.concat(
                Utils.styleStrings(getString(R.string.title_home)),
                " - ",
                Prefs.getString(BlinkitConstants.LOCATION)
            )
        } else {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
            checkLocationPermission()
        }
    }

    private fun initializeViewModel() {
        val database = BlinkitDatabase.getDatabase(requireContext()).blinkitDao()
        val firebaseDatabase = FirebaseDatabase.getInstance()
        homeViewModel = HomeViewModel(database, firebaseDatabase)
    }

    private fun observeHomeListItems() {
        homeViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) showLoading() else hideLoading()
        }
        homeViewModel.itemList.observe(viewLifecycleOwner) { items ->
            setupRecyclerView(items)
        }
    }

    private fun setRandomDeliveryTime() {
        val time = Random.nextInt(10, 21)
        binding.tvDeliveryTime.text = getString(R.string.minutes, time.toString())
    }

    private fun setupSwipeRefreshLayout() {
        binding.apply {
            swipeRefreshLayout.setOnRefreshListener {
                swipeRefreshLayout.isRefreshing = true
                observeHomeListItems()
                Handler().postDelayed({
                    swipeRefreshLayout.isRefreshing = false
                }, 4000)
                swipeRefreshLayout.setColorSchemeColors(
                    resources.getColor(R.color.red),
                    resources.getColor(R.color.yellow),
                    resources.getColor(R.color.green_1B7938),
                    resources.getColor(R.color.blue_037CBF)
                )
            }
        }
    }

    private fun setupRecyclerView(itemList: List<ItemDataClass>) {
        filteredList.clear()
        filteredList.addAll(itemList)
        itemNameList.add(0, "FREE DELIVERY FOR ORDER ABOVE 99")
        itemNameList.addAll(itemList.map { it.name!! })
        // Call the notification function
        val (title, body) = Utils.generateRandomGroceryNotification(itemNameList)
        Utils.scheduleRandomNotification(title, body)
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

            searchView.apply {
                setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        return false
                    }

                    override fun onQueryTextChange(query: String?): Boolean {
                        if (query.isNullOrEmpty()) {
                            filteredList.clear()
                            filteredList.addAll(itemList)
                        } else {
                            val searchQuery = query.lowercase()
                            filteredList.clear()
                            val results = itemList.filter {
                                it.name?.lowercase()?.contains(searchQuery) == true
                            }
                            filteredList.addAll(results)
                        }
                        adapter.notifyDataSetChanged()
                        return true
                    }
                })

                setOnSearchClickListener {
                    (context as MainActivity).hideBottomNavAndCart()
                }
                setOnCloseListener {
                    (context as MainActivity).showBottomNavAndCart()
                    false
                }
            }
        }
    }

    fun checkLocationPermission() {
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

    private fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            requireActivity().window.statusBarColor =
                ContextCompat.getColor(requireContext(), R.color.yellow_F7E0B4)
        }
    }

    private fun turnOnLocation() {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 10000
            fastestInterval = 5000
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
                homeViewModel.insertAddress(addressString)
                homeViewModel.setCurrentAddress(addressString)
            } else {
                showNoAddressFoundDialog()
            }
        } catch (e: IOException) {
            showNoAddressFoundDialog()
            Log.e("TAG", e.localizedMessage)
        }
    }

    private fun showNoAddressFoundDialog() {
        if (!isAddressSet()) {
            val bottomSheet = ItemListDialogFragment(homeViewModel)
            bottomSheet.show(childFragmentManager, bottomSheet.tag)
        }
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
        binding.tvLocation.text = TextUtils.concat(
            Utils.styleStrings(getString(R.string.title_home)), " - ", location
        )
    }

    private fun isAddressSet(): Boolean {
        return homeViewModel.getCurrentAddress() != null
    }

    private fun showLoading() {
        binding.apply {
            (context as MainActivity).hideBottomNavAndCart()
            shimmerLayout.visibility = View.VISIBLE
            shimmerLayout.startShimmer()
            appBarLayout.visibility = View.GONE
            tvBestseller.visibility = View.GONE
        }
    }

    private fun hideLoading() {
        binding.apply {
            (context as MainActivity).showBottomNavAndCart()
            shimmerLayout.stopShimmer()
            shimmerLayout.visibility = View.GONE
            appBarLayout.visibility = View.VISIBLE
            tvBestseller.visibility = View.VISIBLE
        }
    }

    private fun setupUserDetail() {
        homeViewModel.userInfo.observe(viewLifecycleOwner) { data ->
            if (data != null) {
                binding.blinkitIn.text = data.name
                Glide.with(binding.circleIv.context).load(data.imageUri).into(binding.circleIv)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
