package com.example.myphotoview

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class VolleySinglerton private  constructor(context: Context){
    companion object{
        private var INSTANCE : VolleySinglerton?=null
        fun getInstance (context: Context) = INSTANCE?:synchronized(this){
            VolleySinglerton(context).also { INSTANCE=it }
        }
    }
    val requestQueue:RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }
}