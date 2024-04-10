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
import org.mathieu.cleanrmapi.domain.models.episode.Episode
import org.mathieu.cleanrmapi.domain.repositories.EpisodeRepository

private const val EPISODE_PREFS = "episode_repository_preferences"
private val nextPage = intPreferencesKey("next_episodes_page_to_load")

private val Context.dataStore by preferencesDataStore(
    name = EPISODE_PREFS
)

internal class EpisodeRepositoryImpl(
    private val context: Context,
    private val episodeApi: EpisodeAPI,
    private val episodeLocal: EpisodeLocal
) : EpisodeRepository {

    override suspend fun getEpisodes(): Flow<List<Episode>> =
        episodeLocal
            .getEpisodes()
            .mapElement(transform = EpisodeObject::toModel)
            .also { if (it.first().isEmpty()) fetchNext() }


    private suspend fun fetchNext() {

        val page = context.dataStore.data.map { prefs -> prefs[nextPage] }.first()

        if (page != -1) {

            val response = episodeApi.getEpisodes(page)

            val nextPageToLoad = response.info.next?.split("?page=")?.last()?.toInt() ?: -1

            context.dataStore.edit { prefs -> prefs[nextPage] = nextPageToLoad }

            val objects = response.results.map(transform = EpisodeResponse::toRealmObject)

            episodeLocal.saveEpisodes(objects)
        }
    }




    override suspend fun getEpisode(id: Int): Episode =
        episodeLocal.getEpisode(id)?.toModel()
            ?: episodeApi.getEpisode(id = id)?.let { response ->
                val obj = response.toRealmObject()
                episodeLocal.insert(obj)
                obj.toModel()
            }
            ?: throw Exception("Episode not found.")


}