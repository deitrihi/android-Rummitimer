// 배너 광고 컴포저블 — AdMob AdView를 Compose에서 래핑
package com.deitrihi.rummitimer

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun BannerAd(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    AndroidView(
        modifier = modifier,
        factory = {
            AdView(it).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = context.getString(R.string.admob_banner_unit_id)
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}
