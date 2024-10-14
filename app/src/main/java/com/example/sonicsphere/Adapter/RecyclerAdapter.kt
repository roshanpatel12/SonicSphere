package com.example.sonicsphere.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sonicsphere.Model.HomeModel
import com.example.sonicsphere.R

class RecyclerAdapter(val list: List<HomeModel>) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    open class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }


    override fun onBindViewHolder(holder: RecyclerAdapter.ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return list.size
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapter.ViewHolder {
        val view  = LayoutInflater.from(parent.context).inflate(R.layout.item_column,parent,false)
        return ViewHolder(view)

    }

}