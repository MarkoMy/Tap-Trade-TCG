package hu.markomy.taptradetcg

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

object PackManager {
    private const val TAG = "PackManager"
    private const val PREFS_NAME = "TapTradePrefs"
    private const val PACKS_COUNT_KEY = "packsCount"

    private var _packsCount = 0
    val packsCount: Int
        get() = _packsCount

    fun loadPackCount(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        _packsCount = prefs.getInt(PACKS_COUNT_KEY, 1)
        Log.d(TAG, "Loaded pack count: $_packsCount")
    }

    fun savePacksCount(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putInt(PACKS_COUNT_KEY, _packsCount).apply()
        Log.d(TAG, "Saved packs count: $_packsCount")
    }

    fun addPack() {
        _packsCount++
        Log.d(TAG, "Pack added! New count: $_packsCount")
    }

    fun openPack() {
        if (_packsCount > 0) {
            _packsCount--
            Log.d(TAG, "Pack opened. Remaining packs: $_packsCount")
        }
        Log.d(TAG, "Cannot open pack. No packs available.")
    }
}