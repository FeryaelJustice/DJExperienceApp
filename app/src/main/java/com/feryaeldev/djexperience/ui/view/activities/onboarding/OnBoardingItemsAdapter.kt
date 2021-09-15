package com.feryaeldev.djexperience.ui.view.activities.onboarding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.feryaeldev.djexperience.R
import com.feryaeldev.djexperience.data.model.domain.OnBoardingItem

class OnBoardingItemsAdapter(private val onBoardingItems: List<OnBoardingItem>) :
    RecyclerView.Adapter<OnBoardingItemsAdapter.OnBoardingItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnBoardingItemViewHolder {
        return OnBoardingItemViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.onboarding_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: OnBoardingItemViewHolder, position: Int) {
        holder.bind(onBoardingItems[position])
    }

    override fun getItemCount(): Int = onBoardingItems.size

    inner class OnBoardingItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val image = view.findViewById<ImageView>(R.id.imageOnboarding)
        private val title = view.findViewById<TextView>(R.id.titleOnboarding)
        private val description = view.findViewById<TextView>(R.id.descriptionOnboarding)

        fun bind(onBoardingItem: OnBoardingItem) {
            image.setImageResource(onBoardingItem.image)
            title.text = onBoardingItem.title
            description.text = onBoardingItem.description
        }
    }
}