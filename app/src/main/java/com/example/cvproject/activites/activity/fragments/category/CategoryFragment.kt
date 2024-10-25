package cvproject.blinkit.activites.activity.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.basicmvvmapp.MainActivity
import com.example.cvproject.activites.activity.adapters.CategoryAdapter
import com.example.cvproject.activites.activity.adapters.ItemAdapter
import com.example.cvproject.activites.activity.dataBase.BlinkitDatabase
import com.example.cvproject.activites.activity.dataBase.HomeItems
import cvproject.blinkit.databinding.FragmentCategoryBinding
import kotlinx.coroutines.launch

class CategoryFragment : Fragment() {

    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var itemAdapter: ItemAdapter
    private lateinit var categoryViewModel: CategoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        categoryViewModel = ViewModelProvider(this)[CategoryViewModel::class.java]
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeAdapter()
        setUpRecyclerView()
        observeViewModel()
    }

    private fun initializeAdapter() {
        categoryAdapter = CategoryAdapter(requireContext(), emptyList()) { selectedCategory ->
            categoryViewModel.filterItemsByCategory(selectedCategory)
        }
        itemAdapter = ItemAdapter(this, emptyList())
    }

    private fun setUpRecyclerView() {
        binding.apply {
            recyclerViewCategory.apply {
                adapter = categoryAdapter
                layoutManager = LinearLayoutManager(requireContext())
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
            recyclerViewItem.apply {
                adapter = itemAdapter
                layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
    }

    private fun observeViewModel() {

        categoryViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) showLoading() else hideLoading()
        }

        categoryViewModel.categoryItems.observe(viewLifecycleOwner) { categories ->
            categoryAdapter.updateCategories(categories)
            if (categories.isNotEmpty()) {
                categoryAdapter.selectItem(0)
                categoryViewModel.filterItemsByCategory(categories[0])
            }
        }

        categoryViewModel.filteredItems.observe(viewLifecycleOwner) { filteredItems ->
            Log.d("CategoryFragment", "Filtered items count: ${filteredItems.size}")
            itemAdapter.updateItems(filteredItems)
        }
    }

    fun insertItemToDb(itemId: String) {
        val database = BlinkitDatabase.getDatabase(requireContext()).blinkitDao()
        lifecycleScope.launch {
            database.insertItemUrl(HomeItems(itemIdGeneratedFromFirebase = itemId))
        }
    }

    private fun showLoading() {
        binding.apply {
            shimmerLayout.visibility = View.VISIBLE
            shimmerLayout.startShimmer()
            recyclerViewCategory.visibility = View.GONE
            recyclerViewItem.visibility = View.GONE
        }
    }

    private fun hideLoading() {
        binding.apply {
            shimmerLayout.stopShimmer()
            shimmerLayout.visibility = View.GONE
            recyclerViewCategory.visibility = View.VISIBLE
            recyclerViewItem.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}