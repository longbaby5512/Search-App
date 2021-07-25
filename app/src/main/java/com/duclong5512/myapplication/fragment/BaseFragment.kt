package com.duclong5512.myapplication.fragment

import androidx.fragment.app.Fragment

abstract class  BaseFragment : Fragment() {
    open fun onBackPressed(): Boolean {
        return false
    }
}