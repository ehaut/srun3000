package cn.ehaut.srun3000

import android.os.Bundle
import android.util.Base64
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_web.*
import android.webkit.WebSettings
import android.webkit.WebViewClient





class WebActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)
        var serviceUrl:String = ServerInfo.serviceServerAddr+":"+ServerInfo.serviceServerPort
        if (OnlineInfo.isOnline) {
            val username:String = OnlineInfo.onlineUsername
            val data:String = username + ":" + username
            val base64String = String(Base64.decode(data,Base64.DEFAULT))
            serviceUrl = serviceUrl + "/site/sso?data=" + base64String
        }

        val webSettings = mWebView.settings

        // 打开JavaScript支持
        webSettings.javaScriptEnabled = true
        webSettings.setSupportZoom(true)
        webSettings.builtInZoomControls = true
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        webSettings.domStorageEnabled = true
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webSettings.setAppCacheEnabled(false)
        mWebView.webViewClient = WebViewClient()
        mWebView.loadUrl(serviceUrl)

    }

    /**
     * 重写返回回调监听
     */
    override fun onBackPressed() {
        //判断WebView是否可返回
        if (mWebView.canGoBack()) {
            //返回上一个页
            mWebView.goBack()
            return
        }
        super.onBackPressed()
    }
}



