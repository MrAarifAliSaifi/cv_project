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
                    R.drawable.login_image
                )
            )
            onboardingCarouselData.add(
                OnboardingItem(
                    R.drawable.login_image
                )
            )
            onboardingCarouselData.add(
                OnboardingItem(
                    R.drawable.login_image
                )
            )
            onboardingCarouselData.add(
                OnboardingItem(
                    R.drawable.login_image
                )
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
    }

}