package com.uoc.tennis

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.uoc.tennis.databinding.ActivityMainBinding
import android.hardware.SensorEventListener
import android.view.View.OnClickListener
import android.widget.TextView
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.google.firebase.firestore.FirebaseFirestore
import java.time.Instant
import java.time.format.DateTimeFormatter

class MainActivity : Activity(), SensorEventListener, OnClickListener {

    private lateinit var binding: ActivityMainBinding
    private val dataArray: ArrayList<SensorData> = java.util.ArrayList()
    private var mSensorManager: SensorManager? = null
    private var mAccelerometer: Sensor? = null
    private var mGyroscope: Sensor? = null
    //private var mMagnetic: Sensor? = null
    private var button: Button? = null
    private var title: TextView? = null
    private var text: TextView? = null
    private var sessionInfo: TextView? = null
    private var clicks: Int = INITIAL_CLICK
    private var sessionID: Int? = null
    private var numberDatum: Int = INITIAL_DATUM
    private var playerType: String = NONE
    private var hitType: String = NONE
    private var gender: String = NONE

    private lateinit var firebase: FirebaseFirestore
    private val jacksonMapper = ObjectMapper().registerModule(KotlinModule.Builder().build())

    data class SensorData(
        val x: Double, val y: Double, val z: Double,
        val timestamp: String, val sensor: String, val sessionID: Int) {
    }

    //TODO try/catch y logger


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionID = (SESSION_ID_MIN..SESSION_ID_MAX).shuffled().first()

        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        mGyroscope = mSensorManager!!.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        //mMagnetic = mSensorManager!!.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        firebase = FirebaseFirestore.getInstance()

        button = findViewById(R.id.button)
        title = findViewById(R.id.textTitle)
        text = findViewById(R.id.text)
        sessionInfo = findViewById(R.id.sessionInfo)

        button!!.setOnClickListener(this)
        sessionInfo!!.setOnClickListener {
            val intent = Intent(this, ConfigurationActivity::class.java)
            startActivityForResult(intent, CONFIGURATION)
        }
    }

    override fun onClick(v: View){
        clicks += 1
        when (clicks) {
            FIRST_CLICK -> {
                sessionInfo!!.text = EMPTY
                button!!.setText(R.string.buttonStop)
                text!!.setText(R.string.stopText)
                title!!.setText(R.string.stopTitle)
                mSensorManager!!.registerListener( this, mAccelerometer!!, SensorManager.SENSOR_DELAY_GAME)
                mSensorManager!!.registerListener(this, mGyroscope!!, SensorManager.SENSOR_DELAY_GAME)
                //mSensorManager!!.registerListener(this, mMagnetic!!, SensorManager.SENSOR_DELAY_GAME)
            }
            SECOND_CLICK -> {
                button!!.setText(R.string.stoppedButton)
                text!!.setText(R.string.stoppedText)
                title!!.setText(R.string.stoppedTitle)
                mSensorManager!!.unregisterListener(this)
                saveInDatabase()
            }
            else -> {
                finish()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent) {
        val datum = SensorData(event.values[X_COORDINATE].toDouble(), event.values[Y_COORDINATE].toDouble(),
            event.values[Z_COORDINATE].toDouble(), DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
            event.sensor.name, sessionID!!)
        dataArray.add(datum)
        numberDatum++
        if ( numberDatum % MAX_DATUM_TO_SEND == ZERO) {
            saveInDatabase()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        playerType =  data!!.getStringExtra(PLAYER_TYPE)!!
        hitType =  data!!.getStringExtra(HIT_TYPE)!!
        gender = data!!.getStringExtra(GENDER)!!
    }

    private fun saveInDatabase() {
        val hashMap = hashMapOf(
            "entries" to dataArray.map { jacksonMapper.convertValue(it, Map::class.java) },
            "playerType" to playerType,
            "hitType" to hitType,
            "gender" to gender,
            "numEntries" to numberDatum
        )
        dataArray.clear()
        numberDatum = INITIAL_DATUM
        firebase.collection("sensorData").add(hashMap)
    }

    companion object Constants {
        const val PLAYER_TYPE = "playerType"
        const val HIT_TYPE = "hitType"
        const val GENDER = "gender"
        const val NONE = "none"
        const val CONFIGURATION = 1
        const val INITIAL_DATUM = 0
        const val INITIAL_CLICK = 0
        const val MAX_DATUM_TO_SEND = 10000
        const val SESSION_ID_MIN = 100000
        const val SESSION_ID_MAX = 999999
        const val FIRST_CLICK = 1
        const val SECOND_CLICK = 2
        const val X_COORDINATE = 0
        const val Y_COORDINATE = 1
        const val Z_COORDINATE = 2
        const val EMPTY = ""
        const val ZERO = 0
    }


}