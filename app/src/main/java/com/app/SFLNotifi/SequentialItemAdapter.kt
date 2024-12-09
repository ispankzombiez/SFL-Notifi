package com.app.SFLNotifi

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.SFLNotifi.databinding.ItemSequentialBinding
import java.util.concurrent.TimeUnit

class SequentialItemAdapter : RecyclerView.Adapter<SequentialItemAdapter.ViewHolder>() {
    private var items = listOf<SequentialItem>()
    private val expansionStates = mutableMapOf<String, Boolean>()

    class ViewHolder(val binding: ItemSequentialBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSequentialBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        
        holder.binding.apply {
            titleText.text = "${item.nodeCount}x ${item.name} (${item.totalAmount.toInt()})"
            categoryText.text = item.category
            updateTimeDisplay(this, item.completionTime)

            // Restore expansion state
            val key = "${item.name}-${item.category}-${item.completionTime}"
            item.isExpanded = expansionStates[key] ?: false
            
            expandedLayout.visibility = if (item.isExpanded) View.VISIBLE else View.GONE
            expandIcon.rotation = if (item.isExpanded) 90f else 0f

            // Update expanded content
            if (item.isExpanded) {
                expandedLayout.removeAllViews()
                item.individualAmounts.forEachIndexed { index, amount ->
                    val itemView = TextView(root.context).apply {
                        text = "Plot ${index + 1}: $amount"
                        setPadding(16, 4, 0, 4)
                    }
                    expandedLayout.addView(itemView)
                }
            }

            // Handle click
            root.setOnClickListener {
                item.isExpanded = !item.isExpanded
                expansionStates[key] = item.isExpanded
                
                notifyItemChanged(position)

                // Animate arrow
                ObjectAnimator.ofFloat(
                    expandIcon,
                    View.ROTATION,
                    if (!item.isExpanded) 90f else 0f,
                    if (item.isExpanded) 90f else 0f
                ).apply {
                    duration = 200
                    start()
                }
            }
        }
    }

    override fun getItemCount() = items.size

    private fun updateTimeDisplay(binding: ItemSequentialBinding, completionTime: Long) {
        val currentTime = System.currentTimeMillis()
        val timeLeft = completionTime - currentTime

        if (timeLeft <= 0) {
            binding.timeText.text = "Ready!"
            return
        }

        val hours = TimeUnit.MILLISECONDS.toHours(timeLeft)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeLeft) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(timeLeft) % 60

        binding.timeText.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    fun updateItems(newItems: List<SequentialItem>) {
        val diffCallback = SequentialItemDiffCallback(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        
        // Update items while preserving expansion states
        newItems.forEach { item ->
            val key = "${item.name}-${item.category}-${item.completionTime}"
            if (!expansionStates.containsKey(key)) {
                expansionStates[key] = false
            }
        }
        
        items = newItems
        diffResult.dispatchUpdatesTo(this)
    }

    fun updateTimeDisplays() {
        notifyItemRangeChanged(0, items.size, PAYLOAD_TIME_UPDATE)
    }

    fun getItems(): List<SequentialItem> {
        return items
    }

    companion object {
        private const val PAYLOAD_TIME_UPDATE = "time_update"
    }
}

data class SequentialItem(
    val name: String,
    val category: String,
    val nodeCount: Int,  // Number of plots/nodes
    val totalAmount: Double,  // Total harvest amount
    val completionTime: Long,
    var isExpanded: Boolean = false,
    val individualAmounts: List<Double> = emptyList()  // Individual harvest amounts
)

private class SequentialItemDiffCallback(
    private val oldList: List<SequentialItem>,
    private val newList: List<SequentialItem>
) : DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].name == newList[newItemPosition].name &&
               oldList[oldItemPosition].category == newList[newItemPosition].category
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
} 