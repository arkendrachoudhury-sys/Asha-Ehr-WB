package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

// ==========================================
// ROOM ENTITIES
// ==========================================

/**
 * Aligned with HL7 FHIR R4 Patient Resource
 */
@Entity(tableName = "patients")
data class PatientEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val identifierAadhaar: String, // FHIR Identifier
    val identifierAbha: String = "", // FHIR Identifier (ABHA)
    val name: String, // FHIR HumanName
    val telecomPhone: String, // FHIR ContactPoint
    val gender: String, // FHIR AdministrativeGender: "female", "male", "other", "unknown"
    val birthDate: String = "", // FHIR date
    val age: Int, // Calculated/Legacy field
    val addressVillage: String, // FHIR Address
    val contactGuardianName: String, // FHIR Patient.contact
    val isPregnant: Boolean = false, // Extension/Observation context
    val isChild: Boolean = false, // Patient group context
    val createdTimestamp: Long = System.currentTimeMillis()
)

/**
 * Aligned with HL7 FHIR R4 Encounter and Observation Resources
 */
@Entity(tableName = "anc_checkups")
data class AncCheckupEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val subjectPatientId: Int, // FHIR subject (Patient reference)
    val periodStart: String, // FHIR Encounter.period.start (checkupDate)
    val lmpDate: String, // Observation: Last Menstrual Period
    val eddDate: String, // Observation: Expected Date of Delivery
    val weightKg: Float, // Observation: Body Weight
    val systolicBp: Int, // Observation: Systolic BP
    val diastolicBp: Int, // Observation: Diastolic BP
    val hemoglobinLevel: Float, // Observation: Hemoglobin [Mass/volume] in Blood
    val ifaSupplements: Boolean, // MedicationStatement or Extension
    val tetanusDose: String, // FHIR Immunization
    val highRiskFactors: String, // Observation.interpretation or Condition
    val clinicalNotes: String = "" // FHIR Annotation
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

/**
 * Aligned with HL7 FHIR R4 Observation and ServiceRequest
 */
@Entity(tableName = "ncd_screenings")
data class NcdScreeningEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val subjectPatientId: Int, // FHIR subject
    val effectiveDateTime: String, // FHIR Observation.effectiveDateTime (screeningDate)
    val systolicBp: Int, // Observation: BP
    val diastolicBp: Int, // Observation: BP
    val bloodGlucose: Int, // Observation: Blood Glucose
    val tobaccoStatus: Boolean, // Observation: Smoking Status
    val symptomsPresent: String, // FHIR Condition/Observation
    val referralRequest: String = "" // FHIR ServiceRequest.performer (referralDestination)
)

/**
 * Aligned with HL7 FHIR R4 Encounter, Observation and ClinicalImpression
 */
@Entity(tableName = "consult_logs")
data class ConsultLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val subjectPatientId: Int, // FHIR subject
    val encounterDate: String, // FHIR Encounter.period.start
    val presentingSymptoms: String, // FHIR Condition.code (text)
    val priority: String, // FHIR Encounter.priority (severity)
    val symptomDuration: Int, // Extension on Condition
    val bodyTemperature: Float, // FHIR Observation: Body Temperature
    val fieldNotes: String, // FHIR Annotation
    val aiClinicalImpression: String, // FHIR ClinicalImpression
    val referralStatus: Boolean = false // FHIR ServiceRequest
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
    @Query("SELECT * FROM anc_checkups WHERE subjectPatientId = :patientId ORDER BY periodStart DESC")
    fun getAncCheckupsForPatientFlow(patientId: Int): Flow<List<AncCheckupEntity>>

    @Query("SELECT * FROM anc_checkups ORDER BY periodStart DESC")
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
    @Query("SELECT * FROM ncd_screenings WHERE subjectPatientId = :patientId ORDER BY effectiveDateTime DESC")
    fun getNcdScreeningsForPatientFlow(patientId: Int): Flow<List<NcdScreeningEntity>>

    @Query("SELECT * FROM ncd_screenings ORDER BY effectiveDateTime DESC")
    fun getAllNcdScreeningsFlow(): Flow<List<NcdScreeningEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNcdScreening(screening: NcdScreeningEntity)

    // --- Consult Operations ---
    @Query("SELECT * FROM consult_logs WHERE subjectPatientId = :patientId ORDER BY encounterDate DESC")
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
    version = 2,
    exportSchema = false
)
abstract class EMRDatabase : RoomDatabase() {
    abstract fun emrDao(): EMRDao
}
