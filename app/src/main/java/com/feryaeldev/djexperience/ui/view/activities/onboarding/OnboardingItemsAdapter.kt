package com.feryaeldev.djexperience.ui.view.activities.onboarding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.feryaeldev.djexperience.R
import com.feryaeldev.djexperience.data.model.domain.OnboardingItem

class OnboardingItemsAdapter(private val onboardingItems: List<OnboardingItem>) :
    RecyclerView.Adapter<OnboardingItemsAdapter.OnboardingItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingItemViewHolder {
        return OnboardingItemViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.onboarding_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: OnboardingItemViewHolder, position: Int) {
        holder.bind(onboardingItems[position])
    }

    override fun getItemCount(): Int = onboardingItems.size

    inner class OnboardingItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val image = view.findViewById<ImageView>(R.id.imageOnboarding)
        private val title = view.findViewById<TextView>(R.id.titleOnboarding)
        private val description = view.findViewById<TextView>(R.id.descriptionOnboarding)

        fun bind(onboardingItem: OnboardingItem) {
            image.setImageResource(onboardingItem.onboardingImage)
            title.text = onboardingItem.title
            description.text = onboardingItem.description
        }
    }
}