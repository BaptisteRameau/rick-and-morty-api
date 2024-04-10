package org.mathieu.cleanrmapi.data.local.objects

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mathieu.cleanrmapi.data.remote.responses.EpisodeResponse
import org.mathieu.cleanrmapi.domain.models.character.Character
import org.mathieu.cleanrmapi.domain.models.episode.Episode

/**
 * Represents an episode entity stored in the SQLite database. This object provides fields
 * necessary to represent all the attributes of an episode from the data source.
 * The object is specifically tailored for SQLite storage using Realm.
 *
 * @property id Unique identifier of the episode.
 * @property name Name of the episode, defaulting to an empty string.
 * @property air_date Air date of the episode, defaulting to an empty string.
 * @property episode Episode code, defaulting to an empty string.
 * @property url URL of the episode, defaulting to an empty string.
 * @property created Creation date of the episode record, defaulting to an empty string.
 * @property characters List of characters appearing in the episode, defaulting to an empty list.
 */
internal class EpisodeObject: RealmObject {
    @PrimaryKey
    var id: Int = 1  // Primary key for the Realm database, with a default value of 1.
    var name: String = ""  // Name of the episode, defaulting to an empty string.
    var air_date: String = ""  // Air date of the episode, defaulting to an empty string.
    var episode: String = ""  // Episode code, defaulting to an empty string.
    var url: String = ""  // URL of the episode, defaulting to an empty string.
    var created: String = ""  // Creation date of the episode record, defaulting to an empty string.
    var characters: RealmList<String> = realmListOf() // List of characters appearing in the episode, defaulting to an empty list.
}

/**
 * Extension function for `EpisodeResponse` to convert it into a `EpisodeObject`.
 */
internal fun EpisodeResponse.toRealmObject() = EpisodeObject().also { obj ->
    obj.id = id  // Sets the `id` from `EpisodeResponse` to `EpisodeObject`.
    obj.name = name  // Sets the `name` from `EpisodeResponse` to `EpisodeObject`.
    obj.air_date = air_date  // Sets the `air_date` from `EpisodeResponse` to `EpisodeObject`.
    obj.episode = episode  // Sets the `episode` from `EpisodeResponse` to `EpisodeObject`.
    obj.url = url  // Sets the `url` from `EpisodeResponse` to `EpisodeObject`.
    obj.created = created  // Sets the `created` from `EpisodeResponse` to `EpisodeObject`.
}

/**
 * Extension function to convert `EpisodeObject` to `Episode` model.
 */
internal fun EpisodeObject.toModel() = Episode(
    id = id,  // Maps `id` from `EpisodeObject` to `Episode`.
    name = name,  // Maps `name` from `EpisodeObject` to `Episode`.
    air_date = air_date,  // Maps `air_date` from `EpisodeObject` to `Episode`.
    episode = episode,  // Maps `episode` from `EpisodeObject` to `Episode`.
    url = url,  // Maps `url` from `EpisodeObject` to `Episode`.
    created = created,  // Maps `created` from `EpisodeObject` to `Episode`.
    characters = characters // Maps `characters` from `EpisodeObject` to `Episode`.
)