package com.drugdose.data

import android.content.Context
import com.drugdose.model.Drug
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

class DrugRepository(private val context: Context) {

    private val gson = Gson()

    private data class DrugDatabase(
        @SerializedName("farmaci") val farmaci: List<Drug>
    )

    fun loadDrugs(): List<Drug> {
        return try {
            val json = context.assets
                .open("drugs.json")
                .bufferedReader()
                .use { it.readText() }

            gson.fromJson(json, DrugDatabase::class.java).farmaci
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
