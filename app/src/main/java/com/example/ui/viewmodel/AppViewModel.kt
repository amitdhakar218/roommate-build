package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.model.PropertyEntity
import com.example.data.model.RentalRequestEntity
import com.example.data.model.ReportEntity
import com.example.data.model.ReviewEntity
import com.example.data.model.UserEntity
import com.example.data.repository.AppRepository
import com.example.data.repository.SmartRankingEngine
import com.example.data.repository.UserSearchContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

data class SearchFilterState(
    val query: String = "",
    val city: String = "Gwalior",
    val area: String = "",
    val college: String = "",
    val minRent: Int = 0,
    val maxRent: Int = 15000,
    val selectedPropertyTypes: Set<String> = emptySet(),
    val selectedFurnishing: Set<String> = emptySet(),
    val selectedFacilities: Set<String> = emptySet(),
    val genderPreference: String = "Any", // "Male", "Female", "Any"
    val availability: String = "Any", // "Available Now", "Available Soon", "Any"
    val sortBy: String = "Recommended", // "Recommended", "Nearest", "Lowest Rent", "Highest Rent", "Newest", "Highest Rated"
    val quickFilter: String? = null // e.g. "Under ₹5,000", "PG", "Furnished", etc.
)

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application, viewModelScope)
    val repository = AppRepository(database)

    val currentUser: StateFlow<UserEntity?> = repository.currentUser
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val allProperties: StateFlow<List<PropertyEntity>> = repository.allProperties
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val savedPropertyIds: StateFlow<Set<String>> = repository.getSavedPropertyIds()
        .combine(MutableStateFlow(Unit)) { list, _ -> list.toSet() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )

    val recentlyViewedIds: StateFlow<List<String>> = repository.recentlyViewedIds
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allRentalRequests: StateFlow<List<RentalRequestEntity>> = repository.allRentalRequests
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Search and Filter State
    private val _filterState = MutableStateFlow(SearchFilterState())
    val filterState: StateFlow<SearchFilterState> = _filterState.asStateFlow()

    // Active Selected Property for Detail Screen
    private val _selectedPropertyId = MutableStateFlow<String?>(null)
    val selectedPropertyId: StateFlow<String?> = _selectedPropertyId.asStateFlow()

    // Compare Room IDs (up to 3)
    private val _comparePropertyIds = MutableStateFlow<Set<String>>(emptySet())
    val comparePropertyIds: StateFlow<Set<String>> = _comparePropertyIds.asStateFlow()

    // Toast/Snackbar notification event
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    // Filtered & Ranked Properties Flow
    val filteredProperties: StateFlow<List<PropertyEntity>> = combine(
        allProperties,
        filterState,
        currentUser
    ) { properties, filter, user ->
        val activeProps = properties.filter { it.status == "ACTIVE" }

        val searchContext = UserSearchContext(
            selectedCity = user?.currentCity ?: filter.city,
            selectedArea = if (filter.area.isNotBlank()) filter.area else (user?.currentArea ?: ""),
            selectedCollege = if (filter.college.isNotBlank()) filter.college else (user?.currentCollege ?: ""),
            targetMaxRent = filter.maxRent,
            targetMinRent = filter.minRent,
            preferredPropertyType = filter.selectedPropertyTypes.firstOrNull(),
            genderPreference = if (filter.genderPreference != "Any") filter.genderPreference else null,
            requiredFacilities = filter.selectedFacilities.toList()
        )

        val filtered = activeProps.filter { prop ->
            // Query filter (matches title, area, landmark, near college, description)
            val matchesQuery = filter.query.isBlank() ||
                    prop.title.contains(filter.query, ignoreCase = true) ||
                    prop.area.contains(filter.query, ignoreCase = true) ||
                    prop.landmark.contains(filter.query, ignoreCase = true) ||
                    prop.nearCollege.contains(filter.query, ignoreCase = true) ||
                    prop.propertyType.contains(filter.query, ignoreCase = true) ||
                    prop.description.contains(filter.query, ignoreCase = true)

            // Area filter
            val matchesArea = filter.area.isBlank() || prop.area.equals(filter.area, ignoreCase = true)

            // College filter
            val matchesCollege = filter.college.isBlank() ||
                    prop.nearCollege.contains(filter.college, ignoreCase = true) ||
                    prop.title.contains(filter.college, ignoreCase = true)

            // Rent filter
            val matchesRent = prop.monthlyRent in filter.minRent..filter.maxRent

            // Property type filter
            val matchesType = filter.selectedPropertyTypes.isEmpty() ||
                    filter.selectedPropertyTypes.any { it.equals(prop.propertyType, ignoreCase = true) }

            // Furnishing filter
            val matchesFurnishing = filter.selectedFurnishing.isEmpty() ||
                    filter.selectedFurnishing.any { it.equals(prop.furnishing, ignoreCase = true) }

            // Facilities filter (must have all selected)
            val matchesFacilities = filter.selectedFacilities.isEmpty() ||
                    filter.selectedFacilities.all { required ->
                        prop.facilities.contains(required, ignoreCase = true)
                    }

            // Gender preference
            val matchesGender = filter.genderPreference == "Any" ||
                    prop.genderPreference.equals("Any", ignoreCase = true) ||
                    prop.genderPreference.equals(filter.genderPreference, ignoreCase = true)

            // Quick Filter logic
            val matchesQuickFilter = when (filter.quickFilter) {
                "Under ₹5,000" -> prop.monthlyRent <= 5000
                "Under ₹7,000" -> prop.monthlyRent <= 7000
                "Under ₹10,000" -> prop.monthlyRent <= 10000
                "Near Me" -> prop.distanceKm <= 2.0
                "Single Room" -> prop.propertyType.equals("Single Room", ignoreCase = true)
                "PG" -> prop.propertyType.equals("PG", ignoreCase = true)
                "1 BHK" -> prop.propertyType.equals("1 BHK", ignoreCase = true)
                "2 BHK" -> prop.propertyType.equals("2 BHK", ignoreCase = true)
                "Furnished" -> prop.furnishing.contains("Furnished", ignoreCase = true)
                "Verified" -> prop.isVerified
                else -> true
            }

            matchesQuery && matchesArea && matchesCollege && matchesRent &&
                    matchesType && matchesFurnishing && matchesFacilities && matchesGender && matchesQuickFilter
        }

        // Sorting
        when (filter.sortBy) {
            "Recommended" -> {
                SmartRankingEngine.rankProperties(filtered, searchContext).map { it.first }
            }
            "Nearest" -> filtered.sortedBy { it.distanceKm }
            "Lowest Rent" -> filtered.sortedBy { it.monthlyRent }
            "Highest Rent" -> filtered.sortedByDescending { it.monthlyRent }
            "Newest" -> filtered.sortedByDescending { it.createdAt }
            "Highest Rated" -> filtered.sortedByDescending { it.ratingAvg }
            else -> filtered
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // User & Onboarding
    fun completeOnboarding(username: String, role: String) {
        viewModelScope.launch {
            val existing = repository.currentUser
            val user = UserEntity(
                id = "current_user",
                username = username.trim().ifBlank { "Student" },
                selectedRole = role,
                isInitialOnboardingCompleted = true,
                currentCity = "Gwalior",
                currentArea = "Thatipur",
                currentCollege = "Vikrant University"
            )
            repository.saveUser(user)
        }
    }

    fun switchRole(newRole: String) {
        viewModelScope.launch {
            repository.updateUserRole(newRole)
            _userMessage.value = "Switched to ${if (newRole == "OWNER") "Owner / Room Rent Par Dena Hai" else "Student / Room Chahiye"} mode"
        }
    }

    fun updateLocation(city: String, area: String, college: String) {
        viewModelScope.launch {
            repository.updateUserLocation(city, area, college)
            _filterState.value = _filterState.value.copy(city = city, area = area, college = college)
            _userMessage.value = "Location set to $area, $city"
        }
    }

    fun updateSearchQuery(query: String) {
        _filterState.value = _filterState.value.copy(query = query)
    }

    fun setQuickFilter(quickFilter: String?) {
        val current = _filterState.value.quickFilter
        _filterState.value = _filterState.value.copy(
            quickFilter = if (current == quickFilter) null else quickFilter
        )
    }

    fun applyDetailedFilters(
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
    ) {
        _filterState.value = _filterState.value.copy(
            minRent = minRent,
            maxRent = maxRent,
            selectedPropertyTypes = types,
            selectedFurnishing = furnishings,
            selectedFacilities = facilities,
            genderPreference = gender,
            availability = availability,
            sortBy = sortBy,
            area = area,
            college = college
        )
    }

    fun resetFilters() {
        val user = currentUser.value
        _filterState.value = SearchFilterState(
            city = user?.currentCity ?: "Gwalior",
            area = "",
            college = ""
        )
    }

    // Property Selection & Tracking
    fun selectProperty(propertyId: String) {
        _selectedPropertyId.value = propertyId
        viewModelScope.launch {
            repository.markAsRecentlyViewed(propertyId)
        }
    }

    // Toggle Saved / Favorite
    fun toggleSaveProperty(propertyId: String) {
        viewModelScope.launch {
            val isSaved = savedPropertyIds.value.contains(propertyId)
            repository.toggleSaveProperty(propertyId, isSaved)
            _userMessage.value = if (isSaved) "Removed from Saved" else "Saved to your list ❤️"
        }
    }

    // Compare Rooms
    fun toggleCompare(propertyId: String) {
        val current = _comparePropertyIds.value
        if (current.contains(propertyId)) {
            _comparePropertyIds.value = current - propertyId
        } else {
            if (current.size >= 3) {
                _userMessage.value = "You can compare up to 3 rooms at a time"
            } else {
                _comparePropertyIds.value = current + propertyId
                _userMessage.value = "Added to comparison (${_comparePropertyIds.value.size}/3)"
            }
        }
    }

    fun clearComparison() {
        _comparePropertyIds.value = emptySet()
    }

    // Rental Request
    fun sendRentalRequest(
        property: PropertyEntity,
        studentName: String,
        studentPhone: String,
        moveInDate: String,
        durationMonths: Int,
        message: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val user = currentUser.value
            val request = RentalRequestEntity(
                id = "req_${UUID.randomUUID().toString().take(8)}",
                propertyId = property.id,
                propertyTitle = property.title,
                propertyArea = property.area,
                propertyRent = property.monthlyRent,
                ownerId = property.ownerId,
                studentName = studentName.trim().ifBlank { user?.username ?: "Student" },
                studentUsername = user?.username ?: "student_user",
                studentPhone = studentPhone.trim(),
                moveInDate = moveInDate,
                durationMonths = durationMonths,
                message = message.trim(),
                status = "PENDING",
                createdAt = System.currentTimeMillis()
            )
            repository.sendRentalRequest(request)
            _userMessage.value = "Rental request sent to owner 🎉"
            onSuccess()
        }
    }

    fun updateRentalRequestStatus(requestId: String, newStatus: String) {
        viewModelScope.launch {
            repository.updateRentalRequestStatus(requestId, newStatus)
            _userMessage.value = "Request status updated to $newStatus"
        }
    }

    // Post Property (Owner Flow)
    fun publishProperty(
        title: String,
        description: String,
        propertyType: String,
        monthlyRent: Int,
        securityDeposit: Int,
        availableFrom: String,
        furnishing: String,
        genderPreference: String,
        suitableFor: String,
        city: String,
        area: String,
        landmark: String,
        address: String,
        facilitiesList: List<String>,
        photosList: List<String>,
        ownerName: String,
        ownerPhone: String,
        preferredContact: String,
        onSuccess: (PropertyEntity) -> Unit
    ) {
        viewModelScope.launch {
            val user = currentUser.value
            val newProperty = PropertyEntity(
                id = "prop_${UUID.randomUUID().toString().take(8)}",
                title = title.trim(),
                description = description.trim(),
                propertyType = propertyType,
                monthlyRent = monthlyRent,
                securityDeposit = securityDeposit,
                availableFrom = availableFrom.ifBlank { "Immediately" },
                furnishing = furnishing,
                genderPreference = genderPreference,
                suitableFor = suitableFor,
                status = "ACTIVE",
                city = city.trim().ifBlank { "Gwalior" },
                area = area.trim().ifBlank { "Thatipur" },
                landmark = landmark.trim(),
                address = address.trim(),
                latitude = 26.2185 + (Math.random() - 0.5) * 0.05,
                longitude = 78.1830 + (Math.random() - 0.5) * 0.05,
                distanceKm = ((Math.random() * 3.5) * 10).toInt() / 10.0 + 0.5,
                isVerified = true, // Owner-submitted properties marked active & verified
                isDemo = false, // User created real property!
                ownerId = user?.id ?: "current_user",
                ownerName = ownerName.trim().ifBlank { user?.username ?: "Property Owner" },
                ownerPhone = ownerPhone.trim().ifBlank { "+91 98765 00000" },
                ownerRating = 5.0,
                ownerListingsCount = 1,
                preferredContactMethod = preferredContact,
                createdAt = System.currentTimeMillis(),
                ratingAvg = 5.0,
                reviewsCount = 1,
                facilities = facilitiesList.joinToString(","),
                photos = if (photosList.isNotEmpty()) photosList.joinToString(",") else "room_study,room_bed,room_interior",
                nearCollege = "Vikrant University",
                viewsCount = 1
            )
            repository.insertProperty(newProperty)
            _userMessage.value = "🎉 Property successfully listed on RoomMate!"
            onSuccess(newProperty)
        }
    }

    fun updatePropertyStatus(propertyId: String, status: String) {
        viewModelScope.launch {
            repository.updatePropertyStatus(propertyId, status)
            _userMessage.value = "Property marked as $status"
        }
    }

    fun deleteProperty(propertyId: String) {
        viewModelScope.launch {
            repository.deleteProperty(propertyId)
            _userMessage.value = "Listing removed"
        }
    }

    // Reviews & Reports
    fun submitReview(
        propertyId: String,
        reviewerName: String,
        rating: Int,
        comment: String,
        locationRating: Int,
        cleanlinessRating: Int,
        ownerRating: Int,
        facilitiesRating: Int,
        valueRating: Int,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val review = ReviewEntity(
                id = "rev_${UUID.randomUUID().toString().take(8)}",
                propertyId = propertyId,
                reviewerName = reviewerName.trim().ifBlank { currentUser.value?.username ?: "Student" },
                rating = rating,
                comment = comment.trim(),
                date = "Today",
                isDemo = false,
                locationRating = locationRating,
                cleanlinessRating = cleanlinessRating,
                ownerRating = ownerRating,
                facilitiesRating = facilitiesRating,
                valueRating = valueRating
            )
            repository.submitReview(review)
            _userMessage.value = "Review submitted! Thank you ⭐"
            onSuccess()
        }
    }

    fun submitReport(
        propertyId: String,
        propertyTitle: String,
        reason: String,
        details: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val report = ReportEntity(
                id = "rep_${UUID.randomUUID().toString().take(8)}",
                propertyId = propertyId,
                propertyTitle = propertyTitle,
                reporterName = currentUser.value?.username ?: "Anonymous",
                reason = reason,
                details = details.trim(),
                createdAt = System.currentTimeMillis()
            )
            repository.submitReport(report)
            _userMessage.value = "Report received. Our safety team will review this listing."
            onSuccess()
        }
    }

    fun getReviewsForProperty(propertyId: String) = repository.getReviewsForProperty(propertyId)

    fun clearUserMessage() {
        _userMessage.value = null
    }

    fun resetTestingData() {
        viewModelScope.launch {
            repository.resetAllData()
            _filterState.value = SearchFilterState()
            _comparePropertyIds.value = emptySet()
            _userMessage.value = "Testing data reset to default demo state"
        }
    }
}
