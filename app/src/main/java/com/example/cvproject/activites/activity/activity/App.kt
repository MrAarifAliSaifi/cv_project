package cvproject.blinkit.activites.activity.activity

import android.app.Application
import com.google.firebase.FirebaseApp
import com.pixplicity.easyprefs.library.Prefs

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Prefs.Builder().setContext(this).build()
        FirebaseApp.initializeApp(this)
    }
}