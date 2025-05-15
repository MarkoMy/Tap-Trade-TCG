package hu.markomy.taptradetcg

import android.content.Context
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.concurrent.TimeUnit

class MainScreen: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.main_screen_activity)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val prefs = getSharedPreferences("hu.markomy.taptradetcg", Context.MODE_PRIVATE)
        if (!prefs.getBoolean("tutorial_shown", false)) {
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(getString(R.string.tutorial_title))
                .setMessage(getString(R.string.tutorial_desc))
                .setPositiveButton(getString(R.string.tutorial_okay)) { dialog, _ ->
                    prefs.edit().putBoolean("tutorial_shown", true).apply()
                    dialog.dismiss()
                }
                .setCancelable(false)
                .show()
        }

        TapManager.loadTapCount(this)
        PackManager.loadPackCount(this)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        val packsCountTextView = findViewById<TextView>(R.id.packsCount)
        packsCountTextView.text = PackManager.packsCount.toString()
        // Kezdő Fragment betöltése
        loadFragment(InventoryFragment())
        updatePacksCount()

        // Periodic WorkManager beállítása 6 óránkénti futtatásra, adjon hozzá egy új csomagot
        val workRequest = PeriodicWorkRequestBuilder<PackWorker>(6, TimeUnit.HOURS)
            .setInitialDelay(6, TimeUnit.HOURS) // To avoid getting 2 packs when game first time starts.
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "PackWorker",
            androidx.work.ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_inventory -> loadFragment(InventoryFragment())
                R.id.nav_packs -> loadFragment(PackOpeningFragment())
                R.id.nav_tap -> loadFragment(TapFragment())
                R.id.nav_trade -> loadFragment(TradeFragment())
            }
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    fun updatePacksCount() {
        val packsCountTextView = findViewById<TextView>(R.id.packsCount)
        packsCountTextView.text = PackManager.packsCount.toString()
    }

    override fun onPause() {
        super.onPause()
        PackManager.savePacksCount(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        PackManager.savePacksCount(this)
    }

    override fun onResume() {
        super.onResume()

        PackManager.loadPackCount(this)
        updatePacksCount()
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        if (currentFragment is InventoryFragment) {
            loadFragment(InventoryFragment())
        }
    }
}