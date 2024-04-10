package org.mathieu.cleanrmapi.data.local.objects

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mathieu.cleanrmapi.data.remote.responses.CharacterResponse
import org.mathieu.cleanrmapi.data.repositories.tryOrNull
import org.mathieu.cleanrmapi.domain.models.character.Character
import org.mathieu.cleanrmapi.domain.models.character.CharacterGender
import org.mathieu.cleanrmapi.domain.models.character.CharacterStatus
import org.mathieu.cleanrmapi.domain.models.episode.Episode

/**
 * Represents a character entity stored in the SQLite database. This object provides fields
 * necessary to represent all the attributes of a character from the data source.
 * The object is specifically tailored for SQLite storage using Realm.
 *
 * @property id Unique identifier of the character.
 * @property name Name of the character.
 * @property status Current status of the character (e.g. 'Alive', 'Dead').
 * @property species Biological species of the character.
 * @property type The type or subspecies of the character.
 * @property gender Gender of the character (e.g. 'Female', 'Male').
 * @property originName The origin location name.
 * @property originId The origin location id.
 * @property locationName The current location name.
 * @property locationId The current location id.
 * @property image URL pointing to the character's avatar image.
 * @property created Timestamp indicating when the character entity was created in the database.
 */
internal class CharacterObject: RealmObject {
    @PrimaryKey
    var id: Int = -1  // Primary key for the Realm object, defaulting to -1.
    var name: String = ""  // Character's name.
    var status: String = ""  // Character's status (e.g., 'Alive', 'Dead').
    var species: String = ""  // Character's species.
    var type: String = ""  // Type or subspecies of the character.
    var gender: String = ""  // Character's gender.
    var originName: String = ""  // Name of the character's origin location.
    var originId: Int = -1  // ID of the character's origin location.
    var locationName: String = ""  // Name of the character's current location.
    var locationId: Int = -1  // ID of the character's current location.
    var image: String = ""  // URL to the character's image.
    var created: String = ""  // Timestamp of when the character was created in the database.
    var episode: RealmList<EpisodeObject> = realmListOf() // List of episodes associated with the character.
}

/**
 * Extension function for `CharacterResponse` to convert it into a `CharacterObject`.
 */
internal fun CharacterResponse.toRealmObject() = CharacterObject().also { obj ->
    obj.id = id
    obj.name = name
    obj.status = status
    obj.species = species
    obj.type = type
    obj.gender = gender
    obj.originName = origin.name
    // Parses the origin ID from the URL, defaults to -1 if parsing fails.
    obj.originId = tryOrNull { origin.url.split("/").last().toInt() } ?: -1
    obj.locationName = location.name
    // Parses the location ID from the URL, defaults to -1 if parsing fails.
    obj.locationId = tryOrNull { location.url.split("/").last().toInt() } ?: -1
    obj.image = image
    obj.created = created
}

/**
 * Extension function to convert `CharacterObject` to `Character` model.
 */
internal fun CharacterObject.toModel() = Character(
    id = id,
    name = name,
    status = tryOrNull { CharacterStatus.valueOf(status) } ?: CharacterStatus.Unknown,  // Converts the string status to enum, defaulting to Unknown.
    species = species,
    type = type,
    gender = tryOrNull { CharacterGender.valueOf(gender) } ?: CharacterGender.Unknown,  // Converts the string gender to enum, defaulting to Unknown.
    origin = originName to originId,
    location = locationName to locationId,
    avatarUrl = image,
    episode = emptyList()
)
