package com.example.data

import com.example.BuildConfig
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

// ==========================================
// MOSHI DATA CLASSES FOR GEMINI REST API
// ==========================================

@JsonClass(generateAdapter = true)
data class GeminiRequestBody(
    @Json(name = "contents") val contents: List<GeminiRequestContent>,
    @Json(name = "systemInstruction") val systemInstruction: GeminiRequestContent? = null
)

@JsonClass(generateAdapter = true)
data class GeminiRequestContent(
    @Json(name = "parts") val parts: List<GeminiRequestPart>
)

@JsonClass(generateAdapter = true)
data class GeminiRequestPart(
    @Json(name = "text") val text: String
)

@JsonClass(generateAdapter = true)
data class GeminiResponseBody(
    @Json(name = "candidates") val candidates: List<GeminiCandidate>?
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    @Json(name = "content") val content: GeminiContentResult?
)

@JsonClass(generateAdapter = true)
data class GeminiContentResult(
    @Json(name = "parts") val parts: List<GeminiPartResult>?
)

@JsonClass(generateAdapter = true)
data class GeminiPartResult(
    @Json(name = "text") val text: String?
)

// ==========================================
// RETROFIT API SERVICE
// ==========================================

interface GeminiApi {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun getTriageAdvice(
        @Query("key") apiKey: String,
        @Body body: GeminiRequestBody
    ): GeminiResponseBody
}

// ==========================================
// GEMINI SERVICE CLIENT WITH TIMEOUTS
// ==========================================

object GeminiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    private val api: GeminiApi by lazy {
        retrofit.create(GeminiApi::class.java)
    }

    /**
     * Executes the generative request on Gemini 3.5 Flash containing the symptoms and context.
     * Includes a comprehensive clinical systemic instruction guiding appropriate triage advice.
     */
    suspend fun askAssistant(symptomsContext: String): String {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return getFallbackAdvice(symptomsContext)
        }

        val systemInstructionText = """
            You are a senior medical decision support assistant advising Accredited Social Health Activists (ASHA) community health workers in rural India.
            Synthesize the symptoms provided by the user according to the National Health Mission (NHM) guidelines, Integrated Management of Neonatal and Childhood Illness (IMNCI), and standard maternal care.
            
            Always format the response in a highly readable, structured, clean bulleted order:
            ### 🚨 IMMEDIATE RED FLAGS (URGENT REFERRAL)
            Identify critical conditions indicating the patient needs immediate referral to the nearest Primary Health Centre (PHC) or Community Health Centre (CHC).
            
            ### 🏡 RECOMMENDED SAFE HOME CARE PROTOCOLS
            Specify actions the ASHA or family should take immediately (hydration, positions, temperature control, etc.) under proper guidance.
            
            ### 📋 KEY ASSESSMENT QUESTIONS FOR ASHA
            Suggest 3 crucial follow-up questions the ASHA should ask the patient or check physically (such as chest in-drawing, capillary refill, capillary temperature).
            
            Keep the language professional, practical, extremely clear, action-oriented, and tailored for field use. Use English.
        """.trimIndent()

        val request = GeminiRequestBody(
            contents = listOf(
                GeminiRequestContent(
                    parts = listOf(GeminiRequestPart(text = "Patient Symptoms and Context:\n$symptomsContext"))
                )
            ),
            systemInstruction = GeminiRequestContent(
                parts = listOf(GeminiRequestPart(text = systemInstructionText))
            )
        )

        return try {
            val response = api.getTriageAdvice(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "No advice was generated. Please evaluate according to standard NHM IMNCI guidelines."
        } catch (e: Exception) {
            getFallbackAdvice(symptomsContext, e.localizedMessage)
        }
    }

    /**
     * Generates expert deterministic clinical logic if the API is offline, keys are unconfigured,
     * or network limits are hit. Extremely high availability for medical tools.
     */
    private fun getFallbackAdvice(context: String, errorMsg: String? = null): String {
        val ctxLower = context.lowercase()
        val hasFever = ctxLower.contains("fever") || ctxLower.contains("temp") || ctxLower.contains("hot")
        val hasCough = ctxLower.contains("cough") || ctxLower.contains("breath") || ctxLower.contains("resp")
        val hasDiarrhea = ctxLower.contains("diarrhea") || ctxLower.contains("loose") || ctxLower.contains("vomit")
        val hasPregnancy = ctxLower.contains("pregnant") || ctxLower.contains("anc") || ctxLower.contains("preg")

        val builder = java.lang.StringBuilder()
        builder.append("### ℹ️ CLINICAL GUIDELINES MANUAL (LOCAL FALLBACK)\n")
        if (errorMsg != null) {
            builder.append("*(Offline Assist: Network query paused)*\n\n")
        } else {
            builder.append("*(Offline Mode Active - Standard Guidelines)*\n\n")
        }

        if (hasPregnancy) {
            builder.append("""
                ### 🚨 IMMEDIATE RED FLAGS (URGENT REFERRAL)
                * Systolic BP ≥ 140 mmHg or Diastolic BP ≥ 90 mmHg.
                * Severe headache with blurred vision or epigastric pain (signs of preeclampsia).
                * Vaginal bleeding or fluid leakage.
                * Decreased fetal movements.
                * Continuous high fever.

                ### 🏡 RECOMMENDED SAFE HOME CARE PROTOCOLS
                * Help the pregnant woman lie down on her left lateral side to optimize uterine blood flow.
                * Suggest high-hydration and immediate rest.
                * Instruct immediate transport configuration to CHC/PHC under Janani Shishu Suraksha Karyakram (JSSK).

                ### 📋 KEY ASSESSMENT QUESTIONS FOR ASHA
                1. Ask the mother about any facial swelling or leg edema.
                2. Verify the exact Expected Date of Delivery (EDD) and check current Hemoglobin level.
                3. Check the number of kicks (fetal movements) felt in the last 2 hours.
            """.trimIndent())
        } else if (hasCough || hasCough && hasFever) {
            builder.append("""
                ### 🚨 IMMEDIATE RED FLAGS (URGENT REFERRAL)
                * Rapid breathing rate (>60/min for infants <2 months, >50/min for 2-11 months, >40/min for 1-5 years).
                * Visible persistent chest in-drawing.
                * Presence of stridor (high-pitched breathing sound) when calm.
                * Inability to feed, breastfeed, or keep fluids down.
                * Extreme lethargy or convulsions.

                ### 🏡 RECOMMENDED SAFE HOME CARE PROTOCOLS
                * Keep the child warm (prevent hypothermia).
                * Continue frequent breastfeeding or offer oral rehydration fluids.
                * Clear the child's nose with gentle saline drops if blocked. Do NOT suppress cough with heavy sedatives.

                ### 📋 KEY ASSESSMENT QUESTIONS FOR ASHA
                1. Count the respiratory rate precisely for one full minute while the child is calm.
                2. Ask if the child is able to drink or nurse.
                3. Check if there is high fever (>101°F) or cold extremities.
            """.trimIndent())
        } else if (hasDiarrhea) {
            builder.append("""
                ### 🚨 IMMEDIATE RED FLAGS (URGENT REFERRAL)
                * Sunken eyes, completely dry tongue, or absent tears.
                * Extreme skin pinch delay (elasticity takes >2 seconds to return).
                * Blood in the stool (indicates mucosal dysentery).
                * Lethargic, unconscious, or floppiness.

                ### 🏡 RECOMMENDED SAFE HOME CARE PROTOCOLS
                * Initiate Oral Rehydration Salts (ORS) solution therapy immediately (approx. 50-100ml after each loose stool).
                * Provide Zinc Supplementation (20mg daily for 14 days; 10mg for infants under 6 months).
                * Encourage continuing normal age-appropriate feeding and breastfeeding.

                ### 📋 KEY ASSESSMENT QUESTIONS FOR ASHA
                1. How many loose/watery stools has the child had in the last 24 hours?
                2. Does the child drink eagerly (thirsty) or is unable to drink at all?
                3. Are there signs of active blood or mucus/pus in the stool?
            """.trimIndent())
        } else {
            builder.append("""
                ### 🚨 IMMEDIATE RED FLAGS (URGENT REFERRAL)
                * High fever (>103°F) that does not respond to standard paracetamol.
                * Convulsions, altered mental state, or unconsciousness.
                * Severe shortness of breath or cyanosis (blue discoloration of lips/tongue).
                * Sudden intense focal abdominal pain or constant vomiting.

                ### 🏡 RECOMMENDED SAFE HOME CARE PROTOCOLS
                * Keep the patient rested, hydrated, and isolated in a cool, ventilated room.
                * Administer basic first aid and paracetamol block (for non-contraindicated adults).
                * Maintain the health card/mother-child passport up to date for transfer.

                ### 📋 KEY ASSESSMENT QUESTIONS FOR ASHA
                1. Measure basic vital stats (Heart rate, temperature, BP if screening kit is available).
                2. Document onset duration of all critical symptoms.
                3. Query historical chronic diseases (Diabetes, Hypertension, Tuberculosis, Kidney).
            """.trimIndent())
        }

        return builder.toString()
    }
}
