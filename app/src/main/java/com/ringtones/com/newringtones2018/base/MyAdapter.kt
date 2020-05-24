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
import es.claucookie.miniequalizerlibrary.EqualizerView

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
        var equilizer: EqualizerView = itemView.findViewById(R.id.equalizer_view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.ringtoneName.text = listContent[position]

        holder.playBtn.setOnClickListener {
            performVisibilityOperations(holder)
            performOnclick(position)
        }

        holder.cardView.setOnClickListener {
            performVisibilityOperations(holder)
            performOnclick(position)
        }
    }

    private fun performVisibilityOperations(holder: ViewHolder){
        if (holder.playBtn.visibility == View.VISIBLE){
            holder.playBtn.visibility = View.GONE
            holder.equilizer.visibility = View.VISIBLE
            holder.equilizer.animateBars()
        }
        else {
            holder.equilizer.stopBars()
            holder.equilizer.visibility = View.GONE
            holder.playBtn.visibility = View.VISIBLE
        }
    }

    override fun getItemCount() = listContent.size

    private fun performOnclick(mPosition : Int){
        val mp: MediaPlayer = MediaPlayer.create(context, resID[mPosition])
        val setRingtone = SetRingtoneFragment()

        setRingtone.setMedia(mp,listContent[mPosition],resID[mPosition])

       /*TODO fragManager
                .beginTransaction()
                .setCustomAnimations(R.anim.abc_slide_in_bottom, R.anim.abc_slide_in_top)
                .replace(R.id.content_frame, setRingtone, "ringtone")
                .addToBackStack(null)
                .commit()*/
    }


}
