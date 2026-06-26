// 가족 정책을 준수하는 AdMob 초기화와 광고 요청을 제공
package com.deitrihi.rummitimer

import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration

object AdMobPolicy {
    @Suppress("DEPRECATION")
    fun initialize(context: Context) {
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder()
                .setTagForChildDirectedTreatment(RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE)
                .setMaxAdContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_G)
                .build()
        )
        MobileAds.initialize(context)
    }

    fun createAdRequest(): AdRequest = AdRequest.Builder().build()
}
