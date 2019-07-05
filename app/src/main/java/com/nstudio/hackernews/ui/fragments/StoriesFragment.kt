package com.nstudio.hackernews.ui.fragments


import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Parcelable
import com.google.android.material.snackbar.Snackbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import com.nstudio.hackernews.model.Comment
import com.nstudio.hackernews.ui.adapters.StoriesAdapter
import com.nstudio.hackernews.ui.viewModels.StoryViewModel
import com.nstudio.hackernews.utils.NetworkState
import java.util.ArrayList

/**
 * fragment to load stories
 */

@Suppress("UNCHECKED_CAST")
class StoriesFragment : Fragment() {

    private lateinit var storiesAdapter: StoriesAdapter
    private lateinit var viewOffline : LinearLayout
    private lateinit var viewModel : StoryViewModel
    private var storyCLickListener: StoriesAdapter.OnStoryCLickListener = object : StoriesAdapter.OnStoryCLickListener {
        override fun onStoryCLick(pos: Int) {

            val fragment = StoryDetailsFragment()

            val bundle = Bundle()
            bundle.putInt("storyIndex",pos)

            fragment.arguments = bundle

            val tag:String = StoryDetailsFragment::class.java.simpleName
            val fragmentTransaction = activity!!.supportFragmentManager.beginTransaction()
            fragmentTransaction.add(R.id.fragmentView,fragment,tag)
            fragmentTransaction.addToBackStack(tag)
            fragmentTransaction.commit()

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(activity!!).get(StoryViewModel::class.java)

        return inflater.inflate(R.layout.fragment_stories, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val rvStories = view.findViewById<RecyclerView>(R.id.rvStories)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        viewOffline = view.findViewById(R.id.viewOffline)

        progressBar.visibility = View.VISIBLE

        viewModel.storyList.observe(this,
            Observer<MutableList<Story>> { stories ->
                storiesAdapter = StoriesAdapter(stories, storyCLickListener)

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
                    Event.COMMENT_ADD ->{

                    }

                }
            })

        rvStories.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = androidx.recyclerview.widget.LinearLayoutManager::class.java.cast(recyclerView.layoutManager)
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

            val list = savedInstanceState.getParcelableArrayList<Story>("stories")
            if (list!=null){
                viewModel.setStoryList(list as MutableList<Story>)
            }
        }


    }

    private fun loadStories(){
        //check and load more stories if available

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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val list = viewModel.getStoryList()
        outState.putParcelableArrayList("stories",list as ArrayList<out Parcelable>)
    }

}
