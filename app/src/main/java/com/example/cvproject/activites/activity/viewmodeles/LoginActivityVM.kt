package com.example.cvproject.activites.activity.viewmodeles

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.cvproject.activites.activity.dataclass.OnboardingItem
import cvproject.blinkit.R


class LoginActivityVM: ViewModel() {

    companion object {
        const val TAG = "LoginActivityVM"
        private var onboardingCarouselData: ArrayList<OnboardingItem> = ArrayList()
    }

    fun getOnboardingCarouselData(context: Context): ArrayList<OnboardingItem> {
        return onboardingCarouselData.apply {
            onboardingCarouselData.add(
                OnboardingItem(
                    R.drawable.ic_image_1
                )
            )
            onboardingCarouselData.add(
                OnboardingItem(
                    R.drawable.ic_image_2

                )
            )
            onboardingCarouselData.add(
                OnboardingItem(
                    R.drawable.ic_image_4
                )
            )
            onboardingCarouselData.add(
                OnboardingItem(
                    R.drawable.ic_image_3
                )
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
    }

}