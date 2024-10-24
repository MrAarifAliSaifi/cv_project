package cvproject.blinkit.activites.activity.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.cvproject.activites.activity.adapters.CategoryAdapter
import com.example.cvproject.activites.activity.adapters.ItemAdapter
import cvproject.blinkit.databinding.FragmentCategoryBinding

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
        itemAdapter = ItemAdapter(requireContext(), emptyList())
    }

    private fun setUpRecyclerView() {
        binding.recyclerViewCategory.apply {
            adapter = categoryAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        binding.recyclerViewItem.apply {
            adapter = itemAdapter
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
    }

    private fun observeViewModel() {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}