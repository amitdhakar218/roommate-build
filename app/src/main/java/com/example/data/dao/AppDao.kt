package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.PropertyEntity
import com.example.data.model.RecentlyViewedEntity
import com.example.data.model.RentalRequestEntity
import com.example.data.model.ReportEntity
import com.example.data.model.ReviewEntity
import com.example.data.model.SavedPropertyEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PropertyDao {
    @Query("SELECT * FROM properties ORDER BY createdAt DESC")
    fun getAllProperties(): Flow<List<PropertyEntity>>

    @Query("SELECT * FROM properties WHERE status = 'ACTIVE' ORDER BY createdAt DESC")
    fun getActiveProperties(): Flow<List<PropertyEntity>>

    @Query("SELECT * FROM properties WHERE id = :id")
    fun getPropertyById(id: String): Flow<PropertyEntity?>

    @Query("SELECT * FROM properties WHERE id = :id")
    suspend fun getPropertyDirect(id: String): PropertyEntity?

    @Query("SELECT * FROM properties WHERE ownerId = :ownerId ORDER BY createdAt DESC")
    fun getPropertiesByOwner(ownerId: String): Flow<List<PropertyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProperty(property: PropertyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProperties(properties: List<PropertyEntity>)

    @Update
    suspend fun updateProperty(property: PropertyEntity)

    @Query("UPDATE properties SET status = :status WHERE id = :id")
    suspend fun updatePropertyStatus(id: String, status: String)

    @Query("DELETE FROM properties WHERE id = :id")
    suspend fun deletePropertyById(id: String)

    @Query("SELECT COUNT(*) FROM properties")
    suspend fun getPropertiesCount(): Int
}

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = 'current_user' LIMIT 1")
    fun getCurrentUser(): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = 'current_user' LIMIT 1")
    suspend fun getCurrentUserDirect(): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUser(user: UserEntity)

    @Query("UPDATE users SET selectedRole = :role WHERE id = 'current_user'")
    suspend fun updateUserRole(role: String)

    @Query("UPDATE users SET currentCity = :city, currentArea = :area, currentCollege = :college WHERE id = 'current_user'")
    suspend fun updateUserLocation(city: String, area: String, college: String)

    @Query("DELETE FROM users")
    suspend fun clearUsers()
}

@Dao
interface SavedPropertyDao {
    @Query("SELECT propertyId FROM saved_properties WHERE userId = :userId")
    fun getSavedPropertyIds(userId: String = "current_user"): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProperty(savedProperty: SavedPropertyEntity)

    @Query("DELETE FROM saved_properties WHERE userId = :userId AND propertyId = :propertyId")
    suspend fun removeSavedProperty(userId: String = "current_user", propertyId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM saved_properties WHERE userId = :userId AND propertyId = :propertyId)")
    fun isPropertySaved(userId: String = "current_user", propertyId: String): Flow<Boolean>
}

@Dao
interface RentalRequestDao {
    @Query("SELECT * FROM rental_requests ORDER BY createdAt DESC")
    fun getAllRequests(): Flow<List<RentalRequestEntity>>

    @Query("SELECT * FROM rental_requests WHERE ownerId = :ownerId ORDER BY createdAt DESC")
    fun getRequestsForOwner(ownerId: String): Flow<List<RentalRequestEntity>>

    @Query("SELECT * FROM rental_requests WHERE studentUsername = :username ORDER BY createdAt DESC")
    fun getRequestsByStudent(username: String): Flow<List<RentalRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: RentalRequestEntity)

    @Query("UPDATE rental_requests SET status = :status WHERE id = :id")
    suspend fun updateRequestStatus(id: String, status: String)
}

@Dao
interface ReviewDao {
    @Query("SELECT * FROM reviews WHERE propertyId = :propertyId ORDER BY isDemo ASC, date DESC")
    fun getReviewsForProperty(propertyId: String): Flow<List<ReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReviews(reviews: List<ReviewEntity>)
}

@Dao
interface ReportDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ReportEntity)

    @Query("SELECT * FROM reports ORDER BY createdAt DESC")
    fun getAllReports(): Flow<List<ReportEntity>>
}

@Dao
interface RecentlyViewedDao {
    @Query("SELECT propertyId FROM recently_viewed ORDER BY viewedAt DESC LIMIT 20")
    fun getRecentlyViewedIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentlyViewed(recentlyViewed: RecentlyViewedEntity)
}
