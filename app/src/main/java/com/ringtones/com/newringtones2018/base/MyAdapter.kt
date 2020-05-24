package com.ringtones.com.newringtones2018.base

import android.content.Context
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.ringtones.com.newringtones2018.R
import com.ringtones.com.newringtones2018.view.SetRingtoneFragment

/**
 * Created by sachin on 17/12/17.
 */
class MyAdapter(var context: Context, private val listContent: Array<String>, private val resID: IntArray, private val fragManager: FragmentManager) :
        RecyclerView.Adapter<MyAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list, parent, false)
        return ViewHolder(v)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ringtoneName: TextView = itemView.findViewById(R.id.name)
        var playBtn: ImageView = itemView.findViewById(R.id.options)
        var cardView: CardView = itemView.findViewById(R.id.card_view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.ringtoneName.text = listContent[position]

        holder.playBtn.setOnClickListener {
            performOnclick(position)
        }

        holder.cardView.setOnClickListener {
            performOnclick(position)
        }
    }

    override fun getItemCount() = listContent.size

    private fun performOnclick(mPosition : Int){
        val mp: MediaPlayer = MediaPlayer.create(context, resID[mPosition])
        val setRingtone = SetRingtoneFragment()

        setRingtone.setMedia(mp,listContent[mPosition],resID[mPosition])

        fragManager
                .beginTransaction()
                .setCustomAnimations(R.anim.abc_slide_in_bottom, R.anim.abc_slide_in_top)
                .replace(R.id.content_frame, setRingtone, "ringtone")
                .addToBackStack(null)
                .commit()
    }


}
