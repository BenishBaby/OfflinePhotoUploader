package com.example.uploadkit.infra

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.storage.ktx.storage
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.io.File
import java.util.UUID

class FirebaseUploader(private val context: Context) {

    fun upload(file: File): Flow<Int> = callbackFlow {
        // Ensure Firebase is initialized (won't crash if google-services.json is absent; it just won't connect)
        try { if (FirebaseApp.getApps(context).isEmpty()) FirebaseApp.initializeApp(context) } catch (_: Throwable) {}

        val storage = Firebase.storage
        val ref = storage.reference.child("uploads/${UUID.randomUUID()}.jpg")
        val task = ref.putFile(android.net.Uri.fromFile(file))

        task.addOnProgressListener { snap ->
            val pct = if (snap.totalByteCount > 0) {
                ((100.0 * snap.bytesTransferred) / snap.totalByteCount).toInt()
            } else 0
            trySend(pct)
        }.addOnSuccessListener {
            trySend(100)
            close()
        }.addOnFailureListener { e ->
            close(e)
        }

        awaitClose {
            // No explicit removal needed; listeners are tied to task
        }
    }
}
