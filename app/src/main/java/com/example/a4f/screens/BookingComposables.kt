// trong com/example/a4f/screens/BookingComposables.kt


package com.example.a4f.screens




import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.a4f.screens.BookingRoutes // <-- DÒNG QUAN TRỌNG ĐÂY


// --- KHÔNG CÓ `object BookingRoutes` ở đây ---


// --- Composable cho TOP BAR (Quay về bản cũ, không có nút back) ---
@Composable
fun BookingTopBar(
    departure: String,
    destination: String,
    date: String,
    backgroundColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(48.dp))


        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$departure ➔ $destination",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = date,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Spacer(modifier = Modifier.width(48.dp))
    }
}


// --- Composable cho STEPPER (Bản ổn định) ---
@Composable
fun BookingStepper(currentStepRoute: String, activeColor: Color) {
    // Nó sẽ xài `BookingRoutes` (đã import) từ file BookingScreen.kt
    val steps = listOf(
        BookingRoutes.FIND_TRIP to "Thời gian",
        BookingRoutes.SELECT_SEAT to "CHỌN GHẾ",
        BookingRoutes.FILL_INFO to "Thông tin",
        BookingRoutes.PAYMENT to "Thanh toán"
    )
    val currentStepIndex = steps.indexOfFirst { it.first == currentStepRoute }.coerceAtLeast(0)


    val inactiveDotColor = Color(0xFFB0BEC5)
    val activeDotColor = activeColor


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StepItem(steps[0].second, currentStepIndex == 0, activeColor)
            StepSpacer(currentStepIndex == 0)
            StepItem(steps[1].second, currentStepIndex == 1, activeColor)
            StepSpacer(currentStepIndex == 1)
            StepItem(steps[2].second, currentStepIndex == 2, activeColor)
            StepSpacer(currentStepIndex == 2)
            StepItem(steps[3].second, currentStepIndex == 3, activeColor)
        }


        Spacer(modifier = Modifier.height(8.dp))


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxWidth()) {
                drawLine(
                    color = inactiveDotColor,
                    start = androidx.compose.ui.geometry.Offset(0f, center.y),
                    end = androidx.compose.ui.geometry.Offset(size.width, center.y),
                    strokeWidth = 2.dp.toPx()
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                steps.forEachIndexed { index, _ ->
                    val isActive = index == currentStepIndex
                    StepDot(
                        isActive = isActive,
                        activeColor = activeDotColor,
                        inactiveColor = inactiveDotColor
                    )
                }
            }
        }
    }
}


@Composable
fun RowScope.StepItem(title: String, isActive: Boolean, activeColor: Color) {
    val weight = if (isActive) 1.5f else 1f
    Box(
        modifier = Modifier.weight(weight),
        contentAlignment = Alignment.Center
    ) {
        if (isActive) {
            Button(
                onClick = { /* No-op */ },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = activeColor),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Clip
                )
            }
        } else {
            Text(
                text = title,
                color = Color.Gray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                textAlign = TextAlign.Center
            )
        }
    }
}


@Composable
fun RowScope.StepSpacer(isActive: Boolean) {
    if (isActive) {
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Tiếp theo",
            tint = Color.Gray,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    } else {
        Spacer(modifier = Modifier.width(28.dp))
    }
}


@Composable
fun StepDot(isActive: Boolean, activeColor: Color, inactiveColor: Color) {
    Box(
        modifier = Modifier
            .size(12.dp)
            .clip(CircleShape)
            .background(if (isActive) activeColor else inactiveColor)
            .border(2.dp, Color.White, CircleShape)
    )
}

