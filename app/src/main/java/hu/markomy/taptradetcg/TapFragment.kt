package hu.markomy.taptradetcg

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment

class TapFragment: Fragment() {

    private lateinit var tapCounterText: TextView
    private lateinit var tapImage: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.tap_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tapCounterText = view.findViewById(R.id.tapCounter)
        tapImage = view.findViewById(R.id.tapImage)

        // Frissítsük a számlálót a jelenlegi értékkel
        updateTapCounter()

        tapImage.setOnClickListener {
            // A TapManager végzi a munkát
            val rewardAchieved = TapManager.tap(requireContext())
            updateTapCounter()

            if (rewardAchieved) {
                // Itt lehet további UI-műveleteket végezni, ha szükséges
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateTapCounter()
    }

    private fun updateTapCounter() {
        tapCounterText.text = "Taps: ${TapManager.tapCount}"
    }
}