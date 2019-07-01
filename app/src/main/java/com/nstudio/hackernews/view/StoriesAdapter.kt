package com.nstudio.hackernews.view

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.nstudio.hackernews.R
import com.nstudio.hackernews.model.Story
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class StoriesAdapter(private val list: List<Story>) : RecyclerView.Adapter<StoriesAdapter.MyViewHolder>(){


    private var dateFormat: DateFormat? = null



    init {
        dateFormat = SimpleDateFormat("dd MMM yy", Locale.ENGLISH)

    }


    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_story,parent,false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
       return list.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder : MyViewHolder, pos: Int) {
        val story = list[pos]

        holder.tvTitle.text = story.title
        holder.tvStoryBy.text = "By "+story.by
        holder.tvVotes.text = ""+story.score
        holder.tvComments.text = ""+story.descendants
        holder.tvTitle.text = story.title

        holder.tvTime.text = dateFormat!!.format(Date(story.time))

        holder.tvOther.text = story.type+" "+story.deleted+" "+story.dead

    }


    inner class MyViewHolder( itemView: View) : RecyclerView.ViewHolder(itemView) {

        var tvTitle : TextView = itemView.findViewById(R.id.tvTitle)
        var tvStoryBy : TextView = itemView.findViewById(R.id.tvStoryBy)
        var tvVotes : TextView = itemView.findViewById(R.id.tvVotes)
        var tvComments : TextView = itemView.findViewById(R.id.tvComment)
        var tvTime : TextView = itemView.findViewById(R.id.tvTime)
        var tvOther : TextView = itemView.findViewById(R.id.tvOther)

    }



}