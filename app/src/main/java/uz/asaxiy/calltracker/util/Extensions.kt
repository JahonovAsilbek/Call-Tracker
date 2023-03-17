package uz.asaxiy.calltracker.util

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

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun EditText.text(): String = this.text.toString().trim()
fun Context.showToast(message: String?) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
fun Fragment.showToast(message: String?) = Toast.makeText(this.requireContext(), message, Toast.LENGTH_SHORT).show()
fun DialogFragment.showToast(message: String?) = Toast.makeText(this.requireContext(), message, Toast.LENGTH_SHORT).show()

fun Int.formatPrice(): String {
    val amount: Int = this
    val formatter = DecimalFormat("###,###,###")
    return formatter.format(amount).replace(",", " ")
}

fun Long.formatPrice(): String {
    val amount: Long = this
    val formatter = DecimalFormat("###,###,###")
    return formatter.format(amount).replace(",", " ")
}

fun Int.formatDate(): String {
    return SimpleDateFormat("dd.MM.yyyy", Locale.US).format(Date(this * 1000L))
}

fun getFormattedDate(duration: Int): String {
    val mills = duration * 1000L
    if (duration < 3600) {
        return String.format(
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(mills), // The change is in this line
            TimeUnit.MILLISECONDS.toSeconds(mills) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mills))
        )
    } else
        return String.format(
            "%02d:%02d:%02d",
            TimeUnit.MILLISECONDS.toHours(mills),
            TimeUnit.MILLISECONDS.toMinutes(mills) -
                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(mills)), // The change is in this line
            TimeUnit.MILLISECONDS.toSeconds(mills) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mills))
        )
}


fun Fragment.callAsaxiy() {
    val phone = "+998712000105"
    val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null))
    startActivity(intent)
}

