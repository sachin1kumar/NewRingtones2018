package com.ringtones.com.newringtones2018

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.media.MediaPlayer
import android.support.v4.app.FragmentManager
import android.support.v7.widget.CardView
import com.google.android.gms.ads.AdView


/**
 * Created by sachin on 17/12/17.
 */
class MyAdapter(var context: Context, val listContent: Array<String>, val resID: IntArray, var fragManager: FragmentManager,
                var mAdView : AdView) :
        RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    var ringtones:Array<MediaPlayer>? = Array<MediaPlayer>(1, { MediaPlayer() } )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.list, parent, false)
        return ViewHolder(v)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ringtoneName: TextView? = itemView.findViewById(R.id.name)
        var playBtn: ImageView? = itemView.findViewById(R.id.options)
        var cardView: CardView? = itemView.findViewById(R.id.card_view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.ringtoneName!!.setText(listContent[position])


        holder.playBtn!!.setOnClickListener(View.OnClickListener { v: View? ->

            performOnclick(position)

        })

        holder.cardView!!.setOnClickListener(View.OnClickListener { v: View? ->

            performOnclick(position)
        })
    }

    override fun getItemCount(): Int {
        return listContent!!.size
    }

    private fun performOnclick(mPosition : Int){
        mAdView.visibility=View.GONE

        var mp: MediaPlayer = MediaPlayer.create(context, resID[mPosition])

        var setRingtone = SetRingtonFragment()
        setRingtone.passAdReference(mAdView)

        setRingtone.setMedia(mp,listContent[mPosition],resID[mPosition])

        fragManager
                .beginTransaction()
                .replace(R.id.content_frame, setRingtone, "ringtone")
                .addToBackStack(null)
                .commit()
    }

}
