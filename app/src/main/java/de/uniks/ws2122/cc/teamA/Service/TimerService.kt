package de.uniks.ws2122.cc.teamA.Service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import de.uniks.ws2122.cc.teamA.CompassActivity
import java.util.*

class TimerService : Service()
{
    private lateinit var timerCallback: (Double) -> Unit
    private var timerStarted = false
    private lateinit var serviceIntent: Intent
    private var time = 0.0
    override fun onBind(p0: Intent?): IBinder? = null

    private val timer = Timer()

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int
    {
        val time = intent.getDoubleExtra(TIME_EXTRA, 0.0)
        timer.scheduleAtFixedRate(TimeTask(time), 0, 1000)
        return START_NOT_STICKY
    }

    override fun onDestroy()
    {
        timer.cancel()
        super.onDestroy()
    }

    private inner class TimeTask(private var time: Double) : TimerTask()
    {
        override fun run()
        {
            val intent = Intent(TIMER_UPDATED)
            time++
            intent.putExtra(TIME_EXTRA, time)
            sendBroadcast(intent)
        }
    }

    companion object
    {
        const val TIMER_UPDATED = "timerUpdated"
        const val TIME_EXTRA = "timeExtra"
    }

    fun setupTimer(compassActivity: AppCompatActivity, timerCallback: (Double) -> Unit): TimerService {
        this.timerCallback = timerCallback
        serviceIntent = Intent(compassActivity.applicationContext, TimerService::class.java)
        compassActivity.registerReceiver(updateTime, IntentFilter(TIMER_UPDATED))
        return this
    }
    fun resetTimer(compassActivity: CompassActivity)
    {
        stopTimer(compassActivity)
        time = 0.0
    }

    fun startTimer(compassActivity: AppCompatActivity)
    {
        serviceIntent.putExtra(TimerService.TIME_EXTRA, time)
        compassActivity.startService(serviceIntent)
        timerStarted = true
    }

    fun stopTimer(compassActivity: AppCompatActivity)
    {
        compassActivity.stopService(serviceIntent)
        timerStarted = false
    }

    private val updateTime: BroadcastReceiver = object : BroadcastReceiver()
    {
        override fun onReceive(context: Context, intent: Intent)
        {
            time = intent.getDoubleExtra(TimerService.TIME_EXTRA, 0.0)
            timerCallback.invoke(time)
        }
    }


}
