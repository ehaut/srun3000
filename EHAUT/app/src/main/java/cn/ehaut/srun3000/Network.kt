package cn.ehaut.srun3000
import android.os.Handler

//   与ios版保持一致
//  see here:https://github.com/ehaut/E-HAUT/blob/master/E-HAUT/Model/AppPreferences.swift
//  Created by chn-student on 2019/5/26.
//  Copyright © 2019 ehaut. All rights reserved.
//

public object OnlineInfo{
    var networkIsConnect:Boolean = false
    var isOnline:Boolean = false
    var onlineIp:String = ""
    var onlineUsername:String = ""
    var usedData:String = ""
    var usedTime:Int = 0
}

public object ServerInfo {
    //服务器地址示范http://172.16.154.130，请不要忘了http://头以及没有最后/符号
    var authServerAddr:String = "http://172.16.154.130"
    var authServerPort:String = "69"
    var serviceServerAddr:String = "http://172.16.154.130"
    var serviceServerPort:String = "8800"
    var macAddr:String = ""
    var acid:String = "1"
    var type:String = "3"
    var drop:String = "0"
    var pop:String = "1"
    var key:String = "1234567890"
}

public object PostResult {
    var networkIsConnect:Boolean = false
    var isLoginOK:Boolean = false
    var isLogoutOK:Boolean = false
    var result:String = ""
}


var handler:Handler? = null


public object UserInfo {
    var username:String = ""
    var password:String = ""
}