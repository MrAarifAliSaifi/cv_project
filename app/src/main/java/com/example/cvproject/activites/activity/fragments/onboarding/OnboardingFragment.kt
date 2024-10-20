package com.example.cvproject.activites.activity.fragments.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.cvproject.activites.activity.dataclass.OnboardingItem
import cvproject.blinkit.R
import cvproject.blinkit.databinding.FragmentHomeBinding
import cvproject.blinkit.databinding.FragmentOnboardingBinding

class OnboardingFragment : Fragment() {
    private lateinit var _binding: FragmentOnboardingBinding
    companion object {
        const val TAG = "OnboardingFragment"
        private const val EXTRA_CAROUSEL_DATA = "EXTRA_CAROUSEL_DATA"
        fun newInstance(carouselData: OnboardingItem) =
            OnboardingFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(EXTRA_CAROUSEL_DATA, carouselData)
                }
            }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            val carouselData = it.getParcelable<OnboardingItem>("EXTRA_CAROUSEL_DATA")
            carouselData?.let { data -> setupData(data) }
        }
    }

    private fun setupData(carouselData: OnboardingItem) {
        _binding.apply {
            Glide.with(ivImage)
                .load(carouselData.image)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(ivImage)
        }
    }

}