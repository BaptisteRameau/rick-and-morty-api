package org.mathieu.cleanrmapi.data.local

import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.mathieu.cleanrmapi.data.local.objects.CharacterObject

internal class CharacterLocal(private val database: RealmDatabase) {

    /**
     * Defines a suspend function to get a flow of a list of `CharacterObject`.
     * This function is asynchronous and can be paused and resumed.
     */
    suspend fun getCharacters(): Flow<List<CharacterObject>> = database.use {
        // Queries the Realm database for all instances of `CharacterObject`,
        // converts the result set to a Flow, and then maps it to a list.
        query<CharacterObject>().find().asFlow().map { it.list }
    }

    /**
     * Defines a suspend function to get a specific `CharacterObject` by its ID.
     *
     * @return the character object if found, or null otherwise.
     */
    suspend fun getCharacter(id: Int): CharacterObject? = database.use {
        // Queries the database for `CharacterObject` with the specific ID and
        // returns the first result of the query.
        query<CharacterObject>("id == $id").first().find()
    }

    /**
     * Defines a suspend function to save a list of `CharacterObject` instances to the database.
     */
    suspend fun saveCharacters(characters: List<CharacterObject>) = characters.onEach {
        // Iterates through the provided list of characters and inserts each one into the database.
        insert(it)
    }

    /**
     * Defines a suspend function to insert a single `CharacterObject` into the database.
     */
    suspend fun insert(character: CharacterObject) {
        // Executes a write transaction in the database to insert or update the `CharacterObject`.
        // `UpdatePolicy.ALL` dictates that all fields should be updated if the object already exists.
        database.write {
            copyToRealm(character, UpdatePolicy.ALL)
        }
    }

}