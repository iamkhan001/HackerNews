package com.nstudio.hackernews.ui.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.nstudio.hackernews.model.Comment
import android.graphics.drawable.GradientDrawable
import com.nstudio.hackernews.R
import com.nstudio.hackernews.utils.ColorPicker
import com.nstudio.hackernews.utils.FormatHtml

/** to show comments/replies in recycler view
 * @param isReply to load layout for comment or replay list
 * @param list list of comments
 * @param replayClickListener to load replies on comment if available
*/
class CommentsAdapter(private val isReply:Boolean, private val list: MutableList<Comment>?, private val replayClickListener: OnReplayClickListener) : androidx.recyclerview.widget.RecyclerView.Adapter<CommentsAdapter.MyViewHolder>(){


    private val timeNow = System.currentTimeMillis()

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): MyViewHolder {
        //change layout if this list is for replies on comment
        val layout:Int = if (isReply){
                                   R.layout.item_replay
                                }else{
                                   R.layout.item_comment
                                }
        val view:View = LayoutInflater.from(parent.context).inflate(layout,parent,false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
       return list!!.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder : MyViewHolder, pos: Int) {

        val comment = list!![pos]

        val name:String

        name = if(comment.by.isNullOrEmpty()){
            "---"
        }else{
            comment.by.trim()
        }


        holder.tvCommentBy.text = name
        holder.bgShape.setColor(Color.parseColor(ColorPicker.getColor(name[0])))
        holder.tvComment.text = FormatHtml.fromHtml(comment.text)
        holder.tvTime.text = DateUtils.getRelativeTimeSpanString(comment.time, timeNow, DateUtils.MINUTE_IN_MILLIS)
        holder.tvIcon.text = ""+name[0].toUpperCase()

        if (comment.kids.isNullOrEmpty()){
            holder.tvReplay.visibility = View.GONE
        }else{
            holder.tvReplay.visibility = View.VISIBLE
            val replay = "Show ${comment.kids.size} Replies"
            holder.tvReplay.text = replay
        }

    }


    inner class MyViewHolder( itemView: View) : RecyclerView.ViewHolder(itemView) {

        var tvCommentBy : TextView = itemView.findViewById(R.id.tvCommentBy)
        var tvComment : TextView = itemView.findViewById(R.id.tvComment)
        var tvReplay : TextView = itemView.findViewById(R.id.tvReplies)
        var tvIcon : TextView = itemView.findViewById(R.id.tvNameIcon)
        var tvTime : TextView = itemView.findViewById(R.id.tvTime)

        var bgShape: GradientDrawable

        init {
            tvReplay.setOnClickListener { replayClickListener.onCommentClick(list!![adapterPosition]) }
            bgShape = tvIcon.background as GradientDrawable
        }
    }


    interface OnReplayClickListener{
        fun onCommentClick(comment: Comment)
    }


}