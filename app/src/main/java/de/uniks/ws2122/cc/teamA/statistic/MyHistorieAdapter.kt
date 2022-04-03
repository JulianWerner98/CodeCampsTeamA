package de.uniks.ws2122.cc.teamA.statistic

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.graphics.drawable.shapes.Shape
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import de.uniks.ws2122.cc.teamA.Constant
import de.uniks.ws2122.cc.teamA.R
import de.uniks.ws2122.cc.teamA.model.MatchResult

class MyHistorieAdapter( val matchResultList: LiveData<List<MatchResult>>, ) : RecyclerView.Adapter<MyHistorieAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.historie_item, parent,
            false
        )
        return MyViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = matchResultList.value!![position]
        holder.gameName.text = currentItem.gamename
        holder.userName.text = currentItem.currentuser
        holder.opponent.text = currentItem.opponent
        holder.points.text = currentItem.points.toString()

        when (currentItem.win) {
            Constant.WIN -> {
                holder.layout.setBackgroundResource(R.color.green)
            }
            Constant.LOSE -> {
                holder.layout.setBackgroundResource(R.color.red)
            }
            else -> {
                holder.layout.setBackgroundResource(R.color.gray)
            }
        }
    }

    override fun getItemCount(): Int {

        return matchResultList.value!!.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val layout: ConstraintLayout = itemView.findViewById(R.id.tvHistorieLayout)
        val gameName: TextView = itemView.findViewById(R.id.tvHistorieGameTyp)
        val userName: TextView = itemView.findViewById(R.id.tvHistoriePlayer1)
        val opponent: TextView = itemView.findViewById(R.id.tvHistoriePlayer2)
        val points: TextView = itemView.findViewById(R.id.tvHistoriePointsNumber)
    }
}