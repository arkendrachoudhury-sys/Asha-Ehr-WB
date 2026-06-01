package com.example.data

import kotlinx.coroutines.flow.Flow

class EMRRepository(private val emrDao: EMRDao) {

    // Reactive streams for lists
    val allPatients: Flow<List<PatientEntity>> = emrDao.getAllPatientsFlow()
    val pregnantPatients: Flow<List<PatientEntity>> = emrDao.getPregnantPatientsFlow()
    val childPatients: Flow<List<PatientEntity>> = emrDao.getChildPatientsFlow()
    val allAncCheckups: Flow<List<AncCheckupEntity>> = emrDao.getAllAncCheckupsFlow()
    val allNcdScreenings: Flow<List<NcdScreeningEntity>> = emrDao.getAllNcdScreeningsFlow()

    // --- Patient Operations ---
    suspend fun getPatientById(id: Int): PatientEntity? {
        return emrDao.getPatientById(id)
    }

    suspend fun insertPatient(patient: PatientEntity): Long {
        return emrDao.insertPatient(patient)
    }

    suspend fun updatePatient(patient: PatientEntity) {
        emrDao.updatePatient(patient)
    }

    suspend fun deletePatientById(id: Int) {
        emrDao.deletePatientById(id)
    }

    // --- ANC Operations ---
    fun getAncCheckupsForPatient(patientId: Int): Flow<List<AncCheckupEntity>> {
        return emrDao.getAncCheckupsForPatientFlow(patientId)
    }

    suspend fun insertAncCheckup(checkup: AncCheckupEntity) {
        emrDao.insertAncCheckup(checkup)
    }

    // --- Child Operations ---
    fun getChildRecordForPatientFlow(patientId: Int): Flow<ChildVaccinationEntity?> {
        return emrDao.getChildRecordForPatientFlow(patientId)
    }

    suspend fun getChildRecordForPatient(patientId: Int): ChildVaccinationEntity? {
        return emrDao.getChildRecordForPatient(patientId)
    }

    suspend fun saveChildRecord(record: ChildVaccinationEntity) {
        emrDao.insertOrUpdateChildRecord(record)
    }

    // --- NCD Operations ---
    fun getNcdScreeningsForPatient(patientId: Int): Flow<List<NcdScreeningEntity>> {
        return emrDao.getNcdScreeningsForPatientFlow(patientId)
    }

    suspend fun insertNcdScreening(screening: NcdScreeningEntity) {
        emrDao.insertNcdScreening(screening)
    }

    // --- Consultation Operations ---
    fun getConsultLogsForPatient(patientId: Int): Flow<List<ConsultLogEntity>> {
        return emrDao.getConsultLogsForPatientFlow(patientId)
    }

    suspend fun insertConsultLog(consult: ConsultLogEntity) {
        emrDao.insertConsultLog(consult)
    }
}
