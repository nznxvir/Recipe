package com.example.recipe

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Recipe(
    var id: String = "",
    val name: String = "",
    val type: String = "",
    val image: String = "", // New field for storing image URL
    val ingredients: String = "",
    val steps: String = ""
) : Parcelable {
    constructor() : this("", "", "", "", "", "") // No-argument constructor
}
