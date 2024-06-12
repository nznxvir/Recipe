package com.example.recipe

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

import com.bumptech.glide.Glide

class RecipeAdapter(private val onItemClick: (Recipe) -> Unit) :
    ListAdapter<Recipe, RecipeAdapter.RecipeViewHolder>(RecipeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class RecipeViewHolder(itemView: View, val onItemClick: (Recipe) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val recipeName: TextView = itemView.findViewById(R.id.recipeName)
        private val recipeType: TextView = itemView.findViewById(R.id.recipeType)
        private val recipeImage: ImageView = itemView.findViewById(R.id.recipeImage) // Add ImageView reference

        fun bind(recipe: Recipe) {
            recipeName.text = recipe.name
            recipeType.text = recipe.type

            // Load image into ImageView using Glide
            Glide.with(itemView)
                .load(recipe.image) // Assuming 'image' is the URL of the image
                .placeholder(R.drawable.spaghetti) // Placeholder image while loading
                .error(R.drawable.spaghetti) // Error image if the loading fails
                .into(recipeImage)

            itemView.setOnClickListener {
                onItemClick(recipe)
            }
        }
    }

    class RecipeDiffCallback : DiffUtil.ItemCallback<Recipe>() {
        override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem == newItem
        }
    }
}
