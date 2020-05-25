package com.ringtones.com.newringtones2018.view

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.multidex.BuildConfig
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.ringtones.com.newringtones2018.R
import com.ringtones.com.newringtones2018.base.MyAdapter
import com.ringtones.com.newringtones2018.utils.Ringtones
import com.startapp.sdk.adsbase.StartAppSDK

class MainActivity : AppCompatActivity() {

    private lateinit var mToolbar: Toolbar
    private lateinit var adapter: MyAdapter
    private lateinit var mTabLayout: TabLayout
    private lateinit var recyclerView: RecyclerView
    private val SHARED_PREFS_GDPR_SHOWN = "gdpr_dialog_was_shown"

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark);
        }

        mToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(mToolbar)
        mTabLayout = findViewById(R.id.tabs)
        mToolbar.setTitle(R.string.app_name)

        setHeadingOnTab()

        recyclerView = findViewById(R.id.recyclerView)
        adapter = MyAdapter(this, Ringtones.listContent, Ringtones.resID,Ringtones.isExpanded, this.supportFragmentManager)

        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = mLayoutManager
        recyclerView.itemAnimator
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.adapter = adapter
    }

    private fun setHeadingOnTab() {
        mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.play_msg)))
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
        StartAppSDK.setTestAdsEnabled(BuildConfig.DEBUG);
    }

}