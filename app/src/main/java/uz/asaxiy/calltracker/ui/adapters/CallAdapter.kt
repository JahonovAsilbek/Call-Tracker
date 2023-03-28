package uz.asaxiy.calltracker.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.asaxiy.calltracker.data.locale.entity.Call
import uz.asaxiy.calltracker.databinding.ItemCallBinding
import java.text.SimpleDateFormat
import java.util.*

class CallAdapter(val calls: List<Call>) : RecyclerView.Adapter<CallAdapter.CallViewHolder>() {

    inner class CallViewHolder(val itemCallBinding: ItemCallBinding) : RecyclerView.ViewHolder(itemCallBinding.root) {
        fun bind(call: Call) {
            itemView.apply {
                itemCallBinding.number.text = call.number
                itemCallBinding.duration.text = SimpleDateFormat("mm:ss").format(Date(call.duration.toLong() * 1000L))
                itemCallBinding.date.text = call.date
                itemCallBinding.type.text = call.type.toString()
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CallViewHolder {
        return CallViewHolder(ItemCallBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: CallViewHolder, position: Int) {
        holder.bind(calls[position])
    }

    override fun getItemCount(): Int {
        return calls.size
    }

}