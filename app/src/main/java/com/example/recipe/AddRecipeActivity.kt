package com.example.recipe

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*

class AddRecipeActivity : AppCompatActivity() {

    private lateinit var viewModel: RecipeViewModel
    private lateinit var imageUri: Uri
    private lateinit var nameEditText: EditText
    private lateinit var typeSpinner: Spinner
    private lateinit var ingredientsEditText: EditText
    private lateinit var stepsEditText: EditText
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_recipe)

        viewModel = ViewModelProvider(this).get(RecipeViewModel::class.java)

        // Initialize views
        nameEditText = findViewById(R.id.edit_recipe_name)
        typeSpinner = findViewById(R.id.spinner_recipe_type)
        ingredientsEditText = findViewById(R.id.edit_recipe_ingredients)
        stepsEditText = findViewById(R.id.edit_recipe_steps)
        imageView = findViewById(R.id.imageView)

        // Initialize spinner
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.recipe_types,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        typeSpinner.adapter = adapter

        // Button to select image
        val selectImageButton: Button = findViewById(R.id.button_image)
        selectImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_REQUEST_CODE)
        }

        // Save button click listener
        val saveButton: Button = findViewById(R.id.button_save)
        saveButton.setOnClickListener {
            saveRecipe()
        }

        val backButton: Button = findViewById(R.id.button_cancel)
        backButton.setOnClickListener {
            // Handle the back button click
            onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                imageUri = uri
                Glide.with(this)
                    .load(imageUri)
                    .into(imageView)
                Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveRecipe() {
        // Ensure an image is selected
        if (::imageUri.isInitialized) {
            val imageName = UUID.randomUUID().toString()
            val storageRef: StorageReference = FirebaseStorage.getInstance().reference.child("images/$imageName")

            lifecycleScope.launch {
                try {
                    // Upload image to Firebase Storage
                    withContext(Dispatchers.IO) {
                        storageRef.putFile(imageUri).await()
                    }
                    // Get the image URL
                    val imageUrl = storageRef.downloadUrl.await().toString()

                    // Create Recipe object with all details including the image URL
                    val newRecipe = Recipe(
                        name = nameEditText.text.toString(),
                        type = typeSpinner.selectedItem.toString(),
                        image = imageUrl,
                        ingredients = ingredientsEditText.text.toString(),
                        steps = stepsEditText.text.toString()
                    )

                    // Save the new recipe to Firestore using the ViewModel
                    viewModel.addRecipe(newRecipe)

                    // Navigate back to the main activity and reload the list view
                    val intent = Intent(this@AddRecipeActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)

                    // Finish the activity after saving
                    finish()
                } catch (e: Exception) {
                    Toast.makeText(this@AddRecipeActivity, "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Please select an image first", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val IMAGE_REQUEST_CODE = 100
    }
}
