package com.example.trackersales

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.navigation.findNavController
import com.example.trackersales.dataclass.Notifikasi
import com.example.trackersales.dataclass.PushNotifikasi
import com.example.trackersales.forfirebasecloudmessaging.RetrofitInstance
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val TOPIC = "/topics/myTopic2"

class PushNotification : Fragment() {

    val TAG="Halosd"
    private lateinit var btnSend:Button
    private lateinit var etTitle:EditText
    private lateinit var etMessage:EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =inflater.inflate(R.layout.fragment_push_notification, container, false)
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
        btnSend = view.findViewById(R.id.btnPushNotif)
        etTitle=view.findViewById(R.id.judulEt)
        etMessage=view.findViewById(R.id.etIsiNotifikasi)



        btnSend.setOnClickListener {
            val title = etTitle.text.toString()
            val message = etMessage.text.toString()
            if(title.isNotEmpty() && message.isNotEmpty()) {
                Log.d(TAG,message)
                PushNotifikasi(
                    Notifikasi(title,message), TOPIC
                ).also {
                    sendNotification(it)
                }
            }
        }

        view.findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            it.findNavController().navigateUp()
        }

        return view
    }

    private fun sendNotification(notification: PushNotifikasi) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful) {
                Log.d(TAG, "Response: ${Gson().toJson(response)}")
            } else {
                Log.e(TAG, response.errorBody().toString())
            }
        } catch(e: Exception) {
            Log.e(TAG, e.toString())
        }
    }

}