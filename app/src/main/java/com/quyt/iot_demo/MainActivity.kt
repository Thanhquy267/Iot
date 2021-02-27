package com.quyt.iot_demo

import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.quyt.iot_demo.Constant.TURN_BULB_ON
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
    private val mDatabase = FirebaseDatabase.getInstance()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        mLayoutBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        //

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
                    mDatabase.getReference("ledValue").setValue(heightProgressToVal(height))
                    mLayoutBinding.tvProgress.text = heightProgressToVal(height).toString()
                    mLayoutBinding.rlProgress.layoutParams = param
                    return@setOnTouchListener true
                }
                else -> {
                    return@setOnTouchListener true
                }
            }
        }
        buttonState()
        listenLedValue(param)
//        scheduleTimerJob(this,10000)
    }

    private fun heightProgressToVal(height : Int) : Int{
        val value = height / (mLayoutBinding.cvProgress.height/100)
        return value - (value  % 10)
    }

    private fun valToHeightProgress(value : Int) : Int{
        return value*8
    }

    private fun listenLedValue(param : FrameLayout.LayoutParams){
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                val value = dataSnapshot.value as Long
                param.height = valToHeightProgress(value.toInt())
                mLayoutBinding.tvProgress.text = value.toString()
                mLayoutBinding.rlProgress.layoutParams = param
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Toast.makeText(this@MainActivity,databaseError.toException().toString(),Toast.LENGTH_SHORT).show()
            }
        }
        mDatabase.getReference("ledValue").addValueEventListener(postListener)
    }

    private fun buttonState(){
        mLayoutBinding.cvStatus.setOnClickListener {
            mStatusState = !mStatusState
            mLayoutBinding.llStatus.setBackgroundColor(
                ContextCompat.getColor(this
                , if (mStatusState) R.color.red54 else R.color.blue31))
            mLayoutBinding.tvStatus.text = if (mStatusState) "ON" else "OFF"
            mDatabase.getReference("ledState").setValue(if (mStatusState) "ON" else "OFF")
        }
        //
        mLayoutBinding.cvMotion.setOnClickListener {
            mMotionState =  !mMotionState
            mLayoutBinding.llMotion.setBackgroundColor(
                ContextCompat.getColor(this
                , if (mMotionState) R.color.red54 else R.color.blue31))
            mLayoutBinding.tvMotion.text = if (mMotionState) "ON" else "OFF"
            mDatabase.getReference("motionState").setValue(if (mStatusState) "ON" else "OFF")
        }
        //
        mLayoutBinding.cvRemote.setOnClickListener {
            mRemoteState =  !mRemoteState
            mLayoutBinding.llRemote.setBackgroundColor(
                ContextCompat.getColor(this
                , if (mRemoteState) R.color.red54 else R.color.blue31))
            mLayoutBinding.tvRemote.text = if (mRemoteState) "ON" else "OFF"
            mDatabase.getReference("remoteState").setValue(if (mStatusState) "ON" else "OFF")
        }
        //
        mLayoutBinding.cvNight.setOnClickListener {
            mNightState =  !mNightState
            mLayoutBinding.llNight.setBackgroundColor(
                ContextCompat.getColor(this
                , if (mNightState) R.color.red54 else R.color.blue31))
            mLayoutBinding.tvNight.text = if (mNightState) "ON" else "OFF"
            mDatabase.getReference("nightModeState").setValue(if (mStatusState) "ON" else "OFF")
        }
        //
        mLayoutBinding.cvTimer.setOnClickListener {
            mTimerState =  !mTimerState
            mLayoutBinding.llTimer.setBackgroundColor(
                ContextCompat.getColor(this
                , if (mTimerState) R.color.red54 else R.color.blue31))
            mLayoutBinding.tvTimer.text = if (mTimerState) "ON" else "OFF"
            mDatabase.getReference("timerState").setValue(if (mStatusState) "ON" else "OFF")
        }
        //
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
}