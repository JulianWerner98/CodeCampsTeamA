package de.uniks.ws2122.cc.teamA.friendlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import de.uniks.ws2122.cc.teamA.R
import de.uniks.ws2122.cc.teamA.model.Friend
import de.uniks.ws2122.cc.teamA.model.User

class MyFriendsAdapter(
    private val userList: LiveData<List<User>>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<MyFriendsAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.friend_item, parent,
            false
        )
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = userList.value!![position]
        holder.nickname.text = currentItem.nickname
    }

    override fun getItemCount(): Int {

        return userList.value!!.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        val nickname: TextView = itemView.findViewById(R.id.tvNickName)

        init {
            itemView.setOnClickListener(this)
        }


        override fun onClick(p0: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}