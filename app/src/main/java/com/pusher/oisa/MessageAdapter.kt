package com.pusher.oisa

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.my_message.view.*
import kotlinx.android.synthetic.main.other_message.view.*
import org.w3c.dom.Text


private const val VIEW_TYPE_MY_MESSAGE = 1
private const val VIEW_TYPE_OTHER_MESSAGE = 2

class MessageAdapter (val context: Context) : RecyclerView.Adapter<MessageViewHolder>() {
    private val messages: ArrayList<Message> = ArrayList()

    private var fontSize:Float = App.fontSize.toFloat()

    fun addMessage(message: Message){
        messages.add(message)
        notifyDataSetChanged()
    }

    fun addMessages(users: List<String>, texts : List<String>, times : List<Long>){
        val count = users.count()
        for(i in 0 until count){
            val message = Message(
                    users[i],
                    texts[i],
                    times[i]
            )
            addMessage(message)
        }
        notifyDataSetChanged()
    }

    fun update() {
        val temp = ArrayList<Message>(messages)
        clear()
        for (message in temp){
            addMessage(message)
        }
        notifyDataSetChanged()
    }

    fun clear(){
        messages.clear()
        notifyDataSetChanged()
    }

    fun changeFontSize() {
        fontSize = App.fontSize.toFloat()
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages.get(position)

        return if(App.user == message.user) {
            VIEW_TYPE_MY_MESSAGE
        }
        else {
            VIEW_TYPE_OTHER_MESSAGE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return if(viewType == VIEW_TYPE_MY_MESSAGE) {
            val view = LayoutInflater.from(context).inflate(R.layout.my_message, parent, false)
            Log.v("MY_FONT_SIZE", fontSize.toString())
            view?.txtMyMessage?.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize)
            MyMessageViewHolder(view)
        } else {
            val view = LayoutInflater.from(context).inflate(R.layout.other_message, parent, false)
            Log.v("OTHER_FONT_SIZE", fontSize.toString())
            view?.txtOtherMessage?.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize)
            OtherMessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages.get(position)

        holder?.bind(message)
    }

    inner class MyMessageViewHolder (view: View) : MessageViewHolder(view) {
        private var messageText: TextView = view.txtMyMessage
        private var timeText: TextView = view.txtMyMessageTime

        override fun bind(message: Message) {
            messageText.text = message.message
            timeText.text = DateUtils.fromMillisToTimeString(message.time)
        }
    }

    inner class OtherMessageViewHolder (view: View) : MessageViewHolder(view) {
        private var messageText: TextView = view.txtOtherMessage
        private var userText: TextView = view.txtOtherUser
        private var timeText: TextView = view.txtOtherMessageTime

        override fun bind(message: Message) {
            messageText.text = message.message
            userText.text = message.user
            timeText.text = DateUtils.fromMillisToTimeString(message.time)
        }
    }
}

open class MessageViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    open fun bind(message:Message) {}
}
