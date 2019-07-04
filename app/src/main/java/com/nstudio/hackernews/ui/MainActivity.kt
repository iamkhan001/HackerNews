package com.nstudio.hackernews.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import com.nstudio.hackernews.R
import com.nstudio.hackernews.ui.adapters.StoriesAdapter
import com.nstudio.hackernews.ui.fragments.StoriesFragment
import com.nstudio.hackernews.ui.fragments.StoryDetailsFragment


class MainActivity : AppCompatActivity(), StoriesAdapter.OnStoryCLickListener{


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


    override fun onStoryCLick(pos: Int) {


        val fragment = StoryDetailsFragment()

        val bundle = Bundle()
        bundle.putInt("storyIndex",pos)

        fragment.arguments = bundle

        val tag:String = StoryDetailsFragment::class.java.simpleName
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.fragmentView,fragment,tag)
        fragmentTransaction.addToBackStack(tag)
        fragmentTransaction.commit()

    }

    override fun onBackPressed() {
        super.onBackPressed()


        if(supportFragmentManager.backStackEntryCount == 0){
            finish()
        }

    }

}
