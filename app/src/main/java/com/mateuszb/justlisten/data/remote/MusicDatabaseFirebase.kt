package com.mateuszb.justlisten.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.mateuszb.justlisten.data.models.Song
import com.mateuszb.justlisten.other.Constants.SONGS_COLLECTION
import kotlinx.coroutines.tasks.await

class MusicDatabaseFirebase {

    private val firestore = FirebaseFirestore.getInstance()
    private val songsCollection = firestore.collection(SONGS_COLLECTION)

    suspend fun getAllSongs(): List<Song> {
        return try {
            songsCollection.get().await().toObjects(Song::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}