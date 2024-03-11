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

class StaffDataAdapter(
    private var staffDataList: ArrayList<StaffDataModel>,
    private val context: Context
) :
    RecyclerView.Adapter<StaffDataAdapter.StaffDataViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(staffData: String?)
    }

    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setFilteredList(searchDataList: ArrayList<StaffDataModel>) {
        staffDataList = searchDataList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StaffDataViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.staff_user_info, parent, false)
        return StaffDataViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return staffDataList.size
    }

    override fun onBindViewHolder(holder: StaffDataViewHolder, position: Int) {
        val currentItem = staffDataList[position]
        holder.staffName.text = currentItem.name

        holder.staffEntryBtn.setOnClickListener {
            listener?.onItemClick(currentItem.id)
        }
    }

    class StaffDataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val staffName: TextView
        val staffEntryBtn: Button

        init {
            staffName = itemView.findViewById(R.id.staffName)
            staffEntryBtn = itemView.findViewById(R.id.staffEntryBtn)
        }
    }
}