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

class MainActivity : AppCompatActivity() {

    companion object {
        private const val ACTION_MY_BROADCAST = "com.example.firstandroidapp.MY_ACTION"
        private const val REQ_POST_NOTIFICATIONS = 1001
        private const val CUSTOM_PERMISSION = "com.example.firstandroidapp.MSE712"
        private const val REQ_MSE712 = 1002
    }

    private lateinit var receiver: MyBroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        receiver = MyBroadcastReceiver()
        requestNotificationPermissionIfNeeded()
        requestMSE712PermissionIfNeeded()

        findViewById<Button>(R.id.button).setOnClickListener {
            openSecondActivityExplicit()
        }

        findViewById<Button>(R.id.button2).setOnClickListener {
            openSecondActivityImplicit()
        }

        findViewById<Button>(R.id.btnStartService).setOnClickListener {
            if (!canPostNotifications()) {
                Toast.makeText(this, "Enable notifications first, then try again", Toast.LENGTH_LONG).show()
                requestNotificationPermissionIfNeeded()
                return@setOnClickListener
            }

            val serviceIntent = Intent(this, MyForegroundService::class.java)
            ContextCompat.startForegroundService(this, serviceIntent)
        }

        findViewById<Button>(R.id.btnSendBroadcast).setOnClickListener {
            val intent = Intent(ACTION_MY_BROADCAST).apply {
                setPackage(packageName)
            }
            sendBroadcast(intent)
        }

        findViewById<Button>(R.id.btnViewImageActivity).setOnClickListener {
            val intent = Intent(this, ThirdActivity::class.java)
            startActivity(intent)
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(ACTION_MY_BROADCAST)

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

    private fun requestMSE712PermissionIfNeeded() {
        if (ContextCompat.checkSelfPermission(
                this,
                CUSTOM_PERMISSION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(CUSTOM_PERMISSION),
                REQ_MSE712
            )
        }
    }

    private fun hasMSE712Permission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            CUSTOM_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun openSecondActivityExplicit() {
        if (hasMSE712Permission()) {
            startActivity(Intent(this, MainActivity2::class.java))
        } else {
            requestMSE712PermissionIfNeeded()
        }
    }

    private fun openSecondActivityImplicit() {
        if (hasMSE712Permission()) {
            startActivity(Intent("com.example.firstandroidapp.OPEN_SECOND"))
        } else {
            requestMSE712PermissionIfNeeded()
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