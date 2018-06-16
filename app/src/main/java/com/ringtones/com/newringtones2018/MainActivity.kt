package com.ringtones.com.newringtones2018

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.AdRequest

class MainActivity : AppCompatActivity() {

    private var mToolbar: Toolbar? = null
    private var adapter: MyAdapter? = null
    private var context: Context? = null
    private var mTabLayout: TabLayout? = null
    private var recyclerView: RecyclerView? = null
    private lateinit var mAdView : AdView


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
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
        }

        //Initialize ad mob
        MobileAds.initialize(this, getString(R.string.admob_app_id))

        viewAds()

        mToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(mToolbar)
        mTabLayout = findViewById(R.id.tabs)
        mToolbar!!.setTitle(R.string.app_name)

        setHeadingOnTab()

        recyclerView = findViewById(R.id.recyclerView)
        context = this

        adapter = MyAdapter(context as MainActivity,listContent,resID, (context as MainActivity).supportFragmentManager,
                mAdView)

        var mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        recyclerView!!.layoutManager = mLayoutManager
        recyclerView!!.itemAnimator
        recyclerView!!.isNestedScrollingEnabled = false
        recyclerView!!.adapter = adapter
    }


    private fun setHeadingOnTab() {
        mTabLayout!!.addTab(mTabLayout!!.newTab().setText("Play and set your tone"))
    }



    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.abc_slide_in_top,
                R.anim.abc_slide_in_bottom)

        mAdView.visibility=View.VISIBLE
    }

    override fun onRestart() {
        super.onRestart()

    }

    private fun viewAds(){
        mAdView = findViewById(R.id.adBanner)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
    }

}