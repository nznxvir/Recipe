package com.example.recipe

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch

class RecipeDetailActivity : AppCompatActivity() {

    private lateinit var viewModel: RecipeViewModel
    private var recipeId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail)

        viewModel = ViewModelProvider(this).get(RecipeViewModel::class.java)

        recipeId = intent.getStringExtra("recipeId") ?: ""
        if (recipeId.isNotEmpty()) {
            lifecycleScope.launch {
                viewModel.selectRecipe(recipeId)
            }
        }

        viewModel.selectedRecipe.observe(this, { recipe ->
            if (recipe != null) {
                findViewById<TextView>(R.id.detail_name).text = recipe.name
                findViewById<TextView>(R.id.detail_type).text = recipe.type
                findViewById<TextView>(R.id.detail_ingredients).text = recipe.ingredients
                findViewById<TextView>(R.id.detail_steps).text = recipe.steps
                loadImage(recipe.image)
            }
        })

        val backButton: ImageButton = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            // Handle the back button click
            onBackPressed()
        }

        findViewById<Button>(R.id.button_delete).setOnClickListener {
            if (recipeId.isNotEmpty()) {
                lifecycleScope.launch {
                    viewModel.deleteRecipe(recipeId)
                    // Navigate back to the main activity and reload the list view
                    val intent = Intent(this@RecipeDetailActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish() // Close the activity after deletion
                }
            }
        }

        findViewById<Button>(R.id.button_update).setOnClickListener {
            if (recipeId.isNotEmpty()) {lifecycleScope.launch {
                viewModel.deleteRecipe(recipeId)
                finish() // Close the activity after deletion
            }
                val intent = Intent(this, AddRecipeActivity::class.java)
                intent.putExtra("recipeId", recipeId)
                startActivity(intent)
            }
        }
    }

    private fun loadImage(imageUrl: String) {
        val imageView = findViewById<ImageView>(R.id.detail_image)
        Glide.with(this)
            .load(imageUrl)
            .into(imageView)
    }
}
