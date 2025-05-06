package hu.markomy.taptradetcg

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

object TapManager {
    private const val TAG = "TapManager"
    private const val MAX_TAP = 10
    private const val PREFS_NAME = "TapTradePrefs"
    private const val TAP_COUNT_KEY = "tapCount"

    private var _tapCount = 0
    val tapCount: Int
        get() = _tapCount

    fun loadTapCount(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        _tapCount = prefs.getInt(TAP_COUNT_KEY, 0)
    }

    fun saveTapCount(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putInt(TAP_COUNT_KEY, _tapCount).apply()
    }

    fun tap(context: Context): Boolean {
        _tapCount++

        // Mentsük el minden koppintás után
        saveTapCount(context)

        // Visszaadjuk, hogy elértük-e a max értéket
        if (_tapCount >= MAX_TAP) {
            rewardAchieved(context)
            _tapCount = 0
            saveTapCount(context)
            return true
        }
        return false
    }

    private fun rewardAchieved(context: Context) {
        Log.d(TAG, "Reward achieved! MAX_TAP ($MAX_TAP) reached.")
        PackManager.addPack()
        PackManager.savePacksCount(context)
        Log.d(TAG, "Pack added! New pack count: ${PackManager.packsCount}")
    }
}