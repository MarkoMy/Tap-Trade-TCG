package hu.markomy.taptradetcg

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.*

class LoadingScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.login_activity)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sharedPreferences = getSharedPreferences("hu.markomy.taptradetcg", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", null)

        if (username == null) {
            Log.d("MainActivity", "User is not logged in")
            val bottomSheet = UsernameBottomSheet().apply{
                onUsernameSetListener = {
                    loadConfigs()
                }
            }
            bottomSheet.show(supportFragmentManager, "UsernameBottomSheet")
        } else {
            Log.d("MainActivity", "User is logged in, hi $username !")
            loadConfigs()
        }
    }

    private fun loadConfigs() {
        GlobalScope.launch {
            delay(5000)
            val intent = Intent(this@LoadingScreen, MainScreen::class.java)
            startActivity(intent)
            finish()
        }
    }
}