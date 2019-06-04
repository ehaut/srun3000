package cn.ehaut.srun3000

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*


class  LoginActivity : AppCompatActivity() {

    private val TAG = "LoginActivity"
    private var preferences: SharedPreferences? = null

    override fun onResume() {
        super.onResume()
        OnlineInfo.networkIsConnect = false
        OnlineInfo.isOnline = false
        if (PostResult.isLogoutOK) {
            Toast.makeText(baseContext, "注销成功！", Toast.LENGTH_LONG).show()
            PostResult.isLoginOK = false
            PostResult.networkIsConnect = false
            PostResult.isLogoutOK = false
            PostResult.networkIsConnect = false
        } else {
            Network.getUserInfo()
        }
    }


    private fun handleGetFinished() {
        handler?.removeCallbacksAndMessages(null)
        if(OnlineInfo.networkIsConnect && OnlineInfo.isOnline) {
            //网络连接成功，且在线，跳转到注销页
            startActivity(Intent(this, LogoutActivity::class.java))
        } else if(!OnlineInfo.networkIsConnect) {
            //网络连接错误，提示网络错误
            Toast.makeText(baseContext, "网络连接错误！", Toast.LENGTH_LONG).show()
        } else if (OnlineInfo.networkIsConnect && !OnlineInfo.isOnline) {
            //网络连接成功，且不在线
            Toast.makeText(baseContext, "获取状态成功！", Toast.LENGTH_LONG).show()
        }
    }

    private fun handlePostFinished() {
        handler?.removeCallbacksAndMessages(null)
        Log.d("Do","Here2")
        if (PostResult.networkIsConnect) {
            if (PostResult.result.contains("login_ok")) {
                PostResult.isLoginOK = true
                saveUserInfo(UserInfo.username, UserInfo.password)
                btn_login.isEnabled = true
                startActivity(Intent(this, LogoutActivity::class.java))
            } else {
                PostResult.isLoginOK = false
                if (PostResult.result.contains("login_error#INFO failed, BAS respond timeout.")) {
                    ServerInfo.acid = "2"
                    Network.Login(UserInfo.username,  UserInfo.password.toCharArray())
                }
                else {
                    Toast.makeText(baseContext, PostResult.result, Toast.LENGTH_LONG).show()
                    btn_login.isEnabled = true
                }
            }
        } else {
            Toast.makeText(baseContext, "网络连接错误！", Toast.LENGTH_LONG).show()
            btn_login.isEnabled = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        preferences = applicationContext.getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
        loadUserInfo()
        link_service1.setOnClickListener{
            startActivity(Intent(this, WebActivity::class.java))
        }
        btn_login.setOnClickListener {
            login()
        }
        handler = @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message?) {
                super.handleMessage(msg)
                Log.d("msg",msg.toString())
                when (msg!!.what) {
                    1 -> handleGetFinished()
                    2 -> handlePostFinished()
                }
            }
        }
    }

    private fun loadUserInfo() {
        var username = preferences?.getString("username","")
        var password= preferences?.getString("password","")
        var acid = preferences?.getString("acid","1")
        if(!username.isNullOrBlank()) {
            input_username.setText(username)
        }
        if(!password.isNullOrBlank()) {
            input_password.setText(password)
        }
        ServerInfo.acid = acid.toString()
    }

    private fun login() {
        btn_login.isEnabled = false

        Log.d(TAG, "Login")

        if (!validate()) {
            onLoginFailed()
            return
        }

        val username = input_username.getText().toString()
        val password = input_password.getText().toString()

        UserInfo.username = username
        UserInfo.password = password

        Network.Login(username, password.toCharArray())

    }

    private fun saveUserInfo(username:String,password:String) {
        val editor = preferences?.edit()
        editor?.putString("username",username)
        editor?.putString("password",password);
        editor?.putString("acid",ServerInfo.acid);
        editor?.apply()
    }

    private fun onLoginFailed() {
        Toast.makeText(baseContext, getString(R.string.loginFailed), Toast.LENGTH_LONG).show()
        btn_login.isEnabled = true
    }

    private fun validate(): Boolean {
        var valid = true

        val username = input_username.getText().toString()
        val password = input_password.getText().toString()

        if (username.isEmpty()) {
            input_username.setError(getString(R.string.vaildUsername))
            valid = false
        } else {
            input_username.setError(null)
        }

        if (password.isEmpty()) {
            input_password.setError(getString(R.string.vaildPassword))
            valid = false
        } else {
            input_password.setError(null)
        }
        return valid
    }


    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}