package hu.markomy.taptradetcg

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.*
import android.widget.*
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class UsernameBottomSheet : BottomSheetDialogFragment() {
    var onUsernameSetListener: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_username_bottomsheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val usernameInput = view.findViewById<EditText>(R.id.usernameInput)
        val btnSave = view.findViewById<Button>(R.id.btnSaveUsername)

        btnSave.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            if (username.isNotEmpty()) {
                // SharedPreferences-ben tárolás
                val sharedPreferences = requireActivity().getSharedPreferences("hu.markomy.taptradetcg", Context.MODE_PRIVATE)
                sharedPreferences.edit().putString("username", username).apply()
                onUsernameSetListener?.invoke()
                dismiss() // Bezárás
            }
        }
    }
}
