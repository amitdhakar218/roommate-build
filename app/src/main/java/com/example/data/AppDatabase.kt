package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.PropertyDao
import com.example.data.dao.RecentlyViewedDao
import com.example.data.dao.RentalRequestDao
import com.example.data.dao.ReportDao
import com.example.data.dao.ReviewDao
import com.example.data.dao.SavedPropertyDao
import com.example.data.dao.UserDao
import com.example.data.model.PropertyEntity
import com.example.data.model.RecentlyViewedEntity
import com.example.data.model.RentalRequestEntity
import com.example.data.model.ReportEntity
import com.example.data.model.ReviewEntity
import com.example.data.model.SavedPropertyEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        PropertyEntity::class,
        SavedPropertyEntity::class,
        RentalRequestEntity::class,
        ReviewEntity::class,
        ReportEntity::class,
        RecentlyViewedEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun propertyDao(): PropertyDao
    abstract fun savedPropertyDao(): SavedPropertyDao
    abstract fun rentalRequestDao(): RentalRequestDao
    abstract fun reviewDao(): ReviewDao
    abstract fun reportDao(): ReportDao
    abstract fun recentlyViewedDao(): RecentlyViewedDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "roommate_database.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database)
                    }
                }
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        if (database.propertyDao().getPropertiesCount() == 0) {
                            populateDatabase(database)
                        }
                    }
                }
            }

            suspend fun populateDatabase(db: AppDatabase) {
                db.propertyDao().insertProperties(DemoData.sampleProperties)
                db.reviewDao().insertReviews(DemoData.sampleReviews)
                
                // Pre-add a sample rental request so the Owner Dashboard and Student requests are lively
                db.rentalRequestDao().insertRequest(
                    RentalRequestEntity(
                        id = "req_demo_1",
                        propertyId = "prop_1",
                        propertyTitle = "Single Furnished Room Near Vikrant University",
                        propertyArea = "Thatipur",
                        propertyRent = 5500,
                        ownerId = "owner_rajesh",
                        studentName = "Adarsh Mishra",
                        studentUsername = "adarsh_m",
                        studentPhone = "+91 91234 56789",
                        moveInDate = "1st Sept 2026",
                        durationMonths = 12,
                        message = "Hi! I am a 2nd year B.Tech student joining this semester. I want to inspect and book this room.",
                        status = "PENDING",
                        createdAt = System.currentTimeMillis() - 3600000L * 4
                    )
                )
            }
        }
    }
}
