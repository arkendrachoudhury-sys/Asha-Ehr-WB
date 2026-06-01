package com.example.ui

import java.util.Locale
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.theme.ColorHighRisk
import com.example.ui.theme.ColorNormal
import com.example.ui.theme.ColorWarning
import kotlinx.coroutines.launch

// ==========================================
// REGIONAL SETTINGS & INTERACTION CONSTANTS
// ==========================================

object AshaSettings {
    var currentLanguage = mutableStateOf("BN") // Defaulting to Bengali (বাংলা) for extensive Use in West Bengal
}

object AshaTranslations {
    private val dictionary = mapOf(
        "app_title" to mapOf(
            "EN" to "ASHA CLINICAL EMR",
            "BN" to "আশা ক্লিনিক্যাল ই-মেডিকেল রেকর্ড",
            "HI" to "आशा क्लिनिकल ई-एमआर"
        ),
        "app_subtitle" to mapOf(
            "EN" to "Dept. of Health & FW, Govt. of West Bengal",
            "BN" to "স্বাস্থ্য ও পরিবার কল্যাণ দপ্তর, পশ্চিমবঙ্গ সরকার",
            "HI" to "स्वास्थ्य एवं परिवार कल्याण विभाग, पश्चिम बंगाल सरकार"
        ),
        "directory" to mapOf(
            "EN" to "Directory",
            "BN" to "রোগী তালিকা",
            "HI" to "रोगी सूची"
        ),
        "anc_mch" to mapOf(
            "EN" to "ANC / Maternal",
            "BN" to "মাতৃ স্বাস্থ্য",
            "HI" to "मातृ स्वास्थ्य"
        ),
        "immunize" to mapOf(
            "EN" to "Immunize",
            "BN" to "শিশু প্রতিষেধক",
            "HI" to "شिशु टीकाकरण"
        ),
        "ncd" to mapOf(
            "EN" to "NCD Stats",
            "BN" to "অসংক্রামক রোগ",
            "HI" to "गैर-संचारी रोग"
        ),
        "search_placeholder" to mapOf(
            "EN" to "Search by Name, Village, ID...",
            "BN" to "নাম, গ্রাম বা আইডি দিয়ে খুঁজুন...",
            "HI" to "नाम, गाँव या आईडी से खोजें..."
        ),
        "sync_success" to mapOf(
            "EN" to "EMR Data synced successfully with PHC server.",
            "BN" to "পিএইচসি সার্ভারের সাথে সমস্ত তথ্য সফলভাবে সিঙ্ক করা হয়েছে।",
            "HI" to "पीएचसी सर्वर के साथ ईएमआर डेटा सफलतापूर्वक सिंक किया गया।"
        ),
        "offline_mode" to mapOf(
            "EN" to "Offline Mode Active (Secured Local Cache)",
            "BN" to "অফলাইন মোড সক্রিয় (স্থানীয় ক্যাশে সুরক্ষিত)",
            "HI" to "ऑफ़लाइन मोड सक्रिय (सुरक्षित स्थानीय कैश)"
        ),
        "geo_fenced" to mapOf(
            "EN" to "Anganwadi Center-Verified Geo-Tag Enforced",
            "BN" to "অঙ্গনওয়াড়ি কেন্দ্র জিপিএস যাচাই সম্পন্ন",
            "HI" to "आंगनवाड़ी केंद्र जीपीएस सत्यापन पूर्ण"
        ),
        "get_advice" to mapOf(
            "EN" to "Get AI Clinical Advice",
            "BN" to "এআই ক্লিনিকাল পরামর্শ পান",
            "HI" to "एआई नैदानিক सलाह प्राप्त करें"
        ),
        "voice_dictate" to mapOf(
            "EN" to "Simulate Voice Dictation",
            "BN" to "কণ্ঠস্বর নির্দেশ অনুকরণ (ভয়েস ডিক্টেশন)",
            "HI" to "आवाज श्रुतलेखन अनुकरण करें"
        ),
        "maternal_priority" to mapOf(
            "EN" to "Maternal Health Highlights",
            "BN" to "মাতৃ স্বাস্থ্য পরিমাপ",
            "HI" to "मातृ स्वास्थ्य हाइलाइट्स"
        ),
        "newborns" to mapOf(
            "EN" to "Newborns Registered",
            "BN" to "নিবন্ধিত নবজাতক",
            "HI" to "पंजीकृत नवजात शिशु"
        ),
        "vaccination_pending" to mapOf(
            "EN" to "Vaccinations Pending",
            "BN" to "বাকি টিকাকরণ",
            "HI" to "टीकाकरण लंबित"
        ),
        "survey_progress" to mapOf(
            "EN" to "Village Mapping & Survey",
            "BN" to "গ্রামের সমীক্ষা অগ্রগতি",
            "HI" to "ग्राम सर्वेक्षण प्रगति"
        ),
        "save_visit" to mapOf(
            "EN" to "Save Visit Record",
            "BN" to "পরিদর্শন রেকর্ড সংরক্ষণ করুন",
            "HI" to "यात्रा रिकॉर्ड सहेजें"
        ),
        "voice_simulated_text" to mapOf(
            "EN" to "Patient reports mild fever and dry cough since yesterday. Advised paracetamol and hydration.",
            "BN" to "গতকাল থেকে রোগীর হালকা জ্বর এবং শুকনো কাশি রয়েছে। প্যারাসিটামল এবং পর্যাপ্ত জল খাওয়ার পরামর্শ দেওয়া হল।",
            "HI" to "कल से मरीज को हल्का बुखार और सूखी खांसी है। पैरासिटामोल और पर्याप्त पानी पीने की सलाह दी गई।"
        )
    )

    fun get(key: String, lang: String = AshaSettings.currentLanguage.value): String {
        return dictionary[key]?.get(lang) ?: dictionary[key]?.get("EN") ?: key
    }
}

// ==========================================
// NAVIGATION BACKSTACK MODEL
// ==========================================

sealed class EMRScreen {
    object Dashboard : EMRScreen()
    object RegisterPatient : EMRScreen()
    data class PatientDetail(val patientId: Int) : EMRScreen()
    data class AddAnc(val patientId: Int) : EMRScreen()
    data class EditChild(val patientId: Int) : EMRScreen()
    data class AddNcd(val patientId: Int) : EMRScreen()
    data class AddConsult(val patientId: Int) : EMRScreen()
}

// ==========================================
// CORE EMR CONTAINER LAYOUT
// ==========================================

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EMRAppContent(viewModel: EMRViewModel) {
    val backStack = remember { mutableStateListOf<EMRScreen>(EMRScreen.Dashboard) }
    val currentScreen = backStack.lastOrNull() ?: EMRScreen.Dashboard

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Intercept hardware back button
    BackHandler(enabled = backStack.size > 1) {
        if (backStack.size > 1) {
            backStack.removeAt(backStack.lastIndex)
        }
    }

    fun navigateTo(screen: EMRScreen) {
        backStack.add(screen)
    }

    fun navigateBack() {
        if (backStack.size > 1) {
            backStack.removeAt(backStack.lastIndex)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            val activeLang = AshaSettings.currentLanguage.value
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = AshaTranslations.get("app_title", activeLang),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = AshaTranslations.get("app_subtitle", activeLang),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                navigationIcon = {
                    if (currentScreen != EMRScreen.Dashboard) {
                        IconButton(onClick = { navigateBack() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Go Back",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Default.LocalHospital,
                            contentDescription = "Medical Logo",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                },
                actions = {
                    Row(
                        modifier = Modifier.padding(end = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Regional prominent single-tap language toggles
                        listOf("EN" to "EN", "BN" to "বাং", "HI" to "हिं").forEach { (code, label) ->
                            val isSelected = activeLang == code
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 2.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.onPrimary 
                                        else Color.Transparent
                                    )
                                    .border(
                                        1.dp, 
                                        if (isSelected) Color.Transparent else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.4f), 
                                        RoundedCornerShape(6.dp)
                                    )
                                    .clickable { 
                                        AshaSettings.currentLanguage.value = code 
                                    }
                                    .padding(horizontal = 6.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                        
                        IconButton(onClick = {
                            scope.launch {
                                snackbarHostState.showSnackbar(AshaTranslations.get("sync_success", activeLang))
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = "Sync Cloud Gateway",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                is EMRScreen.Dashboard -> {
                    DashboardScreen(
                        viewModel = viewModel,
                        onRegisterClicked = { navigateTo(EMRScreen.RegisterPatient) },
                        onPatientClicked = { id ->
                            viewModel.selectPatient(id)
                            navigateTo(EMRScreen.PatientDetail(id))
                        },
                        snackbarHostState = snackbarHostState
                    )
                }
                is EMRScreen.RegisterPatient -> {
                    RegisterPatientScreen(
                        viewModel = viewModel,
                        onSubmitted = { id ->
                            viewModel.selectPatient(id)
                            // Remove Register screen and load Details
                            backStack.removeAt(backStack.lastIndex)
                            navigateTo(EMRScreen.PatientDetail(id))
                            scope.launch {
                                snackbarHostState.showSnackbar("Patient registered successfully!")
                            }
                        },
                        onCancelled = { navigateBack() }
                    )
                }
                is EMRScreen.PatientDetail -> {
                    val pId = currentScreen.patientId
                    PatientDetailsScreen(
                        patientId = pId,
                        viewModel = viewModel,
                        onNavBack = { navigateBack() },
                        onAddAncClicked = { navigateTo(EMRScreen.AddAnc(pId)) },
                        onEditChildClicked = { navigateTo(EMRScreen.EditChild(pId)) },
                        onAddNcdClicked = { navigateTo(EMRScreen.AddNcd(pId)) },
                        onAddConsultClicked = { navigateTo(EMRScreen.AddConsult(pId)) }
                    )
                }
                is EMRScreen.AddAnc -> {
                    AddAncScreen(
                        patientId = currentScreen.patientId,
                        viewModel = viewModel,
                        onSuccess = {
                            navigateBack()
                            scope.launch {
                                snackbarHostState.showSnackbar("ANC checkup recorded.")
                            }
                        },
                        onClose = { navigateBack() }
                    )
                }
                is EMRScreen.EditChild -> {
                    EditChildScreen(
                        patientId = currentScreen.patientId,
                        viewModel = viewModel,
                        onSuccess = {
                            navigateBack()
                            scope.launch {
                                snackbarHostState.showSnackbar("Child vaccinations updated.")
                            }
                        },
                        onClose = { navigateBack() }
                    )
                }
                is EMRScreen.AddNcd -> {
                    AddNcdScreen(
                        patientId = currentScreen.patientId,
                        viewModel = viewModel,
                        onSuccess = {
                            navigateBack()
                            scope.launch {
                                snackbarHostState.showSnackbar("NCD screening recorded.")
                            }
                        },
                        onClose = { navigateBack() }
                    )
                }
                is EMRScreen.AddConsult -> {
                    AddConsultScreen(
                        patientId = currentScreen.patientId,
                        viewModel = viewModel,
                        onSuccess = {
                            navigateBack()
                            scope.launch {
                                snackbarHostState.showSnackbar("Symptom consultation saved.")
                            }
                        },
                        onClose = { navigateBack() }
                    )
                }
            }
        }
    }
}

// ==========================================
// 1. BENTO GRID STATS PANEL & HOME DASHBOARD
// ==========================================

@Composable
fun BentoGridDashboard(
    pregnantCount: Int,
    childCount: Int,
    onTabChange: (Int) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val lang = AshaSettings.currentLanguage.value
    val scope = rememberCoroutineScope()
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp)
            .background(Color(0xFFFEF7F3), RoundedCornerShape(28.dp))
            .border(1.5.dp, Color(0xFFF4D2C6), RoundedCornerShape(28.dp))
            .padding(18.dp)
    ) {
        // Anganwadi Worker header row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (lang == "BN") "পশ্চিমবঙ্গ আশা কর্মী" else if (lang == "HI") "पश्चिम बंगाल आशा कार्यकर्ता" else "WEST BENGAL ASHA WORKER",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF79747E),
                    letterSpacing = 1.sp
                )
                Text(
                    text = if (lang == "BN") "সুস্মিতা ব্যানার্জি" else if (lang == "HI") "सुस्मिता बनर्जी" else "Susmita Banerjee",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF201A19)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4CAF50))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (lang == "BN") "বীরভূম সেক্টর ৪ • অনলাইন" else if (lang == "HI") "बीरभूम सेक्टर ४ • ऑनलाइन" else "Birbhum Sector 4 • Active Online",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                }
            }
            
            // Avatar Circle
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFDBCF))
                    .border(2.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "SB",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = Color(0xFF201A19)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Bento Grid structure
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Saffron/Peach Bento Card: Maternal Health (Colspan 1.2)
            Column(
                modifier = Modifier
                    .weight(1.1f)
                    .height(135.dp)
                    .background(Color(0xFFFFDBCF), RoundedCornerShape(26.dp))
                    .border(1.dp, Color(0xFFF4D2C6), RoundedCornerShape(26.dp))
                    .clickable { onTabChange(1) }
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(Color.White.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PregnantWoman,
                            contentDescription = "Maternal Priority Icon",
                            tint = Color(0xFFFF6F00),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.40f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (lang == "BN") "অগ্রাধিকার" else if (lang == "HI") "प्राथमिकता" else "Priority",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF201A19)
                        )
                    }
                }
                
                Column {
                    Text(
                        text = AshaTranslations.get("maternal_priority", lang),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF201A19),
                        lineHeight = 15.sp
                    )
                    Text(
                        text = if (lang == "BN") "১৪টি এএনসি পরীক্ষা বাকি" else if (lang == "HI") "१४ जांच लंबित" else "14 ANC due this week",
                        fontSize = 11.sp,
                        color = Color(0xFF524441),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            // Soft Blue Bento Card: newborns (Colspan 0.8)
            Column(
                modifier = Modifier
                    .weight(0.9f)
                    .height(135.dp)
                    .background(Color(0xFFD3E4FF), RoundedCornerShape(26.dp))
                    .border(1.dp, Color(0xFFBAC9E2), RoundedCornerShape(26.dp))
                    .clickable { onTabChange(2) }
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .background(Color.White.copy(alpha = 0.6f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("👶", fontSize = 16.sp)
                }
                
                Column {
                    Text(
                        text = String.format("%02d", childCount),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D1B1E)
                    )
                    Text(
                        text = AshaTranslations.get("newborns", lang),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF49454F),
                        lineHeight = 13.sp
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(10.dp))
        
        // Colspan 2: Progress Survey Box
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(26.dp))
                .border(1.5.dp, Color(0xFFE6E0E9), RoundedCornerShape(26.dp))
                .clickable {
                    scope.launch {
                        snackbarHostState.showSnackbar(AshaTranslations.get("geo_fenced", lang))
                    }
                }
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = AshaTranslations.get("survey_progress", lang),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp,
                    color = Color(0xFF1D1B1E)
                )
                
                // Thick rounded capsule progress bar conforming to Bento parameters
                Box(
                    modifier = Modifier
                        .padding(vertical = 6.dp)
                        .fillMaxWidth()
                        .height(9.dp)
                        .background(Color(0xFFE6E0E9), RoundedCornerShape(5.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(0.65f)
                            .background(Color(0xFFFF5449), RoundedCornerShape(5.dp))
                    )
                }
                
                Text(
                    text = if (lang == "BN") "৬৫% সিউড়ি গ্রাম সমীক্ষা সম্পন্ন হয়েছে" else if (lang == "HI") "६५% ग्राम सर्वे पूर्ण" else "65% of Village Birbhum mapped",
                    fontSize = 11.sp,
                    color = Color(0xFF49454F),
                    fontWeight = FontWeight.Medium
                )
            }
            
            // Beautiful progress percentage circle
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .border(3.1.dp, Color(0xFFFF5449), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "65%",
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    color = Color(0xFF1D1B1E)
                )
            }
        }
    }
}

// ==========================================
// 2. DASHBOARD COMPOSABLE
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: EMRViewModel,
    onRegisterClicked: () -> Unit,
    onPatientClicked: (Int) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    var activeTab by remember { mutableStateOf(0) } // 0: Patient List, 1: MCH (Maternal Tracker), 2: Immunization, 3: NCD Stats
    val searchVal by viewModel.searchQuery.collectAsState()
    val activeLang = AshaSettings.currentLanguage.value
    
    val pregnantList by viewModel.pregnantPatients.collectAsState()
    val childList by viewModel.childPatients.collectAsState()

    var showVoiceSearchDialog by remember { mutableStateOf(false) }

    // Simulated Voice dictation Search popup
    if (showVoiceSearchDialog) {
        AlertDialog(
            onDismissRequest = { showVoiceSearchDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Mic, "Mic", tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (activeLang == "BN") "কণ্ঠস্বর বার্তা বিশ্লেষণ..." 
                               else if (activeLang == "HI") "आवाज संदेश विश्लेषण..." 
                               else "Voice Triage Translation...",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = if (activeLang == "BN") "আপনার বক্তব্য নথিভুক্ত হচ্ছে। অনুগ্রহ করে রোগীর নাম বলুন..." 
                               else if (activeLang == "HI") "मरीज का नाम बोलें..." 
                               else "Speak resident name to trigger instant matching...",
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Waveforms
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf(15, 35, 55, 30, 45, 20, 40).forEach { h ->
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .height(h.dp)
                                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(2.dp))
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (activeLang == "BN") "খোঁজা হচ্ছে: \"মীরা দাস\"" else if (activeLang == "HI") "खोज: \"मीरा दास\"" else "Recognized: \"Meera Das\"",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.updateSearchQuery("Meera")
                    showVoiceSearchDialog = false
                }) {
                    Text(if (activeLang == "BN") "খুঁজুন" else if (activeLang == "HI") "खोजें" else "Search")
                }
            },
            dismissButton = {
                TextButton(onClick = { showVoiceSearchDialog = false }) {
                    Text(if (activeLang == "BN") "বাতিল" else if (activeLang == "HI") "रद्द करें" else "Cancel")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Render beautiful Bento Grid Stats Panel at top
        BentoGridDashboard(
            pregnantCount = pregnantList.size,
            childCount = childList.size,
            onTabChange = { activeTab = it },
            snackbarHostState = snackbarHostState
        )

        // Search & Register Header Strip
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchVal,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("search_input"),
                    placeholder = { Text(AshaTranslations.get("search_placeholder", activeLang), fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, "Search Icon") },
                    trailingIcon = {
                        IconButton(onClick = { showVoiceSearchDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Mic, 
                                contentDescription = "Voice Dictation", 
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onRegisterClicked,
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .height(54.dp)
                        .testTag("register_btn")
                ) {
                    Icon(Icons.Default.PersonAdd, "Add Patient Icon")
                }
            }
        }

        // Segmented Tabs
        TabRow(selectedTabIndex = activeTab, containerColor = MaterialTheme.colorScheme.background) {
            Tab(selected = activeTab == 0, onClick = { activeTab = 0 }) {
                Column(
                    modifier = Modifier.padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = if (activeTab == 0) Icons.Filled.List else Icons.Outlined.List,
                        contentDescription = "Directory Tab"
                    )
                    Text(AshaTranslations.get("directory", activeLang), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
            Tab(selected = activeTab == 1, onClick = { activeTab = 1 }) {
                Column(
                    modifier = Modifier.padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = if (activeTab == 1) Icons.Filled.PregnantWoman else Icons.Outlined.PregnantWoman,
                        contentDescription = "MCH Pregnancy Tab"
                    )
                    Text(AshaTranslations.get("anc_mch", activeLang), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
            Tab(selected = activeTab == 2, onClick = { activeTab = 2 }) {
                Column(
                    modifier = Modifier.padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = if (activeTab == 2) Icons.Filled.Face else Icons.Outlined.Face,
                        contentDescription = "Child Immunization Tab"
                    )
                    Text(AshaTranslations.get("immunize", activeLang), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
            Tab(selected = activeTab == 3, onClick = { activeTab = 3 }) {
                Column(
                    modifier = Modifier.padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = if (activeTab == 3) Icons.Filled.Healing else Icons.Outlined.Healing,
                        contentDescription = "NCD Stats Tab"
                    )
                    Text(AshaTranslations.get("ncd", activeLang), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Tab Content
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 350.dp, max = 1200.dp)
        ) {
            when (activeTab) {
                0 -> PatientListTab(viewModel, onPatientClicked)
                1 -> MaternalAncTab(viewModel, onPatientClicked)
                2 -> ChildImmunizationTab(viewModel, onPatientClicked)
                3 -> NcdAdultTab(viewModel, onPatientClicked)
            }
        }
    }
}

// --- TAB SUBCOMPOSABLES ---

@Composable
fun PatientListTab(viewModel: EMRViewModel, onPatientClicked: (Int) -> Unit) {
    val patients by viewModel.filteredPatients.collectAsState()

    if (patients.isEmpty()) {
        EmptyPlaceholder(
            title = "No Patients Registered",
            description = "ASHA workers can tap the green '+' button on the top right to register individuals in their catchment village."
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(patients, key = { it.id }) { patient ->
                PatientRowItem(patient, onPatientClicked)
            }
        }
    }
}

@Composable
fun MaternalAncTab(viewModel: EMRViewModel, onPatientClicked: (Int) -> Unit) {
    val pregnantList by viewModel.pregnantPatients.collectAsState()
    val allCheckups by viewModel.allAncCheckups.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp)) {
        // Quick Stats banner
        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Active Pregnancies", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Text(
                        "${pregnantList.size} Mothers in Registry",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
                Text(
                    "${allCheckups.size} ANC Logs",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        if (pregnantList.isEmpty()) {
            EmptyPlaceholder(
                title = "No ANC Program Records",
                description = "Maternal Health tracking lists pregnant women needing antenatal checkups. Mark 'Is Pregnant' in the directory details."
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 14.dp)
            ) {
                items(pregnantList) { m ->
                    ElevatedCard(
                        onClick = { onPatientClicked(m.id) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(m.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("Village: ${m.village} • Age: ${m.age}", fontSize = 12.sp)
                                Text("Aadhaar: ${m.aadhaar}", fontSize = 11.sp, color = Color.Gray)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ColorHighRisk.copy(alpha = 0.15f))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text("ANC Due", color = ColorHighRisk, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChildImmunizationTab(viewModel: EMRViewModel, onPatientClicked: (Int) -> Unit) {
    val childList by viewModel.childPatients.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Children monitored", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Text("0 to 5 Years Wellness Profiles", fontSize = 11.sp)
                }
                Text("${childList.size} Infants", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (childList.isEmpty()) {
            EmptyPlaceholder(
                title = "No Children Profiles",
                description = "Track growth status and immunization dates. Register or edit standard patients with 'Is Child' checked."
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 14.dp)
            ) {
                items(childList) { c ->
                    ElevatedCard(
                        onClick = { onPatientClicked(c.id) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(c.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("Guardian: ${c.guardianName}", fontSize = 12.sp)
                                Text("Village: ${c.village} • Age: ${c.age} Yrs", fontSize = 12.sp)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ColorNormal.copy(alpha = 0.15f))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text("Growth OK", color = ColorNormal, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NcdAdultTab(viewModel: EMRViewModel, onPatientClicked: (Int) -> Unit) {
    val list by viewModel.patients.collectAsState()
    val adultList = list.filter { it.age >= 30 }
    val ncdScreeningList by viewModel.allNcdScreenings.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("NCD Screenings (Age 30+)", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Text("Preventative Diabetes & Hypertension Screens", fontSize = 11.sp)
                }
                Text("${ncdScreeningList.size} Done", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (adultList.isEmpty()) {
            EmptyPlaceholder(
                title = "No Eligible Adults Available",
                description = "ASHA tracks adult health metrics (Tobacco consumption, sugar counts, and BP) to screen for Non-Communicable chronic diseases."
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 14.dp)
            ) {
                items(adultList) { adult ->
                    ElevatedCard(
                        onClick = { onPatientClicked(adult.id) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(adult.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("Village: ${adult.village} • Age: ${adult.age}", fontSize = 12.sp)
                                Text("Phone: ${adult.phone}", fontSize = 12.sp)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ColorWarning.copy(alpha = 0.15f))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text("Screening Due", color = ColorWarning, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- SINGLE LIST ROW DESIGNS ---

@Composable
fun PatientRowItem(patient: PatientEntity, onPatientClicked: (Int) -> Unit) {
    val lang = AshaSettings.currentLanguage.value
    val containerBg = when {
        patient.isPregnant -> Color(0xFFFFDBCF) // Saffron primary-peach for pregnancy/ANC
        patient.isChild -> Color(0xFFE2F1E4) // Secondary Leaf-Green for child/immunization
        else -> Color.White // Clean white for others
    }
    val borderCol = when {
        patient.isPregnant -> Color(0xFFF4D2C6)
        patient.isChild -> Color(0xFFC0E2C3)
        else -> Color(0xFFE6E0E9)
    }

    Card(
        onClick = { onPatientClicked(patient.id) },
        modifier = Modifier
            .fillMaxWidth()
            .testTag("patient_card_${patient.id}")
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(26.dp),
        border = BorderStroke(1.5.dp, borderCol),
        colors = CardDefaults.cardColors(containerColor = containerBg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar Medical Badge
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(1.5.dp, borderCol, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when {
                        patient.isPregnant -> Icons.Default.PregnantWoman
                        patient.isChild -> Icons.Default.Face
                        else -> Icons.Default.Person
                    },
                    contentDescription = "Contact Avatar Graphic",
                    tint = when {
                        patient.isPregnant -> Color(0xFFFF6F00) // Saffron
                        patient.isChild -> Color(0xFF4CAF50) // Leaf Green
                        else -> MaterialTheme.colorScheme.primary
                    },
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = patient.name,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 17.sp,
                        color = Color(0xFF201A19)
                    )
                    if (patient.isPregnant) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFFF6F00))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (lang == "BN") "গর্ভবতী" else if (lang == "HI") "गर्भवती" else "ANC ACTIVE",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    } else if (patient.isChild) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF4CAF50))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (lang == "BN") "শিশু" else if (lang == "HI") "शिशु" else "CHILD / IMM",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "${if (lang == "BN") "বয়স" else if (lang == "HI") "उम्र" else "Age"}: ${patient.age} • ${if (lang == "BN") "গ্রাম" else if (lang == "HI") "गाँव" else "Village"}: ${patient.village}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF524441)
                )
                if (patient.aadhaar.isNotEmpty()) {
                    Text(
                        text = "${if (lang == "BN") "আধার" else if (lang == "HI") "आधार" else "Aadhaar"}: ${patient.aadhaar}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Gray
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Details indicator icon",
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
            )
        }
    }
}

// ==========================================
// 2. REGISTER PATIENT SCREEN
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterPatientScreen(
    viewModel: EMRViewModel,
    onSubmitted: (Int) -> Unit,
    onCancelled: () -> Unit
) {
    var name by remember { mutableStateOf(viewModel.pFormName) }
    var age by remember { mutableStateOf(viewModel.pFormAge) }
    var gender by remember { mutableStateOf(viewModel.pFormGender) }
    var village by remember { mutableStateOf(viewModel.pFormVillage) }
    var phone by remember { mutableStateOf(viewModel.pFormPhone) }
    var aadhaar by remember { mutableStateOf(viewModel.pFormAadhaar) }
    var guardian by remember { mutableStateOf(viewModel.pFormGuardianName) }
    var pregnant by remember { mutableStateOf(viewModel.pFormIsPregnant) }
    var child by remember { mutableStateOf(viewModel.pFormIsChild) }

    // Validation
    var nameErr by remember { mutableStateOf(false) }
    var ageErr by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(18.dp)
    ) {
        Text("Register Resident", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = MaterialTheme.colorScheme.primary)
        Text("Register families and members in the village catchment ledger.", fontSize = 13.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it; nameErr = false; viewModel.pFormName = it },
            label = { Text("Resident Full Name *") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .testTag("form_name"),
            isError = nameErr,
            singleLine = true
        )
        if (nameErr) {
            Text("Full Patient Name is required.", color = ColorHighRisk, fontSize = 11.sp)
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
                value = age,
                onValueChange = { age = it; ageErr = false; viewModel.pFormAge = it },
                label = { Text("Age (Years) *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp),
                isError = ageErr,
                singleLine = true
            )
            // Gender Dropdown Mock Selection
            Column(modifier = Modifier.weight(1f).padding(vertical = 8.dp)) {
                Text("Gender Selector", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                var expanded by remember { mutableStateOf(false) }
                Box {
                    Button(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                        Text(gender)
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        listOf("Female", "Male", "Other").forEach { tag ->
                            DropdownMenuItem(text = { Text(tag) }, onClick = {
                                gender = tag
                                viewModel.pFormGender = tag
                                expanded = false
                            })
                        }
                    }
                }
            }
        }
        if (ageErr) {
            Text("Enter a valid whole-number age.", color = ColorHighRisk, fontSize = 11.sp)
        }

        OutlinedTextField(
            value = village,
            onValueChange = { village = it; viewModel.pFormVillage = it },
            label = { Text("Village / Ward Name *") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .testTag("form_village"),
            singleLine = true
        )

        OutlinedTextField(
            value = guardian,
            onValueChange = { guardian = it; viewModel.pFormGuardianName = it },
            label = { Text("Guardian / Husband's Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it; viewModel.pFormPhone = it },
            label = { Text("Mobile Contact Number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = aadhaar,
            onValueChange = { aadhaar = it; viewModel.pFormAadhaar = it },
            label = { Text("12-Digit Aadhaar Identification") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            singleLine = true
        )

        // Program Classifications
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Health Program Ledger Segmenting:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().clickable {
                        pregnant = !pregnant
                        viewModel.pFormIsPregnant = pregnant
                    }
                ) {
                    Checkbox(checked = pregnant, onCheckedChange = {
                        pregnant = it
                        viewModel.pFormIsPregnant = it
                    })
                    Text("Classify Pregnant (Enroll in ANC/MCH Care Ledger)", fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().clickable {
                        child = !child
                        viewModel.pFormIsChild = child
                    }
                ) {
                    Checkbox(checked = child, onCheckedChange = {
                        child = it
                        viewModel.pFormIsChild = it
                    })
                    Text("Classify Child under 5 (Enroll in Immunization Record)", fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Actions Row
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = onCancelled,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }
            Button(
                onClick = {
                    if (name.trim().isEmpty()) {
                        nameErr = true
                        return@Button
                    }
                    if (age.trim().toIntOrNull() == null) {
                        ageErr = true
                        return@Button
                    }
                    viewModel.submitPatientForm { id ->
                        onSubmitted(id)
                    }
                },
                modifier = Modifier.weight(1f).testTag("submit_form_btn")
            ) {
                Text("Register")
            }
        }
    }
}

// ==========================================
// 3. PATIENT DETAILS SCREEN
// ==========================================

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PatientDetailsScreen(
    patientId: Int,
    viewModel: EMRViewModel,
    onNavBack: () -> Unit,
    onAddAncClicked: () -> Unit,
    onEditChildClicked: () -> Unit,
    onAddNcdClicked: () -> Unit,
    onAddConsultClicked: () -> Unit
) {
    val patientState by viewModel.selectedPatient.collectAsState()
    val checkups by viewModel.selectedPatientAncCheckups.collectAsState()
    val childRecord by viewModel.selectedPatientChildRecord.collectAsState()
    val screenings by viewModel.selectedPatientNcdScreenings.collectAsState()
    val consults by viewModel.selectedPatientConsultLogs.collectAsState()

    val patient = patientState ?: return

    // Screen consists of detailed vertical subpanels in a LazyColumn
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Demographics card
        item {
            val lang = AshaSettings.currentLanguage.value
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF7F3)) // Peach background
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (lang == "BN") "রোগীর বিবরণ" else if (lang == "HI") "रोगी का विवरण" else "BENEFICIARY PROFILE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = patient.name.uppercase(),
                                fontWeight = FontWeight.Black,
                                fontSize = 21.sp,
                                color = Color(0xFF201A19)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                "ID: ${patient.id}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                    
                    Divider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                    
                    // Distinct Bento-Grid Sub-tiles with rounded shapes
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Village tile
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.White)
                                .border(1.dp, Color(0xFFE6E0E9), RoundedCornerShape(14.dp))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = if (lang == "BN") "গ্রাম / অঞ্চল:" else if (lang == "HI") "गाँव / क्षेत्र:" else "Village Group:",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                            Text(
                                text = patient.village,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF1D1B1E)
                            )
                        }
                        // Age & Gender tile
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.White)
                                .border(1.dp, Color(0xFFE6E0E9), RoundedCornerShape(14.dp))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = if (lang == "BN") "বয়স ও লিঙ্গ:" else if (lang == "HI") "उम्र और लिंग:" else "Age & Gender:",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                            Text(
                                text = "${patient.age} Yrs • ${patient.gender}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF1D1B1E)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (patient.guardianName.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                                .border(1.dp, Color(0xFFE6E0E9), RoundedCornerShape(12.dp))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (lang == "BN") "অভিভাবক / স্বামী: " else if (lang == "HI") "अभिवक्ता / पति: " else "Guardian: ",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                            Text(
                                text = patient.guardianName,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF1D1B1E)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    if (patient.aadhaar.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                                .border(1.dp, Color(0xFFE6E0E9), RoundedCornerShape(12.dp))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (lang == "BN") "আধার নম্বর: " else if (lang == "HI") "आधार संख्या: " else "Aadhaar: ",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                            Text(
                                text = patient.aadhaar,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF1D1B1E)
                            )
                        }
                    }
                }
            }
        }

        // Program specific sections
        if (patient.isPregnant) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.PregnantWoman, "Mother ANC icon", tint = ColorHighRisk)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("MCH - Antenatal Care Tracking", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                            IconButton(onClick = onAddAncClicked) {
                                Icon(Icons.Default.Add, "Log Checkup")
                            }
                        }

                        if (checkups.isEmpty()) {
                            Text("No ANC checkups logged yet.", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(vertical = 8.dp))
                        } else {
                            val latest = checkups.first()
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.background)
                                    .padding(10.dp)
                            ) {
                                Column {
                                    Text("Latest ANC Checkup Done: ${latest.checkupDate}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Expected Delivery (EDD): ${latest.eddDate}", color = ColorHighRisk, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Weight: ${latest.weightKg} Kg • BP: ${latest.systolicBp}/${latest.diastolicBp} mmHg", fontSize = 12.sp)
                                    Text("Hemoglobin: ${latest.hemoglobinLevel} g/dL", fontSize = 12.sp)
                                    Text("High-Risk Factors: ${latest.highRiskFactors}", fontSize = 12.sp, color = if (latest.highRiskFactors != "None") ColorHighRisk else ColorNormal)
                                }
                            }
                        }
                    }
                }
            }
        }

        if (patient.isChild) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Face, "Baby icon", tint = ColorNormal)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Infant Immunization & Growth", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                            IconButton(onClick = {
                                viewModel.loadSelectedChildState(childRecord)
                                onEditChildClicked()
                            }) {
                                Icon(Icons.Default.Edit, "Modify child logs")
                            }
                        }

                        if (childRecord == null) {
                            Text("No child immunization record exists.", fontSize = 12.sp, color = Color.Gray)
                        } else {
                            childRecord?.let { record ->
                                Column {
                                    Text("Weight: ${record.weightKg} Kg • Height: ${record.heightCm} Cm", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text("Wellness status: ${record.growthStatus}", fontSize = 12.sp, color = if (record.growthStatus != "Normal") ColorHighRisk else ColorNormal)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("VACCINES COMPLETED:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    FlowRow(
                                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        if (record.bcgDose) CompletedVaccBadge("BCG")
                                        if (record.opvDose0) CompletedVaccBadge("OPV Birth")
                                        if (record.hepbBirth) CompletedVaccBadge("HepB Birth")
                                        if (record.opvDose1) CompletedVaccBadge("OPV 1")
                                        if (record.pentavalent1) CompletedVaccBadge("Penta 1")
                                        if (record.opvDose2) CompletedVaccBadge("OPV 2")
                                        if (record.pentavalent2) CompletedVaccBadge("Penta 2")
                                        if (record.pentavalent3) CompletedVaccBadge("Penta 3")
                                        if (record.mrDose1) CompletedVaccBadge("MR1")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // NCD screening Card
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Healing, "ECG sensor", tint = ColorWarning)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Preventative NCD Screening", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        IconButton(onClick = onAddNcdClicked) {
                            Icon(Icons.Default.Add, "Log Checkup")
                        }
                    }

                    if (screenings.isEmpty()) {
                        Text("No screening completed.", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(vertical = 8.dp))
                    } else {
                        val lat = screenings.first()
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.background)
                                .padding(10.dp)
                        ) {
                            Column {
                                Text("Last Screening Date: ${lat.screeningDate}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("BP Metric: ${lat.systolicBp}/${lat.diastolicBp} mmHg", fontSize = 12.sp)
                                Text("Blood Sugar (Random): ${lat.bloodSugarMgDl} mg/dL", fontSize = 12.sp)
                                Text("Tobacco Consumer: ${if (lat.tobaccoUser) "Yes" else "No"}", fontSize = 12.sp)
                                if (lat.referralDestination != "None") {
                                    Text("Referral Advice: ${lat.referralDestination}", color = ColorHighRisk, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Consultations & Symptoms logs (Integrated with Gemini)
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Assessment, "Report icon", tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Clinical Consultations & Triage", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Button(
                            onClick = onAddConsultClicked,
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.testTag("ai_triage_btn_launch")
                        ) {
                            Icon(Icons.Default.AutoAwesome, "Gemini Spark", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Triage", fontSize = 11.sp)
                        }
                    }

                    if (consults.isEmpty()) {
                        Text("No diagnostic visit logged.", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(vertical = 10.dp))
                    } else {
                        consults.forEach { log ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.background)
                                    .padding(10.dp)
                            ) {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Encounter Date: ${log.consultationDate}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(
                                                    if (log.severity == "Severe") ColorHighRisk.copy(alpha = 0.15f)
                                                    else if (log.severity == "Moderate") ColorWarning.copy(alpha = 0.15f)
                                                    else ColorNormal.copy(alpha = 0.15f)
                                                )
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                log.severity,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (log.severity == "Severe") ColorHighRisk else if (log.severity == "Moderate") ColorWarning else ColorNormal
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Symptoms: ${log.symptoms} (${log.durationDays} days)", fontSize = 12.sp)
                                    Text("Temp: ${log.temperatureFahrenheit}°F", fontSize = 12.sp)
                                    if (log.ashaNotes.isNotEmpty()) {
                                        Text("ASHA Companion Notes: ${log.ashaNotes}", fontSize = 12.sp, color = Color.DarkGray)
                                    }
                                    if (log.aiTriageAdvice.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Divider(color = Color.LightGray.copy(alpha = 0.5f))
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(verticalAlignment = Alignment.Top) {
                                            Icon(
                                                Icons.Default.AutoAwesome,
                                                "Stars",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(16.dp).padding(top = 2.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(log.aiTriageAdvice, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CompletedVaccBadge(name: String) {
    Box(
        modifier = Modifier
            .padding(vertical = 3.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(ColorNormal.copy(alpha = 0.12f))
            .border(1.dp, ColorNormal.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.CheckCircle, "Checked Badge", tint = ColorNormal, modifier = Modifier.size(11.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(name, color = ColorNormal, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// ==========================================
// 4. ADD ANC CHECKUP SCREEN
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAncScreen(
    patientId: Int,
    viewModel: EMRViewModel,
    onSuccess: () -> Unit,
    onClose: () -> Unit
) {
    var lmpDate by remember { mutableStateOf(viewModel.ancLmpDate) }
    var weight by remember { mutableStateOf(viewModel.ancWeightKg) }
    var systolic by remember { mutableStateOf(viewModel.ancSystolicBp) }
    var diastolic by remember { mutableStateOf(viewModel.ancDiastolicBp) }
    var hemoglobin by remember { mutableStateOf(viewModel.ancHemoglobin) }
    var ifaTab by remember { mutableStateOf(viewModel.ancIfaSupplements) }
    var tetanusDose by remember { mutableStateOf(viewModel.ancTetanusDose) }
    var notes by remember { mutableStateOf(viewModel.ancNotes) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(18.dp)
    ) {
        Text("Record ANC Checkup", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
        Text("Log vitals, hemoglobin level, and tetanus doses for prenatal safety.", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 12.dp))

        OutlinedTextField(
            value = lmpDate,
            onValueChange = { lmpDate = it; viewModel.ancLmpDate = it },
            label = { Text("Last Menstrual Period (LMP) [dd/mm/yyyy] *") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
            singleLine = true
        )

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it; viewModel.ancWeightKg = it },
                label = { Text("Weight (Kg)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f).padding(vertical = 6.dp),
                singleLine = true
            )
            OutlinedTextField(
                value = hemoglobin,
                onValueChange = { hemoglobin = it; viewModel.ancHemoglobin = it },
                label = { Text("Hemoglobin (g/dL)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f).padding(vertical = 6.dp),
                singleLine = true
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
                value = systolic,
                onValueChange = { systolic = it; viewModel.ancSystolicBp = it },
                label = { Text("Systolic BP (mmHg)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f).padding(vertical = 6.dp),
                singleLine = true
            )
            OutlinedTextField(
                value = diastolic,
                onValueChange = { diastolic = it; viewModel.ancDiastolicBp = it },
                label = { Text("Diastolic BP (mmHg)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f).padding(vertical = 6.dp),
                singleLine = true
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable {
                    ifaTab = !ifaTab
                    viewModel.ancIfaSupplements = ifaTab
                }
        ) {
            Checkbox(checked = ifaTab, onCheckedChange = {
                ifaTab = it
                viewModel.ancIfaSupplements = it
            })
            Text("Iron & Folic Acid (IFA) Supplements Distributed")
        }

        // Tetanus Dose Selection dropdown simulation
        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
            Text("Tetanus Vaccine Dose Segment", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
            var expanded by remember { mutableStateOf(false) }
            Box {
                Button(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                    Text("Selected: $tetanusDose")
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    listOf("None", "Dose 1", "Dose 2", "Booster").forEach { d ->
                        DropdownMenuItem(text = { Text(d) }, onClick = {
                            tetanusDose = d
                            viewModel.ancTetanusDose = d
                            expanded = false
                        })
                    }
                }
            }
        }

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it; viewModel.ancNotes = it },
            label = { Text("ASHA Observations / Clinical Notes") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
            maxLines = 4
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(onClick = onClose, modifier = Modifier.weight(1f)) {
                Text("Cancel")
            }
            Button(
                onClick = {
                    viewModel.submitAncForm(patientId) {
                        viewModel.clearAncForm()
                        onSuccess()
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Save Record")
            }
        }
    }
}

// ==========================================
// 5. EDIT CHILD IMMUNIZATION SCREEN
// ==========================================

@Composable
fun EditChildScreen(
    patientId: Int,
    viewModel: EMRViewModel,
    onSuccess: () -> Unit,
    onClose: () -> Unit
) {
    val lang = AshaSettings.currentLanguage.value
    var dob by remember { mutableStateOf(viewModel.cDob) }
    var weight by remember { mutableStateOf(viewModel.cWeightKg) }
    var height by remember { mutableStateOf(viewModel.cHeightCm) }

    var bcg by remember { mutableStateOf(viewModel.cBcg) }
    var opv0 by remember { mutableStateOf(viewModel.cOpv0) }
    var hepb by remember { mutableStateOf(viewModel.cHepb) }
    var opv1 by remember { mutableStateOf(viewModel.cOpv1) }
    var penta1 by remember { mutableStateOf(viewModel.cPenta1) }
    var opv2 by remember { mutableStateOf(viewModel.cOpv2) }
    var penta2 by remember { mutableStateOf(viewModel.cPenta2) }
    var penta3 by remember { mutableStateOf(viewModel.cPenta3) }
    var mr1 by remember { mutableStateOf(viewModel.cMr1) }

    // WHO status rules matching specified Amber/Red/Green colors
    val weightVal = weight.toFloatOrNull() ?: 3.0f
    val heightVal = height.toFloatOrNull() ?: 50.0f
    
    val (growthStatus, statusColor, growthDesc) = when {
        weightVal < 2.5f -> Triple(
            if (lang == "BN") "গুরুতর অপুষ্টি (Critical / SAM)" else if (lang == "HI") "गंभीर कुपोषण" else "Critical / Severe Malnutrition (SAM)",
            Color(0xFFD32F2F), // Critical Red
            if (lang == "BN") "শিশুর সুরক্ষার জন্য নিকটস্থ হাসপাতালে রেফার করুন।" else if (lang == "HI") "कृपया नजदीकी अस्पताल रेफर करें।" else "High priority alert! Refer child immediately to the nearest health facility."
        )
        weightVal in 2.5f..4.0f -> Triple(
            if (lang == "BN") "ঝুঁকিপূর্ণ (At-Risk / MAM)" else if (lang == "HI") "जोखिम श्रेणी" else "At-Risk / Moderate Malnutrition (MAM)",
            Color(0xFFFFC107), // Warning Amber
            if (lang == "BN") "শিশুর পুষ্টি বাড়ানোর পরামর্শ দিন। ১ সপ্তাহ পরে আবার পরীক্ষা করুন।" else if (lang == "HI") "पोषण में वृद्धि करें और पुनः जांचें।" else "Moderate malnutrition risk. Recommend nutrition supplements and reassess in 7 days."
        )
        else -> Triple(
            if (lang == "BN") "স্বাভাবিক বিকাশ (Normal Development)" else if (lang == "HI") "सामान्य विकास" else "Normal Development (Healthy WHO Guideline)",
            Color(0xFF4CAF50), // Leaf Green
            if (lang == "BN") "শিশুর বৃদ্ধি স্বাভাবিক হারে হচ্ছে। ইউনিভার্সাল টিকাকরণ চালিয়ে যান।" else if (lang == "HI") "शिशु का विकास सामान्य है।" else "Growth and measurements are conforming perfectly to WHO healthy parameters."
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(18.dp)
    ) {
        Text(
            text = if (lang == "BN") "শিশুর টিকাকরণ ও পুষ্টি বৃদ্ধি" else if (lang == "HI") "शिशु प्रतिरक्षण और विकास" else "Update Immunizations & Growth", 
            fontWeight = FontWeight.Bold, 
            fontSize = 20.sp, 
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = if (lang == "BN") "জাতীয় সার্বজনীন টিকাকরণ সময়সূচী অনুযায়ী শিশুর বিবরণ নথিভুক্ত করুন।" else if (lang == "HI") "टीकाकरण अनुसूची के अनुसार विवरण सहेजें।" else "Check completed vaccinations according to Universal Immunization Schedule India.", 
            fontSize = 12.sp, 
            color = Color.Gray, 
            modifier = Modifier.padding(bottom = 12.dp)
        )

        OutlinedTextField(
            value = dob,
            onValueChange = { dob = it; viewModel.cDob = it },
            label = { Text(if (lang == "BN") "শিশুর জন্ম তারিখ (DoB) [দিন/মাস/বছর]" else if (lang == "HI") "शिशु की जन्म तिथि (DoB)" else "Infant Date of Birth [dd/mm/yyyy]") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Large Premium Slide Control Cards conforming to interactive requirements
        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (lang == "BN") "শিশুর ওজন (কেজি):" else if (lang == "HI") "शिशु का वजन (किग्रा):" else "Current Weight (Kg):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "$weight Kg",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Slider(
                    value = weightVal,
                    onValueChange = {
                        val formatted = String.format(Locale.US, "%.1f", it)
                        weight = formatted
                        viewModel.cWeightKg = formatted
                    },
                    valueRange = 1.0f..18.0f,
                    steps = 170,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (lang == "BN") "শিশুর উচ্চতা (সেমি):" else if (lang == "HI") "शिशु की ऊंचाई (सेमी):" else "Current Height (Cm):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "$height Cm",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Slider(
                    value = heightVal,
                    onValueChange = {
                        val formatted = String.format(Locale.US, "%.1f", it)
                        height = formatted
                        viewModel.cHeightCm = formatted
                    },
                    valueRange = 35.0f..115.0f,
                    steps = 160,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Color-coded live growth status evaluator banner
        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = statusColor.copy(alpha = 0.08f)),
            border = BorderStroke(1.5.dp, statusColor)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "${if (lang == "BN") "WHO বৃদ্ধি স্থিতি" else if (lang == "HI") "WHO विकास स्तर" else "WHO Growth Level"}: $growthStatus",
                    color = statusColor,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp
                )
                Text(
                    text = growthDesc,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = if (lang == "BN") "জন্মকালীন টিকাকরণ (২৪ ঘণ্টার মধ্যে):" else if (lang == "HI") "जन्म के समय टीकाकरण:" else "BIRTH IMMUNIZATIONS (0 - 24 Hours):", 
                    fontWeight = FontWeight.Bold, 
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                VaccineCheckRow("BCG (Tuberculosis prevent)", bcg) { bcg = it; viewModel.cBcg = it }
                VaccineCheckRow("OPV 0 (Oral Polio Birth Dose)", opv0) { opv0 = it; viewModel.cOpv0 = it }
                VaccineCheckRow("HepB Birth (Hepatitis B)", hepb) { hepb = it; viewModel.cHepb = it }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = if (lang == "BN") "৬ সপ্তাহের টিকাকরণ:" else if (lang == "HI") "६ सप्ताह का टीकाकरण:" else "6 WEEKS IMMUNIZATIONS:", 
                    fontWeight = FontWeight.Bold, 
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                VaccineCheckRow("OPV 1 (Oral Polio Dose 1)", opv1) { opv1 = it; viewModel.cOpv1 = it }
                VaccineCheckRow("Pentavalent 1 (DPT, HepB, Hib 1)", penta1) { penta1 = it; viewModel.cPenta1 = it }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = if (lang == "BN") "১০ ও ১৪ সপ্তাহের টিকাকরণ:" else if (lang == "HI") "१० और १४ सप्ताह का टीका:" else "10 & 14 WEEKS VACCINES:", 
                    fontWeight = FontWeight.Bold, 
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                VaccineCheckRow("OPV 2 (Oral Polio Dose 2)", opv2) { opv2 = it; viewModel.cOpv2 = it }
                VaccineCheckRow("Pentavalent 2 (DPT, HepB, Hib 2)", penta2) { penta2 = it; viewModel.cPenta2 = it }
                VaccineCheckRow("Pentavalent 3 (DPT, HepB, Hib 3)", penta3) { penta3 = it; viewModel.cPenta3 = it }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = if (lang == "BN") "৯ মাসের টিকাকরণ:" else if (lang == "HI") "९ माह का टीका:" else "9 MONTHS VACCINES:", 
                    fontWeight = FontWeight.Bold, 
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                VaccineCheckRow("MR 1 (Measles & Rubella 1st Dose)", mr1) { mr1 = it; viewModel.cMr1 = it }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(onClick = onClose, modifier = Modifier.weight(1f)) {
                Text(if (lang == "BN") "বাতিল" else if (lang == "HI") "रद्द करें" else "Cancel")
            }
            Button(
                onClick = {
                    viewModel.submitChildRecord(patientId) {
                        onSuccess()
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(if (lang == "BN") "সংরক্ষণ করুন" else if (lang == "HI") "सुरक्षित करें" else "Save Track")
            }
        }
    }
}

@Composable
fun VaccineCheckRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp)
    ) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        Text(label, fontSize = 13.sp)
    }
}

// ==========================================
// 6. RECORD NCD SCREENING SCREEN
// ==========================================

@Composable
fun AddNcdScreen(
    patientId: Int,
    viewModel: EMRViewModel,
    onSuccess: () -> Unit,
    onClose: () -> Unit
) {
    var systolic by remember { mutableStateOf(viewModel.ncdSystolic) }
    var diastolic by remember { mutableStateOf(viewModel.ncdDiastolic) }
    var bloodSugar by remember { mutableStateOf(viewModel.ncdSugar) }
    var tobacco by remember { mutableStateOf(viewModel.ncdTobacco) }
    var symptoms by remember { mutableStateOf(viewModel.ncdSymptoms) }
    var referral by remember { mutableStateOf(viewModel.ncdReferral) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(18.dp)
    ) {
        Text("Adult NCD Care Screening", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
        Text("Record Hypertension, Glycemic index, and risk questionnaires.", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
                value = systolic,
                onValueChange = { systolic = it; viewModel.ncdSystolic = it },
                label = { Text("Systolic BP") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f).padding(vertical = 4.dp),
                singleLine = true
            )
            OutlinedTextField(
                value = diastolic,
                onValueChange = { diastolic = it; viewModel.ncdDiastolic = it },
                label = { Text("Diastolic BP") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f).padding(vertical = 4.dp),
                singleLine = true
            )
        }

        OutlinedTextField(
            value = bloodSugar,
            onValueChange = { bloodSugar = it; viewModel.ncdSugar = it },
            label = { Text("Random Blood Glucose (mg/dL)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            singleLine = true
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable {
                    tobacco = !tobacco
                    viewModel.ncdTobacco = tobacco
                }
        ) {
            Checkbox(checked = tobacco, onCheckedChange = {
                tobacco = it
                viewModel.ncdTobacco = it
            })
            Text("Active Tobacco/Smoking Consumer")
        }

        OutlinedTextField(
            value = symptoms,
            onValueChange = { symptoms = it; viewModel.ncdSymptoms = it },
            label = { Text("Related Chronic Symptoms (e.g. polyuria, dizziness)") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            singleLine = true
        )

        // Referral target dropdown mock
        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
            Text("Referred Destination", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
            var expanded by remember { mutableStateOf(false) }
            Box {
                Button(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                    Text("Referred: $referral")
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    listOf("None", "PHC - Primary Health Centre", "CHC - Community Health Centre", "District Subdiv Hospital").forEach { d ->
                        DropdownMenuItem(text = { Text(d) }, onClick = {
                            referral = d
                            viewModel.ncdReferral = d
                            expanded = false
                        })
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(onClick = onClose, modifier = Modifier.weight(1f)) {
                Text("Cancel")
            }
            Button(
                onClick = {
                    viewModel.submitNcdForm(patientId) {
                        viewModel.clearNcdForm()
                        onSuccess()
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Save Screening")
            }
        }
    }
}

// ==========================================
// 7. SYMPTOM CONSULTATION & AI TRIAGE
// ==========================================

@Composable
fun AddConsultScreen(
    patientId: Int,
    viewModel: EMRViewModel,
    onSuccess: () -> Unit,
    onClose: () -> Unit
) {
    val patientState by viewModel.selectedPatient.collectAsState()
    val patient = patientState ?: return

    val lang = AshaSettings.currentLanguage.value

    var symptoms by remember { mutableStateOf(viewModel.conSymptoms) }
    var severity by remember { mutableStateOf(viewModel.conSeverity) }
    var duration by remember { mutableStateOf(viewModel.conDuration) }
    var tempf by remember { mutableStateOf(viewModel.conTemp) }
    var ashaNotes by remember { mutableStateOf(viewModel.conNotes) }
    var referred by remember { mutableStateOf(viewModel.conReferred) }

    val aiAdvice by viewModel.aiTriageResult.collectAsState()
    val aiLoading by viewModel.aiTriageLoading.collectAsState()

    var showVoiceDialog by remember { mutableStateOf(false) }
    var voiceTargetField by remember { mutableStateOf("") } // "symptoms" or "notes"

    if (showVoiceDialog) {
        AlertDialog(
            onDismissRequest = { showVoiceDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Mic, "Mic", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (lang == "BN") "এআই ভয়েস ডিক্টেশন..." 
                        else if (lang == "HI") "एआई वॉयस डिक्टेशन..." 
                        else "AI Voice Dictation...",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = if (lang == "BN") "আপনার কণ্ঠস্বর রেকর্ড করা হচ্ছে। অনুগ্রহ করে কথা বলুন..." 
                               else if (lang == "HI") "आपकी आवाज़ रिकॉर्ड की जा रही है। कृपया बोलें..." 
                               else "Listening to your voice. Please dictate now...",
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    // Simulated Animated Equalizer Bars
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf(20, 45, 30, 60, 40, 25, 50, 15).forEach { h ->
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .height(h.dp)
                                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(2.dp))
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = if (lang == "BN") "সম্ভাব্য পাঠ্য লিখুন..." else if (lang == "HI") "संभावित पाठ..." else "Recognized text draft...",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray
                    )
                    Text(
                        text = if (voiceTargetField == "symptoms") {
                            if (lang == "BN") "রোগীর গতকাল থেকে জ্বর এবং অবিরাম শুকনো কাশি রয়েছে।"
                            else if (lang == "HI") "मरीज को कल से बुखार और लगातार सूखी खांसी है।"
                            else "Patient reports mild fever and continuous dry cough since yesterday."
                        } else {
                            if (lang == "BN") "প্যারাসিটামল দিন এবং শরীরে পর্যাপ্ত জল বজায় রাখতে বলুন।"
                            else if (lang == "HI") "पैरासिटामोल दें और शरीर में पर्याप्त पानी बनाए रखने को कहें।"
                            else "Advised paracetamol thrice daily and continuous hydration support."
                        },
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val text = if (voiceTargetField == "symptoms") {
                            if (lang == "BN") "রোগীর গতকাল থেকে জ্বর এবং অবিরাম শুকনো কাশি রয়েছে।"
                            else if (lang == "HI") "मरीज को कल से बुखार और लगातार सूखी खांसी है।"
                            else "Patient reports mild fever and continuous dry cough since yesterday."
                        } else {
                            if (lang == "BN") "প্যারাসিটামল দিন এবং শরীরে পর্যাপ্ত জল বজায় রাখতে বলুন।"
                            else if (lang == "HI") "पैरासिटामोल दें और शरीर में पर्याप्त पानी बनाए रखने को कहें।"
                            else "Advised paracetamol thrice daily and continuous hydration support."
                        }
                        if (voiceTargetField == "symptoms") {
                            symptoms = text
                            viewModel.conSymptoms = text
                        } else {
                            ashaNotes = text
                            viewModel.conNotes = text
                        }
                        showVoiceDialog = false
                    }
                ) {
                    Text(if (lang == "BN") "সম্পন্ন করুন" else if (lang == "HI") "पूरा करें" else "Apply")
                }
            },
            dismissButton = {
                TextButton(onClick = { showVoiceDialog = false }) {
                    Text(if (lang == "BN") "বাতিল" else if (lang == "HI") "রद्द करें" else "Cancel")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(18.dp)
    ) {
        Text(
            text = if (lang == "BN") "লক্ষণ ভিত্তিক পরামর্শ ট্রায়াজ" else if (lang == "HI") "लक्षण आधारित परामर्श ट्राइएज" else "Symptomatic Consultation Triage", 
            fontWeight = FontWeight.Bold, 
            fontSize = 20.sp, 
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = if (lang == "BN") "লক্ষণগুলি প্রদান করুন এবং অবিলম্বে ক্লিনিকাল সিদ্ধান্তের জন্য জেমিনি এআই চালু করুন।" else if (lang == "HI") "लक्षण प्रदान करें और जेमिनी एआई त्वरित निदान सहायता बुलाएं।" else "Provide symptoms & call Gemini AI for immediate clinical decision support.", 
            fontSize = 12.sp, 
            color = Color.Gray, 
            modifier = Modifier.padding(bottom = 12.dp)
        )

        OutlinedTextField(
            value = symptoms,
            onValueChange = { symptoms = it; viewModel.conSymptoms = it },
            label = { Text(if (lang == "BN") "রোগীর বর্তমান লক্ষণসমূহ * (উদাঃ জ্বর, কাশি)" else if (lang == "HI") "रोगी के वर्तमान लक्षण * (जैसे बुखार, खांसी)" else "Patient Current Symptoms * (e.g. fever, rapid breath)") },
            trailingIcon = {
                IconButton(onClick = {
                    voiceTargetField = "symptoms"
                    showVoiceDialog = true
                }) {
                    Icon(Icons.Filled.Mic, "Speak symptoms", tint = MaterialTheme.colorScheme.primary)
                }
            },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).testTag("consult_symptoms"),
            singleLine = true
        )

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
                value = duration,
                onValueChange = { duration = it; viewModel.conDuration = it },
                label = { Text(if (lang == "BN") "স্থায়িত্ব (দিন)" else if (lang == "HI") "अवधि (दिन)" else "Duration (Days)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f).padding(vertical = 4.dp),
                singleLine = true
            )
            OutlinedTextField(
                value = tempf,
                onValueChange = { tempf = it; viewModel.conTemp = it },
                label = { Text(if (lang == "BN") "তাপমাত্রা (°F)" else if (lang == "HI") "तापमान (°F)" else "Temperature (°F)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f).padding(vertical = 4.dp),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Severity selector Dropdown simulation
        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
            Text(if (lang == "BN") "লক্ষণ গুরুত্ব" else if (lang == "HI") "लक्षण की गंभीरता" else "Symptom Severity", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
            var expanded by remember { mutableStateOf(false) }
            Box {
                Button(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                    Text("${if (lang == "BN") "গুরুত্ব" else if (lang == "HI") "गंभीरता" else "Severity"}: $severity")
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    listOf("Mild", "Moderate", "Severe").forEach { s ->
                        DropdownMenuItem(text = { Text(s) }, onClick = {
                            severity = s
                            viewModel.conSeverity = s
                            expanded = false
                        })
                    }
                }
            }
        }

        OutlinedTextField(
            value = ashaNotes,
            onValueChange = { ashaNotes = it; viewModel.conNotes = it },
            label = { Text(if (lang == "BN") "আশা কর্মীর বিশেষ নোটস" else if (lang == "HI") "आशा कार्यकर्ता के विशेष नोट्स" else "Special ASHA Field Notes") },
            trailingIcon = {
                IconButton(onClick = {
                    voiceTargetField = "notes"
                    showVoiceDialog = true
                }) {
                    Icon(Icons.Filled.Mic, "Speak notes", tint = MaterialTheme.colorScheme.primary)
                }
            },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            maxLines = 3
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable {
                    referred = !referred
                    viewModel.conReferred = referred
                }
        ) {
            Checkbox(checked = referred, onCheckedChange = {
                referred = it
                viewModel.conReferred = it
            })
            Text(if (lang == "BN") "অনিলম্বেই প্রাথমিক স্বাস্থ্য কেন্দ্রে (PHC) রেফার করুন" else if (lang == "HI") "सीधे पीएचसी चिकित्सा अधिकारी को संदर्भित करें" else "Refer Directly to PHC Medical Officer")
        }

        Spacer(modifier = Modifier.height(10.dp))

        // GEMINI Spark Clinical Decision Engine Call
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("GEMINI CLINICAL ADVISOR", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                    Button(
                        onClick = {
                            viewModel.runAiTriage(
                                patientName = patient.name,
                                patientAge = patient.age,
                                isMother = patient.isPregnant,
                                isChild = patient.isChild
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.testTag("run_ai_triage")
                    ) {
                        Icon(Icons.Default.AutoAwesome, "Spark icon")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Get Advice", fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (aiLoading) {
                    Box(modifier = Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                } else if (!aiAdvice.isNullOrEmpty()) {
                    Text(
                        text = aiAdvice ?: "",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.testTag("ai_result_text")
                    )
                } else {
                    Text(
                        text = "Tap 'Get Advice' to analyze symptoms using the offline-first IMNCI manual index or live Gemini triage logic.",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(onClick = onClose, modifier = Modifier.weight(1f)) {
                Text("Cancel")
            }
            Button(
                onClick = {
                    viewModel.submitConsultLog(patientId) {
                        viewModel.clearConsultForm()
                        onSuccess()
                    }
                },
                modifier = Modifier.weight(1f).testTag("save_consult_btn")
            ) {
                Text("Save Visit")
            }
        }
    }
}

// ==========================================
// 8. GENERIC EMPTY PLACEHOLDER COMPOSABLE
// ==========================================

@Composable
fun EmptyPlaceholder(title: String, description: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.MedicalServices,
                contentDescription = "Medical Bag Icon indicator",
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.65f),
                modifier = Modifier.size(36.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 17.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )
    }
}
