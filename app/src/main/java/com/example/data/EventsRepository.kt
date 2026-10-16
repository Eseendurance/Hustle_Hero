package com.example.data

import android.content.Context
import com.example.model.EventChoice
import com.example.model.GameEvent
import org.json.JSONArray

object EventsRepository {
    private var cachedEvents: List<GameEvent>? = null

    fun loadEvents(context: Context): List<GameEvent> {
        cachedEvents?.let { return it }

        return try {
            val jsonString = context.assets.open("events.json").bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(jsonString)
            val list = mutableListOf<GameEvent>()

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val id = obj.getString("id")
                val title = obj.getString("title")
                val category = obj.getString("category")
                val description = obj.getString("description")
                val iconEmoji = obj.getString("iconEmoji")

                val choicesArray = obj.getJSONArray("choices")
                val choices = mutableListOf<EventChoice>()
                for (j in 0 until choicesArray.length()) {
                    val cObj = choicesArray.getJSONObject(j)
                    choices.add(
                        EventChoice(
                            id = cObj.getString("id"),
                            text = cObj.getString("text"),
                            businessCashImpact = cObj.optLong("businessCashImpact", 0L),
                            personalCashImpact = cObj.optLong("personalCashImpact", 0L),
                            savingsImpact = cObj.optLong("savingsImpact", 0L),
                            inventoryImpact = cObj.optInt("inventoryImpact", 0),
                            stressImpact = cObj.optInt("stressImpact", 0),
                            reputationImpact = cObj.optInt("reputationImpact", 0),
                            debtImpact = cObj.optLong("debtImpact", 0L),
                            outcomeMessage = cObj.getString("outcomeMessage"),
                            moneyLesson = cObj.getString("moneyLesson")
                        )
                    )
                }

                list.add(
                    GameEvent(
                        id = id,
                        title = title,
                        category = category,
                        description = description,
                        iconEmoji = iconEmoji,
                        choices = choices
                    )
                )
            }
            cachedEvents = list
            list
        } catch (e: Exception) {
            e.printStackTrace()
            getFallbackEvents()
        }
    }

    private fun getFallbackEvents(): List<GameEvent> {
        return listOf(
            GameEvent(
                id = "fuel_hike",
                title = "Fuel Price Hike (PMS Scarcity)",
                category = "Energy & Cost Shock",
                description = "Fuel queues stretch 2km. Generator fuel jumped to ₦1,400/litre.",
                iconEmoji = "⛽",
                choices = listOf(
                    EventChoice(
                        id = "c1",
                        text = "Buy expensive fuel to keep the shop running (-₦25,000)",
                        businessCashImpact = -25000,
                        outcomeMessage = "Your lights stayed on! Customers appreciated your reliability.",
                        moneyLesson = "Energy resilience is a core business expense in Nigeria."
                    ),
                    EventChoice(
                        id = "c2",
                        text = "Close shop early at 4 PM to save fuel (₦0 extra cost)",
                        businessCashImpact = -8000,
                        outcomeMessage = "Saved fuel, but missed out on regular evening foot traffic.",
                        moneyLesson = "Closing early protects cash, but risks breaking customer habits."
                    )
                )
            )
        )
    }
}
