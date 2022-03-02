package de.uniks.ws2122.cc.teamA

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.common.util.Hex
import de.uniks.ws2122.cc.teamA.databinding.ActivityCompassBinding
import de.uniks.ws2122.cc.teamA.model.AppViewModel
import de.uniks.ws2122.cc.teamA.model.CompassViewModel
import kotlin.math.roundToInt


class CompassActivity : AppCompatActivity() {
    private lateinit var emblems: TextView
    private lateinit var appViewModel: AppViewModel
    private lateinit var viewModel: CompassViewModel
    private lateinit var binding: ActivityCompassBinding
    private lateinit var imageView: ImageView
    private lateinit var textView: TextView
    private lateinit var background: ConstraintLayout
    private var angle: Double = 0F.toDouble()
    private var searchedDegree:Double = 0F.toDouble()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompassBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageView = binding.arrow
        textView = binding.degree
        emblems = binding.emblems
        background = binding.background
        background.setBackgroundColor(Color.parseColor("#393E46"))

        //ViewModel
        viewModel = ViewModelProvider(this)[CompassViewModel::class.java]
        appViewModel = ViewModelProvider(this)[AppViewModel::class.java]
        viewModel.setupSensors(this) { newSensorValue(it) }

        viewModel.getRandomLocation(this) { emblems ->
            this.emblems.text = ""
            emblems.forEachIndexed { index, it ->
                this.emblems.text =
                    this.emblems.text as String + (index + 1).toString() + ". " + it.properties.Objekt + " \n"
            }
            viewModel.getAngleToLocation(this, emblems[0]) { degree ->
                Log.d("Debug Degree", emblems[0].properties.Objekt + ": " + degree.toString())
                binding.degree2.text = degree.toString()
                searchedDegree = degree
            }
        }
        imageView.rotation = -90F

    }

    fun newSensorValue(floatOrientation: FloatArray) {
        textView.text = (floatOrientation[0]*180/Math.PI).roundToInt().toString()
        angle = floatOrientation[0]*180/Math.PI
        if(searchedDegree + 15 >= angle && searchedDegree - 15 <= angle){
            background.setBackgroundColor(Color.GREEN)
        } else {
            background.setBackgroundColor(Color.parseColor("#393E46"))
        }
    }


}