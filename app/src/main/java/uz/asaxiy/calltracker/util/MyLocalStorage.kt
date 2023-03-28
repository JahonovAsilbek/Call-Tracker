package uz.asaxiy.calltracker.util

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.Keep

@Keep
object
MyLocalStorage {
    private const val NAME = "token"
    private const val MODE = Context.MODE_PRIVATE
    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(NAME, MODE)
    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    var url: String? = ""
        get() = sharedPreferences.getString("url", field)
        set(value) = sharedPreferences.edit {
            if (value != null) {
                it.putString("url", value)
            }
        }

}