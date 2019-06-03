package cn.ehaut.srun3000

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_logout.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.timerTask

class LogoutActivity : AppCompatActivity() {
    private val TAG = "LogoutActivity"
    private var time:Long = 0
    private var timer: Timer? = null
    private var task: TimerTask? = null
    private var sdf:SimpleDateFormat? = null

    override fun onPause() {
        super.onPause()
        stopTimer()
    }

    override fun onResume() {
        super.onResume()
        if(PostResult.isLoginOK) {
            PostResult.isLoginOK = false
            PostResult.networkIsConnect = false
            PostResult.isLogoutOK = false
            Network.getUserInfo()
            setDisplay()
            Toast.makeText(baseContext, "登录成功！", Toast.LENGTH_LONG).show()
        } else {
            Network.getUserInfo()
            if(OnlineInfo.networkIsConnect && OnlineInfo.isOnline) {
                //网络连接成功，且在线，留在本页，提示重新获取状态成功
                setDisplay()
                Toast.makeText(baseContext, "获取状态成功！", Toast.LENGTH_LONG).show()
            } else if(!OnlineInfo.networkIsConnect) {
                //网络连接错误，提示网络错误，跳转到登陆页
                stopTimer()
                startActivity(Intent(this, LoginActivity::class.java))
            } else if (OnlineInfo.networkIsConnect && !OnlineInfo.isOnline) {
                //网络连接成功，且不在线，跳转到登录页
                stopTimer()
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }
    }

    // 停止定时器
    private fun stopTimer() {
        if (timer != null) {
            timer!!.cancel()
            // 一定设置为null，否则定时器不会被回收
            timer = null
        }
    }

    private fun setDisplay() {
        onlinename.text = OnlineInfo.onlineUsername
        onlineip.text = OnlineInfo.onlineIp
        useddata.text = OnlineInfo.usedData
        sdf = SimpleDateFormat("HH 小时 mm 分 ss 秒")
        time = OnlineInfo.usedTime

        task = timerTask {
            usedtime.text = sdf!!.format(Date(time))
            time++
        }
        timer = Timer()
        timer!!.schedule(task, 1000)
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
    }

    private fun logout() {
        btn_logout.isEnabled = false
        Log.d(TAG, "Logout")
        val response = Network.logout(OnlineInfo.onlineUsername)
        Log.d(TAG, response)
        if(PostResult.networkIsConnect) {
            if(PostResult.isLogoutOK)   {
                btn_logout.isEnabled = true
                stopTimer()
                startActivity(Intent(this, LoginActivity::class.java))
            } else {
                if(response.contains("You are not online.")) {
                    btn_logout.isEnabled = true
                    stopTimer()
                    startActivity(Intent(this, LoginActivity::class.java))
                } else {
                    Toast.makeText(baseContext, response, Toast.LENGTH_LONG).show()
                    btn_logout.isEnabled = true
                }
            }
        } else {
            btn_logout.isEnabled = true
            stopTimer()
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

}

