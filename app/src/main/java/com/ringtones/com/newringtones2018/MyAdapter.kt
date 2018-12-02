package com.ringtones.com.newringtones2018

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.media.MediaPlayer
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.CardView
import android.widget.Toast
import com.google.android.gms.ads.AdView
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus


/**
 * Created by sachin on 17/12/17.
 */
class MyAdapter(var context: Context, val listContent: Array<String>, val resID: IntArray, var fragManager: FragmentManager,
                var mAdView : AdView) :
        RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    var ringtones:Array<MediaPlayer>? = Array<MediaPlayer>(1, { MediaPlayer() } )

    private lateinit var manager: SplitInstallManager

    /** Listener used to handle changes in state for install requests. */
    private val listener = SplitInstallStateUpdatedListener { state ->
        val multiInstall = state.moduleNames().size > 1
        state.moduleNames().forEach { name ->
            // Handle changes in state.
            when (state.status()) {
                SplitInstallSessionStatus.DOWNLOADING -> {
                    //  In order to see this, the application has to be uploaded to the Play Store.
                    //displayLoadingState(state, "Downloading $name")
                    Toast.makeText(context,"downloading",Toast.LENGTH_SHORT).show()
                }
                SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {
                    /*
                      This may occur when attempting to download a sufficiently large module.

                      In order to see this, the application has to be uploaded to the Play Store.
                      Then features can be requested until the confirmation path is triggered.
                     */
                    //startIntentSender(state.resolutionIntent()?.intentSender, null, 0, 0, 0)
                }
                SplitInstallSessionStatus.INSTALLED -> {
                    Toast.makeText(context,"installed",Toast.LENGTH_SHORT).show()
                    onSuccessfulLoad(name, launch = !multiInstall)
                }

                SplitInstallSessionStatus.INSTALLING ->
                    Toast.makeText(context,"installing..",Toast.LENGTH_SHORT).show()

                SplitInstallSessionStatus.FAILED -> {
                    //toastAndLog("Error: ${state.errorCode()} for module ${state.moduleNames()}")
                    Toast.makeText(context,"failed",Toast.LENGTH_SHORT).show()

                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.list, parent, false)
        manager = SplitInstallManagerFactory.create(context)
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
        manager.registerListener(listener)
        /*mAdView.visibility=View.GONE

        var mp: MediaPlayer = MediaPlayer.create(context, resID[mPosition])

        var setRingtone = SetRingtonFragment()
        setRingtone.passAdReference(mAdView)

        setRingtone.setMedia(mp,listContent[mPosition],resID[mPosition])

        fragManager
                .beginTransaction()
                .setCustomAnimations(R.anim.abc_slide_in_bottom,R.anim.abc_slide_in_top)
                .replace(R.id.content_frame, setRingtone, "ringtone")
                .addToBackStack(null)
                .commit()*/

        // Creates a request to install a module.
        var request: SplitInstallRequest =
                SplitInstallRequest
                        .newBuilder()
                        // You can download multiple on demand modules per
                        // request by invoking the following method for each
                        // module you want to install.
                        .addModule("dynamicFeature")
                        .build()

        // Skip loading if the module already is installed. Perform success action directly.
        if (manager.installedModules.contains("dynamicFeature")) {
            //updateProgressMessage("Already installed")
            onSuccessfulLoad("dynamicFeature", launch = true)
            return
        }

        manager.startInstall(request)

    }

    private fun onSuccessfulLoad(moduleName: String, launch: Boolean) {
        if (launch) {
            Toast.makeText(context,"Onsuccessfulload",Toast.LENGTH_SHORT).show()

            var intent1 = Intent(context, Class.forName("com.example.dynamicfeature.MainActivity"))
            context.startActivity(intent1)
        }
    }

}
