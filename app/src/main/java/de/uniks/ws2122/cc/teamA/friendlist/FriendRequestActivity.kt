package de.uniks.ws2122.cc.teamA.friendlist

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.uniks.ws2122.cc.teamA.Constant
import de.uniks.ws2122.cc.teamA.R
import de.uniks.ws2122.cc.teamA.databinding.ActivityFriendRequestBinding
import de.uniks.ws2122.cc.teamA.model.FriendRequestViewModel

class FriendRequestActivity : AppCompatActivity(), MyRequestAdapter.OnItemClickListener,
    MySendRequestAdapter.OnItemClickListener {
    private lateinit var binding: ActivityFriendRequestBinding
    private lateinit var myRequestAdapter: MyRequestAdapter
    private lateinit var mySendRequestAdapter: MySendRequestAdapter
    private lateinit var viewModel: FriendRequestViewModel

    private lateinit var recyclerViewRequestList: RecyclerView
    private lateinit var recyclerViewSendList: RecyclerView

    private var notificationId = 42

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)


        recyclerViewRequestList = binding.rvRequestList
        recyclerViewRequestList.layoutManager = LinearLayoutManager(this)
        recyclerViewRequestList.setHasFixedSize(true)
        recyclerViewSendList = binding.rvSendList
        recyclerViewSendList.layoutManager = LinearLayoutManager(this)
        recyclerViewSendList.setHasFixedSize(true)

        viewModel = ViewModelProvider(this).get(FriendRequestViewModel::class.java)

        myRequestAdapter = MyRequestAdapter(viewModel.getLiveDataRequestList(), this)
        mySendRequestAdapter = MySendRequestAdapter(viewModel.getLiveDataSendList(), this)

        viewModel.fetchReceivedRequestList()
        viewModel.fetchSendRequestList()

        viewModel.getLiveDataRequestList().observe(this, Observer {
            recyclerViewRequestList.adapter = myRequestAdapter
        })

        viewModel.getLiveDataSendList().observe(this, Observer {
            recyclerViewSendList.adapter = mySendRequestAdapter
        })

        binding.btnFriendList.setOnClickListener {
            startActivity(Intent(this@FriendRequestActivity, FriendListActivity::class.java))
            finish()
        }
    }

    override fun onRequestAcceptClick(position: Int) {
        val friend = viewModel.getLiveDataRequestList().value!![position]
        viewModel.getFriendRequestController().acceptFriendRequest(friend) { msg ->
            Toast.makeText(this@FriendRequestActivity, msg, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestDeclineClick(position: Int) {
        val friend = viewModel.getLiveDataRequestList().value!![position]
        viewModel.getFriendRequestController().declineFriendRequest(friend) { msg ->
            Toast.makeText(this@FriendRequestActivity, msg, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSendCancelClick(position: Int) {
        val friend = viewModel.getLiveDataSendList().value!![position]
        viewModel.getFriendRequestController().cancelSendFriendRequest(friend) { msg ->
            Toast.makeText(this@FriendRequestActivity, msg, Toast.LENGTH_SHORT).show()
        }
    }
}