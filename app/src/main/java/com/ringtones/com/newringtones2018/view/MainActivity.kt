package com.ringtones.com.newringtones2018.view

import android.app.Dialog
import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.multidex.BuildConfig
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.ringtones.com.newringtones2018.R
import com.ringtones.com.newringtones2018.base.MyAdapter
import com.ringtones.com.newringtones2018.utils.Ringtones
import com.startapp.sdk.adsbase.StartAppAd
import com.startapp.sdk.adsbase.StartAppSDK
import kotlin.math.ceil


class MainActivity : AppCompatActivity() {

    private lateinit var mToolbar: Toolbar
    private lateinit var adapter: MyAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: EditText
    private lateinit var songName: TextView
    private lateinit var seekBar: SeekBar
    private lateinit var duration: TextView
    private lateinit var playButton: FloatingActionButton
    private lateinit var handler: Handler
    private val SHARED_PREFS_GDPR_SHOWN = "gdpr_dialog_was_shown"
    private var mediaPlayer: MediaPlayer? = null
    private var wasPlaying = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUpUI()
        initStartAppSdkAccordingToConsent()

        /*manager = SplitInstallManagerFactory.create(this)
        SplitCompat.install(this)*/
        //dynamicFeature()
    }

    private fun setUpUI(){

        mToolbar = findViewById(R.id.toolbar)
        searchView = findViewById(R.id.search_view)
        songName = findViewById(R.id.tv_song_name)
        seekBar = findViewById(R.id.tv_seekbar)
        duration = findViewById(R.id.tv_duration)
        playButton = findViewById(R.id.tv_play_button)
        setSearchView()
        setFabButtonListener()
        setSeekBarListener()
        handlerCallBack()
        setSupportActionBar(mToolbar)
        mToolbar.setTitle(R.string.app_name)

        recyclerView = findViewById(R.id.recyclerView)
        adapter = MyAdapter(this, handler, Ringtones.listContent, Ringtones.resID, Ringtones.isExpanded, this.supportFragmentManager)

        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = mLayoutManager
        recyclerView.itemAnimator
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.adapter = adapter
    }

    private fun setSeekBarListener() {
        mediaPlayer = MediaPlayer()
        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                duration.visibility = View.VISIBLE
            }

            override fun onProgressChanged(seekBar: SeekBar,progress : Int, fromTouch: Boolean) {
                duration.visibility = View.VISIBLE
                val x: Int = ceil(progress / 1000f).toInt()


                if (x == 0 && !mediaPlayer?.isPlaying!!) {
                   // clearMediaPlayer()
                    playButton.setImageDrawable(ContextCompat.getDrawable(applicationContext, android.R.drawable.ic_media_play))
                    seekBar.progress = 0
                }
            }

            override fun onStopTrackingTouch(seekBar: SeekBar){
                if (mediaPlayer != null && mediaPlayer?.isPlaying!!) {
                    mediaPlayer?.seekTo(seekBar.progress);
                }
            }
        })
    }

/*    fun run() {
        var currentPosition = mediaPlayer.currentPosition
        val total = mediaPlayer.duration
        while (mediaPlayer.isPlaying && currentPosition < total) {
            currentPosition = try {
                Thread.sleep(1000)
                mediaPlayer.currentPosition
            } catch (e: InterruptedException) {
                return
            } catch (e: Exception) {
                return
            }
            seekBar.progress = currentPosition
        }
    }*/

    private fun handlerCallBack(){
        handler = object : Handler() {
            override fun handleMessage(msg: Message) {
                val bundle: Bundle = msg.data
                val resId = bundle.getInt("id")
                val songName = bundle.getString("song")
                songName?.let { playSong(it, resId) }
            }
        }
    }

    fun playSong(songName: String, resId: Int) {
        try {
            if (mediaPlayer !=null && mediaPlayer?.isPlaying!!) {
                clearMediaPlayer()
                seekBar.progress = 0
                wasPlaying = true
                playButton.setImageDrawable(ContextCompat.getDrawable(this@MainActivity, android.R.drawable.ic_media_play))
            }
            if (!wasPlaying) {
                val descriptor: AssetFileDescriptor = resources.openRawResourceFd(resId)
                mediaPlayer?.setDataSource(descriptor.fileDescriptor, descriptor.startOffset, descriptor.length)
                descriptor.close()
                mediaPlayer?.prepare()
                mediaPlayer?.setVolume(0.5f, 0.5f)
                mediaPlayer?.isLooping = false
                seekBar.max = mediaPlayer?.duration!!
                mediaPlayer?.start()
                Thread{
                    playButton.setImageDrawable(ContextCompat.getDrawable(this@MainActivity, android.R.drawable.ic_media_pause))
                    var currentPosition = mediaPlayer?.currentPosition
                    val total = mediaPlayer?.duration!!
                    if (currentPosition != null) {
                        while (mediaPlayer?.isPlaying!! && currentPosition!! < total) {
                            currentPosition = try {
                                Thread.sleep(1000)
                                mediaPlayer?.currentPosition
                            } catch (e: InterruptedException) {
                                return@Thread
                            } catch (e: Exception) {
                                return@Thread
                            }
                            if (currentPosition != null) {
                                seekBar.progress = currentPosition
                            }
                        }
                    }
                }.start()
            }
            wasPlaying = false
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun setFabButtonListener() {
        playButton.setOnClickListener {
            //TODO
        }
    }

    private fun setSearchView() {
        searchView.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
                // When user changed the Text
                filter(cs.toString())
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int,
                                           arg3: Int) {
                // TODO Auto-generated method stub
            }

            override fun afterTextChanged(arg0: Editable) {
                // TODO Auto-generated method stub
            }
        })
    }

    private fun filter(text: String) {
        val filteredList: ArrayList<String> = ArrayList()
        for (element in Ringtones.listContent){
            if (element.toLowerCase().contains(text.toLowerCase())){
                filteredList.add(element)
            }
        }
        adapter.filterList(filteredList.toTypedArray())
    }

   /* private fun dynamicFeature(){

        manager.registerListener(listener)

       *//* mFabButton!!.setOnClickListener {
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
            }

            manager.startInstall(request)
        }*//*
    }*/



    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(R.anim.abc_slide_in_top,
                R.anim.abc_slide_in_bottom)

    }

   /* *//** Listener used to handle changes in state for install requests. *//*
    private val listener = SplitInstallStateUpdatedListener { state ->
        val multiInstall = state.moduleNames().size > 1
        state.moduleNames().forEach { name ->
            // Handle changes in state.
            when (state.status()) {
                SplitInstallSessionStatus.DOWNLOADING -> {
                    //  In order to see this, the application has to be uploaded to the Play Store.
                    //displayLoadingState(state, "Downloading $name")
                    Toast.makeText(this,"downloading",Toast.LENGTH_SHORT).show()
                }
                SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {
                    *//*
                      This may occur when attempting to download a sufficiently large module.

                      In order to see this, the application has to be uploaded to the Play Store.
                      Then features can be requested until the confirmation path is triggered.
                     *//*
                    //startIntentSender(state.resolutionIntent()?.intentSender, null, 0, 0, 0)
                }
                SplitInstallSessionStatus.INSTALLED -> {
                    Toast.makeText(this,"installed",Toast.LENGTH_SHORT).show()
                    onSuccessfulLoad(name, launch = !multiInstall)
                }

                SplitInstallSessionStatus.INSTALLING ->
                    Toast.makeText(this,"installing..",Toast.LENGTH_SHORT).show()

                SplitInstallSessionStatus.FAILED -> {
                    //toastAndLog("Error: ${state.errorCode()} for module ${state.moduleNames()}")
                    Toast.makeText(this,"failed",Toast.LENGTH_SHORT).show()

                }
            }
        }
    }*/

   /* private fun onSuccessfulLoad(moduleName: String, launch: Boolean) {
        if (launch) {
            Toast.makeText(this,"Onsuccessfulload",Toast.LENGTH_SHORT).show()

            var intent1 = Intent(this, Class.forName("com.example.dynamicfeature.MainActivity"))
            startActivity(intent1)
        }
    }
*/

    private fun initStartAppSdkAccordingToConsent() {
        if (getPreferences(Context.MODE_PRIVATE).getBoolean(SHARED_PREFS_GDPR_SHOWN, false)) {
            initStartAppSdk()
        }
        else {
            showGdprDialog()
        }
    }

    private fun showGdprDialog() {
        val view: View = layoutInflater.inflate(R.layout.dialog_gdpr, null)
        val dialog  = Dialog(this, android.R.style.Theme_Light_NoTitleBar)
        dialog.setContentView(view)
/*
        var medium: Typeface = Typeface.createFromAsset(getAssets(), "gotham_medium.ttf")
        var book: Typeface  = Typeface.createFromAsset(getAssets(), "gotham_book.ttf")*/

        val okBtn: Button = view.findViewById(R.id.okBtn)
        //okBtn.typeface = medium

        okBtn.setOnClickListener {

            writePersonalizedAdsConsent(true)
            dialog.dismiss()
        }

        val cancelBtn: Button  = view.findViewById(R.id.cancelBtn)
        //cancelBtn.typeface = medium

        cancelBtn.setOnClickListener {

            writePersonalizedAdsConsent(false)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun writePersonalizedAdsConsent(userConsent: Boolean) {
        StartAppSDK.setUserConsent(this,
                "pas",
                System.currentTimeMillis(),
                userConsent)

        getPreferences(Context.MODE_PRIVATE)
                .edit()
                .putBoolean(SHARED_PREFS_GDPR_SHOWN, true)
                .apply()
    }

    private fun initStartAppSdk() {
        // NOTE always use test ads during development and testing
        StartAppSDK.setTestAdsEnabled(BuildConfig.DEBUG)
        StartAppAd.disableSplash()
    }

    override fun onDestroy() {
        super.onDestroy()
        clearMediaPlayer()
    }

    fun clearMediaPlayer() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}