package com.example.ui.screens.post

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DemoData
import com.example.data.model.PropertyEntity
import com.example.data.model.UserEntity
import com.example.ui.components.PropertyCard
import com.example.ui.components.RoomVisualBox
import com.example.ui.theme.EmeraldVerified
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.viewmodel.AppViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PostPropertyScreen(
    currentUser: UserEntity?,
    viewModel: AppViewModel,
    onFinish: (PropertyEntity?) -> Unit,
    onCancel: () -> Unit
) {
    var step by remember { mutableIntStateOf(1) }
    val totalSteps = 8

    // Form state
    var propertyType by remember { mutableStateOf("Single Room") }
    var title by remember { mutableStateOf("") }
    var monthlyRent by remember { mutableStateOf("5000") }
    var securityDeposit by remember { mutableStateOf("5000") }
    var availableFrom by remember { mutableStateOf("Immediately") }
    var description by remember { mutableStateOf("") }

    var photosList by remember { mutableStateOf(listOf("room_front", "room_study", "room_bathroom")) }

    var selectedFacilities by remember {
        mutableStateOf(
            setOf("Wi-Fi", "Bed", "Fan", "Table", "Chair", "Wardrobe", "Water", "Electricity", "Attached Bathroom")
        )
    }

    var city by remember { mutableStateOf(currentUser?.currentCity ?: "Gwalior") }
    var area by remember { mutableStateOf(currentUser?.currentArea ?: "Thatipur") }
    var landmark by remember { mutableStateOf("Near University Campus") }
    var address by remember { mutableStateOf("Mayur Nagar, Thatipur, Gwalior") }

    var genderPreference by remember { mutableStateOf("Any") }
    var suitableFor by remember { mutableStateOf("Students") }

    var ownerName by remember { mutableStateOf(currentUser?.username ?: "Property Owner") }
    var ownerPhone by remember { mutableStateOf("+91 98260 55443") }
    var preferredContact by remember { mutableStateOf("Call") }

    var createdPropertyResult by remember { mutableStateOf<PropertyEntity?>(null) }
    var isPublishedSuccess by remember { mutableStateOf(false) }

    val allFacilityOptions = listOf(
        "Wi-Fi", "AC", "Cooler", "Fan", "Bed", "Mattress", "Table", "Chair",
        "Wardrobe", "Kitchen", "Parking", "Water", "Electricity",
        "Washing Machine", "Food", "Attached Bathroom", "Balcony"
    )

    val propertyTypeOptions = listOf(
        "Single Room", "Shared Room", "PG", "Hostel", "1 BHK", "2 BHK", "3 BHK", "House", "Other"
    )

    if (isPublishedSuccess && createdPropertyResult != null) {
        // Success Screen (Section 15)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFD1FAE5),
                        modifier = Modifier.size(90.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("🎉", fontSize = 44.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Your property has been listed!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Students in Gwalior will now see your listing in search, nearby rooms and recommendations.",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Preview of the created card
                    createdPropertyResult?.let { prop ->
                        PropertyCard(
                            property = prop,
                            isSaved = false,
                            onSaveToggle = {},
                            onClick = { onFinish(prop) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = { onFinish(createdPropertyResult) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("success_view_listing_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("View Listing", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            isPublishedSuccess = false
                            step = 1
                            title = ""
                            description = ""
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("success_add_another_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Add Another Property")
                    }

                    OutlinedButton(
                        onClick = { onFinish(null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("success_go_home_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Go Home")
                    }
                }
            }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header with step progress
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                if (step > 1) step-- else onCancel()
                            }
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "List Your Property",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "Step $step of $totalSteps",
                        fontSize = 13.sp,
                        color = PrimaryIndigo,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { step.toFloat() / totalSteps.toFloat() },
                    color = PrimaryIndigo,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                )
            }
        }

        // Step Form Content
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            when (step) {
                // STEP 1 — PROPERTY TYPE
                1 -> {
                    Text("STEP 1", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryIndigo)
                    Text("Choose Property Type", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Select what kind of rental space you want to list", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(modifier = Modifier.height(18.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        propertyTypeOptions.forEach { type ->
                            val isSelected = propertyType == type
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                                ),
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, PrimaryIndigo) else null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { propertyType = type }
                                    .testTag("prop_type_$type")
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = type,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) PrimaryIndigo else MaterialTheme.colorScheme.onSurface
                                    )
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { propertyType = type }
                                    )
                                }
                            }
                        }
                    }
                }

                // STEP 2 — BASIC DETAILS
                2 -> {
                    Text("STEP 2", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryIndigo)
                    Text("Basic Details & Rent", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Enter rent, deposit and room title", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(modifier = Modifier.height(18.dp))

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Property Title") },
                        placeholder = { Text("e.g. Single Furnished Room Near Vikrant University") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("post_title_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = monthlyRent,
                        onValueChange = { monthlyRent = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Monthly Rent (₹)") },
                        placeholder = { Text("5000") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("post_rent_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = securityDeposit,
                        onValueChange = { securityDeposit = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Security Deposit (₹)") },
                        placeholder = { Text("5000") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = availableFrom,
                        onValueChange = { availableFrom = it },
                        label = { Text("Available From") },
                        placeholder = { Text("Immediately / 1st of next month") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Property Description") },
                        placeholder = { Text("Describe room features, study environment, distance to campus...") },
                        minLines = 4,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // STEP 3 — PROPERTY PHOTOS
                3 -> {
                    Text("STEP 3", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryIndigo)
                    Text("Property Photos", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Recommended: at least 3 photos (Front, Room, Bathroom, Kitchen)", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(modifier = Modifier.height(18.dp))

                    Text("Selected Photos (${photosList.size}):", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        photosList.forEachIndexed { index, photoKey ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.size(64.dp, 48.dp)
                                    ) {
                                        RoomVisualBox(photoKey = photoKey, propertyType = propertyType, modifier = Modifier.fillMaxSize())
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Photo #${index + 1}: ${photoKey.replace("room_", "").replace("_", " ").replaceFirstChar { it.uppercase() }}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                        Text("High quality preview ready", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    IconButton(
                                        onClick = {
                                            if (photosList.size > 1) {
                                                photosList = photosList.toMutableList().apply { removeAt(index) }
                                            }
                                        }
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444))
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Add Room Photo Preset:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("room_front", "room_study", "room_balcony", "room_bed", "room_bathroom", "flat_kitchen", "flat_hall").forEach { preset ->
                            OutlinedButton(
                                onClick = {
                                    photosList = photosList + preset
                                },
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("+ ${preset.replace("room_", "").replace("flat_", "").replaceFirstChar { it.uppercase() }}", fontSize = 11.sp)
                            }
                        }
                    }
                }

                // STEP 4 — FACILITIES
                4 -> {
                    Text("STEP 4", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryIndigo)
                    Text("Facilities & Amenities", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Check all facilities included with this room", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(modifier = Modifier.height(16.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        allFacilityOptions.forEach { facility ->
                            val isChecked = selectedFacilities.contains(facility)
                            FilterChip(
                                selected = isChecked,
                                onClick = {
                                    selectedFacilities = if (isChecked) selectedFacilities - facility else selectedFacilities + facility
                                },
                                label = { Text(facility, fontSize = 12.sp) }
                            )
                        }
                    }
                }

                // STEP 5 — LOCATION
                5 -> {
                    Text("STEP 5", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryIndigo)
                    Text("Location & Address", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Help students find your room easily", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(modifier = Modifier.height(18.dp))

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                city = "Gwalior"
                                area = "Thatipur"
                                landmark = "Near Vikrant University Bus Route"
                                address = "House 42, Mayur Nagar, Thatipur, Gwalior"
                            }
                    ) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MyLocation, contentDescription = null, tint = PrimaryIndigo)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Use Current Location (Gwalior)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Auto-populates Thatipur, Gwalior", fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text("City") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = area,
                        onValueChange = { area = it },
                        label = { Text("Area / Locality") },
                        placeholder = { Text("e.g. Thatipur, City Centre, Morar") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = landmark,
                        onValueChange = { landmark = it },
                        label = { Text("Nearby Landmark / College") },
                        placeholder = { Text("e.g. Near Vikrant University / Jiwaji Gate") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Full Address (Shared upon request)") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // STEP 6 — PREFERENCES
                6 -> {
                    Text("STEP 6", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryIndigo)
                    Text("Tenant Preferences", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Select who is suitable for this room", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(modifier = Modifier.height(18.dp))

                    Text("Gender Suitability:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Any", "Male", "Female").forEach { g ->
                            FilterChip(
                                selected = genderPreference == g,
                                onClick = { genderPreference = g },
                                label = { Text(g) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text("Suitable For:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Students", "Working Professionals", "Family", "Any").forEach { s ->
                            FilterChip(
                                selected = suitableFor == s,
                                onClick = { suitableFor = s },
                                label = { Text(s) }
                            )
                        }
                    }
                }

                // STEP 7 — CONTACT INFO
                7 -> {
                    Text("STEP 7", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryIndigo)
                    Text("Owner Contact Info", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("How should interested students reach you?", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(modifier = Modifier.height(18.dp))

                    OutlinedTextField(
                        value = ownerName,
                        onValueChange = { ownerName = it },
                        label = { Text("Owner / Host Name") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = ownerPhone,
                        onValueChange = { ownerPhone = it },
                        label = { Text("Contact Phone Number") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Preferred Contact Mode:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Call", "Message", "Both").forEach { mode ->
                            FilterChip(
                                selected = preferredContact == mode,
                                onClick = { preferredContact = mode },
                                label = { Text(mode) }
                            )
                        }
                    }
                }

                // STEP 8 — PREVIEW & PUBLISH
                8 -> {
                    Text("STEP 8", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryIndigo)
                    Text("Preview Your Listing", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("This is exactly how students will see your property", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(modifier = Modifier.height(16.dp))

                    val previewProperty = PropertyEntity(
                        id = "preview_id",
                        title = title.ifBlank { "$propertyType in $area" },
                        description = description.ifBlank { "Clean room suitable for students near campus." },
                        propertyType = propertyType,
                        monthlyRent = monthlyRent.toIntOrNull() ?: 5000,
                        securityDeposit = securityDeposit.toIntOrNull() ?: 5000,
                        availableFrom = availableFrom,
                        furnishing = "Fully Furnished",
                        genderPreference = genderPreference,
                        suitableFor = suitableFor,
                        status = "ACTIVE",
                        city = city,
                        area = area,
                        landmark = landmark,
                        address = address,
                        distanceKm = 1.0,
                        isVerified = true,
                        isDemo = false,
                        ownerName = ownerName,
                        ownerPhone = ownerPhone,
                        facilities = selectedFacilities.joinToString(","),
                        photos = photosList.joinToString(",")
                    )

                    PropertyCard(
                        property = previewProperty,
                        isSaved = false,
                        onSaveToggle = {},
                        onClick = {},
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Bottom Nav Buttons
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (step > 1) {
                    OutlinedButton(
                        onClick = { step-- },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Text("Previous")
                    }
                }

                Button(
                    onClick = {
                        if (step < totalSteps) {
                            if (step == 2 && title.isBlank()) {
                                title = "$propertyType Near $landmark"
                            }
                            step++
                        } else {
                            // Publish Listing
                            val rentVal = monthlyRent.toIntOrNull() ?: 5000
                            val depVal = securityDeposit.toIntOrNull() ?: rentVal
                            viewModel.publishProperty(
                                title = title.ifBlank { "$propertyType in $area" },
                                description = description.ifBlank { "Clean room with good ventilation for students." },
                                propertyType = propertyType,
                                monthlyRent = rentVal,
                                securityDeposit = depVal,
                                availableFrom = availableFrom,
                                furnishing = "Fully Furnished",
                                genderPreference = genderPreference,
                                suitableFor = suitableFor,
                                city = city,
                                area = area,
                                landmark = landmark,
                                address = address,
                                facilitiesList = selectedFacilities.toList(),
                                photosList = photosList,
                                ownerName = ownerName,
                                ownerPhone = ownerPhone,
                                preferredContact = preferredContact,
                                onSuccess = { created ->
                                    createdPropertyResult = created
                                    isPublishedSuccess = true
                                }
                            )
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag(if (step == totalSteps) "publish_listing_button" else "next_step_button")
                ) {
                    Text(
                        text = if (step == totalSteps) "Publish Listing" else "Next Step",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
