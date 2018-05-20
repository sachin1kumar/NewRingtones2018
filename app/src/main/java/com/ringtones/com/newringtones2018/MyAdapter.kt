package com.ringtones.com.newringtones2018

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import android.os.Handler
import android.support.annotation.Nullable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by sachin on 17/12/17.
 */
class MyAdapter(var context: Context) :
        RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.list, parent, false)
        return ViewHolder(v)
    }

    var mFilteredList: List<String>? = null


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var appTextView: TextView = itemView.findViewById(R.id.name)
        var mBookmark: ImageView? = itemView.findViewById(R.id.play)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    }


    override fun getItemCount(): Int {
        return mFilteredList!!.size
    }


}
