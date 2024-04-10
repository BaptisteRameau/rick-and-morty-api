package org.mathieu.cleanrmapi.data.local

import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.mathieu.cleanrmapi.data.local.objects.CharacterObject
import org.mathieu.cleanrmapi.data.local.objects.EpisodeObject

internal class EpisodeLocal(private val database: RealmDatabase) {

    /**
     * Function to get a list of all `EpisodeObject` entities from the database.
     * @return It returns a Flow, which is a type that can emit multiple values over time, representing the list of episodes.
     */
    suspend fun getEpisodes(): Flow<List<EpisodeObject>> = database.use {
        // Queries the Realm database for all `EpisodeObject` entities, converts the results to a Flow, and then maps each set to its list.
        query<EpisodeObject>().find().asFlow().map { it.list }
    }

    /**
     * Function to get a specific `EpisodeObject` by its ID.
     * @return The `EpisodeObject` if found, or null if not.
     */
    suspend fun getEpisode(id: Int): EpisodeObject? = database.use {
        // Queries the database for an `EpisodeObject` with the specific ID, and returns the first result found, if any.
        query<EpisodeObject>("id == $id").first().find()
    }

    /**
     * Function to save a list of `EpisodeObject` entities into the database.
     */
    suspend fun saveEpisodes(episodes: List<EpisodeObject>) = episodes.onEach {
        // Iterates through each episode in the list and inserts it into the database.
        insert(it)
    }

    /**
     * Function to insert a single `EpisodeObject` into the database.
     */
    suspend fun insert(episode: EpisodeObject) {
        // Executes a write operation on the database, copying the `EpisodeObject` into Realm.
        // `UpdatePolicy.ALL` means that if an object with the same primary key already exists, it will be updated with the new values.
        database.write {
            copyToRealm(episode, UpdatePolicy.ALL)
        }
    }
}