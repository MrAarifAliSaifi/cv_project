package com.example.cvproject.activites.activity.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.cvproject.activites.activity.dataclass.OnboardingItem
import com.example.cvproject.activites.activity.fragments.onboarding.OnboardingFragment

class ViewPagerAdapter(
        fragmentActivity: FragmentActivity,
        private val carouselData: List<OnboardingItem>
    ) : FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int = minOf(carouselData.size, 4)

        override fun createFragment(position: Int): Fragment {
            return OnboardingFragment.newInstance(carouselData[position])
        }
    }