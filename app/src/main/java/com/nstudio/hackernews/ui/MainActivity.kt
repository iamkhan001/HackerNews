package com.nstudio.hackernews.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.nstudio.hackernews.R
import com.nstudio.hackernews.ui.fragments.StoriesFragment


class MainActivity : AppCompatActivity(){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState==null){

            val fragment: Fragment= StoriesFragment()
            val tag:String = StoriesFragment::class.java.simpleName
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.add(R.id.fragmentView,fragment,tag)
            fragmentTransaction.addToBackStack(tag)
            fragmentTransaction.commit()

        }
    }

    override fun onBackPressed() {
        super.onBackPressed()


        if(supportFragmentManager.backStackEntryCount == 0){
            finish()
        }

    }

}
