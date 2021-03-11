package com.quyt.iot_demo.ui

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.firebase.database.FirebaseDatabase
import com.quyt.iot_demo.R
import com.quyt.iot_demo.TimerDialog
import com.quyt.iot_demo.databinding.ActivityHomeBinding
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
    private lateinit var mRoom : RoomType

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        mLayoutBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mRoom = intent.extras?.get("type") as RoomType
        setActionBar(mRoom.title)
//        Toast.makeText(this,intent.get("type",1).toString(),Toast.LENGTH_SHORT).show()
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
                    mDatabase.getReference(mRoom.key).child("brightness").setValue(heightProgressToVal(height))
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
    }

    private fun setActionBar(title : String){
        mLayoutBinding.tvActionTitle.text = title
        mLayoutBinding.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun heightProgressToVal(height : Int) : Int{
        val value = height / (mLayoutBinding.cvProgress.height/100)
        return value - (value  % 10)
    }

    private fun valToHeightProgress(value : Int) : Int{
        return value*8
    }

//    private fun listenLedValue(param : FrameLayout.LayoutParams){
//        val postListener = object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                // Get Post object and use the values to update the UI
//                val value = dataSnapshot.value as Long
//                param.height = valToHeightProgress(value.toInt())
//                mLayoutBinding.tvProgress.text = value.toString()
//                mLayoutBinding.rlProgress.layoutParams = param
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                // Getting Post failed, log a message
//                Toast.makeText(this@MainActivity,databaseError.toException().toString(),Toast.LENGTH_SHORT).show()
//            }
//        }
//        mDatabase.getReference("ledValue").addValueEventListener(postListener)
//    }

    private fun buttonState(){
        mLayoutBinding.cvStatus.setOnClickListener {
            mStatusState = !mStatusState
            mLayoutBinding.llStatus.setBackgroundColor(
                ContextCompat.getColor(this
                , if (mStatusState) R.color.red54 else R.color.blue31))
            mLayoutBinding.tvStatus.text = if (mStatusState) "ON" else "OFF"
            mDatabase.getReference(mRoom.key).child("state").setValue(if (mStatusState) "ON" else "OFF")
        }
        //
        mLayoutBinding.cvMotion.setOnClickListener {
            mMotionState =  !mMotionState
            mLayoutBinding.llMotion.setBackgroundColor(
                ContextCompat.getColor(this
                , if (mMotionState) R.color.red54 else R.color.blue31))
            mLayoutBinding.tvMotion.text = if (mMotionState) "ON" else "OFF"
            mDatabase.getReference(mRoom.key).child("motionState").setValue(if (mStatusState) "ON" else "OFF")
        }
        //
        mLayoutBinding.cvRemote.setOnClickListener {
            mRemoteState =  !mRemoteState
            mLayoutBinding.llRemote.setBackgroundColor(
                ContextCompat.getColor(this
                , if (mRemoteState) R.color.red54 else R.color.blue31))
            mLayoutBinding.tvRemote.text = if (mRemoteState) "ON" else "OFF"
            mDatabase.getReference(mRoom.key).child("remoteState").setValue(if (mStatusState) "ON" else "OFF")
        }
        //
        mLayoutBinding.cvNight.setOnClickListener {
            mNightState =  !mNightState
            mLayoutBinding.llNight.setBackgroundColor(
                ContextCompat.getColor(this
                , if (mNightState) R.color.red54 else R.color.blue31))
            mLayoutBinding.tvNight.text = if (mNightState) "ON" else "OFF"
            mDatabase.getReference(mRoom.key).child("nightModeState").setValue(if (mStatusState) "ON" else "OFF")
        }
        //
        mLayoutBinding.cvTimer.setOnClickListener {
            mTimerState =  !mTimerState
            mLayoutBinding.llTimer.setBackgroundColor(
                ContextCompat.getColor(this
                , if (mTimerState) R.color.red54 else R.color.blue31))
            mLayoutBinding.tvTimer.text = if (mTimerState) "ON" else "OFF"
            mDatabase.getReference(mRoom.key).child("timerState").setValue(if (mStatusState) "ON" else "OFF")
        }
        //
        mLayoutBinding.cvStatus.setOnLongClickListener {
            Toast.makeText(this,"LongCLick",Toast.LENGTH_SHORT).show()
            return@setOnLongClickListener true
        }
//        mLayoutBinding.cvTimer.setOnLongClickListener {
//            val timerDialog = TimerDialog()
//            timerDialog.show(supportFragmentManager,"timerDialog")
//            return@setOnLongClickListener true
//        }
    }

}

enum class RoomType(val value: Int,val key: String,val title : String){
    LIVING_ROOM(0,"livingRoom","Phòng khách"),
    KITCHEN(1,"kitchen","Phòng ăn"),
    BATHROOM(2,"bathroom","Phòng tắm"),
    BEDROOM(3,"bedroom","Phòng ngủ")
}