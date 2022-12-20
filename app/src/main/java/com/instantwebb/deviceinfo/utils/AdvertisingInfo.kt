package com.instantwebb.deviceinfo.utils

import android.content.Context
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AdvertisingInfo(val context: Context) {

    private val adInfo = AdvertisingIdClient(context.applicationContext)

    suspend fun getAdvertisingId(): String =
        withContext(Dispatchers.IO) {
            //Connect with start(), disconnect with finish()
            adInfo.start()
            val adIdInfo = adInfo.info
//            adInfo.finish()
            adIdInfo.id!!
        }
}