package com.example.data.repository

import android.content.Context
import com.example.data.local.NoiseComplaintDao
import com.example.data.local.NoiseLogDao
import com.example.data.local.QuietCareDatabase
import com.example.data.model.ComplaintStatus
import com.example.data.model.NoiseComplaint
import com.example.data.model.NoiseLog
import com.example.data.model.NoiseSeverity
import com.example.data.model.NoiseSourceType
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow

class NoiseRepository(
    private val complaintDao: NoiseComplaintDao,
    private val noiseLogDao: NoiseLogDao
) {
    constructor(context: Context) : this(
        complaintDao = QuietCareDatabase.getInstance(context).complaintDao(),
        noiseLogDao = QuietCareDatabase.getInstance(context).noiseLogDao()
    )

    fun observeComplaints(): Flow<List<NoiseComplaint>> = complaintDao.observeAll()

    fun observeNoiseLogs(): Flow<List<NoiseLog>> = noiseLogDao.observeAll()

    suspend fun createComplaint(
        reporterName: String,
        facilityName: String,
        sourceType: NoiseSourceType,
        measuredDb: Int,
        peakDb: Int,
        description: String
    ): Result<Unit> {
        return try {
            val cleanReporter = reporterName.trim().ifBlank { "Healthcare Staff / Citizen" }.take(80)
            val cleanFacility = facilityName.trim().take(120)
            val cleanDesc = description.trim().take(1000)
            require(cleanFacility.isNotEmpty()) { "ဆေးရုံ သို့မဟုတ် ဌာနအမည် ထည့်သွင်းပါ" }
            require(cleanDesc.isNotEmpty()) { "တိုင်ကြားလိုသည့် အကြောင်းအရာ ထည့်သွင်းပါ" }

            val clampedMeasured = measuredDb.coerceIn(0, 160)
            val clampedPeak = peakDb.coerceIn(0, 160)
            val severity = NoiseSeverity.fromDb(clampedPeak)
            val now = System.currentTimeMillis()

            complaintDao.insert(
                NoiseComplaint(
                    reporterName = cleanReporter,
                    facilityName = cleanFacility,
                    sourceType = sourceType.name,
                    measuredDb = clampedMeasured,
                    peakDb = clampedPeak,
                    severity = severity.name,
                    status = ComplaintStatus.SUBMITTED.name,
                    description = cleanDesc,
                    createdAtMillis = now,
                    updatedAtMillis = now
                )
            )
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateComplaintStatus(
        complaintId: Long,
        newStatus: ComplaintStatus,
        authorityResponse: String?
    ): Result<Unit> {
        return try {
            require(complaintId > 0L) { "Invalid complaint ID" }
            val trimmedNote = authorityResponse?.trim()?.take(500)?.ifBlank { null }
            complaintDao.updateStatus(
                id = complaintId,
                status = newStatus.name,
                authorityResponse = trimmedNote,
                updatedAtMillis = System.currentTimeMillis()
            )
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteComplaint(complaintId: Long): Result<Unit> {
        return try {
            require(complaintId > 0L) { "Invalid complaint ID" }
            complaintDao.deleteById(complaintId)
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun recordNoiseLog(
        zoneName: String,
        avgDb: Int,
        peakDb: Int,
        thresholdDb: Int,
        autoGuarded: Boolean
    ): Result<Unit> {
        return try {
            val cleanZone = zoneName.trim().ifBlank { "Hospital Quiet Zone" }.take(100)
            noiseLogDao.insert(
                NoiseLog(
                    zoneName = cleanZone,
                    avgDb = avgDb.coerceIn(0, 160),
                    peakDb = peakDb.coerceIn(0, 160),
                    thresholdDb = thresholdDb.coerceIn(20, 120),
                    autoGuarded = autoGuarded,
                    createdAtMillis = System.currentTimeMillis()
                )
            )
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteNoiseLog(logId: Long): Result<Unit> {
        return try {
            require(logId > 0L) { "Invalid log ID" }
            noiseLogDao.deleteById(logId)
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
