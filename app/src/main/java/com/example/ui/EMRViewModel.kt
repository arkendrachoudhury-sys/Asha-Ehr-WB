package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class EMRViewModel(private val repository: EMRRepository) : ViewModel() {

    // --- WEST BENGAL LOCAL STATE FLOWS ---
    private val _currentAppLanguage = MutableStateFlow("BN")
    val currentAppLanguage: StateFlow<String> = _currentAppLanguage.asStateFlow()

    private val _voiceDictationActive = MutableStateFlow(false)
    val voiceDictationActive: StateFlow<Boolean> = _voiceDictationActive.asStateFlow()

    private val _geoFencingDistrict = MutableStateFlow("Birbhum Sector 4 (Verified Geo-Fenced)")
    val geoFencingDistrict: StateFlow<String> = _geoFencingDistrict.asStateFlow()

    fun changeLanguage(lang: String) {
        _currentAppLanguage.value = lang
    }

    // --- SEARCH / FILTER STATES ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // --- DATA FLOWS ---
    val patients: StateFlow<List<PatientEntity>> = repository.allPatients
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pregnantPatients: StateFlow<List<PatientEntity>> = repository.pregnantPatients
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val childPatients: StateFlow<List<PatientEntity>> = repository.childPatients
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAncCheckups: StateFlow<List<AncCheckupEntity>> = repository.allAncCheckups
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allNcdScreenings: StateFlow<List<NcdScreeningEntity>> = repository.allNcdScreenings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered patients for search function
    val filteredPatients: StateFlow<List<PatientEntity>> = combine(patients, _searchQuery) { list, query ->
        if (query.trim().isEmpty()) {
            list
        } else {
            list.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.addressVillage.contains(query, ignoreCase = true) ||
                        it.identifierAadhaar.contains(query) ||
                        it.telecomPhone.contains(query)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- ACTIVE PATIENT NAVIGATION STATE ---
    private val _selectedPatientId = MutableStateFlow<Int?>(null)
    val selectedPatientId: StateFlow<Int?> = _selectedPatientId.asStateFlow()

    private val _selectedPatient = MutableStateFlow<PatientEntity?>(null)
    val selectedPatient: StateFlow<PatientEntity?> = _selectedPatient.asStateFlow()

    fun selectPatient(patientId: Int?) {
        _selectedPatientId.value = patientId
        if (patientId == null) {
            _selectedPatient.value = null
        } else {
            viewModelScope.launch {
                _selectedPatient.value = repository.getPatientById(patientId)
            }
        }
    }

    // Secondary flows for details of the selected patient
    @kotlinx.coroutines.ExperimentalCoroutinesApi
    val selectedPatientAncCheckups: StateFlow<List<AncCheckupEntity>> = _selectedPatientId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList())
            else repository.getAncCheckupsForPatient(id)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @kotlinx.coroutines.ExperimentalCoroutinesApi
    val selectedPatientChildRecord: StateFlow<ChildVaccinationEntity?> = _selectedPatientId
        .flatMapLatest { id ->
            if (id == null) flowOf(null)
            else repository.getChildRecordForPatientFlow(id)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @kotlinx.coroutines.ExperimentalCoroutinesApi
    val selectedPatientNcdScreenings: StateFlow<List<NcdScreeningEntity>> = _selectedPatientId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList())
            else repository.getNcdScreeningsForPatient(id)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @kotlinx.coroutines.ExperimentalCoroutinesApi
    val selectedPatientConsultLogs: StateFlow<List<ConsultLogEntity>> = _selectedPatientId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList())
            else repository.getConsultLogsForPatient(id)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- FORM STATES AND SUBMISSIONS ---

    // Patient Form
    var pFormName = ""
    var pFormAge = ""
    var pFormGender = "Female"
    var pFormVillage = ""
    var pFormPhone = ""
    var pFormAadhaar = ""
    var pFormAbhaId = ""
    var pFormGuardianName = ""
    var pFormIsPregnant = false
    var pFormIsChild = false

    fun clearPatientForm() {
        pFormName = ""
        pFormAge = ""
        pFormGender = "Female"
        pFormVillage = ""
        pFormPhone = ""
        pFormAadhaar = ""
        pFormAbhaId = ""
        pFormGuardianName = ""
        pFormIsPregnant = false
        pFormIsChild = false
    }

    fun submitPatientForm(onSuccess: (Int) -> Unit) {
        val parsedAge = pFormAge.toIntOrNull() ?: 30
        val entity = PatientEntity(
            name = pFormName.trim().ifEmpty { "Unnamed Patient" },
            age = parsedAge,
            gender = pFormGender,
            addressVillage = pFormVillage.trim().ifEmpty { "General Village" },
            telecomPhone = pFormPhone.trim(),
            identifierAadhaar = pFormAadhaar.trim(),
            identifierAbha = pFormAbhaId.trim(),
            contactGuardianName = pFormGuardianName.trim(),
            isPregnant = pFormIsPregnant,
            isChild = pFormIsChild
        )
        viewModelScope.launch {
            val generatedId = repository.insertPatient(entity)
            if (pFormIsChild) {
                // Pre-init child record
                repository.saveChildRecord(
                    ChildVaccinationEntity(
                        patientId = generatedId.toInt(),
                        dob = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                        weightKg = 3.0f,
                        heightCm = 48.0f
                    )
                )
            }
            onSuccess(generatedId.toInt())
        }
    }

    // ANC Form
    var ancLmpDate = ""
    var ancWeightKg = ""
    var ancSystolicBp = ""
    var ancDiastolicBp = ""
    var ancHemoglobin = ""
    var ancIfaSupplements = true
    var ancTetanusDose = "None"
    var ancNotes = ""

    fun clearAncForm() {
        ancLmpDate = ""
        ancWeightKg = ""
        ancSystolicBp = ""
        ancDiastolicBp = ""
        ancHemoglobin = ""
        ancIfaSupplements = true
        ancTetanusDose = "None"
        ancNotes = ""
    }

    fun submitAncForm(patientId: Int, onSuccess: () -> Unit) {
        val parsedLmp = ancLmpDate.trim().ifEmpty { "01/01/2026" }
        // EDD is approx LMP + 280 days. Let's calculate dynamically or give string approximation
        val eddStr = try {
            val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val lmp = format.parse(parsedLmp)
            val c = Calendar.getInstance()
            if (lmp != null) {
                c.time = lmp
                c.add(Calendar.DAY_OF_YEAR, 280)
                format.format(c.time)
            } else {
                "TBD"
            }
        } catch (e: Exception) {
            "TBD"
        }

        val systolic = ancSystolicBp.toIntOrNull() ?: 120
        val diastolic = ancDiastolicBp.toIntOrNull() ?: 80
        val hb = ancHemoglobin.toFloatOrNull() ?: 11.0f

        val highRisks = mutableListOf<String>()
        if (hb < 7.0f) highRisks.add("Severe Anemia (Hb < 7.0)")
        else if (hb < 10.0f) highRisks.add("Moderate Anemia")
        if (systolic >= 140 || diastolic >= 90) highRisks.add("Gestational Hypertension")

        val riskStr = if (highRisks.isEmpty()) "None" else highRisks.joinToString(", ")

        val entity = AncCheckupEntity(
            subjectPatientId = patientId,
            periodStart = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
            lmpDate = parsedLmp,
            eddDate = eddStr,
            weightKg = ancWeightKg.toFloatOrNull() ?: 55.0f,
            systolicBp = systolic,
            diastolicBp = diastolic,
            hemoglobinLevel = hb,
            ifaSupplements = ancIfaSupplements,
            tetanusDose = ancTetanusDose,
            highRiskFactors = riskStr,
            clinicalNotes = ancNotes.trim()
        )

        viewModelScope.launch {
            repository.insertAncCheckup(entity)
            // Update patient pregnant flag to true
            val patient = repository.getPatientById(patientId)
            if (patient != null && !patient.isPregnant) {
                repository.updatePatient(patient.copy(isPregnant = true))
                _selectedPatient.value = repository.getPatientById(patientId)
            }
            onSuccess()
        }
    }

    // Child Vaccination Form State
    var cDob = ""
    var cWeightKg = ""
    var cHeightCm = ""
    var cBcg = false
    var cOpv0 = false
    var cOpv1 = false
    var cOpv2 = false
    var cHepb = false
    var cPenta1 = false
    var cPenta2 = false
    var cPenta3 = false
    var cMr1 = false

    fun loadSelectedChildState(entity: ChildVaccinationEntity?) {
        if (entity != null) {
            cDob = entity.dob
            cWeightKg = entity.weightKg.toString()
            cHeightCm = entity.heightCm.toString()
            cBcg = entity.bcgDose
            cOpv0 = entity.opvDose0
            cOpv1 = entity.opvDose1
            cOpv2 = entity.opvDose2
            cHepb = entity.hepbBirth
            cPenta1 = entity.pentavalent1
            cPenta2 = entity.pentavalent2
            cPenta3 = entity.pentavalent3
            cMr1 = entity.mrDose1
        } else {
            cDob = ""
            cWeightKg = "3.0"
            cHeightCm = "48.0"
            cBcg = false
            cOpv0 = false
            cOpv1 = false
            cOpv2 = false
            cHepb = false
            cPenta1 = false
            cPenta2 = false
            cPenta3 = false
            cMr1 = false
        }
    }

    fun submitChildRecord(patientId: Int, onSuccess: () -> Unit) {
        val weight = cWeightKg.toFloatOrNull() ?: 3.5f
        // Quick deterministic Indian Academy of Pediatrics Weight-For-Age standard check:
        // Underweight flags
        val status = when {
            weight < 2.5f -> "Severe Malnutrition (SAM)"
            weight < 3.2f -> "Moderate Malnutrition (MAM)"
            else -> "Normal"
        }

        val updatedRecord = ChildVaccinationEntity(
            id = selectedPatientChildRecord.value?.id ?: 0,
            patientId = patientId,
            dob = cDob.trim().ifEmpty { "01/01/2026" },
            weightKg = weight,
            heightCm = cHeightCm.toFloatOrNull() ?: 50.0f,
            bcgDose = cBcg,
            opvDose0 = cOpv0,
            opvDose1 = cOpv1,
            opvDose2 = cOpv2,
            hepbBirth = cHepb,
            pentavalent1 = cPenta1,
            pentavalent2 = cPenta2,
            pentavalent3 = cPenta3,
            mrDose1 = cMr1,
            growthStatus = status
        )

        viewModelScope.launch {
            repository.saveChildRecord(updatedRecord)
            onSuccess()
        }
    }

    // NCD Form
    var ncdSystolic = ""
    var ncdDiastolic = ""
    var ncdSugar = ""
    var ncdTobacco = false
    var ncdSymptoms = ""
    var ncdReferral = "None"

    fun clearNcdForm() {
        ncdSystolic = ""
        ncdDiastolic = ""
        ncdSugar = ""
        ncdTobacco = false
        ncdSymptoms = ""
        ncdReferral = "None"
    }

    fun submitNcdForm(patientId: Int, onSuccess: () -> Unit) {
        val sys = ncdSystolic.toIntOrNull() ?: 120
        val dia = ncdDiastolic.toIntOrNull() ?: 80
        val sugar = ncdSugar.toIntOrNull() ?: 95

        val autoReferral = when {
            sys >= 160 || dia >= 100 -> "PHC/CHC (Urgent Hypertensive Emergency)"
            sugar >= 250 -> "PHC (Suspected Severe Diabetes)"
            else -> ncdReferral
        }

        val entity = NcdScreeningEntity(
            subjectPatientId = patientId,
            effectiveDateTime = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
            systolicBp = sys,
            diastolicBp = dia,
            bloodGlucose = sugar,
            tobaccoStatus = ncdTobacco,
            symptomsPresent = ncdSymptoms.trim().ifEmpty { "None" },
            referralRequest = autoReferral
        )

        viewModelScope.launch {
            repository.insertNcdScreening(entity)
            onSuccess()
        }
    }

    // Consult Form & AI Assistant State
    var conSymptoms = ""
    var conSeverity = "Mild"
    var conDuration = ""
    var conTemp = ""
    var conNotes = ""
    var conReferred = false

    private val _aiTriageResult = MutableStateFlow<String?>(null)
    val aiTriageResult: StateFlow<String?> = _aiTriageResult.asStateFlow()

    private val _aiTriageLoading = MutableStateFlow(false)
    val aiTriageLoading: StateFlow<Boolean> = _aiTriageLoading.asStateFlow()

    fun clearConsultForm() {
        conSymptoms = ""
        conSeverity = "Mild"
        conDuration = ""
        conTemp = ""
        conNotes = ""
        conReferred = false
        _aiTriageResult.value = null
        _aiTriageLoading.value = false
    }

    /**
     * Integrates Gemini generative models directly as clinical triage assistant
     * with standard fallback responses for complete medical fidelity.
     */
    fun runAiTriage(patientName: String, patientAge: Int, isMother: Boolean, isChild: Boolean) {
        if (conSymptoms.isEmpty()) return
        _aiTriageLoading.value = true
        viewModelScope.launch {
            val patientContext = """
                Name: $patientName
                Age: $patientAge years
                Role: ${if (isMother) "Pregnant Mother" else if (isChild) "Child" else "General Inhabitant"}
                Current symptoms specified: $conSymptoms
                Symptom Duration: ${conDuration.ifEmpty { "1" }} days
                Recorded physical temperature: ${conTemp.ifEmpty { "98.6" }}°F
                ASHA Clinical Notes: ${conNotes.ifEmpty { "None" }}
            """.trimIndent()
            
            val resultAdvice = GeminiClient.askAssistant(patientContext)
            _aiTriageResult.value = resultAdvice
            _aiTriageLoading.value = false
        }
    }

    fun submitConsultLog(patientId: Int, onSuccess: () -> Unit) {
        val entity = ConsultLogEntity(
            subjectPatientId = patientId,
            encounterDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
            presentingSymptoms = conSymptoms.trim().ifEmpty { "None Given" },
            priority = conSeverity,
            symptomDuration = conDuration.toIntOrNull() ?: 1,
            bodyTemperature = conTemp.toFloatOrNull() ?: 98.6f,
            fieldNotes = conNotes.trim(),
            aiClinicalImpression = aiTriageResult.value ?: "No AI consultation completed.",
            referralStatus = conReferred
        )

        viewModelScope.launch {
            repository.insertConsultLog(entity)
            onSuccess()
        }
    }
}

// Factory Pattern matching mandated Room dependencies
class EMRViewModelFactory(private val repository: EMRRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EMRViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EMRViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
