package cn.ehaut.srun3000

// Thanks to qianchengyu

import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset
import android.R.attr.port
import android.util.Log
import java.net.URLEncoder
import kotlin.concurrent.thread

val networkTag = "Network"
var result:String = ""

class Network {
    companion object {
        fun getUserInfo() {
                try {
                    val serverAddress = ServerInfo.authServerAddr //+ ":" + ServerInfo.authServerPort
                    val url = serverAddress + "/cgi-bin/rad_user_info"
                    HttpGet(url)
                    Log.d("Network", result)
                    if (result.contains("not_online") || result.contains("not_online_error")) {
                        OnlineInfo.isOnline = false
                    } else{
                        val infoArray = result.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        OnlineInfo.onlineUsername = infoArray[0]
                        var data = java.lang.Long.valueOf(infoArray[6])
                        data /= (1024 * 1024).toLong()
                        OnlineInfo.usedData = data.toString() + "M"
                        OnlineInfo.onlineIp = infoArray[8]
                        OnlineInfo.usedTime = java.lang.Long.valueOf(infoArray[7])
                        OnlineInfo.usedTime  += java.lang.Long.valueOf(infoArray[2]) - java.lang.Long.valueOf(infoArray[1])
                        OnlineInfo.isOnline = true
                    }
                } catch (e: java.net.SocketTimeoutException) {
                    OnlineInfo.networkIsConnect = false
                } catch (e: Exception) {
                    Log.d(networkTag,e.toString())
                }
        }

        @Throws(Exception::class)
        fun Login(usr: String, pwd: CharArray): String {
            val url = ServerInfo.authServerAddr + ":" + ServerInfo.authServerPort + "/cgi-bin/srun_portal"
            val urlencode_usr = URLEncoder.encode(Crypto.usrEncode(usr), "utf-8")
            val urlencode_pwd = URLEncoder.encode(Crypto.pwdEncode(String(pwd)), "utf-8")
            val urlencode_acid = URLEncoder.encode(ServerInfo.acid, "utf-8")
            val urlencode_mac = URLEncoder.encode(ServerInfo.macAddr, "utf-8")
            val data = ("action=login&username="
                    + urlencode_usr
                    + "&password="
                    + urlencode_pwd
                    + "&drop="
                    + ServerInfo.drop
                    +"&pop="
                    + ServerInfo.pop
                    +"&type="
                    + ServerInfo.type
                    + "&n=117&mbytes=0&minutes=0&ac_id="
                    + urlencode_acid
                    + "&mac="
                    + urlencode_mac)
            HttpPost(url, data)
            Log.d("Network", result)
            if (result.contains("login_ok")) {
                OnlineInfo.isOnline = true
                return "success"
            } else {
                OnlineInfo.isOnline = false
                return result
            }
        }

        @Throws(Exception::class)
        fun logout(usr: String): String {
            val url = ServerInfo.authServerAddr + ":" + ServerInfo.authServerPort + "/cgi-bin/srun_portal"
            val urlencode_usr = URLEncoder.encode(Crypto.usrEncode(usr), "utf-8")
            val urlencode_acid = URLEncoder.encode(ServerInfo.acid, "utf-8")
            val urlencode_mac = URLEncoder.encode(ServerInfo.macAddr, "utf-8")
            HttpPost(url,
                   "action=logout&ac_id=$urlencode_acid&username=$urlencode_usr&mac=$urlencode_mac&type=2")
            Log.d("Network", result)
            if (result.contains("logout_ok")) {
                OnlineInfo.isOnline = false
                return "success"
            } else {
                return result
            }
        }


        @Throws(Exception::class)
        fun HttpPost(url: String, data: String) {
            thread(start = true) {
                val urlObj = URL(null, url)
                val urlConnection = urlObj.openConnection()
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                val httpURLConnection = urlConnection as HttpURLConnection
                httpURLConnection.setRequestMethod("POST")
                httpURLConnection.setDoOutput(true)
                val contentBytes = data.toByteArray(charset("UTF-8"))
                httpURLConnection.setFixedLengthStreamingMode(contentBytes.size)
                val out = httpURLConnection.getOutputStream()
                try {
                    out.write(contentBytes)
                } finally {
                    out.flush()
                }
                val inputStream = urlConnection.getInputStream()
                var contentLength = urlConnection.getContentLength()
                contentLength = if (contentLength == -1) 4096 else contentLength
                var buffer = ByteArray(contentLength)
                var offset = 0
                while (true) {
                    var remain = buffer.size - offset
                    if (remain <= 0) {
                        val newSize = buffer.size * 2
                        val newBuffer = ByteArray(newSize)
                        System.arraycopy(buffer, 0, newBuffer, 0, offset)
                        buffer = newBuffer
                        remain = buffer.size - offset
                    }
                    val numRead = inputStream.read(buffer, offset, remain)
                    if (numRead == -1) {
                        break
                    }
                    offset += numRead
                }
                if (offset < buffer.size) {
                    val newBuffer = ByteArray(offset)
                    System.arraycopy(buffer, 0, newBuffer, 0, offset)
                    buffer = newBuffer
                }
                result = String(buffer,  Charset.forName("UTF-8"))
            }
        }

        @Throws(Exception::class)
        fun HttpGet(url: String) {
            thread(start = true) {
                val urlObj = URL(null, url)
                val urlConnection = urlObj.openConnection()
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                val httpURLConnection = urlConnection as HttpURLConnection
                httpURLConnection.requestMethod = "GET"
                val inputStream = urlConnection.getInputStream()
                var contentLength = urlConnection.getContentLength()
                contentLength = if (contentLength == -1) 4096 else contentLength
                var buffer = ByteArray(contentLength)
                var offset = 0
                while (true) {
                    var remain = buffer.size - offset
                    if (remain <= 0) {
                        val newSize = buffer.size * 2
                        val newBuffer = ByteArray(newSize)
                        System.arraycopy(buffer, 0, newBuffer, 0, offset)
                        buffer = newBuffer
                        remain = buffer.size - offset
                    }
                    val numRead = inputStream.read(buffer, offset, remain)
                    if (numRead == -1) {
                        break
                    }
                    offset += numRead
                }
                if (offset < buffer.size) {
                    val newBuffer = ByteArray(offset)
                    System.arraycopy(buffer, 0, newBuffer, 0, offset)
                    buffer = newBuffer
                }
                result = String(buffer,  Charset.forName("UTF-8"))
            }
        }
    }
}