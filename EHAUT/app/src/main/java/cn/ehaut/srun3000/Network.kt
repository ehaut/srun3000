package cn.ehaut.srun3000

// Thanks to qianchengyu

import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset
import android.os.Message
import android.util.Log
import java.net.URLEncoder

val networkTag = "Network"
var result:String = ""

class Network {
    companion object {
        fun getUserInfo() {
            result = ""
            Thread(object : Runnable {
                override fun run() {
                    try {
                        val serverAddress = ServerInfo.authServerAddr //+ ":" + ServerInfo.authServerPort
                        val url = serverAddress + "/cgi-bin/rad_user_info"
                        HttpGet(url)
                        Log.d("Network", result)
                        if (result.contains("not_online") || result.contains("not_online_error")) {
                            OnlineInfo.isOnline = false
                            OnlineInfo.networkIsConnect = true
                        } else {
                            val infoArray = result.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                            OnlineInfo.onlineUsername = infoArray[0]
                            var data = java.lang.Long.valueOf(infoArray[6])
                            data /= (1024 * 1024).toLong()
                            OnlineInfo.usedData = data.toString() + "M"
                            OnlineInfo.onlineIp = infoArray[8]
                            OnlineInfo.usedTime = infoArray[7].toInt()
                            OnlineInfo.usedTime += infoArray[2].toInt() - infoArray[1].toInt()
                            OnlineInfo.isOnline = true
                            OnlineInfo.networkIsConnect = true
                        }
                    } catch (e: java.net.SocketTimeoutException) {
                        OnlineInfo.networkIsConnect = false
                    } catch (e: Exception) {
                        Log.d(networkTag, e.toString())
                    }
                    val msg: Message = Message.obtain()
                    msg.what = 1
                    msg.obj = "HTTP GET FINSHED"
                    handler?.sendMessage(msg)
                }
            }).start()
        }
        @Throws(Exception::class)
        fun Login(usr: String, pwd: CharArray) {
            result = ""
            Thread(object : Runnable {
                override fun run() {
                    try {
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
                            PostResult.result = "login_ok"
                            PostResult.isLoginOK = true
                            PostResult.networkIsConnect = true
                        } else {
                            OnlineInfo.isOnline = false
                            PostResult.isLoginOK = false
                            PostResult.result = result
                            PostResult.networkIsConnect = true
                        }
                    } catch (e: java.net.SocketTimeoutException) {
                        PostResult.networkIsConnect = false
                    } catch (e: Exception) {
                        Log.d(networkTag, e.toString())
                    }
                    val msg:Message = Message.obtain()
                    msg.what = 2
                    msg.obj = "HTTP POST FINSHED"
                    handler?.sendMessage(msg)
                }
            }).start()
        }

        @Throws(Exception::class)
        fun logout(usr: String){
            result = ""
            Thread(object : Runnable {
                override fun run() {
                    try {
                        val url = ServerInfo.authServerAddr + ":" + ServerInfo.authServerPort + "/cgi-bin/srun_portal"
                        val urlencode_usr = URLEncoder.encode(Crypto.usrEncode(usr), "utf-8")
                        val urlencode_acid = URLEncoder.encode(ServerInfo.acid, "utf-8")
                        val urlencode_mac = URLEncoder.encode(ServerInfo.macAddr, "utf-8")
                        HttpPost(url,
                            "action=logout&ac_id=$urlencode_acid&username=$urlencode_usr&mac=$urlencode_mac&type=2")
                        Log.d("Network", result)
                        if (result.contains("logout_ok")) {
                            OnlineInfo.isOnline = false
                            PostResult.result = "logout_ok"
                            PostResult.isLogoutOK = true
                            PostResult.networkIsConnect = true
                        } else {
                            PostResult.isLogoutOK = false
                            PostResult.result = result
                            PostResult.networkIsConnect = true
                        }
                    } catch (e: java.net.SocketTimeoutException) {
                        PostResult.networkIsConnect = false
                    } catch (e: Exception) {
                        Log.d(networkTag, e.toString())
                    }
                    val msg:Message = Message.obtain()
                    msg.what = 2
                    msg.obj = "HTTP POST FINSHED"
                    handler?.sendMessage(msg)
                }
            }).start()
        }

        @Throws(Exception::class)
        fun HttpPost(url: String, data: String) {
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

        @Throws(Exception::class)
        fun HttpGet(url: String) {
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