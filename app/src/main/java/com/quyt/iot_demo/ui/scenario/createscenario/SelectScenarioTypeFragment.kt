package com.quyt.iot_demo.ui.scenario.createscenario

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.quyt.iot_demo.R
import com.quyt.iot_demo.databinding.FragmentSelectScenarioTypeBinding
import com.quyt.iot_demo.ui.scenario.createscenario.devicetype.ChooseDeviceFragment
import com.quyt.iot_demo.ui.scenario.createscenario.timetype.ChooseTimeFragment

class SelectScenarioTypeFragment : Fragment() {
    lateinit var mLayoutBinding: FragmentSelectScenarioTypeBinding
    private lateinit var mActivity: CreateScenarioActivity
    private var mIsInput = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_select_scenario_type, container, false)
        return mLayoutBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBar()
        navigate()
    }

    private fun setupActionBar() {
        mLayoutBinding.ivBack.setOnClickListener {
            mActivity.supportFragmentManager.popBackStack()
        }
    }

    private fun navigate() {
        mLayoutBinding.cvLocation.setOnClickListener {

        }
        mLayoutBinding.cvTime.setOnClickListener {
            val fragmentManager = mActivity.supportFragmentManager
            val ft = fragmentManager.beginTransaction()
            ft.add(
                    R.id.rl_root,
                    ChooseTimeFragment.newInstance(mActivity),
                    ChooseTimeFragment().javaClass.simpleName
            )
            ft.addToBackStack(ChooseTimeFragment().javaClass.simpleName)
            ft.commitAllowingStateLoss()
        }
        mLayoutBinding.cvDevice.setOnClickListener {
            val fragmentManager = mActivity.supportFragmentManager
            val ft = fragmentManager.beginTransaction()
            ft.add(
                    R.id.rl_root,
                    ChooseDeviceFragment.newInstance(mActivity),
                    ChooseDeviceFragment().javaClass.simpleName
            )
            ft.addToBackStack(ChooseDeviceFragment().javaClass.simpleName)
            ft.commitAllowingStateLoss()
        }
    }

    companion object {
        fun newInstance(activity: CreateScenarioActivity, isInput: Boolean = true): SelectScenarioTypeFragment {
            val fragment = SelectScenarioTypeFragment()
            fragment.mActivity = activity
            fragment.mIsInput = isInput
            return fragment
        }
    }

}