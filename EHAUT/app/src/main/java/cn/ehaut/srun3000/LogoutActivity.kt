package cn.ehaut.srun3000

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_logout.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.schedule

class LogoutActivity : AppCompatActivity() {
    private val TAG = "LogoutActivity"
    private var time:Int = 0
    private var signal:Boolean = false

    override fun onPause() {
        super.onPause()
        signal = false
    }

    private fun handleGetFinished() {
        handler?.removeCallbacksAndMessages(null)
        if(OnlineInfo.networkIsConnect && OnlineInfo.isOnline) {
            //网络连接成功，且在线，留在本页，提示重新获取状态成功
            setDisplay()
            Toast.makeText(baseContext, "获取状态成功！", Toast.LENGTH_LONG).show()
        } else if(!OnlineInfo.networkIsConnect) {
            //网络连接错误，提示网络错误，跳转到登陆页
            signal = false
            startActivity(Intent(this, LoginActivity::class.java))
        } else if (OnlineInfo.networkIsConnect && !OnlineInfo.isOnline) {
            //网络连接成功，且不在线，跳转到登录页
            signal = false
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun handlePostFinished() {
        handler?.removeCallbacksAndMessages(null)
        if(PostResult.networkIsConnect) {
            if(PostResult.isLogoutOK)   {
                btn_logout.isEnabled = true
                signal = false
                startActivity(Intent(this, LoginActivity::class.java))
            } else {
                if(PostResult.result.contains("You are not online.")) {
                    btn_logout.isEnabled = true
                    signal = false
                    startActivity(Intent(this, LoginActivity::class.java))
                } else {
                    Toast.makeText(baseContext, PostResult.result, Toast.LENGTH_LONG).show()
                    btn_logout.isEnabled = true
                }
            }
        } else {
            btn_logout.isEnabled = true
            signal = false
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        if(PostResult.isLoginOK) {
            PostResult.isLoginOK = false
            PostResult.networkIsConnect = false
            PostResult.isLogoutOK = false
            PostResult.networkIsConnect = false
            Network.getUserInfo()
            Toast.makeText(baseContext, "登录成功！", Toast.LENGTH_LONG).show()
        } else {
            Network.getUserInfo()
        }
    }

    //
    @SuppressLint("SimpleDateFormat")
    private fun setDisplay() {
        onlinename.text = OnlineInfo.onlineUsername
        onlineip.text = OnlineInfo.onlineIp
        useddata.text = OnlineInfo.usedData

        signal = true
        handler?.sendEmptyMessage(3)
    }

    private fun reresh() {
        time += 1
        var hour = (time/3600) as Int
        val minute = (time / 60) as Int % 60
        val second = (time % 60) as Int
        val show:String = hour.toString() + " 小时 " + minute.toString() + " 分 " + second + " 秒"
        usedtime.setText(show)
        if(signal) {
            handler?.sendEmptyMessageDelayed(3, 1000);
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logout)
        link_service2.setOnClickListener{
            startActivity(Intent(this, WebActivity::class.java))
        }
        btn_logout.setOnClickListener {
            logout()
        }
        handler = @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message?) {
                super.handleMessage(msg)
                Log.d("msg",msg.toString())
                when (msg!!.what) {
                    1 -> handleGetFinished()
                    2 -> handlePostFinished()
                    3 -> reresh()
                }
            }
        }
        time = OnlineInfo.usedTime
    }

    private fun logout() {
        btn_logout.isEnabled = false
        Log.d(TAG, "Logout")
        Network.logout(OnlineInfo.onlineUsername)
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}

