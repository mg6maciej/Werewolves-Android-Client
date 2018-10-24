package pl.mg6.werewolves

import android.content.Context
import android.preference.PreferenceManager
import java.util.*

fun Context.getMyId(): String {
    val prefs = PreferenceManager.getDefaultSharedPreferences(this)
    var id = prefs.getString("id", null)
    if (id == null) {
        id = UUID.randomUUID().toString()
        prefs.edit().putString("id", id).apply()
    }
    return id
}
