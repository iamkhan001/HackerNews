package com.nstudio.hackernews.ui.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.text.HtmlCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.text.format.DateUtils
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
import com.nstudio.hackernews.utils.ColorPicker
import com.nstudio.hackernews.utils.FormatHtml
import com.nstudio.hackernews.utils.NetworkState
import kotlinx.android.synthetic.main.fragment_stories.*
import java.util.ArrayList

/**
 * fragment to show replies on parent comment
 */

class ReplayFragment :Fragment(){

    private lateinit var commentsAdapter: CommentsAdapter
    private lateinit var viewOffline : LinearLayout
    private lateinit var viewModel : CommentViewModel
    private lateinit var storyCLickListener: StoriesAdapter.OnStoryCLickListener
    private var storyTitle: String? = ""

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        storyCLickListener = context as StoriesAdapter.OnStoryCLickListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProviders.of(this).get(CommentViewModel::class.java)
        return inflater.inflate(R.layout.fragment_replay, container, false)
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

        storyTitle = arguments!!.getString("story")

        tvTitle.text = storyTitle

        val ids = arguments!!.getIntegerArrayList("kids") ?: return

        //set ids of comments / replies on comments
        viewModel.setCommentIds(ids)

        //set parent comment on which replay is given
        var name = arguments!!.getString("name")
        var comment = arguments!!.getString("comment")
        val time = arguments!!.getLong("time")

        val tvCommentBy : TextView = view.findViewById(R.id.tvCommentBy)
        val tvComment : TextView = view.findViewById(R.id.tvComment)
        val tvIcon : TextView = view.findViewById(R.id.tvNameIcon)
        val tvTime : TextView = view.findViewById(R.id.tvTime)
        val bgShape: GradientDrawable
        bgShape = tvIcon.background as GradientDrawable

        if(name.isNullOrEmpty()){
            name = "---"
        }

        if (comment.isNullOrEmpty()){
            comment = "---"
        }

        //set icon from name
        tvCommentBy.text = name
        bgShape.setColor(Color.parseColor(ColorPicker.getColor(name[0])))

        tvComment.text = FormatHtml.fromHtml(comment)

        tvTime.text = DateUtils.getRelativeTimeSpanString(time, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS)
        val text =  ""+name[0].toUpperCase()
        tvIcon.text = text


        val clickListener = object : CommentsAdapter.OnReplayClickListener {
            override fun onCommentClick(comment: Comment) {

                showReplies(comment)

            }
        }

        viewModel.commentList.observe(this,
            Observer<MutableList<Comment>> { comments ->
                commentsAdapter = CommentsAdapter(true,comments, clickListener)

                val controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_from_bottom);
                rvComments.layoutAnimation = controller

                rvComments.layoutManager = LinearLayoutManager(context)
                rvComments.adapter = commentsAdapter
            })

        viewModel.eventListener.observe(this@ReplayFragment,
            Observer<QueryResponse> {
                when(it!!.event){
                    Event.STORY_ADD ->{

                    }
                    Event.STORY_LIST_LOAD ->{

                    }
                    Event.ERROR ->{
                        if (progressBar.visibility == View.VISIBLE){
                            progressBar.visibility = View.GONE
                        }
                        Snackbar.make(view, it.msg, Snackbar.LENGTH_SHORT).show()
                    }
                    Event.COMMENT_ADD -> {
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
        //show replies on selected comment in new fragment

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
