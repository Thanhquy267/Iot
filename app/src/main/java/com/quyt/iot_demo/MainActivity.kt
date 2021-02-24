package com.quyt.iot_demo

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.quyt.iot_demo.databinding.ActivityMainBinding

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : AppCompatActivity() {
    lateinit var mLayoutBinding : ActivityMainBinding
    private var mStatusState = false
    private var mWattState = false
    private var mMotionState = false
    private var mRemoteState = false
    private var mNightState = false
    private var mTimerState = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        mLayoutBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val param = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 50)
        param.gravity = Gravity.BOTTOM

        mLayoutBinding.cvProgress.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    return@setOnTouchListener true
                }
                MotionEvent.ACTION_DOWN -> {
                    return@setOnTouchListener true
                }
                MotionEvent.ACTION_MOVE -> {
                    val height = mLayoutBinding.cvProgress.height - event.y.toInt()
                    if (height > mLayoutBinding.cvProgress.height || height < 0) return@setOnTouchListener true
                    param.height = height
                    val value = height / (mLayoutBinding.cvProgress.height/100)
                    val valueWithStep = value - (value  % 10)
                    mLayoutBinding.tvProgress.text = valueWithStep.toString()
                    mLayoutBinding.rlProgress.layoutParams = param
                    return@setOnTouchListener true
                }
                else -> {
                    return@setOnTouchListener true
                }
            }
        }
        buttonState()
    }

    private fun buttonState(){
        mLayoutBinding.cvStatus.setOnClickListener {
            mStatusState = !mStatusState
            mLayoutBinding.llStatus.setBackgroundColor(
                ContextCompat.getColor(this
                , if (mStatusState) R.color.red54 else R.color.blue31))
            mLayoutBinding.tvStatus.text = if (mStatusState) "ON" else "OFF"
        }
        mLayoutBinding.cvMotion.setOnClickListener {
            mMotionState =  !mMotionState
            mLayoutBinding.llMotion.setBackgroundColor(
                ContextCompat.getColor(this
                , if (mMotionState) R.color.red54 else R.color.blue31))
            mLayoutBinding.tvMotion.text = if (mMotionState) "ON" else "OFF"
        }
        mLayoutBinding.cvRemote.setOnClickListener {
            mRemoteState =  !mRemoteState
            mLayoutBinding.llRemote.setBackgroundColor(
                ContextCompat.getColor(this
                , if (mRemoteState) R.color.red54 else R.color.blue31))
            mLayoutBinding.tvRemote.text = if (mRemoteState) "ON" else "OFF"
        }
        mLayoutBinding.cvNight.setOnClickListener {
            mNightState =  !mNightState
            mLayoutBinding.llNight.setBackgroundColor(
                ContextCompat.getColor(this
                , if (mNightState) R.color.red54 else R.color.blue31))
            mLayoutBinding.tvNight.text = if (mNightState) "ON" else "OFF"
        }
        mLayoutBinding.cvTimer.setOnClickListener {
            mTimerState =  !mTimerState
            mLayoutBinding.llTimer.setBackgroundColor(
                ContextCompat.getColor(this
                , if (mTimerState) R.color.red54 else R.color.blue31))
            mLayoutBinding.tvTimer.text = if (mTimerState) "ON" else "OFF"
        }

        mLayoutBinding.cvStatus.setOnLongClickListener {
            Toast.makeText(this,"LongCLick",Toast.LENGTH_SHORT).show()
            return@setOnLongClickListener true
        }
        mLayoutBinding.cvTimer.setOnLongClickListener {
            val timerDialog = TimerDialog()
            timerDialog.show(supportFragmentManager,"timerDialog")
            return@setOnLongClickListener true
        }
    }
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//        // Write a message to the database
//
//        // Write a message to the database
//        val database = FirebaseDatabase.getInstance()
//        val myRef = database.getReference("LED_VALUE")
//
//        val sb = findViewById<SeekBar>(R.id.sb)
//        val tv = findViewById<TextView>(R.id.tv_value)
//        val btn = findViewById<AppCompatButton>(R.id.btn)
//        sb.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
//            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
//                tv.text = progress.toString()
//                myRef.setValue(progress)
//            }
//
//            override fun onStartTrackingTouch(seekBar: SeekBar?) {
//            }
//
//            override fun onStopTrackingTouch(seekBar: SeekBar?) {
//
//            }
//
//        })
//        //
//        val postListener = object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                // Get Post object and use the values to update the UI
//                val value = dataSnapshot.value.toString()
//                if (value == "ON"){
//                    createNotification()
//                }
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                // Getting Post failed, log a message
//                Toast.makeText(this@MainActivity,databaseError.toException().toString(),Toast.LENGTH_SHORT).show()
//            }
//        }
//        database.getReference("LIGHT_VALUE").addValueEventListener(postListener)
//        btn.setOnClickListener {
//            createNotification()
//        }
//    }
//
//
//    fun createNotification(){
//        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//        val audioAttributes = AudioAttributes.Builder()
//            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
//            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
//            .build()
//        val notifyChanel = NotificationChannel("123", "test_chanel",NotificationManager.IMPORTANCE_HIGH)
//        notifyChanel.setSound(defaultSound, null)
//        notifyChanel.enableVibration(true)
//        notificationManager.createNotificationChannel(notifyChanel)
//        //
//        val builder = NotificationCompat.Builder(this,"123")
//        builder.setSmallIcon(R.drawable.logo_2)
//            .setContentTitle(title)
//            .setContentText("Trời tối rồi")
//            .setAutoCancel(true)
//            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//        val notification = builder.build()
//        notificationManager.notify(123, notification)
//    }
}