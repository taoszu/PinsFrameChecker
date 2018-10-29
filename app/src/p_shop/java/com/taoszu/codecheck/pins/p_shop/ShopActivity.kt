package com.taoszu.codecheck.pins.p_shop

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.taoszu.codecheck.pins.R
import com.taoszu.codecheck.pins.p_pay.PayActivity

class ShopActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_shop)

        val pay = PayActivity()
    }
}
