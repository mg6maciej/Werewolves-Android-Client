package pl.mg6.werewolves

import android.content.Context
import android.preference.PreferenceManager
import java.util.*

fun Context.getMyName(): String {
    val prefs = PreferenceManager.getDefaultSharedPreferences(this)
    return prefs.getString("name", "")
}

fun Context.setMyName(name: String) {
    val prefs = PreferenceManager.getDefaultSharedPreferences(this)
    prefs.edit().putString("name", name).apply()
}
