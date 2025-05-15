package hu.markomy.taptradetcg

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LoadingScreen : AppCompatActivity() {
    private val PERM_REQUEST_CODE = 1001
    private var proceededAfterPermissions = false

    private val requiredPerms = mutableListOf<String>().apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.NEARBY_WIFI_DEVICES)
            add(Manifest.permission.POST_NOTIFICATIONS)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            add(Manifest.permission.BLUETOOTH_SCAN)
            add(Manifest.permission.BLUETOOTH_ADVERTISE)
            add(Manifest.permission.BLUETOOTH_CONNECT)
        }
        add(Manifest.permission.ACCESS_COARSE_LOCATION)
        add(Manifest.permission.ACCESS_FINE_LOCATION)
    }.toTypedArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.login_activity)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login)) { v, insets ->
            val sys = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(sys.left, sys.top, sys.right, sys.bottom)
            insets
        }
        requestPermissionsIfNeeded()
    }

    private fun requestPermissionsIfNeeded() {
        val toRequest = requiredPerms.filter {
            checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (toRequest.isEmpty()) {
            proceedAfterPermissions()
        } else {
            requestPermissions(toRequest, PERM_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != PERM_REQUEST_CODE) return

        val denied = permissions.zip(grantResults.toList())
            .filter { it.second != PackageManager.PERMISSION_GRANTED }
            .map { it.first }

        if (denied.isEmpty()) {
            proceedAfterPermissions()
        } else {
            showSettingsDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!proceededAfterPermissions) {
            val missing = requiredPerms.filter {
                checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED
            }
            if (missing.isEmpty()) {
                proceedAfterPermissions()
            } else {
                showSettingsDialog()
            }
        }
    }

    private fun showSettingsDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.permission_required))
            .setMessage(getString(R.string.permission_rationale))
            .setPositiveButton(getString(R.string.open_settings)) { _, _ ->
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                }.also { startActivity(it) }
            }
            .setCancelable(false)
            .show()
    }

    private fun proceedAfterPermissions() {
        proceededAfterPermissions = true
        CardManager.initialize(this)
        val prefs = getSharedPreferences("hu.markomy.taptradetcg", MODE_PRIVATE)
        val username = prefs.getString("username", null)
        if (username.isNullOrBlank()) {
            UsernameBottomSheet().apply {
                onUsernameSetListener = { loadConfigs() }
            }.show(supportFragmentManager, "UsernameBottomSheet")
        } else {
            loadConfigs()
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun loadConfigs() {
        GlobalScope.launch {
            startActivity(Intent(this@LoadingScreen, MainScreen::class.java))
            finish()
        }
    }
}