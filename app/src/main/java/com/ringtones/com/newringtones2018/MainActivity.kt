package com.ringtones.com.newringtones2018

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.AdRequest
import com.google.android.play.core.splitcompat.SplitCompat
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import com.startapp.android.publish.ads.nativead.NativeAdPreferences
import com.startapp.android.publish.adsCommon.StartAppSDK
import com.startapp.android.publish.ads.nativead.StartAppNativeAd

class MainActivity : AppCompatActivity() {

    private var mToolbar: Toolbar? = null
    private var adapter: MyAdapter? = null
    private var context: Context? = null
    private var mTabLayout: TabLayout? = null
    private var recyclerView: RecyclerView? = null
    private lateinit var manager: SplitInstallManager
    private val startAppNativeAd = StartAppNativeAd(this)
    private val SHARED_PREFS_GDPR_SHOWN = "gdpr_dialog_was_shown"


    //Add a list items in String
    val listContent = arrayOf("BTS DNA","Bodak yellow","Body like a back road","Havana 2","How long 2","Mask off","Mi gente 2","Perfect","Perfect strangers","Praying","Pretty girl","Rockstar"
            ,"Silence","Something just like this","Sorry not sorry","Swalla","Symphony","There is nothing holding me","Two u two","What lovers do","Base gente remix","Shape of you","Apple ringtone","Apple ringtone extended","Base william voodoo", "Astronomia","Nice ringtone","Dad is calling","Titanic flute","Fade"
    ,"I miss you","Iphone 8 plus ringtone","Cock ringtone","Iphone 8 plus sms","Best flute ringtone","Classic phone","Nokia","You got text message","Water ring","Ting tong","Tweet ringtone","S8 charger connected","Warrior pixel2","Zooh",
            "Xiaomi notification","Break","Tear down","Fall down","Squeeze","Alert","Unlock","Bee bom")
    //Add a resource of music files in Array
    val resID = intArrayOf(R.raw.bts_dna,R.raw.bodak_yellow,R.raw.body_like_a_back_road,R.raw.havana_2,R.raw.how_long_2,R.raw.mask_off,R.raw.mi_gente_2,R.raw.perfect,R.raw.perfect_strangers,R.raw.praying
            ,R.raw.pretty_girl,R.raw.rockstar,R.raw.silence,R.raw.something_just_like_this,R.raw.sorry_not_sorry,R.raw.swalla,R.raw.symphony,R.raw.the_nothing,R.raw.two_u,R.raw.what_lovers,R.raw.mi_gente_bassremix,R.raw.shape_of_you,R.raw.apple_ring,R.raw.a_2,R.raw.willy_william_voodoo, R.raw.astronomia,R.raw.nice_ringtone,R.raw.dad_is_calling,R.raw.titanic_flute,R.raw.fade,
            R.raw.i_miss_you,R.raw.iphone_8_plus_ring,R.raw.a_22,R.raw.iphone_sms_original,R.raw.zedge_best_flute,R.raw.aa_4,R.raw.aa_5,R.raw.c_17,R.raw.a_66,R.raw.b_1,R.raw.tweet_ringtone,R.raw.s8_charge_connected,
            R.raw.warriors_pixel2,R.raw.s_16,R.raw.s_15,R.raw.s_13,R.raw.s_10,R.raw.s_7,R.raw.s_6,R.raw.s_2,R.raw.s_1,R.raw.c_8)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initStartAppSdkAccordingToConsent()

        setContentView(R.layout.activity_main)
        manager = SplitInstallManagerFactory.create(this)
        SplitCompat.install(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
        }

        mToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(mToolbar)
        mTabLayout = findViewById(R.id.tabs)
        mToolbar!!.setTitle(R.string.app_name)

        setHeadingOnTab()
        dynamicFeature()

        recyclerView = findViewById(R.id.recyclerView)
        context = this


        adapter = MyAdapter(context as MainActivity,listContent,resID, (context as MainActivity).supportFragmentManager)

        var mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        recyclerView!!.layoutManager = mLayoutManager
        recyclerView!!.itemAnimator
        recyclerView!!.isNestedScrollingEnabled = false
        recyclerView!!.adapter = adapter
    }


    private fun setHeadingOnTab() {
        mTabLayout!!.addTab(mTabLayout!!.newTab().setText("Play and set your tone"))
    }

    private fun dynamicFeature(){

        manager.registerListener(listener)

       /* mFabButton!!.setOnClickListener {
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
        }*/
    }



    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(R.anim.abc_slide_in_top,
                R.anim.abc_slide_in_bottom)

    }

    /** Listener used to handle changes in state for install requests. */
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
                    /*
                      This may occur when attempting to download a sufficiently large module.

                      In order to see this, the application has to be uploaded to the Play Store.
                      Then features can be requested until the confirmation path is triggered.
                     */
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
    }

    private fun onSuccessfulLoad(moduleName: String, launch: Boolean) {
        if (launch) {
            Toast.makeText(this,"Onsuccessfulload",Toast.LENGTH_SHORT).show()

            var intent1 = Intent(this, Class.forName("com.example.dynamicfeature.MainActivity"))
            startActivity(intent1)
        }
    }


    private fun initStartAppSdkAccordingToConsent() {
        if (getPreferences(Context.MODE_PRIVATE).getBoolean(SHARED_PREFS_GDPR_SHOWN, false)) {
            initStartAppSdk()
        }
        else {
            showGdprDialog()
        }
    }

    private fun showGdprDialog() {
        var view: View = layoutInflater.inflate(R.layout.dialog_gdpr, null)
        var dialog  = Dialog(this, android.R.style.Theme_Light_NoTitleBar)
        dialog.setContentView(view)
/*
        var medium: Typeface = Typeface.createFromAsset(getAssets(), "gotham_medium.ttf")
        var book: Typeface  = Typeface.createFromAsset(getAssets(), "gotham_book.ttf")*/

        var okBtn: Button = view.findViewById(R.id.okBtn)
        //okBtn.typeface = medium

        okBtn.setOnClickListener {

            writePersonalizedAdsConsent(true)
            dialog.dismiss()
        }

        var cancelBtn: Button  = view.findViewById(R.id.cancelBtn)
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
                .commit()
    }

    private fun initStartAppSdk() {
        //Initialize start-app sdk
        StartAppSDK.init(this, "200809134", true)
    }

}