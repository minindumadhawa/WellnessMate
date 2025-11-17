package com.example.wellnessmate.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.wellnessmate.R
import com.example.wellnessmate.databinding.FragmentSettingsBinding
import com.example.wellnessmate.LoginActivity
import com.example.wellnessmate.utils.AuthManager

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: AuthManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = AuthManager(requireContext())
        loadSettings()
        setupClickListeners()
    }

    private fun loadSettings() {
        // Load settings from SharedPreferences
        val sharedPrefs = requireContext().getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        binding.notificationsSwitch.isChecked = sharedPrefs.getBoolean("notifications_enabled", true)
        binding.darkModeSwitch.isChecked = sharedPrefs.getBoolean("dark_mode_enabled", false)

        // My Account
        val email = auth.getUserEmail()
        binding.accountEmailText.text = email ?: getString(R.string.not_signed_in)
    }

    private fun setupClickListeners() {
        binding.notificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            saveSetting("notifications_enabled", isChecked)
        }

        binding.darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            saveSetting("dark_mode_enabled", isChecked)
            // Show message that app restart is needed
            showRestartMessage()
        }

        binding.shareAppButton.setOnClickListener {
            shareApp()
        }

        binding.rateAppButton.setOnClickListener {
            // Open play store or show rating dialog
            showRatingDialog()
        }

        binding.feedbackButton.setOnClickListener {
            sendFeedback()
        }

        binding.aboutButton.setOnClickListener {
            showAboutDialog()
        }

        binding.logoutButton.setOnClickListener {
            confirmAndLogout()
        }
    }

    private fun saveSetting(key: String, value: Boolean) {
        val sharedPrefs = requireContext().getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        sharedPrefs.edit().putBoolean(key, value).apply()
    }

    private fun shareApp() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.check_out_wellnessmate))
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_text))
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_via)))
    }

    private fun showRatingDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.rate_app))
            .setMessage(getString(R.string.rate_app_message))
            .setPositiveButton(getString(R.string.rate_now)) { dialog, _ ->
                // Open play store or implement rating logic
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.later)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun sendFeedback() {
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.type = "message/rfc822"
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("support@wellnessmate.com"))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.wellnessmate_feedback))
        startActivity(Intent.createChooser(emailIntent, getString(R.string.send_feedback)))
    }

    private fun showAboutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.about))
            .setMessage(getString(R.string.about_message))
            .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showRestartMessage() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.restart_required))
            .setMessage(getString(R.string.restart_message))
            .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun confirmAndLogout() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.sign_out))
            .setMessage(getString(R.string.sign_out_message))
            .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                dialog.dismiss()
                auth.logout()
                navigateToLogin()
            }
            .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun navigateToLogin() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        requireActivity().finishAffinity()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}