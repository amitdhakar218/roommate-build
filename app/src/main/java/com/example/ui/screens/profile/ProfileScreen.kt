package com.example.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.PropertyEntity
import com.example.data.model.RentalRequestEntity
import com.example.data.model.UserEntity
import com.example.ui.components.LocationSelectorDialog
import com.example.ui.components.RoomVisualBox
import com.example.ui.theme.EmeraldVerified
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.viewmodel.AppViewModel

@Composable
fun ProfileScreen(
    currentUser: UserEntity?,
    viewModel: AppViewModel,
    onNavigateToPost: () -> Unit,
    onNavigateToProperty: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val allProperties by viewModel.allProperties.collectAsStateWithLifecycle()
    val allRentalRequests by viewModel.allRentalRequests.collectAsStateWithLifecycle()
    val savedIds by viewModel.savedPropertyIds.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) }
    var showLocationDialog by remember { mutableStateOf(false) }
    var showFaqDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showResetConfirmation by remember { mutableStateOf(false) }

    if (showLocationDialog) {
        LocationSelectorDialog(
            currentCity = currentUser?.currentCity ?: "Gwalior",
            currentArea = currentUser?.currentArea ?: "Thatipur",
            currentCollege = currentUser?.currentCollege ?: "Vikrant University",
            onLocationSelected = { city, area, college ->
                viewModel.updateLocation(city, area, college)
            },
            onDismiss = { showLocationDialog = false }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // User Profile Header Card
        item {
            Card(
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(56.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = PrimaryIndigo,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = currentUser?.username ?: "Student User",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Active Role: ${if (currentUser?.selectedRole == "OWNER") "Room Owner" else "Student"}",
                                    fontSize = 12.sp,
                                    color = PrimaryIndigo,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        // Switch Role Pill
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier
                                .clickable {
                                    val nextRole = if (currentUser?.selectedRole == "OWNER") "STUDENT" else "OWNER"
                                    viewModel.switchRole(nextRole)
                                }
                                .testTag("profile_switch_role_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = PrimaryIndigo, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (currentUser?.selectedRole == "OWNER") "Switch to Student" else "Switch to Owner",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryIndigo
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stats row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ProfileStatItem("Saved", "${savedIds.size}")
                        ProfileStatItem("Requests", "${allRentalRequests.size}")
                        ProfileStatItem("City", currentUser?.currentCity ?: "Gwalior")
                    }
                }
            }
        }

        // Section Tabs
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)) {
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text(if (currentUser?.selectedRole == "OWNER") "Owner Listings" else "Student Dashboard", fontWeight = FontWeight.SemiBold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Rental Requests (${allRentalRequests.size})", fontWeight = FontWeight.SemiBold) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Settings", fontWeight = FontWeight.SemiBold) }
                    )
                }
            }
        }

        when (selectedTab) {
            // TAB 0: Role-specific Dashboard
            0 -> {
                if (currentUser?.selectedRole == "OWNER") {
                    // Owner Listings Management (Section 16)
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Your Properties", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Button(
                                onClick = onNavigateToPost,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("owner_add_property_button")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add Room")
                            }
                        }
                    }

                    val ownerListings = allProperties.filter { it.ownerId == currentUser.id || !it.isDemo }
                    if (ownerListings.isEmpty()) {
                        item {
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp)
                            ) {
                                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("🏠", fontSize = 36.sp)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("You haven't listed any rooms yet", fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("List your room or PG to get verified inquiries from Gwalior students.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Button(onClick = onNavigateToPost) {
                                        Text("List Property Now")
                                    }
                                }
                            }
                        }
                    } else {
                        items(ownerListings) { prop ->
                            OwnerPropertyItem(
                                property = prop,
                                onStatusToggle = {
                                    val newStatus = if (prop.status == "ACTIVE") "OCCUPIED" else "ACTIVE"
                                    viewModel.updatePropertyStatus(prop.id, newStatus)
                                },
                                onDelete = { viewModel.deleteProperty(prop.id) },
                                onClick = { onNavigateToProperty(prop.id) }
                            )
                        }
                    }
                } else {
                    // Student Dashboard (Section 20)
                    item {
                        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                            Text("Student Hub", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(10.dp))

                            ElevatedCard(
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("🎓 Preferred Campus & Locality", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("${currentUser?.currentCollege ?: "Vikrant University"} • ${currentUser?.currentArea ?: "Thatipur"}", fontSize = 13.sp, color = PrimaryIndigo)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    OutlinedButton(
                                        onClick = { showLocationDialog = true },
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Change Campus / Area")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // TAB 1: Rental Requests Manager (Section 17)
            1 -> {
                if (allRentalRequests.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("📬", fontSize = 36.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("No rental requests yet", fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Requests sent by students to room owners will appear here with live status.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                } else {
                    items(allRentalRequests) { req ->
                        RentalRequestItem(
                            request = req,
                            onAccept = { viewModel.updateRentalRequestStatus(req.id, "ACCEPTED") },
                            onReject = { viewModel.updateRentalRequestStatus(req.id, "REJECTED") }
                        )
                    }
                }
            }

            // TAB 2: Settings & App Management (Section 21)
            2 -> {
                item {
                    Column(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SettingsRowItem(
                            icon = Icons.Default.LocationOn,
                            title = "Location Preferences",
                            subtitle = "${currentUser?.currentArea ?: "Thatipur"}, ${currentUser?.currentCity ?: "Gwalior"}",
                            onClick = { showLocationDialog = true }
                        )

                        SettingsRowItem(
                            icon = Icons.Default.HelpOutline,
                            title = "FAQ & Student Guide",
                            subtitle = "How room booking & owner direct contact works",
                            onClick = { showFaqDialog = true }
                        )

                        SettingsRowItem(
                            icon = Icons.Default.Info,
                            title = "About RoomMate",
                            subtitle = "Version 1.0.0 • Designed for Student Rental Marketplace",
                            onClick = { showAboutDialog = true }
                        )

                        SettingsRowItem(
                            icon = Icons.Default.Refresh,
                            title = "Reset Testing Data",
                            subtitle = "Re-seed Gwalior rooms and clear local changes",
                            onClick = { showResetConfirmation = true }
                        )
                    }
                }
            }
        }
    }

    // FAQ Dialog
    if (showFaqDialog) {
        AlertDialog(
            onDismissRequest = { showFaqDialog = false },
            title = { Text("RoomMate FAQ", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("❓ Is there any brokerage fee?\nNo! RoomMate is 100% free with direct owner connect.", fontSize = 13.sp)
                    Text("❓ How do I verify a property?\nLook for the 'Verified' green badge inspected by our community team in Gwalior.", fontSize = 13.sp)
                    Text("❓ Can I switch between Owner and Student?\nYes! Tap 'Switch Role' anytime from Home or Profile.", fontSize = 13.sp)
                }
            },
            confirmButton = {
                Button(onClick = { showFaqDialog = false }) { Text("Got it") }
            }
        )
    }

    // About Dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("About RoomMate", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "RoomMate is a mobile-first room rental marketplace connecting students and property owners across Gwalior.\n\n" +
                            "Features: Smart Location & Budget Ranking, Verified Badges, Multi-Room Comparison, Rental Requests and Direct Calling.",
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(onClick = { showAboutDialog = false }) { Text("Close") }
            }
        )
    }

    // Reset Confirmation Dialog
    if (showResetConfirmation) {
        AlertDialog(
            onDismissRequest = { showResetConfirmation = false },
            title = { Text("Reset Demo Data?") },
            text = { Text("This will restore default Gwalior demo properties and clear saved lists.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetTestingData()
                        showResetConfirmation = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Reset")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showResetConfirmation = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun ProfileStatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryIndigo)
        Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun OwnerPropertyItem(
    property: PropertyEntity,
    onStatusToggle: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(70.dp, 54.dp)
                ) {
                    RoomVisualBox(
                        photoKey = property.photos.split(",").firstOrNull() ?: "room_front",
                        propertyType = property.propertyType,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(property.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                    Text("₹${property.monthlyRent}/mo • ${property.area}", fontSize = 12.sp, color = PrimaryIndigo, fontWeight = FontWeight.SemiBold)
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = if (property.status == "ACTIVE") Color(0xFFD1FAE5) else Color(0xFFFEE2E2),
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Text(
                            text = if (property.status == "ACTIVE") "Active Listing" else "Occupied / Inactive",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (property.status == "ACTIVE") Color(0xFF065F46) else Color(0xFF991B1B),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilledTonalButton(
                    onClick = onStatusToggle,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (property.status == "ACTIVE") "Mark Occupied" else "Mark Active", fontSize = 11.sp)
                }

                OutlinedButton(
                    onClick = onDelete,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444))
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun RentalRequestItem(
    request: RentalRequestEntity,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(request.studentName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("For: ${request.propertyTitle}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (request.status) {
                        "ACCEPTED" -> Color(0xFFD1FAE5)
                        "REJECTED" -> Color(0xFFFEE2E2)
                        else -> Color(0xFFFEF3C7)
                    }
                ) {
                    Text(
                        text = request.status,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (request.status) {
                            "ACCEPTED" -> Color(0xFF065F46)
                            "REJECTED" -> Color(0xFF991B1B)
                            else -> Color(0xFF92400E)
                        },
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text("Phone: ${request.studentPhone} • Move-in: ${request.moveInDate}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
            if (request.message.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text("\"${request.message}\"", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            if (request.status == "PENDING") {
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onAccept,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldVerified),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Accept", fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = onReject,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Decline", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsRowItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = PrimaryIndigo, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
