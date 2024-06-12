package com.example.recipe

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipe.Recipe
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class RecipeViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val recipesCollection = firestore.collection("recipes")

    private val _recipes = MutableLiveData<List<Recipe>>()
    val recipes: LiveData<List<Recipe>> get() = _recipes

    private val _selectedRecipe = MutableLiveData<Recipe>()
    val selectedRecipe: LiveData<Recipe> get() = _selectedRecipe

    init {
        loadRecipes()
    }

    private fun loadRecipes() {
        recipesCollection.get()
            .addOnSuccessListener { result ->
                val recipeList = result.map { document ->
                    val recipe = document.toObject(Recipe::class.java)
                    recipe.id = document.id
                    recipe // Assigning image URL here
                }
                _recipes.value = recipeList
            }
            .addOnFailureListener { exception ->
                // Handle the error
            }
    }

    fun filterRecipes(type: String) {
        if (type == "All") {
            loadRecipes()
        } else {
            recipesCollection.whereEqualTo("type", type).get()
                .addOnSuccessListener { result ->
                    val filteredList = result.map { document ->
                        val recipe = document.toObject(Recipe::class.java)
                        recipe.id = document.id
                        recipe // Assigning image URL here
                    }
                    _recipes.value = filteredList
                }
                .addOnFailureListener { exception ->
                    // Handle the error
                }
        }
    }

    fun selectRecipe(id: String) {
        recipesCollection.document(id).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    _selectedRecipe.value = document.toObject(Recipe::class.java)?.apply { this.id = document.id }
                }
            }
            .addOnFailureListener { exception ->
                // Handle the error
            }
    }

    fun deleteRecipe(id: String) {
        recipesCollection.document(id).delete()
            .addOnSuccessListener {
                loadRecipes()
            }
            .addOnFailureListener { exception ->
                // Handle the error
            }
    }

    fun updateRecipe(recipe: Recipe) {
        if (recipe.id.isNotEmpty()) {
            recipesCollection.document(recipe.id).set(recipe)
                .addOnSuccessListener {
                    loadRecipes()
                }
                .addOnFailureListener { exception ->
                    // Handle the error
                }
        }
    }

    fun addRecipe(recipe: Recipe) {
        recipesCollection.add(recipe)
            .addOnSuccessListener { documentReference ->
                recipe.id = documentReference.id
                loadRecipes()
            }
            .addOnFailureListener { exception ->
                // Handle the error
            }
    }
}
