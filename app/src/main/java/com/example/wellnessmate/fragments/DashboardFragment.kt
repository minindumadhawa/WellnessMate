package com.example.wellnessmate.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.wellnessmate.MainActivity
import com.example.wellnessmate.databinding.FragmentDashboardBinding
import com.example.wellnessmate.utils.DataManager

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var dataManager: DataManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataManager = DataManager(requireContext())
        setupProgressBars()
        setupQuickActions()
        loadUserData()
    }

    private fun setupProgressBars() {
        // Progress bars will be updated with real data in loadUserData()
    }

    private fun setupQuickActions() {
        binding.quickWater.setOnClickListener {
            // Add water intake
            incrementWaterIntake()
        }

        binding.quickMood.setOnClickListener {
            // Log mood - navigate to mood fragment
            (activity as? MainActivity)?.navigateToMood()
        }

        binding.quickHabit.setOnClickListener {
            // Add habit - navigate to habits fragment
            (activity as? MainActivity)?.navigateToHabits()
        }
    }

    private fun loadUserData() {
        // Load data using DataManager
        val habitProgress = dataManager.getHabitProgress()
        binding.habitProgressBar.progress = habitProgress
        binding.habitProgressText.text = "$habitProgress%"

        val waterIntake = dataManager.getWaterIntake()
        val waterTarget = dataManager.getWaterTarget()
        val waterProgress = if (waterTarget > 0) (waterIntake * 100) / waterTarget else 0
        binding.waterProgressBar.progress = waterProgress
        binding.waterCountText.text = "$waterIntake/$waterTarget glasses"

        val lastMood = dataManager.getLastMood()
        binding.moodSummaryText.text = lastMood
    }

    private fun incrementWaterIntake() {
        val current = dataManager.getWaterIntake()
        val newValue = current + 1
        dataManager.saveWaterIntake(newValue)

        // Update UI
        val waterTarget = dataManager.getWaterTarget()
        val waterProgress = if (waterTarget > 0) (newValue * 100) / waterTarget else 0
        binding.waterProgressBar.progress = waterProgress
        binding.waterCountText.text = "$newValue/$waterTarget glasses"
    }

    override fun onResume() {
        super.onResume()
        // Refresh data when fragment becomes visible
        loadUserData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): DashboardFragment {
            return DashboardFragment()
        }
    }
}