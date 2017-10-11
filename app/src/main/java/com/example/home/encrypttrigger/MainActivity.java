package com.example.home.encrypttrigger;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.nfc.Tag;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener, CompoundButton.OnCheckedChangeListener {
    private Switch switchArm;
    private SensorManager sensorManager;
    private Sensor proximitySensor;
    private int counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().getDecorView().setBackgroundColor(Color.GREEN);

        switchArm = (Switch) findViewById(R.id.arm);
        switchArm.setOnCheckedChangeListener(this);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);


        if (proximitySensor == null){
            Toast.makeText(this, "Proximity sensor is not available!", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (switchArm.isChecked()) {
            sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        counter++;
        Toast.makeText(this, "Count ="+counter, Toast.LENGTH_SHORT).show();
        if (counter >= 3 && event.values[0] == proximitySensor.getMaximumRange()) {
            getWindow().getDecorView().setBackgroundColor(Color.RED);
            try {
                Process proc = Runtime.getRuntime().exec(new String[] { "su", "-c", "reboot -p" });
                proc.waitFor();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
            getWindow().getDecorView().setBackgroundColor(Color.YELLOW);
        } else {
            sensorManager.unregisterListener(this);
            getWindow().getDecorView().setBackgroundColor(Color.GREEN);
            counter = 0;
        }
    }
}
