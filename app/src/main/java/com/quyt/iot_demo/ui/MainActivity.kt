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
import com.google.gson.Gson
import com.quyt.iot_demo.R
import com.quyt.iot_demo.TimerDialog
import com.quyt.iot_demo.databinding.ActivityHomeBinding
import com.quyt.iot_demo.databinding.ActivityMainBinding
import com.quyt.iot_demo.model.Device

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : AppCompatActivity() {
    lateinit var mLayoutBinding : ActivityMainBinding
    private var mStatusState = false
    private var mWattState = false
    private var mMotionState = false
    private var mRemoteState = false
    private var mNightState = false
    private var mTimerState = false
    private var mDevice : Device? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        mLayoutBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mDevice = Gson().fromJson(intent.extras?.get("device") as String,Device::class.java)
        setActionBar(mDevice?.name?:"")
//        Toast.makeText(this,intent.get("type",1).toString(),Toast.LENGTH_SHORT).show()
        //

        val param = FrameLayout.LayoutParams(50,ViewGroup.LayoutParams.MATCH_PARENT)
        param.gravity = Gravity.START

        mLayoutBinding.cvProgress.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    return@setOnTouchListener true
                }
                MotionEvent.ACTION_DOWN -> {
                    return@setOnTouchListener true
                }
                MotionEvent.ACTION_MOVE -> {
                    val width = event.x.toInt()
                    if (width > mLayoutBinding.cvProgress.width + 10 || width < 0) return@setOnTouchListener true
                    param.width = width
//                    mDatabase.getReference(mRoom.key).child("brightness").setValue(heightProgressToVal(height))
                    mLayoutBinding.tvProgress.text = heightProgressToVal(width).toString()
                    mLayoutBinding.rlProgress.layoutParams = param
                    return@setOnTouchListener true
                }
                else -> {
                    return@setOnTouchListener true
                }
            }
        }
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


}

enum class RoomType(val value: Int,val key: String,val title : String){
    LIVING_ROOM(0,"livingRoom","Phòng khách"),
    KITCHEN(1,"kitchen","Phòng ăn"),
    BATHROOM(2,"bathroom","Phòng tắm"),
    BEDROOM(3,"bedroom","Phòng ngủ")
}