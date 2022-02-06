package de.uniks.ws2122.cc.teamA.friendlist

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.uniks.ws2122.cc.teamA.Constant
import de.uniks.ws2122.cc.teamA.databinding.ActivityFriendRequestBinding
import de.uniks.ws2122.cc.teamA.model.Friend
import de.uniks.ws2122.cc.teamA.model.FriendRequestViewModel
import de.uniks.ws2122.cc.teamA.model.User

class FriendRequestActivity : AppCompatActivity(), MyRequestAdapter.OnItemClickListener, MySendRequestAdapter.OnItemClickListener {

    private val currentUser = FirebaseAuth.getInstance().currentUser!!
    private lateinit var binding: ActivityFriendRequestBinding
    private lateinit var dbref: DatabaseReference
    private lateinit var receivedList: ArrayList<Friend>
    private lateinit var myRequestAdapter: MyRequestAdapter
    private lateinit var mySendRequestAdapter: MySendRequestAdapter
    private lateinit var currentUserName: String
    private lateinit var viewModel: FriendRequestViewModel

    private lateinit var recyclerViewRequestList : RecyclerView
    private lateinit var recyclerViewSendList : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbref = FirebaseDatabase.getInstance(Constant.FIREBASE_URL).reference

        recyclerViewRequestList = binding.rvRequestList
        recyclerViewSendList = binding.rvSendList

        recyclerViewRequestList.layoutManager = LinearLayoutManager(this)
        recyclerViewRequestList.setHasFixedSize(true)

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

        binding.btnFriendList.setOnClickListener{
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