package cn.ehaut.srun3000

// Thanks to qianchengyu

class Crypto {
    companion object {
        fun usrEncode(usr: String): String {
            var rtn = "{SRUN3}\r\n"
            val usr_arr = usr.toCharArray()
            for (i in usr_arr.indices) {
                rtn += (usr_arr[i].toInt() + 4).toChar()
            }
            return rtn
        }

        fun pwdEncode(pwd: String): String {
            var pe = ""
            val key = ServerInfo.key
            for (i in 0 until pwd.length) {
                val index = key.length - i % key.length - 1
                val ki = (key[index].toInt()) xor (pwd[i].toInt())
                val _l = ((ki and 0x0F) + 0x36).toChar()
                val _h = ((ki shr 4 and 0x0F) + 0x63).toChar()
                if (i % 2 == 0)
                    pe += _l.toString() + _h.toString()
                else
                    pe += _h.toString() + _l.toString()
            }
            return pe
        }
    }
}