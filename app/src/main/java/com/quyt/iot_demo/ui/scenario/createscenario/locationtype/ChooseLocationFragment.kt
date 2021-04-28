package com.quyt.iot_demo.ui.scenario.createscenario.locationtype

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.quyt.iot_demo.R
import com.quyt.iot_demo.databinding.FragmentChooseLocationBinding
import com.quyt.iot_demo.model.Condition
import com.quyt.iot_demo.ui.scenario.createscenario.CreateScenarioActivity
import com.quyt.iot_demo.ui.scenario.createscenario.SelectScenarioTypeFragment

class ChooseLocationFragment : Fragment() {
    lateinit var mLayoutBinding: FragmentChooseLocationBinding
    lateinit var mActivity: CreateScenarioActivity
    private var mIsInput = true
    private var mLocationType = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_choose_location, container, false)
        return mLayoutBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBar()
        mLayoutBinding.rgLocation.setOnCheckedChangeListener { _, checkedId ->
            mLocationType = if (checkedId == R.id.rb_go_home) "Về nhà" else "Rời khỏi nhà"
        }
        mLayoutBinding.cvCreate.setOnClickListener {
            mActivity.addInput(Condition().apply {
                this.type = "location"
                this.location = mLocationType
            })
            val fm = mActivity.supportFragmentManager.findFragmentByTag(SelectScenarioTypeFragment().javaClass.simpleName)
            if (fm != null) {
                mActivity.supportFragmentManager.beginTransaction().remove(fm).commit()
            }
            mActivity.supportFragmentManager.popBackStack()
        }
    }

    private fun setupActionBar() {
        mLayoutBinding.ivBack.setOnClickListener {
            mActivity.supportFragmentManager.popBackStack()
        }
    }

    companion object {
        fun newInstance(activity: CreateScenarioActivity, isInput: Boolean = true): ChooseLocationFragment {
            val fragment = ChooseLocationFragment()
            fragment.mActivity = activity
            fragment.mIsInput = isInput
            return fragment
        }
    }

}