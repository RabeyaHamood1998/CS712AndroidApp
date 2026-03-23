package com.example.firstandroidapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    companion object {
        private const val ACTION_MY_BROADCAST = "com.example.firstandroidapp.MY_ACTION"
        private const val REQ_POST_NOTIFICATIONS = 1001
    }

    private lateinit var receiver: MyBroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        receiver = MyBroadcastReceiver()
        requestNotificationPermissionIfNeeded()
        // Explicit Activity
        findViewById<Button>(R.id.button).setOnClickListener {
            startActivity(Intent(this, MainActivity2::class.java))
        }

        // Implicit Activity
        findViewById<Button>(R.id.button2).setOnClickListener {
            startActivity(Intent("com.example.firstandroidapp.OPEN_SECOND"))
        }

        // Start Foreground Service
        findViewById<Button>(R.id.btnStartService).setOnClickListener {

            if (!canPostNotifications()) {
                Toast.makeText(this, "Enable notifications first, then try again", Toast.LENGTH_LONG).show()
                requestNotificationPermissionIfNeeded()
                return@setOnClickListener
            }

            val serviceIntent = Intent(this, MyForegroundService::class.java)
            ContextCompat.startForegroundService(this, serviceIntent)
        }

        // Send Broadcast
        findViewById<Button>(R.id.btnSendBroadcast).setOnClickListener {


            val intent = Intent(ACTION_MY_BROADCAST).apply {
                setPackage(packageName)
            }
            sendBroadcast(intent)
        }
        // Open Third Activity (Camera Activity)
        findViewById<Button>(R.id.btnViewImageActivity).setOnClickListener {
            val intent = Intent(this, ThirdActivity::class.java)
            startActivity(intent)
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(ACTION_MY_BROADCAST)

        // API 33+ needs a flag
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.registerReceiver(
                this,
                receiver,
                filter,
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
        } else {
            registerReceiver(receiver, filter)
        }
    }

    override fun onStop() {
        super.onStop()
        try {
            unregisterReceiver(receiver)
        } catch (_: IllegalArgumentException) {
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!granted) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    REQ_POST_NOTIFICATIONS
                )
            }
        }
    }

    private fun canPostNotifications(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else true
    }
}
