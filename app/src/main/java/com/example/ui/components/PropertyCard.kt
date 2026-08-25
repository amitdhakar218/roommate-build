package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Bathtub
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DinnerDining
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.LocalLaundryService
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SingleBed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PropertyEntity
import com.example.ui.theme.AmberRating
import com.example.ui.theme.EmeraldVerified
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.RoseFavorite

/**
 * Visual illustration representation for room types and photos
 */
@Composable
fun RoomVisualBox(
    photoKey: String,
    propertyType: String,
    modifier: Modifier = Modifier
) {
    val gradientColors = when {
        photoKey.contains("balcony") || propertyType == "PG" -> listOf(Color(0xFF0D9488), Color(0xFF065F46))
        photoKey.contains("hall") || propertyType.contains("BHK") -> listOf(Color(0xFF4F46E5), Color(0xFF312E81))
        photoKey.contains("study") || propertyType == "Single Room" -> listOf(Color(0xFF2563EB), Color(0xFF1E3A8A))
        photoKey.contains("shared") || propertyType == "Shared Room" -> listOf(Color(0xFF7C3AED), Color(0xFF4C1D95))
        else -> listOf(Color(0xFF0284C7), Color(0xFF0F172A))
    }

    val icon: ImageVector = when {
        photoKey.contains("balcony") -> Icons.Default.Apartment
        photoKey.contains("kitchen") -> Icons.Default.Kitchen
        photoKey.contains("bathroom") -> Icons.Default.Bathtub
        photoKey.contains("study") -> Icons.Default.School
        propertyType == "PG" -> Icons.Default.DinnerDining
        propertyType == "Single Room" -> Icons.Default.SingleBed
        propertyType.contains("BHK") -> Icons.Default.Home
        else -> Icons.Default.MeetingRoom
    }

    val label = when {
        photoKey.contains("study") -> "Study Room & Bed"
        photoKey.contains("balcony") -> "Balcony & View"
        photoKey.contains("bathroom") -> "Attached Bathroom"
        photoKey.contains("kitchen") -> "Kitchen Area"
        photoKey.contains("dining") -> "Dining & Food"
        photoKey.contains("hall") -> "Living Hall"
        propertyType == "PG" -> "PG & Food Included"
        propertyType == "Single Room" -> "Private Single Room"
        propertyType == "Shared Room" -> "Shared Double Room"
        propertyType == "1 BHK" -> "Independent 1 BHK"
        propertyType == "2 BHK" -> "Spacious 2 BHK"
        else -> "Room Photo"
    }

    Box(
        modifier = modifier
            .background(Brush.linearGradient(gradientColors))
    ) {
        // Decorative geometric backdrop
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(120.dp)
                .background(Color.White.copy(alpha = 0.08f), shape = CircleShape)
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(80.dp)
                .background(Color.White.copy(alpha = 0.05f), shape = CircleShape)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.2f),
                modifier = Modifier.size(54.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Clean & Well Ventilated",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 11.sp
            )
        }
    }
}

/**
 * Modern Property Card complying with Section 7 of specification
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PropertyCard(
    property: PropertyEntity,
    isSaved: Boolean,
    onSaveToggle: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("property_card_${property.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Image Box + Overlay Badges
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
            ) {
                val firstPhoto = property.photos.split(",").firstOrNull() ?: "room_front"
                RoomVisualBox(
                    photoKey = firstPhoto,
                    propertyType = property.propertyType,
                    modifier = Modifier.fillMaxSize()
                )

                // Top Left Badges: Verified / Demo
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (property.isVerified) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = EmeraldVerified,
                            shadowElevation = 2.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Verified",
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Verified",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    if (property.isDemo) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF64748B),
                            shadowElevation = 2.dp
                        ) {
                            Text(
                                text = "Demo Listing",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFEA580C),
                            shadowElevation = 2.dp
                        ) {
                            Text(
                                text = "NEW",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                // Top Right: Save Button
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .size(36.dp)
                        .testTag("save_button_${property.id}")
                        .clickable { onSaveToggle() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (isSaved) "Saved" else "Save Property",
                            tint = if (isSaved) RoseFavorite else Color(0xFF475569),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Bottom Left Property Type pill
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.65f),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(10.dp)
                ) {
                    Text(
                        text = property.propertyType,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Bottom Right Rating
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.7f),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = AmberRating,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = String.format("%.1f", property.ratingAvg),
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = " (${property.reviewsCount})",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 10.sp
                        )
                    }
                }
            }

            // Property Card Content Details
            Column(
                modifier = Modifier.padding(14.dp)
            ) {
                // Title
                Text(
                    text = property.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Location & Distance
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = PrimaryIndigo,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "${String.format("%.1f", property.distanceKm)} km • ${property.area}, ${property.city}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Facility Mini Chips
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    maxItemsInEachRow = 3
                ) {
                    val facilityList = property.facilities.split(",").take(3)
                    facilityList.forEach { facility ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.height(24.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val icon = getFacilityIcon(facility)
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = facility.trim(),
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Rent, Deposit and View Details Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "₹${property.monthlyRent}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryIndigo
                            )
                            Text(
                                text = " / month",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }
                        Text(
                            text = "Deposit: ₹${property.securityDeposit}",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    FilledTonalButton(
                        onClick = onClick,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("view_details_${property.id}")
                    ) {
                        Text(
                            text = "View Details",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

fun getFacilityIcon(facilityName: String): ImageVector {
    val lower = facilityName.lowercase().trim()
    return when {
        lower.contains("wi-fi") || lower.contains("wifi") -> Icons.Default.Wifi
        lower.contains("ac") -> Icons.Default.AcUnit
        lower.contains("cooler") || lower.contains("fan") -> Icons.Default.AcUnit
        lower.contains("bed") || lower.contains("mattress") -> Icons.Default.Bed
        lower.contains("kitchen") -> Icons.Default.Kitchen
        lower.contains("food") -> Icons.Default.DinnerDining
        lower.contains("parking") -> Icons.Default.LocalParking
        lower.contains("washing") || lower.contains("laundry") -> Icons.Default.LocalLaundryService
        lower.contains("water") -> Icons.Default.WaterDrop
        lower.contains("electricity") -> Icons.Default.ElectricBolt
        lower.contains("bathroom") -> Icons.Default.Bathtub
        else -> Icons.Default.CheckCircle
    }
}
