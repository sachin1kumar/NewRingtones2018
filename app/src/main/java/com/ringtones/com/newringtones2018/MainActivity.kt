package com.ringtones.com.newringtones2018

import android.content.Context
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View

class MainActivity : AppCompatActivity() {

    private var mToolbar: Toolbar? = null
    private var adapter: MyAdapter? = null
    private var context: Context? = null
    private var mTabLayout: TabLayout? = null
    private var recyclerView: RecyclerView? = null

    //Add a list items in String
    val listContent = arrayOf("apple_ring", "astronomia")
    //Add a resource of music files in Array
    val resID = intArrayOf(R.raw.apple_ring, R.raw.astronomia)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(mToolbar)
        mTabLayout = findViewById(R.id.tabs)
        mToolbar!!.setTitle(R.string.app_name)

        setHeadingOnTab()

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
        mTabLayout!!.addTab(mTabLayout!!.newTab().setText("Today's Latest Tech News"))
    }



    override fun onBackPressed() {
        super.onBackPressed()
    }

}