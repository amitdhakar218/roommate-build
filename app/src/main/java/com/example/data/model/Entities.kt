package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String = "current_user",
    val username: String,
    val selectedRole: String = "STUDENT", // "STUDENT" or "OWNER"
    val phone: String = "",
    val email: String = "",
    val isVerified: Boolean = false,
    val joinedDate: String = "August 2026",
    val currentCity: String = "Gwalior",
    val currentArea: String = "Thatipur",
    val currentCollege: String = "Vikrant University",
    val isInitialOnboardingCompleted: Boolean = false
)

@Entity(tableName = "properties")
data class PropertyEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val propertyType: String, // "Single Room", "Shared Room", "PG", "Hostel", "1 BHK", "2 BHK", "3 BHK", "Flat", "House"
    val monthlyRent: Int,
    val securityDeposit: Int,
    val availableFrom: String = "Immediately",
    val furnishing: String = "Fully Furnished", // "Fully Furnished", "Semi Furnished", "Unfurnished"
    val genderPreference: String = "Any", // "Male", "Female", "Any"
    val suitableFor: String = "Students", // "Students", "Working Professionals", "Family", "Any"
    val status: String = "ACTIVE", // "ACTIVE", "PAUSED", "RENTED", "EXPIRED"
    val city: String = "Gwalior",
    val area: String = "Thatipur",
    val landmark: String = "",
    val address: String = "",
    val latitude: Double = 26.2183,
    val longitude: Double = 78.1828,
    val distanceKm: Double = 1.2,
    val isVerified: Boolean = true,
    val isDemo: Boolean = true,
    val ownerId: String = "owner_1",
    val ownerName: String = "Rajesh Sharma",
    val ownerPhone: String = "+91 98765 43210",
    val ownerRating: Double = 4.8,
    val ownerListingsCount: Int = 3,
    val preferredContactMethod: String = "Call", // "Call", "Message", "Both"
    val createdAt: Long = System.currentTimeMillis(),
    val ratingAvg: Double = 4.7,
    val reviewsCount: Int = 28,
    val facilities: String = "Wi-Fi,Bed,Fan,AC,Table,Chair,Wardrobe,Parking,Water,Electricity",
    val photos: String = "room_front,room_interior,room_bathroom",
    val nearCollege: String = "Vikrant University",
    val viewsCount: Int = 142
)

@Entity(tableName = "saved_properties", primaryKeys = ["userId", "propertyId"])
data class SavedPropertyEntity(
    val userId: String = "current_user",
    val propertyId: String,
    val savedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "rental_requests")
data class RentalRequestEntity(
    @PrimaryKey val id: String,
    val propertyId: String,
    val propertyTitle: String,
    val propertyArea: String,
    val propertyRent: Int,
    val ownerId: String,
    val studentName: String,
    val studentUsername: String,
    val studentPhone: String = "",
    val moveInDate: String,
    val durationMonths: Int = 6,
    val message: String,
    val status: String = "PENDING", // "PENDING", "ACCEPTED", "REJECTED"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey val id: String,
    val propertyId: String,
    val reviewerName: String,
    val rating: Int,
    val comment: String,
    val date: String,
    val isDemo: Boolean = true,
    val locationRating: Int = 5,
    val cleanlinessRating: Int = 5,
    val ownerRating: Int = 5,
    val facilitiesRating: Int = 4,
    val valueRating: Int = 5
)

@Entity(tableName = "reports")
data class ReportEntity(
    @PrimaryKey val id: String,
    val propertyId: String,
    val propertyTitle: String,
    val reporterName: String,
    val reason: String,
    val details: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "recently_viewed")
data class RecentlyViewedEntity(
    @PrimaryKey val propertyId: String,
    val viewedAt: Long = System.currentTimeMillis()
)
