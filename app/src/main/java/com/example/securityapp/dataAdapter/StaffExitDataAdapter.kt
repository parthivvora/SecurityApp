package com.example.securityapp.dataAdapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.securityapp.R
import com.example.securityapp.dataModel.StaffDataModel
import com.example.securityapp.dataModel.StaffDetailsModel

class StaffExitDataAdapter(
    private var staffDataList: ArrayList<StaffDetailsModel>,
    private val context: Context
) :
    RecyclerView.Adapter<StaffExitDataAdapter.StaffDataViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(staffData: String?)
    }

    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setFilteredList(searchDataList: ArrayList<StaffDetailsModel>) {
        staffDataList = searchDataList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StaffDataViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.staff_user_exit_info, parent, false)
        return StaffDataViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return staffDataList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: StaffDataViewHolder, position: Int) {
        val currentItem = staffDataList[position]
        holder.staffId.text = currentItem.staffId
        holder.inTime.text = "In Time : ${currentItem.inTime}"

        holder.staffExitBtn.setOnClickListener {
            listener?.onItemClick(currentItem.staffId)
        }
    }

    class StaffDataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val staffId: TextView
        val inTime: TextView
        val staffExitBtn: Button

        init {
            staffId = itemView.findViewById(R.id.staffId)
            inTime = itemView.findViewById(R.id.inTime)
            staffExitBtn = itemView.findViewById(R.id.staffExitBtn)
        }
    }
}