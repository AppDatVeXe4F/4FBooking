package com.example.a4f.screens


import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun OnboardingPage(
    modifier: Modifier = Modifier,
    @DrawableRes imageRes: Int,
    title: String,
    description: String,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 60.dp)
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = description,
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
        }


        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center)
                .width(500.dp),
            contentScale = ContentScale.FillWidth
        )
    }
}



