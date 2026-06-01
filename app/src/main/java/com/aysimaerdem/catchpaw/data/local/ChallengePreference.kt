package com.aysimaerdem.catchpaw.data.local

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChallengePreference @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs = context.getSharedPreferences("catchpaw_challenge", Context.MODE_PRIVATE)

    private fun dailyKey(cal: Calendar = Calendar.getInstance()): String =
        "daily_${cal.get(Calendar.YEAR)}_${cal.get(Calendar.MONTH)}_${cal.get(Calendar.DAY_OF_MONTH)}"

    fun getDailyBestScore(): Int = prefs.getInt(dailyKey(), 0)

    fun setDailyBestScore(score: Int) {
        prefs.edit().putInt(dailyKey(), score).apply()
    }

    fun isDailyCompleted(): Boolean = prefs.contains(dailyKey())

    /** Sum of each day's best score within the current week (Mon–Sun or locale week). */
    fun getWeeklyTotalScore(): Int {
        val weekStart = weekStartCalendar()
        var total = 0
        val check = weekStart.clone() as Calendar
        repeat(7) {
            total += prefs.getInt(dailyKey(check), 0)
            check.add(Calendar.DAY_OF_MONTH, 1)
        }
        return total
    }

    /** How many distinct days were played this week. */
    fun getDaysPlayedThisWeek(): Int {
        val weekStart = weekStartCalendar()
        var count = 0
        val check = weekStart.clone() as Calendar
        repeat(7) {
            if (prefs.contains(dailyKey(check))) count++
            check.add(Calendar.DAY_OF_MONTH, 1)
        }
        return count
    }

    private fun weekStartCalendar(): Calendar {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
        return cal
    }
}
