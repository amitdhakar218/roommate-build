package com.example.data.repository

import com.example.data.AppDatabase
import com.example.data.DemoData
import com.example.data.model.PropertyEntity
import com.example.data.model.RecentlyViewedEntity
import com.example.data.model.RentalRequestEntity
import com.example.data.model.ReportEntity
import com.example.data.model.ReviewEntity
import com.example.data.model.SavedPropertyEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppRepository(private val database: AppDatabase) {

    private val userDao = database.userDao()
    private val propertyDao = database.propertyDao()
    private val savedPropertyDao = database.savedPropertyDao()
    private val rentalRequestDao = database.rentalRequestDao()
    private val reviewDao = database.reviewDao()
    private val reportDao = database.reportDao()
    private val recentlyViewedDao = database.recentlyViewedDao()

    // User Operations
    val currentUser: Flow<UserEntity?> = userDao.getCurrentUser()

    suspend fun saveUser(user: UserEntity) = userDao.insertOrUpdateUser(user)

    suspend fun updateUserRole(role: String) = userDao.updateUserRole(role)

    suspend fun updateUserLocation(city: String, area: String, college: String) =
        userDao.updateUserLocation(city, area, college)

    suspend fun resetAllData() {
        userDao.clearUsers()
        // Re-populate demo data
        propertyDao.insertProperties(DemoData.sampleProperties)
        reviewDao.insertReviews(DemoData.sampleReviews)
    }

    // Property Operations
    val allProperties: Flow<List<PropertyEntity>> = propertyDao.getAllProperties()
    val activeProperties: Flow<List<PropertyEntity>> = propertyDao.getActiveProperties()

    fun getPropertyById(id: String): Flow<PropertyEntity?> = propertyDao.getPropertyById(id)
    suspend fun getPropertyDirect(id: String): PropertyEntity? = propertyDao.getPropertyDirect(id)

    fun getPropertiesByOwner(ownerId: String): Flow<List<PropertyEntity>> =
        propertyDao.getPropertiesByOwner(ownerId)

    suspend fun insertProperty(property: PropertyEntity) = propertyDao.insertProperty(property)
    suspend fun updateProperty(property: PropertyEntity) = propertyDao.updateProperty(property)
    suspend fun updatePropertyStatus(id: String, status: String) =
        propertyDao.updatePropertyStatus(id, status)
    suspend fun deleteProperty(id: String) = propertyDao.deletePropertyById(id)

    // Saved / Favorites
    fun getSavedPropertyIds(userId: String = "current_user"): Flow<List<String>> =
        savedPropertyDao.getSavedPropertyIds(userId)

    suspend fun toggleSaveProperty(propertyId: String, isCurrentlySaved: Boolean) {
        if (isCurrentlySaved) {
            savedPropertyDao.removeSavedProperty("current_user", propertyId)
        } else {
            savedPropertyDao.saveProperty(
                SavedPropertyEntity(
                    userId = "current_user",
                    propertyId = propertyId
                )
            )
        }
    }

    fun isPropertySaved(propertyId: String): Flow<Boolean> =
        savedPropertyDao.isPropertySaved("current_user", propertyId)

    // Rental Requests
    val allRentalRequests: Flow<List<RentalRequestEntity>> = rentalRequestDao.getAllRequests()

    fun getRequestsForOwner(ownerId: String): Flow<List<RentalRequestEntity>> =
        rentalRequestDao.getRequestsForOwner(ownerId)

    fun getRequestsByStudent(username: String): Flow<List<RentalRequestEntity>> =
        rentalRequestDao.getRequestsByStudent(username)

    suspend fun sendRentalRequest(request: RentalRequestEntity) =
        rentalRequestDao.insertRequest(request)

    suspend fun updateRentalRequestStatus(id: String, status: String) =
        rentalRequestDao.updateRequestStatus(id, status)

    // Reviews
    fun getReviewsForProperty(propertyId: String): Flow<List<ReviewEntity>> =
        reviewDao.getReviewsForProperty(propertyId)

    suspend fun submitReview(review: ReviewEntity) = reviewDao.insertReview(review)

    // Reports
    suspend fun submitReport(report: ReportEntity) = reportDao.insertReport(report)
    val allReports: Flow<List<ReportEntity>> = reportDao.getAllReports()

    // Recently Viewed
    val recentlyViewedIds: Flow<List<String>> = recentlyViewedDao.getRecentlyViewedIds()

    suspend fun markAsRecentlyViewed(propertyId: String) {
        recentlyViewedDao.insertRecentlyViewed(RecentlyViewedEntity(propertyId = propertyId))
    }
}
