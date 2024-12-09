package com.app.SFLNotifi

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.SFLNotifi.databinding.ItemCropGroupBinding
import java.util.concurrent.TimeUnit

class BuildingGroupAdapter : RecyclerView.Adapter<BuildingGroupAdapter.ViewHolder>() {
    private var buildingGroups: List<BuildingGroup> = emptyList()

    companion object {
        private const val PAYLOAD_TIME_UPDATE = "time_update"
        private val COOKING_BUILDINGS = setOf(
            "fire pit", "kitchen", "deli", "smoothie shack", "bakery",
            "compost bin", "turbo composter", "premium composter"
        )
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
        val group = buildingGroups[position]
        val binding = holder.binding

        // Set item name with count
        binding.cropNameText.text = "${group.buildings.size}x ${group.itemName}"
        updateTimeDisplay(binding, group)
        binding.totalAmountText.visibility = View.GONE

        // Set expansion state
        binding.expandedLayout.visibility = if (group.isExpanded) View.VISIBLE else View.GONE
        binding.expandIcon.rotation = if (group.isExpanded) 90f else 0f

        // Update expanded content
        if (group.isExpanded) {
            binding.expandedLayout.removeAllViews()
            group.buildings.forEach { building ->
                val itemView = TextView(binding.root.context).apply {
                    val text = when (group.buildingType.lowercase()) {
                        "compost bin", "turbo composter", "premium composter" -> {
                            "Egg Boost: ${building.crafting?.boost?.get("eggBoost") ?: 0}"
                        }
                        else -> {
                            "Amount: ${building.crafting?.amount ?: 0}"
                        }
                    }
                    this.text = text
                    setPadding(0, 4, 0, 4)
                }
                binding.expandedLayout.addView(itemView)
            }
        }

        // Handle click
        binding.root.setOnClickListener {
            // Create new list with updated expansion state
            val newGroups = buildingGroups.toMutableList()
            newGroups[position] = group.copy(isExpanded = !group.isExpanded)
            
            // Calculate the diff and update
            val diffCallback = BuildingGroupDiffCallback(buildingGroups, newGroups)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            
            buildingGroups = newGroups
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
            updateTimeDisplay(holder.binding, buildingGroups[position])
        } else {
            onBindViewHolder(holder, position)
        }
    }

    private fun updateTimeDisplay(binding: ItemCropGroupBinding, group: BuildingGroup) {
        val timeRemaining = group.readyAt - System.currentTimeMillis()
        val hours = TimeUnit.MILLISECONDS.toHours(timeRemaining)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeRemaining) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(timeRemaining) % 60
        binding.timeRemainingText.text = String.format(
            "Time Remaining: %02d:%02d:%02d",
            hours, minutes, seconds
        )
    }

    override fun getItemCount() = buildingGroups.size

    fun updateGroups(newGroups: List<BuildingGroup>) {
        val diffCallback = BuildingGroupDiffCallback(buildingGroups, newGroups)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        buildingGroups = newGroups
        diffResult.dispatchUpdatesTo(this)
    }

    fun updateTimeDisplays() {
        notifyItemRangeChanged(0, buildingGroups.size, PAYLOAD_TIME_UPDATE)
    }

    private class BuildingGroupDiffCallback(
        private val oldList: List<BuildingGroup>,
        private val newList: List<BuildingGroup>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldList.size
        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].itemName == newList[newItemPosition].itemName &&
                   oldList[oldItemPosition].buildingType == newList[newItemPosition].buildingType
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }

    // Add this method to get current groups
    fun getGroups(): List<BuildingGroup> {
        return buildingGroups
    }
} 