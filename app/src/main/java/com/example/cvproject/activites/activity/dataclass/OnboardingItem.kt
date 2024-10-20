package com.example.cvproject.activites.activity.dataclass

import android.os.Parcelable
import android.os.Parcel

data class OnboardingItem(
    val image: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(image)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OnboardingItem> {
        override fun createFromParcel(parcel: Parcel): OnboardingItem {
            return OnboardingItem(parcel)
        }

        override fun newArray(size: Int): Array<OnboardingItem?> {
            return arrayOfNulls(size)
        }
    }
}
