package com.nstudio.hackernews.ui.fragments


import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView

import com.nstudio.hackernews.R
import com.nstudio.hackernews.model.Story
import android.webkit.WebChromeClient
import android.widget.*
import com.nstudio.hackernews.ui.viewModels.StoryViewModel
import com.nstudio.hackernews.utils.NetworkState
import java.util.ArrayList



/**
 * fragment to show story details from given url
 */

class StoryDetailsFragment : Fragment() {

    private lateinit var viewModel: StoryViewModel
    private lateinit var webView:WebView
    private var position:Int = 0
    private lateinit var story:Story
    private lateinit var seekBar: SeekBar
    private lateinit var viewOffline:LinearLayout
    private lateinit var btnComments:Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewModel = ViewModelProviders.of(activity!!).get(StoryViewModel::class.java)

        return inflater.inflate(R.layout.fragment_story_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        webView = view.findViewById(R.id.webView)
        seekBar = view.findViewById(R.id.seekBar)
        viewOffline = view.findViewById(R.id.viewOffline)
        btnComments = view.findViewById(R.id.btnComments)

        position = arguments?.getInt("storyIndex") ?: 0

        Log.e(tag, "index $position")

        story = viewModel.getStory(position)!!

        Log.e(tag, "story id ${story.id}")


        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)


        tvTitle.text = story.title



        view.findViewById<ImageView>(R.id.imgBack).setOnClickListener {
            if (activity!=null){
                activity!!.supportFragmentManager.popBackStackImmediate()
            }
        }

        view.findViewById<Button>(R.id.btnRetry).setOnClickListener {
            if(NetworkState.isOnline(context!!)){
                loadUrl(story.url)
            }else{
                seekBar.visibility = View.GONE
                webView.visibility = View.GONE
                viewOffline.visibility = View.VISIBLE
            }
        }

        btnComments.setOnClickListener {
            showComments()
        }


        if (savedInstanceState==null){
            if(NetworkState.isOnline(context!!)){
                loadUrl(story.url)
            }else{
                seekBar.visibility = View.GONE
                webView.visibility = View.GONE
                viewOffline.visibility = View.VISIBLE
            }
        }else{
            webView.restoreState(savedInstanceState)

            if (story.descendants>0){
                btnComments.visibility = View.VISIBLE
                val text = "Show Comments (${story.descendants})"
                btnComments.text = text
            }

        }



    }

    private fun showComments() {
        //show comments of story on comments fragment
        val bundle = Bundle()
        bundle.putIntegerArrayList("kids", story.kids as ArrayList<Int>)
        bundle.putString("story",story.title)

        val fragment = CommentsFragment()
        fragment.arguments = bundle

        val tag:String = CommentsFragment::class.java.simpleName
        val fragmentTransaction = activity!!.supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.fragmentView,fragment,tag)
        fragmentTransaction.addToBackStack(tag)
        fragmentTransaction.commit()
    }

    private fun loadUrl(url: String) {
        //show story from url

        seekBar.visibility = View.VISIBLE
        seekBar.progress = 0

        if (viewOffline.visibility == View.VISIBLE){
            viewOffline.visibility = View.GONE
            webView.visibility = View.VISIBLE
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, progress: Int) {
                Log.e(tag, "progress $progress")
                seekBar.progress = progress * 100
                if (progress == 100){
                    seekBar.visibility = View.GONE


                    if (story.descendants>0){
                        btnComments.visibility = View.VISIBLE
                        val text = "Show Comments (${story.descendants})"
                        btnComments.text = text
                    }
                }

            }
        }

        webView.loadUrl(url)

    }

    override fun onSaveInstanceState(outState: Bundle) {
        webView.saveState(outState)
    }
}
