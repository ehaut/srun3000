package cn.ehaut.srun3000

//   与ios版保持一致
//  see here:https://github.com/ehaut/E-HAUT/blob/master/E-HAUT/Model/AppPreferences.swift
//  Created by chn-student on 2019/5/26.
//  Copyright © 2019 ehaut. All rights reserved.
//

public object OnlineInfo{
    val networkIsConnect:Boolean = false
    val isOnline:Boolean = false
    val onlineIp:String = ""
    val onlineUsername:String = ""
    val usedData:String = ""
    val usedTime:Int = 0
    val serverTime:Int = 0
    val loginTime:Int = 0
}

public object ServerInfo {
    //服务器地址示范http://172.16.154.130，请不要忘了http://头以及没有最后/符号
    val authServerAddr:String = "http://172.16.154.130"
    val authServerPort:String = "69"
    val serviceServerAddr:String = "http://172.16.154.130"
    val serviceServerPort:String = "8800"
    val macAddr:String = ""
    val acid:String = "1"
    val type:String = "3"
    val drop:String = "0"
    val pop:String = "1"
    val key:String = "1234567890"
}



