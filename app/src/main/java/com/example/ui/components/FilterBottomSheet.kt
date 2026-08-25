package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.viewmodel.SearchFilterState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheet(
    sheetState: SheetState,
    filterState: SearchFilterState,
    onApply: (
        minRent: Int,
        maxRent: Int,
        types: Set<String>,
        furnishings: Set<String>,
        facilities: Set<String>,
        gender: String,
        availability: String,
        sortBy: String,
        area: String,
        college: String
    ) -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit
) {
    var minRent by remember { mutableFloatStateOf(filterState.minRent.toFloat()) }
    var maxRent by remember { mutableFloatStateOf(filterState.maxRent.toFloat().coerceAtLeast(12000f)) }
    var selectedTypes by remember { mutableStateOf(filterState.selectedPropertyTypes) }
    var selectedFurnishing by remember { mutableStateOf(filterState.selectedFurnishing) }
    var selectedFacilities by remember { mutableStateOf(filterState.selectedFacilities) }
    var selectedGender by remember { mutableStateOf(filterState.genderPreference) }
    var selectedAvailability by remember { mutableStateOf(filterState.availability) }
    var selectedSortBy by remember { mutableStateOf(filterState.sortBy) }
    var selectedArea by remember { mutableStateOf(filterState.area) }
    var selectedCollege by remember { mutableStateOf(filterState.college) }

    val propertyTypes = listOf(
        "Single Room", "Shared Room", "PG", "Hostel", "1 BHK", "2 BHK", "3 BHK", "Flat", "House"
    )

    val furnishingOptions = listOf(
        "Fully Furnished", "Semi Furnished", "Unfurnished"
    )

    val allFacilities = listOf(
        "Wi-Fi", "AC", "Cooler", "Fan", "Bed", "Table", "Chair",
        "Wardrobe", "Kitchen", "Parking", "Washing Machine",
        "Electricity Included", "Water Included", "Food Included", "Attached Bathroom", "Balcony"
    )

    val sortOptions = listOf(
        "Recommended", "Nearest", "Lowest Rent", "Highest Rent", "Newest", "Highest Rated"
    )

    val genderOptions = listOf("Any", "Male", "Female")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filters & Sort",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Reset",
                        color = PrimaryIndigo,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clickable {
                                minRent = 0f
                                maxRent = 15000f
                                selectedTypes = emptySet()
                                selectedFurnishing = emptySet()
                                selectedFacilities = emptySet()
                                selectedGender = "Any"
                                selectedAvailability = "Any"
                                selectedSortBy = "Recommended"
                                selectedArea = ""
                                selectedCollege = ""
                                onReset()
                            }
                            .padding(8.dp)
                            .testTag("reset_filters_button")
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sort By Section
            SectionHeader("Sort By")
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                sortOptions.forEach { sort ->
                    FilterChip(
                        selected = selectedSortBy == sort,
                        onClick = { selectedSortBy = sort },
                        label = { Text(sort, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Rent Range Section
            SectionHeader("Monthly Rent Range: ₹${minRent.toInt()} - ₹${maxRent.toInt()}")
            RangeSlider(
                value = minRent..maxRent,
                onValueChange = { range ->
                    minRent = range.start
                    maxRent = range.endInclusive
                },
                valueRange = 0f..25000f,
                steps = 49,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("rent_range_slider")
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("₹0", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("₹25,000+", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Property Type Section
            SectionHeader("Property Type")
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                propertyTypes.forEach { type ->
                    val isSelected = selectedTypes.contains(type)
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedTypes = if (isSelected) selectedTypes - type else selectedTypes + type
                        },
                        label = { Text(type, fontSize = 12.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Furnishing Section
            SectionHeader("Furnishing")
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                furnishingOptions.forEach { furnishing ->
                    val isSelected = selectedFurnishing.contains(furnishing)
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedFurnishing = if (isSelected) selectedFurnishing - furnishing else selectedFurnishing + furnishing
                        },
                        label = { Text(furnishing, fontSize = 12.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Gender Preference Section
            SectionHeader("Suitable For / Gender")
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                genderOptions.forEach { gender ->
                    FilterChip(
                        selected = selectedGender == gender,
                        onClick = { selectedGender = gender },
                        label = { Text(gender, fontSize = 12.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Facilities Section
            SectionHeader("Facilities & Amenities")
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                allFacilities.forEach { facility ->
                    val isSelected = selectedFacilities.contains(facility)
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedFacilities = if (isSelected) selectedFacilities - facility else selectedFacilities + facility
                        },
                        label = { Text(facility, fontSize = 11.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Bottom Action Button
            Button(
                onClick = {
                    onApply(
                        minRent.toInt(),
                        maxRent.toInt(),
                        selectedTypes,
                        selectedFurnishing,
                        selectedFacilities,
                        selectedGender,
                        selectedAvailability,
                        selectedSortBy,
                        selectedArea,
                        selectedCollege
                    )
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("apply_filters_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Apply Filters",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}
