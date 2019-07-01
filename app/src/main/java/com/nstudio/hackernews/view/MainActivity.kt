package com.nstudio.hackernews.view

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import com.nstudio.hackernews.R



class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        showFragment(StoriesFragment::class.java.simpleName)
    }

    private fun showFragment(name: String) {

        var fragment: Fragment? = null
        var tag:String? = null

        when(name){
            "StoriesFragment" -> {
                fragment = StoriesFragment()
                tag = StoriesFragment::class.java.simpleName
            }
        }


        if (fragment != null) {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.add(R.id.fragmentView,fragment,tag)
            fragmentTransaction.addToBackStack(tag)
            fragmentTransaction.commit()
        }



    }
}
