// 배너 광고 컴포저블 — AdMob AdView를 Compose에서 래핑
package com.deitrihi.rummitimer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun BannerAd(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Column(modifier = modifier.navigationBarsPadding()) {
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = {
                AdView(it).apply {
                    setAdSize(AdSize.BANNER)
                    adUnitId = context.getString(R.string.admob_banner_unit_id)
                    loadAd(AdMobPolicy.createAdRequest())
                }
            }
        )
    }
}
