package uz.asaxiy.calltracker.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


fun EditText.text(): String = this.text.toString().trim()
fun String.formatPhone(): String {
    return when (this.length) {
        9 -> "+998$this"
        13 -> this
        else -> ""
    }
}