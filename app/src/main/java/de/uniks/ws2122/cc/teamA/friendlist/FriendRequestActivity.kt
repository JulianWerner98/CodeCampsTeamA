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

        // Create ViewModel
        viewModel = ViewModelProvider(this).get(FriendRequestViewModel::class.java)

        // Create adapter
        myRequestAdapter = MyRequestAdapter(viewModel.getLiveDataRequestList(), this)
        mySendRequestAdapter = MySendRequestAdapter(viewModel.getLiveDataSendList(), this)

        // Fetch received and send list from database
        viewModel.fetchReceivedRequestList()
        viewModel.fetchSendRequestList()

        // Add observer on these lists
        viewModel.getLiveDataRequestList().observe(this, Observer {
            recyclerViewRequestList.adapter = myRequestAdapter
        })

        viewModel.getLiveDataSendList().observe(this, Observer {
            recyclerViewSendList.adapter = mySendRequestAdapter
        })

        // Change to FriendsList
        binding.btnFriendList.setOnClickListener {
            startActivity(Intent(this@FriendRequestActivity, FriendListActivity::class.java))
            finish()
        }
    }

    // Accept friend request
    override fun onRequestAcceptClick(position: Int) {
        val friend = viewModel.getLiveDataRequestList().value!![position]
        viewModel.acceptFriendRequest(friend) { msg ->
            Toast.makeText(this@FriendRequestActivity, msg, Toast.LENGTH_SHORT).show()
        }
    }

    // Decline friend request
    override fun onRequestDeclineClick(position: Int) {
        val friend = viewModel.getLiveDataRequestList().value!![position]
        viewModel.declineFriendRequest(friend) { msg ->
            Toast.makeText(this@FriendRequestActivity, msg, Toast.LENGTH_SHORT).show()
        }
    }

    // Cancel your friend request
    override fun onSendCancelClick(position: Int) {
        val friend = viewModel.getLiveDataSendList().value!![position]
        viewModel.cancelSendFriendRequest(friend) { msg ->
            Toast.makeText(this@FriendRequestActivity, msg, Toast.LENGTH_SHORT).show()
        }
    }
}