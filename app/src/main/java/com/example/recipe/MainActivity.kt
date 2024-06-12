package com.example.recipe

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: RecipeViewModel
    private lateinit var adapter: RecipeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase
        FirebaseFirestore.getInstance().collection("recipes").get()
            .addOnSuccessListener {
                Log.d("MainActivity", "Firebase connected successfully")
            }
            .addOnFailureListener { exception ->
                Log.e("MainActivity", "Error connecting to Firebase", exception)
            }

        viewModel = ViewModelProvider(this).get(RecipeViewModel::class.java)

        adapter = RecipeAdapter { recipe ->
            val intent = Intent(this, RecipeDetailActivity::class.java).apply {
                putExtra("recipeId", recipe.id)
            }
            startActivity(intent)
        }

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Observe the LiveData from the ViewModel
        viewModel.recipes.observe(this, Observer { recipeList ->
            if (recipeList != null) {
                adapter.submitList(recipeList)
            }
        })

        val spinner: Spinner = findViewById(R.id.spinner)
        val types = listOf("All", "Pasta", "Vegetarian") // Add more types
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, types)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedType = types[position]
                viewModel.filterRecipes(selectedType)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        findViewById<Button>(R.id.button_add_recipe).setOnClickListener {
            val intent = Intent(this@MainActivity, AddRecipeActivity::class.java)
            startActivity(intent)
        }
    }
}
