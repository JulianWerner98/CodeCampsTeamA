package de.uniks.ws2122.cc.teamA.friendlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.uniks.ws2122.cc.teamA.R
import de.uniks.ws2122.cc.teamA.model.Friend

class MyFriendsAdapter(
    private val userList: ArrayList<Friend>,
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
        val currentItem = userList[position]
        holder.nickname.text = currentItem.nickname
        holder.id.text = currentItem.id
    }

    override fun getItemCount(): Int {

        return userList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        val nickname: TextView = itemView.findViewById(R.id.tvNickName)
        val btnInvite: Button = itemView.findViewById(R.id.btnInviteFriend)
        val btnRemove: Button = itemView.findViewById(R.id.btnRemoveFriend)
        val id: TextView = itemView.findViewById(R.id.tvUserId)

        init {
            btnInvite.setOnClickListener(this)
            btnRemove.setOnClickListener(this)
        }


        override fun onClick(p0: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                if (p0 == btnRemove) {
                    listener.onRemoveClick(position)
                }
                if (p0 == btnInvite) {
                    listener.onInviteClick(position)
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onRemoveClick(position: Int)
        fun onInviteClick(position: Int)
    }
}