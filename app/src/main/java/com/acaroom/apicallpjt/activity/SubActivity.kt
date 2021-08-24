package com.acaroom.apicallpjt.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.acaroom.apicallpjt.R
import kotlinx.android.synthetic.main.activity_sub.*

class SubActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub)

        btnClose.setOnClickListener {
            finish()
        }
    }
}