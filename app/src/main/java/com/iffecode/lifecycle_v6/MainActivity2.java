package com.iffecode.lifecycle_v6;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity2 extends AppCompatActivity
implements SensorEventListener {

    private TextView stepCountText;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private long lastStepTime;
    private Sensor stepCounter;
    private boolean usingStepCounter = false;
    private int steps = 0;
    private float lastMagnitude = 0f;
    private TextView heightText;
    private TextView weightText;
    private TextView genderText;
    private TextView dateOfBirthText;
    private TextView bmiText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);

        stepCountText = findViewById(R.id.textView14);

        sensorManager =
                (SensorManager) getSystemService(SENSOR_SERVICE);
        stepCounter =
                sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        accelerometer =
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if (stepCounter != null) {

            usingStepCounter = true;

            stepCountText.setText("0");

        } else {

            usingStepCounter = false;

            if (accelerometer != null) {

                stepCountText.setText("0");

            } else {

                stepCountText.setText(
                        "Step Counter and Accelerometer not supported"
                );
            }
        }






        heightText = findViewById(R.id.textView19);
        weightText = findViewById(R.id.textView20);
        genderText = findViewById(R.id.textView21);
        dateOfBirthText = findViewById(R.id.textView23);
        bmiText = findViewById(R.id.textView25);

        Button edit = findViewById(R.id.UpdateBtn);

        FrameLayout fragmentContainer = findViewById(R.id.fragmentContainer);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fragmentContainer.setVisibility(View.VISIBLE);


                EditProfileFragment fragment = new EditProfileFragment();

                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                                fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loadProfileData();
    }

    private void loadProfileData(){
        SharedPreferences preferences =
                getSharedPreferences("profile", MODE_PRIVATE);


        String height =
                preferences.getString("height", "");

        String weight =
                preferences.getString("weight", "");

        String gender =
                preferences.getString("gender", "");

        String dateOfBirth =
                preferences.getString("dateOfBirth", "");


        if (!height.isEmpty()) {

            heightText.setText(height + " cm");

        } else {

            heightText.setText("Not set");
        }


        if (!weight.isEmpty()) {

            weightText.setText(weight + " kg");

        } else {

            weightText.setText("Not set");
        }

        if (!gender.isEmpty()) {

            genderText.setText(gender);

        } else {

            genderText.setText("Not set");
        }

        if (!dateOfBirth.isEmpty()) {

            dateOfBirthText.setText(dateOfBirth);

        } else {

            dateOfBirthText.setText("Not set");
        }

        calculateBMI(height, weight);
    }

    private void calculateBMI(String height, String weight){

        if (height.isEmpty() || weight.isEmpty()) {

            bmiText.setText("Not set");

            return;
        }


        try {

            double heightCm =
                    Double.parseDouble(height);

            double weightKg =
                    Double.parseDouble(weight);


            double heightMeters =
                    heightCm / 100;


            double bmi =
                    weightKg /
                            (heightMeters * heightMeters);


            // Visa BMI med en decimal
            bmiText.setText(
                    String.format("%.1f", bmi)
            );


        } catch (NumberFormatException e) {

            bmiText.setText("Not set");
        }
    }

    @Override
    protected void onResume() {

        super.onResume();

        loadProfileData();

        if (usingStepCounter && stepCounter != null) {

            sensorManager.registerListener(
                    this,
                    stepCounter,
                    SensorManager.SENSOR_DELAY_NORMAL
            );

        } else if (accelerometer != null) {

            sensorManager.registerListener(
                    this,
                    accelerometer,
                    SensorManager.SENSOR_DELAY_GAME
            );
        }


    }

    @Override
    protected void onPause() {
        super.onPause();

        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {

            int totalSteps = (int) event.values[0];

            stepCountText.setText(
                    String.valueOf(totalSteps)
            );

        }

        else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            float magnitude =
                    (float) Math.sqrt(
                            x * x +
                                    y * y +
                                    z * z
                    );

            if (magnitude - lastMagnitude > 2.0f) {

                long currentTime = System.currentTimeMillis();

                if (currentTime - lastStepTime > 500) {

                    steps++;

                    stepCountText.setText(
                            String.valueOf(steps)
                    );

                    lastStepTime = currentTime;
                }
            }
            lastMagnitude = magnitude;
        }
    }
}