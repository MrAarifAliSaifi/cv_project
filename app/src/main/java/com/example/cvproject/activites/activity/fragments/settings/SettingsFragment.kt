package cvproject.blinkit.activites.activity.ui.notifications

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import cvproject.blinkit.BuildConfig
import android.view.ViewGroup
import cvproject.blinkit.R
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cvproject.activites.activity.activity.AdminActivity
import com.example.cvproject.activites.activity.activity.LoginActivity
import com.example.cvproject.activites.activity.adapters.NotificationAdapter
import com.example.cvproject.activites.activity.constant.BlinkitConstants
import com.example.cvproject.activites.activity.dataclass.ListItemNotification
import com.pixplicity.easyprefs.library.Prefs
import cvproject.blinkit.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private var adapter: NotificationAdapter? = null
    private lateinit var viewModel: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding?.apply {
            val dataList = listOf(
                ListItemNotification(getString(R.string.contact_us), R.drawable.contact),
                ListItemNotification(getString(R.string.rate_us_on_playstore), R.drawable.star),
                ListItemNotification(getString(R.string.privacy_policy), R.drawable.privacy_policy),
                ListItemNotification(getString(R.string.share_app), R.drawable.share),
                ListItemNotification(getString(R.string.logout), R.drawable.logout),
                ListItemNotification(
                    getString(R.string.notification),
                    R.drawable.ic_notifications_black_24dp_outlined
                ),
                ListItemNotification(getString(R.string.about_us), R.drawable.info),
                ListItemNotification(getString(R.string.admin), R.drawable.profile)
            )

            adapter = NotificationAdapter(requireContext(), dataList) { item ->
                handleItemClick(item)
            }
            xRecyclerView.adapter = adapter
            xRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            xImageView.background = resources.getDrawable(R.drawable.profile)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun handleItemClick(item: ListItemNotification) {
        when (item.text) {
            getString(R.string.contact_us) -> {

            }

            getString(R.string.rate_us_on_playstore) -> {
                openPlayStorePage()
            }

            getString(R.string.share_app) -> {
                shareApp()
            }


            getString(R.string.privacy_policy) -> {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://www.termsfeed.com/live/251cc4f5-6fa1-48ed-8821-37d42c176770")
                    )
                )
            }

            getString(R.string.admin) -> {
                startActivity(AdminActivity.getStartIntent(requireContext()))
            }

            getString(R.string.logout) -> {
                logout()
            }
        }
    }

    private fun openPlayStorePage() {
        val packageName = context?.packageName
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
        } catch (e: android.content.ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                )
            )
        }
    }

    private fun shareApp() {
        try {
            BuildConfig.APPLICATION_ID
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
            var shareMessage = "\nLet me recommend you this fantastic application\n\n"
            shareMessage =
                """ ${shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID} """.trimIndent()
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            startActivity(Intent.createChooser(shareIntent, "choose one"))
        } catch (_: java.lang.Exception) {

        }
    }

    private fun logout() {
        Prefs.putBoolean(BlinkitConstants.IS_LOGGED_IN, false)
        Prefs.remove(BlinkitConstants.Verification_ID)
        val intent = LoginActivity.getStartIntent(requireContext())
        startActivity(intent)
        requireActivity().finish()
    }
}