package com.example.ui.screens.saved

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PropertyEntity
import com.example.ui.components.RoomVisualBox
import com.example.ui.theme.AmberRating
import com.example.ui.theme.PrimaryIndigo

@Composable
fun CompareRoomsScreen(
    properties: List<PropertyEntity>,
    onPropertyClick: (String) -> Unit,
    onRemoveFromCompare: (String) -> Unit,
    onBack: () -> Unit
) {
    val comparisonFeatures = listOf(
        "Monthly Rent",
        "Security Deposit",
        "Property Type",
        "Distance to Campus",
        "Furnishing",
        "Gender Suitability",
        "Wi-Fi Included",
        "AC / Cooler",
        "Attached Bathroom",
        "Food / Kitchen",
        "Rating & Reviews",
        "Owner Name"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Compare Rooms", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Side-by-side room analysis for students", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        if (properties.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("⚖️", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No rooms selected for comparison", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Save at least 2 properties to compare side-by-side.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onBack) {
                        Text("Go Back")
                    }
                }
            }
            return
        }

        // Table Matrix
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Horizontal scroll for multiple rooms side-by-side
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                // Column 1: Feature Labels
                Column(
                    modifier = Modifier.width(130.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Header space
                    Box(modifier = Modifier.height(140.dp), contentAlignment = Alignment.CenterStart) {
                        Text("Property\nComparison", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = PrimaryIndigo)
                    }

                    comparisonFeatures.forEach { feature ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = feature,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Property Columns
                properties.forEach { prop ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(2.dp),
                        modifier = Modifier
                            .width(200.dp)
                            .padding(start = 12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Top image & title
                            Box(modifier = Modifier.height(140.dp)) {
                                Column {
                                    Surface(shape = RoundedCornerShape(8.dp), modifier = Modifier.size(180.dp, 80.dp)) {
                                        RoomVisualBox(
                                            photoKey = prop.photos.split(",").firstOrNull() ?: "room_front",
                                            propertyType = prop.propertyType,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(prop.title, fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                }
                            }

                            // Values corresponding to comparisonFeatures:
                            // 1. Monthly Rent
                            CompareValueBox("₹${prop.monthlyRent} /mo", isHighlight = true)
                            // 2. Security Deposit
                            CompareValueBox("₹${prop.securityDeposit}")
                            // 3. Property Type
                            CompareValueBox(prop.propertyType)
                            // 4. Distance
                            CompareValueBox("${String.format("%.1f", prop.distanceKm)} km • ${prop.area}")
                            // 5. Furnishing
                            CompareValueBox(prop.furnishing)
                            // 6. Gender
                            CompareValueBox(prop.genderPreference)
                            // 7. Wi-Fi
                            CompareIconBox(prop.facilities.contains("Wi-Fi", ignoreCase = true))
                            // 8. AC
                            CompareIconBox(prop.facilities.contains("AC", ignoreCase = true) || prop.facilities.contains("Cooler", ignoreCase = true))
                            // 9. Bathroom
                            CompareIconBox(prop.facilities.contains("Bathroom", ignoreCase = true))
                            // 10. Food / Kitchen
                            CompareIconBox(prop.facilities.contains("Kitchen", ignoreCase = true) || prop.facilities.contains("Food", ignoreCase = true))
                            // 11. Rating
                            CompareValueBox("⭐ ${prop.ratingAvg} (${prop.reviewsCount})")
                            // 12. Owner
                            CompareValueBox(prop.ownerName)

                            Spacer(modifier = Modifier.height(6.dp))

                            Button(
                                onClick = { onPropertyClick(prop.id) },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("View Details", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun CompareValueBox(text: String, isHighlight: Boolean = false) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Medium,
            color = if (isHighlight) PrimaryIndigo else MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun CompareIconBox(hasFeature: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        if (hasFeature) {
            Icon(Icons.Default.Check, contentDescription = "Yes", tint = Color(0xFF10B981), modifier = Modifier.size(20.dp))
        } else {
            Icon(Icons.Default.Close, contentDescription = "No", tint = Color(0xFFEF4444), modifier = Modifier.size(20.dp))
        }
    }
}
