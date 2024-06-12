package com.example.recipe

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch

class UpdateRecipeActivity : AppCompatActivity() {

    private lateinit var viewModel: RecipeViewModel
    private var recipeId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_recipe)

        viewModel = ViewModelProvider(this).get(RecipeViewModel::class.java)

        recipeId = intent.getStringExtra("recipeId") ?: ""

        val nameEditText = findViewById<EditText>(R.id.edit_recipe_name)
        val typeSpinner = findViewById<Spinner>(R.id.spinner_recipe_type)
        val ingredientsEditText = findViewById<EditText>(R.id.edit_recipe_ingredients)
        val stepsEditText = findViewById<EditText>(R.id.edit_recipe_steps)
        val imageView = findViewById<ImageView>(R.id.imageView)

        val adapter = ArrayAdapter.createFromResource(this, R.array.recipe_types, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        typeSpinner.adapter = adapter

        if (recipeId.isNotEmpty()) {
            lifecycleScope.launch {
                viewModel.selectRecipe(recipeId)
            }
        }

        viewModel.selectedRecipe.observe(this, { recipe ->
            if (recipe != null) {
                nameEditText.setText(recipe.name)
                typeSpinner.setSelection(adapter.getPosition(recipe.type))
                ingredientsEditText.setText(recipe.ingredients)
                stepsEditText.setText(recipe.steps)
                Glide.with(this@UpdateRecipeActivity)
                    .load(recipe.image)
                    .into(imageView)
            }
        })

        findViewById<Button>(R.id.button_save).setOnClickListener {
            val updatedRecipe = Recipe(
                id = recipeId,
                name = nameEditText.text.toString(),
                type = typeSpinner.selectedItem.toString(),
                image = "", // Placeholder for now, will be updated with the image URL from Firebase Storage
                ingredients = ingredientsEditText.text.toString(),
                steps = stepsEditText.text.toString()
            )
            lifecycleScope.launch {
                viewModel.updateRecipe(updatedRecipe)

                finish() // Close the activity after saving
            }
        }

        findViewById<Button>(R.id.button_cancel).setOnClickListener {
            finish() // Close the activity without saving
        }
    }
}
