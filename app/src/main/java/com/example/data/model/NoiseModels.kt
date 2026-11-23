package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class NoiseSourceType(val labelMm: String, val labelEn: String) {
    LOUDSPEAKER("ဆောင်းဘောက် / စပီကာ", "Loudspeaker / Soundbox"),
    EVENT_PA("အခမ်းအနား / ဟောပြောသံချဲ့စက်", "Event PA System"),
    VEHICLE("ယာဉ် / ဟွန်းသံ", "Vehicle / Horn"),
    OTHER("အခြားဆူညံသံ", "Other Noise");

    companion object {
        fun fromRaw(raw: String?): NoiseSourceType =
            entries.firstOrNull { it.name == raw } ?: LOUDSPEAKER
    }
}

enum class NoiseSeverity(val labelMm: String, val labelEn: String) {
    MODERATE("အသင့်အတင့် (သတိပြု)", "Moderate"),
    HIGH("မြင့်မား (အနှောင့်အယှက်)", "High"),
    CRITICAL("အလွန်ဆူညံ (လူနာအန္တရာယ်)", "Critical");

    companion object {
        fun fromRaw(raw: String?): NoiseSeverity =
            entries.firstOrNull { it.name == raw } ?: HIGH

        fun fromDb(peakDb: Int): NoiseSeverity = when {
            peakDb >= 70 -> CRITICAL
            peakDb >= 55 -> HIGH
            else -> MODERATE
        }
    }
}

enum class ComplaintStatus(val labelMm: String, val labelEn: String) {
    SUBMITTED("တင်သွင်းပြီး", "Submitted"),
    ESCALATED("ဌာနဆိုင်ရာသို့ ပေးပို့ထား", "Escalated"),
    RESOLVED("ဖြေရှင်းပြီး", "Resolved");

    companion object {
        fun fromRaw(raw: String?): ComplaintStatus =
            entries.firstOrNull { it.name == raw } ?: SUBMITTED
    }
}

enum class QuietZonePreset(val labelMm: String, val labelEn: String, val thresholdDb: Int) {
    ICU_NIGHT("ICU / ညပိုင်းလူနာဆောင်", "ICU / Night Ward", 35),
    GENERAL_WARD("အထွေထွေလူနာဆောင်", "General Ward", 45),
    HEALTH_DEPT("ကျန်းမာရေးဌာန / ဆေးခန်း", "Health Dept / Clinic", 55),
    PERIMETER("ဆေးရုံဝင်းပြင်ပ", "Hospital Perimeter", 65)
}

@Entity(tableName = "noise_complaints")
data class NoiseComplaint(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val reporterName: String = "",
    val facilityName: String = "",
    val sourceType: String = NoiseSourceType.LOUDSPEAKER.name,
    val measuredDb: Int = 0,
    val peakDb: Int = 0,
    val severity: String = NoiseSeverity.HIGH.name,
    val status: String = ComplaintStatus.SUBMITTED.name,
    val description: String = "",
    val authorityResponse: String? = null,
    val createdAtMillis: Long = System.currentTimeMillis(),
    val updatedAtMillis: Long = System.currentTimeMillis()
) {
    val sourceEnum: NoiseSourceType get() = NoiseSourceType.fromRaw(sourceType)
    val severityEnum: NoiseSeverity get() = NoiseSeverity.fromRaw(severity)
    val statusEnum: ComplaintStatus get() = ComplaintStatus.fromRaw(status)
}

@Entity(tableName = "noise_logs")
data class NoiseLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val zoneName: String = "",
    val avgDb: Int = 0,
    val peakDb: Int = 0,
    val thresholdDb: Int = 45,
    val autoGuarded: Boolean = false,
    val createdAtMillis: Long = System.currentTimeMillis()
)

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}
