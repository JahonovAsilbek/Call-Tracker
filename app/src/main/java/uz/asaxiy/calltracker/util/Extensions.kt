package uz.asaxiy.calltracker.util

import android.widget.EditText

fun EditText.text(): String = this.text.toString().trim()
fun String.formatPhone(): String {
    return when (this.length) {
        9 -> "+998$this"
        13 -> this
        else -> ""
    }
}