package com.example.wellnessmate.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wellnessmate.R
import com.example.wellnessmate.adapters.MoodAdapter
import com.example.wellnessmate.databinding.FragmentMoodBinding
import com.example.wellnessmate.models.MoodEntry
import com.example.wellnessmate.utils.DataManager

import java.util.*

class MoodFragment : Fragment() {

    private var _binding: FragmentMoodBinding? = null
    private val binding get() = _binding!!
    private lateinit var moodAdapter: MoodAdapter
    private lateinit var dataManager: DataManager
    private val moodEntries = mutableListOf<MoodEntry>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoodBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataManager = DataManager(requireContext())
        setupEmojiSelector()
        setupRecyclerView()
        loadMoodEntries()
        setupSwipeToDelete()
    }

    private fun setupSwipeToDelete() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val moodEntry = moodEntries[position]
                showDeleteMoodDialog(moodEntry)
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.moodHistoryRecyclerView)
    }

    private fun showDeleteMoodDialog(moodEntry: MoodEntry) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.confirm_delete))
            .setMessage(getString(R.string.delete_mood_message))
            .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                deleteMoodEntry(moodEntry)
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                moodAdapter.notifyDataSetChanged() // Refresh to undo swipe
                dialog.dismiss()
            }
            .setOnCancelListener {
                moodAdapter.notifyDataSetChanged() // Refresh to undo swipe
            }
            .show()
    }

    private fun deleteMoodEntry(moodEntry: MoodEntry) {
        moodEntries.removeAll { it.id == moodEntry.id }
        dataManager.saveMoodEntries(moodEntries)
        moodAdapter.notifyDataSetChanged()
        updateEmptyState()
    }

    private fun setupEmojiSelector() {
        binding.emojiHappy.setOnClickListener { logMood("😊", "Happy") }
        binding.emojiNeutral.setOnClickListener { logMood("😐", "Neutral") }
        binding.emojiSad.setOnClickListener { logMood("😢", "Sad") }
        binding.emojiAngry.setOnClickListener { logMood("😠", "Angry") }
        binding.emojiExcited.setOnClickListener { logMood("😄", "Excited") }
    }

    private fun logMood(emoji: String, mood: String) {
        val newEntry = MoodEntry(
            id = UUID.randomUUID().toString(),
            emoji = emoji,
            mood = mood,
            note = binding.moodNoteEditText.text.toString(),
            timestamp = System.currentTimeMillis()
        )

        // Save to DataManager
        dataManager.addMoodEntry(newEntry)

        moodEntries.add(0, newEntry)
        moodAdapter.notifyItemInserted(0)
        binding.moodNoteEditText.text?.clear()

        // Scroll to top
        binding.moodHistoryRecyclerView.smoothScrollToPosition(0)
        updateEmptyState()
    }

    private fun setupRecyclerView() {
        moodAdapter = MoodAdapter(moodEntries,
            onDeleteMood = { moodEntry ->
                showDeleteMoodDialog(moodEntry)
            }
        )
        binding.moodHistoryRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = moodAdapter
            setHasFixedSize(true)
        }
    }

    private fun loadMoodEntries() {
        moodEntries.clear()
        moodEntries.addAll(dataManager.loadMoodEntries())
        moodAdapter.notifyDataSetChanged()
        updateEmptyState()
    }

    private fun updateEmptyState() {
        if (moodEntries.isEmpty()) {
            binding.emptyState.visibility = View.VISIBLE
            binding.moodHistoryRecyclerView.visibility = View.GONE
        } else {
            binding.emptyState.visibility = View.GONE
            binding.moodHistoryRecyclerView.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh data when fragment becomes visible
        loadMoodEntries()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): MoodFragment {
            return MoodFragment()
        }
    }
}