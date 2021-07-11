package com.feryaeldev.djexperience

import android.os.Bundle
import com.feryaeldev.djexperience.base.BaseActivity

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}