package cn.ehaut.srun3000

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.View
import kotlinx.android.synthetic.main.activity_login.*

class LogoutActivity : AppCompatActivity() {
    private val TAG = "LogoutActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logout)
    }
}