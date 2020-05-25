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
class MyAdapter(private var context: Context, private val listContent: Array<String>, private val resID: IntArray, private var isExpandedArray: Array<Boolean>, private val fragManager: FragmentManager) :
        RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    private var mExpandedPosition: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        isExpandedArray[position] = position == mExpandedPosition
        holder.bind(isExpandedArray[position],listContent[position], resID[position],context,fragManager)

        holder.itemView.setOnClickListener {
            mExpandedPosition = if (isExpandedArray[position]) -1 else position
            notifyDataSetChanged()
        }
    }

    override fun getItemCount() = listContent.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val ringtoneName: TextView
        private val playBtn: ImageView
        private val cardView: CardView
        private val equilizer: EqualizerView
        private var isExpanded: Boolean

        init {
            ringtoneName = itemView.findViewById(R.id.name)
            playBtn = itemView.findViewById(R.id.options)
            cardView = itemView.findViewById(R.id.card_view)
            equilizer= itemView.findViewById(R.id.equalizer_view)
            isExpanded = false
        }

        fun bind(expanded: Boolean, name: String, resource: Int, context: Context, fragManager: FragmentManager) {
            equilizer.visibility = if (expanded) View.VISIBLE else View.GONE
            ringtoneName.text = name

            if(equilizer.visibility == View.VISIBLE){
                val mp: MediaPlayer = MediaPlayer.create(context,resource)
                val setRingtone = SetRingtoneFragment()
                setRingtone.setMedia(mp,name,resource)
                fragManager
                        .beginTransaction()
                        .setCustomAnimations(R.anim.abc_slide_in_bottom, R.anim.abc_slide_in_top)
                        .replace(R.id.content_frame, setRingtone, "ringtone")
                        .addToBackStack(null)
                        .commit()
                equilizer.animateBars()
                playBtn.visibility = View.GONE
            }
            else{
                equilizer.stopBars()
                playBtn.visibility = View.VISIBLE
            }
        }

    }

}
