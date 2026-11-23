package com.example.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.NoiseComplaint
import com.example.data.model.NoiseLog
import kotlinx.coroutines.flow.Flow

@Dao
interface NoiseComplaintDao {
    @Query("SELECT * FROM noise_complaints ORDER BY createdAtMillis DESC")
    fun observeAll(): Flow<List<NoiseComplaint>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(complaint: NoiseComplaint): Long

    @Query(
        """
        UPDATE noise_complaints
        SET status = :status,
            authorityResponse = COALESCE(:authorityResponse, authorityResponse),
            updatedAtMillis = :updatedAtMillis
        WHERE id = :id
        """
    )
    suspend fun updateStatus(
        id: Long,
        status: String,
        authorityResponse: String?,
        updatedAtMillis: Long
    )

    @Query("DELETE FROM noise_complaints WHERE id = :id")
    suspend fun deleteById(id: Long)
}

@Dao
interface NoiseLogDao {
    @Query("SELECT * FROM noise_logs ORDER BY createdAtMillis DESC")
    fun observeAll(): Flow<List<NoiseLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: NoiseLog): Long

    @Query("DELETE FROM noise_logs WHERE id = :id")
    suspend fun deleteById(id: Long)
}

@Database(
    entities = [NoiseComplaint::class, NoiseLog::class],
    version = 1,
    exportSchema = false
)
abstract class QuietCareDatabase : RoomDatabase() {
    abstract fun complaintDao(): NoiseComplaintDao
    abstract fun noiseLogDao(): NoiseLogDao

    companion object {
        @Volatile
        private var instance: QuietCareDatabase? = null

        fun getInstance(context: Context): QuietCareDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    QuietCareDatabase::class.java,
                    "quietcare_local.db"
                ).build().also { instance = it }
            }
        }
    }
}
