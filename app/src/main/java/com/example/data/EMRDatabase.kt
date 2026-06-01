package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

// ==========================================
// ROOM ENTITIES
// ==========================================

@Entity(tableName = "patients")
data class PatientEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val age: Int,
    val gender: String, // "Female", "Male", "Other"
    val village: String,
    val phone: String,
    val aadhaar: String,
    val guardianName: String,
    val isPregnant: Boolean = false,
    val isChild: Boolean = false,
    val createdTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "anc_checkups")
data class AncCheckupEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val patientId: Int, // Foreign Key linking to PatientEntity.id
    val checkupDate: String,
    val lmpDate: String, // Last Menstrual Period
    val eddDate: String, // Expected Delivery Date
    val weightKg: Float,
    val systolicBp: Int,
    val diastolicBp: Int,
    val hemoglobinLevel: Float, // g/dL
    val ironFolicAcidSupplements: Boolean,
    val tetanusDose: String, // "None", "Dose 1", "Dose 2", "Booster"
    val highRiskFactors: String, // e.g., "Severe Anemia", "Hypertension", "None"
    val notes: String = ""
)

@Entity(tableName = "child_vaccinations")
data class ChildVaccinationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val patientId: Int, // Links to PatientEntity.id
    val dob: String,
    val weightKg: Float,
    val heightCm: Float,
    val bcgDose: Boolean = false,
    val opvDose0: Boolean = false,
    val opvDose1: Boolean = false,
    val opvDose2: Boolean = false,
    val hepbBirth: Boolean = false,
    val pentavalent1: Boolean = false,
    val pentavalent2: Boolean = false,
    val pentavalent3: Boolean = false,
    val mrDose1: Boolean = false,
    val growthStatus: String = "Normal" // "Normal", "Moderate Malnutrition (MAM)", "Severe Malnutrition (SAM)"
)

@Entity(tableName = "ncd_screenings")
data class NcdScreeningEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val patientId: Int, // Links to PatientEntity
    val screeningDate: String,
    val systolicBp: Int,
    val diastolicBp: Int,
    val bloodSugarMgDl: Int,
    val tobaccoUser: Boolean,
    val symptoms: String, // e.g., "Frequent Urination", "Dizziness", "None"
    val referralDestination: String = "" // e.g., "None", "PHC Primary Health Centre", "District Hospital"
)

@Entity(tableName = "consult_logs")
data class ConsultLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val patientId: Int,
    val consultationDate: String,
    val symptoms: String,
    val severity: String, // "Mild", "Moderate", "Severe"
    val durationDays: Int,
    val temperatureFahrenheit: Float,
    val ashaNotes: String,
    val aiTriageAdvice: String, // AI consultation summary or guidelines fallback
    val referredToDoctor: Boolean = false
)

// ==========================================
// ROOM DAOS
// ==========================================

@Dao
interface EMRDao {

    // --- Patient Operations ---
    @Query("SELECT * FROM patients ORDER BY createdTimestamp DESC")
    fun getAllPatientsFlow(): Flow<List<PatientEntity>>

    @Query("SELECT * FROM patients WHERE id = :id LIMIT 1")
    suspend fun getPatientById(id: Int): PatientEntity?

    @Query("SELECT * FROM patients WHERE isPregnant = 1")
    fun getPregnantPatientsFlow(): Flow<List<PatientEntity>>

    @Query("SELECT * FROM patients WHERE isChild = 1")
    fun getChildPatientsFlow(): Flow<List<PatientEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatient(patient: PatientEntity): Long

    @Update
    suspend fun updatePatient(patient: PatientEntity)

    @Query("DELETE FROM patients WHERE id = :id")
    suspend fun deletePatientById(id: Int)

    // --- ANC Operations ---
    @Query("SELECT * FROM anc_checkups WHERE patientId = :patientId ORDER BY checkupDate DESC")
    fun getAncCheckupsForPatientFlow(patientId: Int): Flow<List<AncCheckupEntity>>

    @Query("SELECT * FROM anc_checkups ORDER BY checkupDate DESC")
    fun getAllAncCheckupsFlow(): Flow<List<AncCheckupEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAncCheckup(checkup: AncCheckupEntity)

    // --- Child Operations ---
    @Query("SELECT * FROM child_vaccinations WHERE patientId = :patientId LIMIT 1")
    fun getChildRecordForPatientFlow(patientId: Int): Flow<ChildVaccinationEntity?>

    @Query("SELECT * FROM child_vaccinations WHERE patientId = :patientId LIMIT 1")
    suspend fun getChildRecordForPatient(patientId: Int): ChildVaccinationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateChildRecord(vaccination: ChildVaccinationEntity)

    // --- NCD Operations ---
    @Query("SELECT * FROM ncd_screenings WHERE patientId = :patientId ORDER BY screeningDate DESC")
    fun getNcdScreeningsForPatientFlow(patientId: Int): Flow<List<NcdScreeningEntity>>

    @Query("SELECT * FROM ncd_screenings ORDER BY screeningDate DESC")
    fun getAllNcdScreeningsFlow(): Flow<List<NcdScreeningEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNcdScreening(screening: NcdScreeningEntity)

    // --- Consult Operations ---
    @Query("SELECT * FROM consult_logs WHERE patientId = :patientId ORDER BY consultationDate DESC")
    fun getConsultLogsForPatientFlow(patientId: Int): Flow<List<ConsultLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConsultLog(consult: ConsultLogEntity)
}

// ==========================================
// APP DATABASE
// ==========================================

@Database(
    entities = [
        PatientEntity::class,
        AncCheckupEntity::class,
        ChildVaccinationEntity::class,
        NcdScreeningEntity::class,
        ConsultLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class EMRDatabase : RoomDatabase() {
    abstract fun emrDao(): EMRDao
}
