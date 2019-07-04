package com.nstudio.hackernews.ui.adapters

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.nstudio.hackernews.R
import com.nstudio.hackernews.model.Story

/** adapter to show list of stories
 * @param list >  list to load stories
 * @param storyCLickListener > to get details from selected story
 */
class StoriesAdapter(private val list: MutableList<Story>?, private val storyCLickListener: OnStoryCLickListener) : RecyclerView.Adapter<StoriesAdapter.MyViewHolder>(){


    private val timeNow = System.currentTimeMillis();

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_story,parent,false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
       return list!!.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder : MyViewHolder, pos: Int) {

        val story = list!![pos]

        holder.tvTitle.text = story.title
        holder.tvStoryBy.text = "By "+story.by
        holder.tvVotes.text = ""+story.score
        holder.tvComments.text = ""+story.descendants
        holder.tvTitle.text = story.title

        holder.tvTime.text = DateUtils.getRelativeTimeSpanString(story.time, timeNow, DateUtils.MINUTE_IN_MILLIS)


    }


    inner class MyViewHolder( itemView: View) : RecyclerView.ViewHolder(itemView) {

        var tvTitle : TextView = itemView.findViewById(R.id.tvTitle)
        var tvStoryBy : TextView = itemView.findViewById(R.id.tvStoryBy)
        var tvVotes : TextView = itemView.findViewById(R.id.tvVotes)
        var tvComments : TextView = itemView.findViewById(R.id.tvComment)
        var tvTime : TextView = itemView.findViewById(R.id.tvTime)

        init {
            itemView.setOnClickListener { storyCLickListener.onStoryCLick(adapterPosition) }
        }
    }


    interface OnStoryCLickListener{
        fun onStoryCLick( pos: Int)
    }


}