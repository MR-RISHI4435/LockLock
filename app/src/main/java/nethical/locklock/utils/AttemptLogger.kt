package nethical.locklock.utils

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

object AttemptLogger {
    private const val PREFS = "locklock_attempt_log"
    private const val KEY_LOG = "attempts"
    private const val MAX_ENTRIES = 100

    fun logFailedAttempt(context: Context, lockedAppName: String? = null) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val array = JSONArray(prefs.getString(KEY_LOG, "[]"))

        val entry = JSONObject().apply {
            put("timestamp", System.currentTimeMillis())
            put("readable", SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date()))
            put("app", lockedAppName ?: "Settings")
        }
        array.put(entry)

        val trimmed = if (array.length() > MAX_ENTRIES) {
            JSONArray().apply {
                for (i in (array.length() - MAX_ENTRIES) until array.length()) put(array.get(i))
            }
        } else array

        prefs.edit().putString(KEY_LOG, trimmed.toString()).apply()
    }

    fun getAttempts(context: Context): List<Pair<String, String>> {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val array = JSONArray(prefs.getString(KEY_LOG, "[]"))
        val result = mutableListOf<Pair<String, String>>()
        for (i in array.length() - 1 downTo 0) {
            val obj = array.getJSONObject(i)
            result.add(obj.getString("app") to obj.getString("readable"))
        }
        return result
    }

    fun clear(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().clear().apply()
    }
}
