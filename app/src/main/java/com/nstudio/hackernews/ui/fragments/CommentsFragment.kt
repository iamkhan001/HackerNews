package com.nstudio.hackernews.ui.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
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
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.nstudio.hackernews.R
import com.nstudio.hackernews.model.Comment
import com.nstudio.hackernews.model.Event
import com.nstudio.hackernews.model.QueryResponse
import com.nstudio.hackernews.ui.adapters.CommentsAdapter
import com.nstudio.hackernews.ui.adapters.StoriesAdapter
import com.nstudio.hackernews.ui.viewModels.CommentViewModel
import com.nstudio.hackernews.utils.NetworkState
import kotlinx.android.synthetic.main.fragment_comments.*
import java.util.ArrayList


/**
 * Fragment to show comments on story
 */
class CommentsFragment :Fragment(){

    private lateinit var commentsAdapter: CommentsAdapter
    private lateinit var viewOffline : LinearLayout
    private lateinit var viewModel : CommentViewModel
    private lateinit var storyCLickListener: StoriesAdapter.OnStoryCLickListener
    private var storyTitle:String? = ""

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        storyCLickListener = context as StoriesAdapter.OnStoryCLickListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(this).get(CommentViewModel::class.java)

        return inflater.inflate(R.layout.fragment_comments, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        val rvComments = view.findViewById<RecyclerView>(R.id.rvComments)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        viewOffline = view.findViewById(R.id.viewOffline)
        progressBar.visibility = View.VISIBLE

        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)


        if (arguments==null){
            return
        }

        storyTitle =  arguments!!.getString("story")

        tvTitle.text = storyTitle

        val ids = arguments!!.getIntegerArrayList("kids") ?: return

        viewModel.setCommentIds(ids)

        Log.e(tag,"Comments Ids ${ids.size}")

        val clickListener = object : CommentsAdapter.OnReplayClickListener {
            override fun onCommentClick(comment: Comment) {
                showReplies(comment)
            }
        }

        viewModel.commentList.observe(this,
            Observer<MutableList<Comment>> { comments ->
                commentsAdapter = CommentsAdapter(false,comments, clickListener)

                val controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_from_bottom);
                rvComments.layoutAnimation = controller

                rvComments.layoutManager = LinearLayoutManager(context)
                rvComments.adapter = commentsAdapter
            })

        viewModel.eventListener.observe(this@CommentsFragment,
            Observer<QueryResponse> {
                when(it!!.event){
                    Event.STORY_ADD ->{

                    }
                    Event.STORY_LIST_LOAD ->{

                    }
                    Event.ERROR ->{
                        //If api call failed

                        if (progressBar.visibility == View.VISIBLE){
                            progressBar.visibility = View.GONE
                        }
                        Snackbar.make(view, it.msg, Snackbar.LENGTH_SHORT).show()
                    }
                    Event.COMMENT_ADD -> {
                        //notify adapter if new comment loaded

                        if (progressBar.visibility == View.VISIBLE){
                            progressBar.visibility = View.GONE
                        }
                        commentsAdapter.notifyItemInserted(commentsAdapter.itemCount)
                    }
                }
            })

        rvComments.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = LinearLayoutManager::class.java.cast(recyclerView.layoutManager)
                val totalItemCount = layoutManager!!.itemCount
                val lastVisible = layoutManager.findLastVisibleItemPosition()

                val endHasBeenReached = lastVisible + 5 >= totalItemCount
                if (totalItemCount > 0 && endHasBeenReached ) {

                    // load more comments if not all comments are loaded

                    Log.i(tag,"Reached End >> Load More Comments")

                    viewModel.loadMoreComments()
                }
            }
        })

        view.findViewById<ImageView>(R.id.imgBack).setOnClickListener {
            if (activity!=null){
                activity!!.supportFragmentManager.popBackStackImmediate()
            }
        }

        btnRetry.setOnClickListener {
            loadComments()
        }

        if (savedInstanceState==null){
            loadComments()
        }else{
            if(progressBar.visibility == View.VISIBLE){
                progressBar.visibility = View.GONE
            }
        }


    }

    private fun showReplies(comment: Comment) {

        val bundle = Bundle()
        bundle.putIntegerArrayList("kids", comment.kids as ArrayList<Int>)
        bundle.putString("story",storyTitle)
        bundle.putString("name",comment.by)
        bundle.putString("comment",comment.text)
        bundle.putLong("time",comment.time)

        val fragment = ReplayFragment()
        fragment.arguments = bundle

        val tag:String = ReplayFragment::class.java.simpleName
        val fragmentTransaction = activity!!.supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.fragmentView,fragment,tag)
        fragmentTransaction.addToBackStack(tag)
        fragmentTransaction.commit()


    }

    private fun loadComments(){
        //load more comments if internet is available
        if(NetworkState.isOnline(context!!)){
            if (progressBar.visibility != View.VISIBLE){
                progressBar.visibility = View.VISIBLE
            }

            if (viewOffline.visibility == View.VISIBLE){
                viewOffline.visibility = View.GONE
            }

            viewModel.loadMoreComments()
            return
        }
        if (progressBar.visibility == View.VISIBLE){
            progressBar.visibility = View.GONE
        }
        viewOffline.visibility = View.VISIBLE
    }


}
