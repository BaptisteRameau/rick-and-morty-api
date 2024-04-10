package org.mathieu.cleanrmapi.data.repositories

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.mathieu.cleanrmapi.data.local.CharacterLocal
import org.mathieu.cleanrmapi.data.local.objects.CharacterObject
import org.mathieu.cleanrmapi.data.local.objects.toModel
import org.mathieu.cleanrmapi.data.local.objects.toRealmObject
import org.mathieu.cleanrmapi.data.remote.CharacterApi
import org.mathieu.cleanrmapi.data.remote.responses.CharacterResponse
import org.mathieu.cleanrmapi.domain.models.character.Character
import org.mathieu.cleanrmapi.domain.models.episode.Episode
import org.mathieu.cleanrmapi.domain.repositories.CharacterRepository

private const val CHARACTER_PREFS = "character_repository_preferences"
private val nextPage = intPreferencesKey("next_characters_page_to_load")

private val Context.dataStore by preferencesDataStore(
    name = CHARACTER_PREFS
)

internal class CharacterRepositoryImpl(
    private val context: Context,
    private val characterApi: CharacterApi,
    private val characterLocal: CharacterLocal
) : CharacterRepository {

    /**
     * Retrieves all the characters
     *
     * Flow<List<Character>>: Le type de retour de la fonction, est un Flow qui émet des listes d'objets Character. Un Flow est un type utilisé dans Kotlin pour représenter un flux asynchrone de données.
     *
     * @return The List<[Character]> representing the episodes.
     */
    override suspend fun getCharacters(): Flow<List<Character>> =
        characterLocal
            .getCharacters()
            .mapElement(transform = CharacterObject::toModel) // Convertit les objets en modèles.
            .also { if (it.first().isEmpty()) fetchNext() } // Charge le prochain lot si nécessaire.


    /**
     * Fetches the next batch of characters and saves them to local storage.
     *
     * This function works as follows:
     * 1. Reads the next page number from the data store.
     * 2. If there's a valid next page (i.e., page is not -1), it fetches characters from the API for that page.
     * 3. Extracts the next page number from the API response and updates the data store with it.
     * 4. Transforms the fetched character data into their corresponding realm objects.
     * 5. Saves the transformed realm objects to the local database.
     *
     * Note: If the `next` attribute from the API response is null or missing, the page number is set to -1, indicating there's no more data to fetch.
     */
    private suspend fun fetchNext() {

        val page = context.dataStore.data.map { prefs -> prefs[nextPage] }.first()

        if (page != -1) {

            val response = characterApi.getCharacters(page) // Appelle l'API.

            val nextPageToLoad = response.info.next?.split("?page=")?.last()?.toInt() ?: -1

            context.dataStore.edit { prefs -> prefs[nextPage] = nextPageToLoad } // Met à jour la page suivante.

            val objects = response.results.map(transform = CharacterResponse::toRealmObject)

            characterLocal.saveCharacters(objects) // Sauvegarde les personnages dans le stockage local.
        }

    }


    /**
     * Fonction pour charger plus de personnages, appelant `fetchNext`.
     */
    override suspend fun loadMore() = fetchNext()


    /**
     * Retrieves the character with the specified ID.
     *
     * The function follows these steps:
     * 1. Tries to fetch the character from the local storage.
     * 2. If not found locally, it fetches the character from the API.
     * 3. Upon successful API retrieval, it saves the character to local storage.
     * 4. If the character is still not found, it throws an exception.
     *
     * @param id The unique identifier of the character to retrieve.
     * @return The [Character] object representing the character details.
     * @throws Exception If the character cannot be found both locally and via the API.
     */
    override suspend fun getCharacter(id: Int): Character =
        characterLocal.getCharacter(id)?.toModel() // Essaye de le trouver localement.
            ?: characterApi.getCharacter(id = id)?.let { response ->
                val obj = response.toRealmObject()
                characterLocal.insert(obj) // Stocke localement si trouvé via l'API.
                obj.toModel()
            }
            ?: throw Exception("Character not found.") // Lance une exception si le personnage n'est pas trouvé.


}

/**
 * Fonction générique pour tenter une opération et retourner null en cas d'exception.
 */
fun <T> tryOrNull(block: () -> T) = try {
    block()
} catch (_: Exception) {
    null
}

/**
 * Fonction d'extension pour transformer les éléments d'un Flow.
  */
inline fun <T, R> Flow<List<T>>.mapElement(crossinline transform: suspend (value: T) -> R): Flow<List<R>> =
    this.map { list ->
        list.map { element -> transform(element) }  // Transforme chaque élément de la liste.
    }