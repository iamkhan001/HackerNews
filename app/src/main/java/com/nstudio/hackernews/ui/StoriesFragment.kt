package com.nstudio.hackernews.ui


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.ProgressBar
import com.nstudio.hackernews.model.Event
import com.nstudio.hackernews.model.QueryResponse
import com.nstudio.hackernews.model.Story
import kotlinx.android.synthetic.main.fragment_stories.*
import com.nstudio.hackernews.R
import com.nstudio.hackernews.utils.NetworkState


class StoriesFragment : Fragment() {


    private lateinit var storiesAdapter:StoriesAdapter
    private lateinit var viewOffline : LinearLayout
    private lateinit var viewModel : StoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_stories, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel = ViewModelProviders.of(this).get(StoryViewModel::class.java)
        val rvStories = view.findViewById<RecyclerView>(R.id.rvStories)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        viewOffline = view.findViewById(R.id.viewOffline)


        progressBar.visibility = View.VISIBLE

        viewModel.storyList.observe(this,
            Observer<MutableList<Story>> { stories ->
                storiesAdapter = StoriesAdapter(stories)

                val controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_from_bottom);
                rvStories.layoutAnimation = controller

                rvStories.layoutManager = LinearLayoutManager(context)
                rvStories.adapter = storiesAdapter
             })

        viewModel.eventListener.observe(this@StoriesFragment,
            Observer<QueryResponse> {
                when(it!!.event){
                    Event.STORY_ADD ->{
                        storiesAdapter.notifyItemInserted(storiesAdapter.itemCount)

                    }
                    Event.STORY_LIST_LOAD ->{
                        if(progressBar.visibility == View.VISIBLE){
                            progressBar.visibility = View.GONE
                        }
                    }
                    Event.ERROR ->{
                        if (progressBar.visibility == View.VISIBLE){
                            progressBar.visibility = View.GONE
                        }
                        Snackbar.make(view, it.msg, Snackbar.LENGTH_SHORT).show()
                    }


                }
            })

        rvStories.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = LinearLayoutManager::class.java.cast(recyclerView.layoutManager)
                val totalItemCount = layoutManager!!.itemCount
                val lastVisible = layoutManager.findLastVisibleItemPosition()

                val endHasBeenReached = lastVisible + 5 >= totalItemCount
                if (totalItemCount > 0 && endHasBeenReached ) {
                    Log.i(tag,"Reached End >> Load More stories")
                    viewModel.loadMoreStories()
                }
            }
        })

        btnRetry.setOnClickListener {
            loadStories()
        }

        if (savedInstanceState==null){
            loadStories()
        }else{
            if(progressBar.visibility == View.VISIBLE){
                progressBar.visibility = View.GONE
            }
        }


    }

    private fun loadStories(){
        if(NetworkState.isOnline(context!!)){
            if (progressBar.visibility != View.VISIBLE){
                progressBar.visibility = View.VISIBLE
            }

            if (viewOffline.visibility == View.VISIBLE){
                viewOffline.visibility = View.GONE
            }

            viewModel.loadStories()
            return
        }
        if (progressBar.visibility == View.VISIBLE){
            progressBar.visibility = View.GONE
        }
        viewOffline.visibility = View.VISIBLE
    }


}
