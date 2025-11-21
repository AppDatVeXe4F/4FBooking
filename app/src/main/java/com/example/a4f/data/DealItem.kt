package com.example.a4f.data


import androidx.annotation.DrawableRes


data class DealItem(
    val id: Int,
    @DrawableRes val imageRes: Int,
    val title: String,
    val description: String
)

