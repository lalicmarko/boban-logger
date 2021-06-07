package com.sample.bobanloggerlibrary

import android.util.Log

object BobanLogger {

    fun log(s: String) {
        Log.e("BOBAN", "$s")
    }
}