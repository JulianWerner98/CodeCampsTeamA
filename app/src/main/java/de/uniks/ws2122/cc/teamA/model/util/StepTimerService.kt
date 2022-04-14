package de.uniks.ws2122.cc.teamA.model.util

import android.app.Service
import android.content.Intent
import android.os.IBinder
import java.util.*

class StepTimerService : Service() {

    override fun onBind(p0: Intent?): IBinder? = null

    private val timer = Timer()

    /** innit timer **/
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        val time = intent.getDoubleExtra(TIME_EXTRA, 0.0)
        // timer schedule 1 sec
        timer.scheduleAtFixedRate(TimeTask(time), 0, 1000)
        return START_NOT_STICKY
    }

    /** kills timer **/
    override fun onDestroy() {
        timer.cancel()
        super.onDestroy()
    }

    /** create timer task to count seconds and broadcast intent to inform the timer has been updated**/
    private inner class TimeTask(private var time: Double) : TimerTask() {

        override fun run() {

            val intent = Intent(TIMER_UPDATE)
            time++
            //add to the intent the counted time
            intent.putExtra(TIME_EXTRA, time)
            sendBroadcast(intent)
        }
    }

    companion object {

        const val TIMER_UPDATE = "timerUpdate"
        const val TIME_EXTRA = "timeExtra"
    }
}