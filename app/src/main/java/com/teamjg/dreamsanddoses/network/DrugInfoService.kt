package com.teamjg.dreamsanddoses.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

// This object handles the API call to fetch medication information from OpenFDA
object DrugInfoService {

    // A suspend function that can be called from a coroutine (like inside LaunchedEffect).
    // It takes a drug name (query) and returns a description string.
    suspend fun fetchDrugInfo(query: String): String {
        // We switch to the IO dispatcher since network calls shouldn't run on the main thread.
        return withContext(Dispatchers.IO) {

            // Build the request URL using the drug name the user typed
            val url = URL("https://api.fda.gov/drug/label.json?search=openfda.brand_name:$query")
            val connection = url.openConnection() as HttpURLConnection // Open the connection

            return@withContext try {
                // Try to read the response from the API
                connection.inputStream.bufferedReader().use {
                    val response = it.readText() // Read the full JSON response as text

                    val json = JSONObject(response) // Parse the JSON string
                    val result = json.getJSONArray("results").getJSONObject(0) // Get the first result from the results array

                    // Try to get the "description" field; if not present, show a fallback message
                    result.optString("description", "No description found.")
                }
            } catch (e: Exception) {
                // If something goes wrong (like no internet or bad API key), show an error message
                "Error fetching drug info: ${e.message}"
            } finally {
                // Always disconnect when done — good practice to avoid memory/network leaks
                connection.disconnect()
            }
        }
    }
}