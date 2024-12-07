package com.example.sflnotifi

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.sflnotifi.databinding.ItemCropGroupBinding
import java.util.concurrent.TimeUnit

class BarnGroupAdapter : RecyclerView.Adapter<BarnGroupAdapter.ViewHolder>() {
    private var barnGroups: List<BarnGroup> = emptyList()

    companion object {
        private const val PAYLOAD_TIME_UPDATE = "time_update"
    }

    class ViewHolder(val binding: ItemCropGroupBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCropGroupBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val group = barnGroups[position]
        val binding = holder.binding

        // Set animal type with count
        binding.cropNameText.text = "${group.animals.size}x ${group.animalType}s"
        updateTimeDisplay(binding, group)
        binding.totalAmountText.visibility = View.GONE

        // Set expansion state
        binding.expandedLayout.visibility = if (group.isExpanded) View.VISIBLE else View.GONE
        binding.expandIcon.rotation = if (group.isExpanded) 90f else 0f

        // Update expanded content
        if (group.isExpanded) {
            binding.expandedLayout.removeAllViews()
            group.animals.forEach { animal ->
                val itemView = TextView(binding.root.context).apply {
                    text = "Experience: ${animal.experience} (${animal.state})"
                    setPadding(0, 4, 0, 4)
                }
                binding.expandedLayout.addView(itemView)
            }
        }

        // Handle click
        binding.root.setOnClickListener {
            // Create new list with updated expansion state
            val newGroups = barnGroups.toMutableList()
            newGroups[position] = group.copy(isExpanded = !group.isExpanded)
            
            // Calculate the diff and update
            val diffCallback = BarnGroupDiffCallback(barnGroups, newGroups)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            
            barnGroups = newGroups
            diffResult.dispatchUpdatesTo(this)

            // Animate arrow
            ObjectAnimator.ofFloat(
                binding.expandIcon,
                View.ROTATION,
                if (group.isExpanded) 90f else 0f,
                if (!group.isExpanded) 90f else 0f
            ).apply {
                duration = 200
                start()
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: List<Any>) {
        if (payloads.contains(PAYLOAD_TIME_UPDATE)) {
            updateTimeDisplay(holder.binding, barnGroups[position])
        } else {
            onBindViewHolder(holder, position)
        }
    }

    private fun updateTimeDisplay(binding: ItemCropGroupBinding, group: BarnGroup) {
        val timeRemaining = group.wakeTime - System.currentTimeMillis()
        val hours = TimeUnit.MILLISECONDS.toHours(timeRemaining)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeRemaining) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(timeRemaining) % 60
        binding.timeRemainingText.text = String.format(
            "Time Until Wake: %02d:%02d:%02d",
            hours, minutes, seconds
        )
    }

    override fun getItemCount() = barnGroups.size

    fun updateGroups(newGroups: List<BarnGroup>) {
        val diffCallback = BarnGroupDiffCallback(barnGroups, newGroups)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        barnGroups = newGroups
        diffResult.dispatchUpdatesTo(this)
    }

    fun updateTimeDisplays() {
        notifyItemRangeChanged(0, barnGroups.size, PAYLOAD_TIME_UPDATE)
    }

    private class BarnGroupDiffCallback(
        private val oldList: List<BarnGroup>,
        private val newList: List<BarnGroup>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldList.size
        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].animalType == newList[newItemPosition].animalType &&
                   oldList[oldItemPosition].wakeTime == newList[newItemPosition].wakeTime
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }

    fun getGroups(): List<BarnGroup> {
        return barnGroups.toList()
    }
} 