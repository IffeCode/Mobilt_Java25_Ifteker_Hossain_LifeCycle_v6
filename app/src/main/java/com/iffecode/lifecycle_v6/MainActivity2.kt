package com.iffecode.lifecycle_v6

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.sqrt

class MainActivity2 : AppCompatActivity(), SensorEventListener {
    private var stepCountText: TextView? = null
    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null
    private var lastStepTime: Long = 0
    private var stepCounter: Sensor? = null
    private var usingStepCounter = false
    private var steps = 0
    private var lastMagnitude = 0f
    private var heightText: TextView? = null
    private var weightText: TextView? = null
    private var genderText: TextView? = null
    private var dateOfBirthText: TextView? = null
    private var bmiText: TextView? = null
    private var nameText: TextView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        setContentView(R.layout.activity_main2)

        stepCountText = findViewById<TextView>(R.id.textView14)

        sensorManager =
            getSystemService(SENSOR_SERVICE) as SensorManager
        stepCounter =
            sensorManager!!.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        accelerometer =
            sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if (stepCounter != null) {
            usingStepCounter = true

            stepCountText!!.setText("0")
        } else {
            usingStepCounter = false

            if (accelerometer != null) {
                stepCountText!!.setText("0")
            } else {
                stepCountText!!.setText(
                    "Step Counter and Accelerometer not supported"
                )
            }
        }






        heightText = findViewById<TextView>(R.id.textView19)
        weightText = findViewById<TextView>(R.id.textView20)
        genderText = findViewById<TextView>(R.id.textView21)
        dateOfBirthText = findViewById<TextView>(R.id.textView23)
        bmiText = findViewById<TextView>(R.id.textView25)
        nameText = findViewById<TextView>(R.id.nameText)

        val edit = findViewById<Button>(R.id.UpdateBtn)

        val fragmentContainer = findViewById<FrameLayout>(R.id.fragmentContainer)

        edit.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                fragmentContainer.setVisibility(View.VISIBLE)


                val fragment = EditProfileFragment()

                getSupportFragmentManager().beginTransaction().replace(
                    R.id.fragmentContainer,
                    fragment
                )
                    .addToBackStack(null)
                    .commit()
            }
        })




        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById<View?>(R.id.main),
            OnApplyWindowInsetsListener { v: View?, insets: WindowInsetsCompat? ->
                val systemBars = insets!!.getInsets(WindowInsetsCompat.Type.systemBars())
                v!!.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            })

        loadProfileData()
    }

    private fun loadProfileData() {
        val preferences =
            getSharedPreferences("profile", MODE_PRIVATE)


        val name: String =
            preferences.getString("name", "")!!

        val height: String =
            preferences.getString("height", "")!!

        val weight: String =
            preferences.getString("weight", "")!!

        val gender: String =
            preferences.getString("gender", "")!!

        val dateOfBirth: String =
            preferences.getString("dateOfBirth", "")!!

        if (!name.isEmpty()) {
            nameText!!.setText(name)
        }else {
            nameText!!.setText("Not set")
        }

        if (!height.isEmpty()) {
            heightText!!.setText(height + " cm")
        } else {
            heightText!!.setText("Not set")
        }


        if (!weight.isEmpty()) {
            weightText!!.setText(weight + " kg")
        } else {
            weightText!!.setText("Not set")
        }

        if (!gender.isEmpty()) {
            genderText!!.setText(gender)
        } else {
            genderText!!.setText("Not set")
        }

        if (!dateOfBirth.isEmpty()) {
            dateOfBirthText!!.setText(dateOfBirth)
        } else {
            dateOfBirthText!!.setText("Not set")
        }

        calculateBMI(height, weight)
    }

    private fun calculateBMI(height: String, weight: String) {
        if (height.isEmpty() || weight.isEmpty()) {
            bmiText!!.setText("Not set")

            return
        }


        try {
            val heightCm = height.toDouble()

            val weightKg = weight.toDouble()


            val heightMeters =
                heightCm / 100


            val bmi =
                weightKg /
                        (heightMeters * heightMeters)


            // Visa BMI med en decimal
            bmiText!!.setText(
                String.format("%.1f", bmi)
            )
        } catch (e: NumberFormatException) {
            bmiText!!.setText("Not set")
        }
    }

    override fun onResume() {
        super.onResume()

        loadProfileData()

        if (usingStepCounter && stepCounter != null) {
            sensorManager!!.registerListener(
                this,
                stepCounter,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        } else if (accelerometer != null) {
            sensorManager!!.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_GAME
            )
        }
    }

    override fun onPause() {
        super.onPause()

        sensorManager!!.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            val totalSteps = event.values[0].toInt()

            stepCountText!!.setText(
                totalSteps.toString()
            )
        } else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val magnitude = sqrt(
                (x * x + y * y + z * z).toDouble()
            ).toFloat()

            if (magnitude - lastMagnitude > 2.0f) {
                val currentTime = System.currentTimeMillis()

                if (currentTime - lastStepTime > 500) {
                    steps++

                    stepCountText!!.setText(
                        steps.toString()
                    )

                    lastStepTime = currentTime
                }
            }
            lastMagnitude = magnitude
        }
    }
}