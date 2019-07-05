package com.nstudio.hackernews.ui.fragments

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Parcelable
import com.google.android.material.snackbar.Snackbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import com.nstudio.hackernews.R
import com.nstudio.hackernews.model.Comment
import com.nstudio.hackernews.model.Event
import com.nstudio.hackernews.model.QueryResponse
import com.nstudio.hackernews.ui.adapters.CommentsAdapter
import com.nstudio.hackernews.ui.viewModels.CommentViewModel
import com.nstudio.hackernews.utils.NetworkState
import java.util.ArrayList


/**
 * Fragment to show comments on story
 */
@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class CommentsFragment : Fragment(){

    private lateinit var commentsAdapter: CommentsAdapter
    private lateinit var viewOffline : LinearLayout
    private lateinit var viewModel : CommentViewModel
    private var storyTitle:String = ""
    private lateinit var progressBar: ProgressBar


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(this).get(CommentViewModel::class.java)

        return inflater.inflate(R.layout.fragment_comments, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        val rvComments = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvComments)
        progressBar = view.findViewById(R.id.progressBar)
        viewOffline = view.findViewById(R.id.viewOffline)
        progressBar.visibility = View.VISIBLE

        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)


        if (arguments==null){
            return
        }

        storyTitle =  arguments!!.getString("story")

        tvTitle.text = storyTitle

        val ids = arguments!!.getIntegerArrayList("kids") ?: return

        viewModel.setCommentIds(ids.toMutableList())

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

        rvComments.addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
                val layoutManager = androidx.recyclerview.widget.LinearLayoutManager::class.java.cast(recyclerView.layoutManager)
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

        view.findViewById<Button>(R.id.btnRetry).setOnClickListener {
            loadComments()
        }

        if (savedInstanceState==null){
            loadComments()
        }else{
            if(progressBar.visibility == View.VISIBLE){
                progressBar.visibility = View.GONE
            }
            val list = savedInstanceState.getParcelableArrayList<Comment>("comments")
            if (list!=null){
                viewModel.setCommentList(list as MutableList<Comment>)
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val list = viewModel.getCommentList()
        outState.putParcelableArrayList("comments",list as ArrayList<out Parcelable>)
    }




}
