package com.example.cvproject.activites.activity.utilities

import android.content.Context
import android.view.View
import android.widget.Toast
import com.example.cvproject.activites.activity.dataclass.HomeItem
import com.google.android.material.snackbar.Snackbar
import cvproject.blinkit.R

object Utils {
    fun getImageNameList(): List<HomeItem> {
        val imageNameList = listOf(
            "Ice Classic",
            R.drawable.ice1,
            "Ice Deluxe",
            R.drawable.ice2,
            "Ice Premium",
            R.drawable.ice3,
            "Maggi Classic",
            R.drawable.magggi1,
            "Maggi Spicy",
            R.drawable.magggi3,
            "Maggi Masala",
            R.drawable.magggi4,
            "Mango Fresh",
            R.drawable.mangoes3,
            "Top Ramen Original",
            R.drawable.topramen,
            "Top Ramen Spicy",
            R.drawable.topramen1,
            "Yippee Classic",
            R.drawable.yippee1,
            "Yippee Masala",
            R.drawable.yippee2,
            "Yippee Spicy",
            R.drawable.yippee3,
            "Ice Classic",
            R.drawable.ice1,
            "Ice Deluxe",
            R.drawable.ice2,
            "Ice Premium",
            R.drawable.ice3,
            "Maggi Classic",
            R.drawable.magggi1,
            "Maggi Spicy",
            R.drawable.magggi3,
            "Maggi Masala",
            R.drawable.magggi4,
            "Mango Fresh",
            R.drawable.mangoes3,
            "Top Ramen Original",
            R.drawable.topramen,
            "Top Ramen Spicy",
            R.drawable.topramen1,
            "Yippee Classic",
            R.drawable.yippee1,
            "Yippee Masala",
            R.drawable.yippee2,
            "Yippee Spicy",
            R.drawable.yippee3,
            "Ice Classic",
            R.drawable.ice1,
            "Ice Deluxe",
            R.drawable.ice2,
            "Ice Premium",
            R.drawable.ice3,
            "Maggi Classic",
            R.drawable.magggi1,
            "Maggi Spicy",
            R.drawable.magggi3,
            "Maggi Masala",
            R.drawable.magggi4,
            "Mango Fresh",
            R.drawable.mangoes3,
            "Top Ramen Classic",
            R.drawable.topramen,
            "Top Ramen Bold",
            R.drawable.topramen1,
            "Yippee Original",
            R.drawable.yippee1,
            "Yippee Tangy",
            R.drawable.yippee2,
            "Yippee Chatpata",
            R.drawable.yippee3,
            "Atta Whole Grain",
            R.drawable.attta_two,
            "Chili Red",
            R.drawable.chili1,
            "Chili Green",
            R.drawable.chili2,
            "Cornetto Chocolate",
            R.drawable.corneto3,
            "Cornetto Vanilla",
            R.drawable.corneto_one,
            "Cornetto Strawberry",
            R.drawable.corneto_two,
            "Dawat Basmati",
            R.drawable.dawat,
            "Dawat Special",
            R.drawable.dawat1,
            "Dawat Super",
            R.drawable.dawat2,
            "Homi Classic",
            R.drawable.homi1,
            "Homi Premium",
            R.drawable.homi2,
            "Homi Deluxe",
            R.drawable.homi3,
            "Homi Gourmet",
            R.drawable.homi4,
            "Top Ramen Supreme",
            R.drawable.topramen1,
            "Top Ramen Gourmet",
            R.drawable.topramen,
            "Top Ramen Premium",
            R.drawable.topramen,
            "Top Ramen Deluxe",
            R.drawable.topramen1,
            "Yippee Tandoori",
            R.drawable.yippee1,
            "Yippee Italian",
            R.drawable.yippee2,
            "Yippee Chinese",
            R.drawable.yippee3,
            "Ice Arctic",
            R.drawable.ice1,
            "Ice Glacier",
            R.drawable.ice2,
            "Ice Snow",
            R.drawable.ice3,
            "Maggi Classic",
            R.drawable.magggi1,
            "Maggi Chatpata",
            R.drawable.magggi3,
            "Maggi Hot",
            R.drawable.magggi4,
            "Mango Tropical",
            R.drawable.mangoes3,
            "Top Ramen Ultimate",
            R.drawable.topramen,
            "Top Ramen Special",
            R.drawable.topramen1,
            "Yippee Cheesy",
            R.drawable.yippee1,
            "Yippee Mexican",
            R.drawable.yippee2,
            "Yippee American",
            R.drawable.yippee3,
            "Atta Multigrain",
            R.drawable.attta_two,
            "Chili Yellow",
            R.drawable.chili1,
            "Chili Red Hot",
            R.drawable.chili2,
            "Cornetto Mint",
            R.drawable.corneto3,
            "Cornetto Almond",
            R.drawable.corneto_one,
            "Cornetto Caramel",
            R.drawable.corneto_two,
            "Dawat Royal",
            R.drawable.dawat,
            "Dawat Aroma",
            R.drawable.dawat1,
            "Dawat Chef Special",
            R.drawable.dawat2,
            "Homi Select",
            R.drawable.homi1,
            "Homi Delight",
            R.drawable.homi2,
            "Homi Rich",
            R.drawable.homi3,
            "Homi Elite",
            R.drawable.homi4,
            "Top Ramen Elite",
            R.drawable.topramen1,
            "Top Ramen Signature",
            R.drawable.topramen
        )

        val itemList = mutableListOf<HomeItem>()
        for (i in imageNameList.indices step 2) {
            val name = imageNameList[i] as String
            val imageResId = imageNameList[i + 1] as Int
            val price = "@" + (50..200).random()
            itemList.add(HomeItem(name, imageResId, price))
        }
        return itemList
    }

    private var snackbar: Snackbar? = null

    @JvmStatic
    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    @JvmStatic
    fun showSnackBar(message: String, view: View) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
    }

    @JvmStatic
    fun showPersistentSnackBar(message: String, view: View) {
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE)
        snackbar.setAction("Dismiss") {
            snackbar.dismiss()
        }
        snackbar.show()
    }
}