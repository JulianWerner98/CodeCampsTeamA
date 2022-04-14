package de.uniks.ws2122.cc.teamA.gameInvite

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import de.uniks.ws2122.cc.teamA.R
import de.uniks.ws2122.cc.teamA.model.GameInvites

class MyInviteAdapter( private val gameInviteList: LiveData<List<GameInvites>>,
private val listener: OnItemClickListener
) : RecyclerView.Adapter<MyInviteAdapter.MyViewHolder>() {
    // Create Items that will show on the recycler view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.invite_item, parent,
            false
        )
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = gameInviteList.value!![position]
        holder.gameName.text = currentItem.gameName
        holder.friendName.text = currentItem.friendName
    }

    override fun getItemCount(): Int {

        return gameInviteList.value!!.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        val gameName: TextView = itemView.findViewById(R.id.tvGameInviteName)
        val friendName: TextView = itemView.findViewById(R.id.tvInviteFromFriend)

        init {
            itemView.setOnClickListener(this)
        }


        override fun onClick(p0: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
               listener.onItemClicked(position)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClicked(position: Int)
    }
}