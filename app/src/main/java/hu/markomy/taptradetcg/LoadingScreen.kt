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

        CardManager.initialize(this)
        val sharedPreferences = getSharedPreferences("hu.markomy.taptradetcg", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", null)

        if (username == null) {
            Log.d("MainActivity", "User is not logged in")
            val bottomSheet = UsernameBottomSheet().apply{
                onUsernameSetListener = {
                    //addTestCardToInventory()
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
            //delay(500)
            val intent = Intent(this@LoadingScreen, MainScreen::class.java)
            startActivity(intent)
            finish()
        }
    }
    private fun addTestCardToInventory() {
        val db = AppDatabase.getInstance(this)
        val cardDao = db.cardDao()
        GlobalScope.launch {
            // Teszt kártya hozzáadása
            addTestCards(6, cardDao)
            // Inventory lekérdezése
            val cards = cardDao.getAll()
            Log.d("Inventory", "Inventory count: ${cards.size}")
            cards.forEach { card ->
                Log.d("Inventory", "Card: id=${card.cardId}, count=${card.count}")
            }
        }
    }

    private fun addTestCards(num: Int, cardDao: CardDao) {
        GlobalScope.launch {
            for(i in 1..num){
                cardDao.insert(PlayerCard(cardId = i, count = i+1))
            }
        }
    }
}