package aodintsov.to_do_list.view

import android.content.Context
import androidx.compose.runtime.*
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import aodintsov.to_do_list.AdConfig

@Composable
fun InterstitialAdView(context: Context, onAdLoaded: (InterstitialAd) -> Unit) {
    var interstitialAd by remember { mutableStateOf<InterstitialAd?>(null) }

    LaunchedEffect(Unit) {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(context, AdConfig.INTERSTITIAL_AD_UNIT_ID, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: InterstitialAd) {
                interstitialAd = ad
                onAdLoaded(ad)
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                interstitialAd = null
            }
        })
    }
}
