package hu.markomy.taptradetcg

import android.Manifest
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.*
import kotlinx.coroutines.launch
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import com.google.android.gms.nearby.connection.Strategy

class TradeFragment : Fragment() {
    private lateinit var adapter: InventoryAdapter
    private lateinit var connectionsClient: ConnectionsClient
    private val nearbyNicknames = mutableListOf<String>()
    private val endpointIdToNickname = mutableMapOf<String, String>()
    private var selectedCardForTrade: Card? = null
    private var myNickname: String = ""
    private var nearbyDialogShown = false
    private var nearbyPlayersDialog: AlertDialog? = null
    private val foundDevices = mutableListOf<BluetoothDevice>()
    private val deviceNames = mutableListOf<String>()
    private var discoveryReceiver: BroadcastReceiver? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.trade_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        connectionsClient = Nearby.getConnectionsClient(requireActivity())
        myNickname = getNicknameFromPrefs()
        val recyclerView = view.findViewById<RecyclerView>(R.id.tradeRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        adapter = InventoryAdapter(emptyList()) { card, count ->
            showTradeConfirmationDialog(card)
        }
        recyclerView.adapter = adapter
        loadInventory()
    }

    private fun getNicknameFromPrefs(): String {
        val prefs = requireContext().getSharedPreferences("hu.markomy.taptradetcg", Context.MODE_PRIVATE)
        return prefs.getString("username", "Unknown") ?: "Unknown"
    }

    private fun loadInventory() {
        lifecycleScope.launch {
            val cardDao = AppDatabase.getInstance(requireContext()).cardDao()
            val playerCards = cardDao.getAll()
            val allCards = CardManager.getAllCards()
            val inventory = playerCards.mapNotNull { pc ->
                val card = allCards.find { it.id == pc.cardId }
                if (card != null) Pair(card, pc.count) else null
            }
            adapter.updateData(inventory)
        }
    }

    private fun showTradeConfirmationDialog(card: Card) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_card, null)
        val cardImage = dialogView.findViewById<ImageView>(R.id.cardImage)
        val cardName = dialogView.findViewById<TextView>(R.id.cardName)
        CardManager.loadCardImage(requireContext(), card, cardImage)
        cardName.text = card.name

        AlertDialog.Builder(requireContext())
            .setTitle(requireContext().getString(R.string.trade_card_title))
            .setView(dialogView)
            .setPositiveButton(requireContext().getString(R.string.trade_card_yes)) { _, _ ->
                selectedCardForTrade = card
                startNearbyDiscovery()
                Toast.makeText(requireContext(), requireContext().getString(R.string.searching_for_players), Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(requireContext().getString(R.string.trade_card_no), null)
            .show()
    }

    private fun startNearbyDiscovery() {
        connectionsClient.stopDiscovery()
        connectionsClient.stopAdvertising()
        nearbyNicknames.clear()
        endpointIdToNickname.clear()
        nearbyDialogShown = false

        viewLifecycleOwner.lifecycleScope.launch {
            kotlinx.coroutines.delay(300)
            connectionsClient.startDiscovery(
                "hu.markomy.taptradetcg",
                object : EndpointDiscoveryCallback() {
                    override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
                        val nickname = info.endpointName
                        if (!endpointIdToNickname.containsKey(endpointId)) {
                            nearbyNicknames.add(nickname)
                            endpointIdToNickname[endpointId] = nickname
                        }
                        // Csak akkor mutasd a dialogot, ha még nem volt megjelenítve
                        if (!nearbyDialogShown && nearbyNicknames.isNotEmpty()) {
                            nearbyDialogShown = true
                            showNearbyPlayersDialog()
                        }
                    }
                    override fun onEndpointLost(endpointId: String) {
                        // Eltávolítás
                    }
                },
                DiscoveryOptions.Builder().setStrategy(Strategy.P2P_STAR).build()
            )
            connectionsClient.startAdvertising(
                myNickname,
                "hu.markomy.taptradetcg",
                connectionLifecycleCallback,
                AdvertisingOptions.Builder().setStrategy(Strategy.P2P_STAR).build()
            )
        }
    }

    private fun showNearbyPlayersDialog() {
        nearbyPlayersDialog = AlertDialog.Builder(requireContext())
            .setTitle(requireContext().getString(R.string.select_player_to_trade_with))
            .setItems(nearbyNicknames.toTypedArray()) { _, which ->
                val selectedNickname = nearbyNicknames[which]
                val endpointId = endpointIdToNickname.entries.first { it.value == selectedNickname }.key
                connectionsClient.requestConnection(myNickname, endpointId,
                    connectionLifecycleCallback
                )
            }
            .setOnDismissListener { nearbyPlayersDialog = null }
            .show()
    }

    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
            // Show confirmation dialog to user
            nearbyPlayersDialog?.dismiss()
            nearbyPlayersDialog = null

            AlertDialog.Builder(requireContext())
                .setTitle(requireContext().getString(R.string.trade_request))
                .setMessage(requireContext().getString(R.string.trade_with_player, info.endpointName))
                .setPositiveButton(requireContext().getString(R.string.trade_card_yes)) { _, _ ->
                    connectionsClient.acceptConnection(endpointId, payloadCallback)
                }
                .setNegativeButton(requireContext().getString(R.string.trade_card_no)) { _, _ ->
                    connectionsClient.rejectConnection(endpointId)
                }
                .show()
        }
        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            if (result.status.isSuccess) {
                // Connection established, send card data
                selectedCardForTrade?.let { card ->
                    val payload = Payload.fromBytes(card.id.toString().toByteArray())
                    connectionsClient.sendPayload(endpointId, payload)
                    Toast.makeText(requireContext(), requireContext().getString(R.string.card_sent_to_trade), Toast.LENGTH_SHORT).show()

                    // Remove the card from your own inventory
                    lifecycleScope.launch {
                        val cardDao = AppDatabase.getInstance(requireContext()).cardDao()
                        val existing = cardDao.getPlayerCardByCardId(card.id)
                        if (existing != null) {
                            if (existing.count > 1) {
                                cardDao.update(existing.copy(count = existing.count - 1))
                            } else {
                                cardDao.delete(existing)
                            }
                            loadInventory()
                        }
                    }
                }
                connectionsClient.disconnectFromEndpoint(endpointId)
                connectionsClient.stopDiscovery()
                connectionsClient.stopAdvertising()
            }
            else {
                // Connection failed, try Bluetooth pairing fallback
                Toast.makeText(requireContext(), "Nearby connection failed. Trying Bluetooth pairing...", Toast.LENGTH_SHORT).show()
                startBluetoothPairing()
            }
        }
        override fun onDisconnected(endpointId: String) {
            //For test purposes
            //Toast.makeText(requireContext(), "Disconnected from player.", Toast.LENGTH_SHORT).show()
            Toast.makeText(requireContext(), requireContext().getString(R.string.ondisconnect), Toast.LENGTH_SHORT).show()
        }
    }

    private fun startBluetoothPairing() {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            Toast.makeText(requireContext(), "Bluetooth not supported", Toast.LENGTH_SHORT).show()
            return
        }
        if (!bluetoothAdapter.isEnabled) {
            // Optionally prompt user to enable Bluetooth
            Toast.makeText(requireContext(), "Please enable Bluetooth", Toast.LENGTH_SHORT).show()
            return
        }
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                requestPermissions(arrayOf(Manifest.permission.BLUETOOTH_SCAN), 1001)
            }
            return
        }

        foundDevices.clear()
        deviceNames.clear()
        discoveryReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: android.content.Intent) {
                if (BluetoothDevice.ACTION_FOUND == intent.action) {
                    val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    device?.let {
                        foundDevices.add(it)
                        deviceNames.add(it.name ?: it.address)
                    }
                }
            }
        }
        requireContext().registerReceiver(discoveryReceiver, android.content.IntentFilter(BluetoothDevice.ACTION_FOUND))
        bluetoothAdapter.startDiscovery()

        // After a short delay, show dialog
        viewLifecycleOwner.lifecycleScope.launch {
            kotlinx.coroutines.delay(5000) // Wait for some devices to be found
            requireContext().unregisterReceiver(discoveryReceiver)
            AlertDialog.Builder(requireContext())
                .setTitle("Select device to pair")
                .setItems(deviceNames.toTypedArray()) { _, which ->
                    foundDevices[which].createBond()
                }
                .show()
        }
    }

    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            // Receive card ID as bytes, convert to Int
            val cardIdStr = payload.asBytes()?.toString(Charsets.UTF_8)
            val cardId = cardIdStr?.toIntOrNull()
            if (cardId != null) {
                lifecycleScope.launch {
                    val cardDao = AppDatabase.getInstance(requireContext()).cardDao()
                    val existing = cardDao.getPlayerCardByCardId(cardId)
                    if (existing != null) {
                        cardDao.update(existing.copy(count = existing.count + 1))
                    } else {
                        cardDao.insert(PlayerCard(cardId = cardId, count = 1))
                    }
                    loadInventory()
                    Toast.makeText(requireContext(), getString(R.string.received_card, cardId), Toast.LENGTH_SHORT).show()
                }
            }
        }
        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {}
    }
}