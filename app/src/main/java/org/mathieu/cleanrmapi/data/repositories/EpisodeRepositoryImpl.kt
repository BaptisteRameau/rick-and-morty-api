package org.mathieu.cleanrmapi.data.repositories

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.mathieu.cleanrmapi.data.local.EpisodeLocal
import org.mathieu.cleanrmapi.data.local.objects.EpisodeObject
import org.mathieu.cleanrmapi.data.local.objects.toModel
import org.mathieu.cleanrmapi.data.local.objects.toRealmObject
import org.mathieu.cleanrmapi.data.remote.EpisodeAPI
import org.mathieu.cleanrmapi.data.remote.responses.EpisodeResponse
import org.mathieu.cleanrmapi.domain.models.character.Character
import org.mathieu.cleanrmapi.domain.models.episode.Episode
import org.mathieu.cleanrmapi.domain.repositories.EpisodeRepository

// Constante définissant le nom des préférences pour le stockage des informations liées aux épisodes.
private const val EPISODE_PREFS = "episode_repository_preferences"

// Clé pour accéder à la préférence qui stocke la prochaine page d'épisodes à charger.
private val nextPage = intPreferencesKey("next_episodes_page_to_load")

// Extension propriété pour le Context qui permet d'accéder à un DataStore de préférences.
// DataStore est une solution de stockage de données clé-valeur avancée.
private val Context.dataStore by preferencesDataStore(
    name = EPISODE_PREFS // Nom du DataStore de préférences, utilisant le nom défini précédemment.
)

internal class EpisodeRepositoryImpl(
    private val context: Context,
    private val episodeApi: EpisodeAPI,
    private val episodeLocal: EpisodeLocal
) : EpisodeRepository {

    /**
     * Retrieves all the episodes
     *
     * Flow<List<Episode>>: Le type de retour de la fonction, est un Flow qui émet des listes d'objets Episode. Un Flow est un type utilisé dans Kotlin pour représenter un flux asynchrone de données.
     *
     * @return The List<[Episode]> representing the episodes.
     */
    override suspend fun getEpisodes(): Flow<List<Episode>> =
        episodeLocal // Référence à la source de données locale
            .getEpisodes() // Appelle la méthode getEpisodes sur l'objet episodeLocal pour récupérer un flux d'épisodes
            .mapElement(transform = EpisodeObject::toModel) // Transforme chaque élément (EpisodeObject) du flux en modèle d'épisode (Episode) via la fonction toModel
            .also {
                // Après avoir obtenu le flux d'épisodes, exécute ce bloc
                if (it.first().isEmpty()) // Vérifie si la première liste d'épisodes émise est vide
                    fetchNext() // Si la liste est vide, appelle fetchNext pour charger les épisodes suivants
            }


    private suspend fun fetchNext() {
        // Récupère la valeur de la page actuelle à partir de DataStore.
        val page = context.dataStore.data.map { prefs -> prefs[nextPage] }.first()

        // Vérifie si la page actuelle n'est pas égale à -1 (qui pourrait signifier qu'il n'y a pas d'autres pages à charger).
        if (page != -1) {

            // Appelle l'API pour obtenir les épisodes de la page spécifique.
            val response = episodeApi.getEpisodes(page)

            // Extrayez le numéro de la prochaine page à partir de l'URL de la prochaine page, ou définissez-le sur -1 si non disponible.
            val nextPageToLoad = response.info.next?.split("?page=")?.last()?.toInt() ?: -1

            // Met à jour le numéro de la prochaine page dans DataStore.
            context.dataStore.edit { prefs -> prefs[nextPage] = nextPageToLoad }

            // Transforme la réponse de l'API en objets de domaine/local à l'aide de la méthode `toRealmObject`.
            val objects = response.results.map(transform = EpisodeResponse::toRealmObject)

            // Sauvegarde les objets transformés dans la source de données locale.
            episodeLocal.saveEpisodes(objects)
        }
    }

    /**
     * Retrieves the episode with the specified ID.
     *
     * The function follows these steps:
     * 1. Tries to fetch the episode from the local storage.
     * 2. If not found locally, it fetches the episode from the API.
     * 3. Upon successful API retrieval, it saves the episode to local storage.
     * 4. If the episode is still not found, it throws an exception.
     *
     * @param id The unique identifier of the episode to retrieve.
     * @return The [Episode] object representing the episode details.
     * @throws Exception If the episode cannot be found both locally and via the API.
     */
    override suspend fun getEpisode(id: Int): Episode =
        // Tentative de récupérer un épisode depuis la source de données locale.
        episodeLocal.getEpisode(id)?.toModel()
            // Si l'épisode n'est pas trouvé localement (null), on essaie de le charger depuis l'API.
            ?: episodeApi.getEpisode(id = id)?.let { response ->
                // Convertit la réponse de l'API en un objet Realm (base de données locale).
                val obj = response.toRealmObject()
                // Insère l'objet converti dans la base de données locale.
                episodeLocal.insert(obj)
                // Retourne l'épisode en convertissant l'objet Realm en modèle.
                obj.toModel()
            }
            // Si l'épisode n'est trouvé ni localement ni via l'API, on lance une exception.
            ?: throw Exception("Episode not found.")
}