package cn.ehaut.srun3000

import android.os.Bundle
import android.os.Message
import android.util.Base64
import android.util.Log
import android.webkit.WebChromeClient
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_web.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import android.webkit.WebView
import android.webkit.WebViewClient


class WebActivity : AppCompatActivity() {
    private fun md5Encode(text: String): String {
        try {
            //获取md5加密对象
            val instance: MessageDigest = MessageDigest.getInstance("MD5")
            //对字符串加密，返回字节数组
            val digest:ByteArray = instance.digest(text.toByteArray())
            var sb : StringBuffer = StringBuffer()
            for (b in digest) {
                //获取低八位有效值
                var i :Int = b.toInt() and 0xff
                //将整数转化为16进制
                var hexString = Integer.toHexString(i)
                if (hexString.length < 2) {
                    //如果是一位的话，补0
                    hexString = "0" + hexString
                }
                sb.append(hexString)
            }
            return sb.toString()

        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)
        var url:String = ""
        var serviceUrl:String = ""
        if (OnlineInfo.isOnline) {
            serviceUrl = ServerInfo.serviceServerAddr+":"+ServerInfo.serviceServerPort
            val username:String = OnlineInfo.onlineUsername
            val data:String = username + ":" + username
            val base64String = String(Base64.encode(data.toByteArray(),Base64.DEFAULT))
            serviceUrl = serviceUrl + "/site/sso?data=" + base64String
            url = serviceUrl
        } else {
            serviceUrl = ServerInfo.serviceServerAddr+":"+ServerInfo.serviceServerPort
            val username:String  = UserInfo.username
            val password:String = UserInfo.password
            var data:String = ""
            if (!username.isNullOrBlank()) {
                if (!password.isNullOrBlank()) {
                    val pwd:String = md5Encode(password)
                    data = username+":"+pwd
                    val base64String = String(Base64.encode(data.toByteArray(),Base64.DEFAULT))
                    serviceUrl = serviceUrl + "/site/sso?data=" + base64String
                    url = serviceUrl
                } else {
                    url = "file:///android_asset/web/service.html"
                }
            } else {
                url = "file:///android_asset/web/service.html"
            }
        }

        val webSettings = mWebView.settings
        webSettings.setSupportMultipleWindows(true);
        // 打开JavaScript支持
        webSettings.javaScriptEnabled = true
        webSettings.setSupportZoom(true)
        webSettings.builtInZoomControls = true
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        webSettings.domStorageEnabled = true
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webSettings.setAppCacheEnabled(false)
        mWebView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url)
                return true
            }
        }
        mWebView.webChromeClient = object : WebChromeClient() {
            override fun onCloseWindow(window: WebView) {
                super.onCloseWindow(window)
            }

            override fun onCreateWindow(
                view: WebView, isDialog: Boolean,
                isUserGesture: Boolean, resultMsg: Message
            ): Boolean {

                val childView = WebView(this@WebActivity)
                childView.webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                        mWebView.loadUrl(url)
                        return true
                    }
                }
                val settings = childView.settings
                settings.javaScriptEnabled = true
                childView.webChromeClient = this
                val transport = resultMsg.obj as WebView.WebViewTransport
                transport.webView = childView
                resultMsg.sendToTarget()
                return true
            }
        }
        //Log.d("url",url)
        mWebView.loadUrl(url)

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

    override fun onDestroy() {
        super.onDestroy()
        mWebView?.clearCache(true)
        mWebView?.stopLoading()
        mWebView?.setWebViewClient(null)
        mWebView?.setWebChromeClient(null)
        mWebView?.removeAllViews()
        mWebView?.destroy()
    }
}



