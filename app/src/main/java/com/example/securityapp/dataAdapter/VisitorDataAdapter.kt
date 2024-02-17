package com.example.securityapp.dataAdapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.securityapp.R
import com.example.securityapp.dataModel.VisitorUserData
import com.google.android.material.imageview.ShapeableImageView

class VisitorDataAdapter(
    private var visitorDataList: ArrayList<VisitorUserData>,
    private val context: Context
) :
    RecyclerView.Adapter<VisitorDataAdapter.VisitorDataViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(visitorData: String?)
    }

    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setFilteredList(searchDataList: ArrayList<VisitorUserData>) {
        visitorDataList = searchDataList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VisitorDataViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.visitor_user_info, parent, false)
        return VisitorDataViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return visitorDataList.size
    }

    override fun onBindViewHolder(holder: VisitorDataViewHolder, position: Int) {
        val currentItem = visitorDataList[position]
        Glide.with(context).load(currentItem.image).into(holder.image)
        holder.name.text = currentItem.name
        holder.inTime.text = currentItem.inTime

        holder.exitBtn.setOnClickListener {
            listener?.onItemClick(currentItem.id)
        }
    }

    class VisitorDataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView
        val inTime: TextView
        val image: ShapeableImageView
        val exitBtn: Button

        init {
            name = itemView.findViewById(R.id.visitorUserName)
            image = itemView.findViewById(R.id.visitorUserImage)
            inTime = itemView.findViewById(R.id.visitInTime)
            exitBtn = itemView.findViewById(R.id.exitBtn)
        }
    }
}