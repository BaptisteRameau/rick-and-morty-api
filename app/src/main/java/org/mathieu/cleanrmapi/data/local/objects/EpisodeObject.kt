package org.mathieu.cleanrmapi.data.local.objects

import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mathieu.cleanrmapi.data.remote.responses.EpisodeResponse
import org.mathieu.cleanrmapi.domain.models.character.Character
import org.mathieu.cleanrmapi.domain.models.episode.Episode

internal class EpisodeObject: RealmObject {
    @PrimaryKey
    var id: Int = 1
    var name: String = ""
    var air_date: String = ""
    var episode: String = ""
    var url: String = ""
    var created: String = ""
    var characters: List<Character> = emptyList()
}


internal fun EpisodeResponse.toRealmObject() = EpisodeObject().also { obj ->
    obj.id = id
    obj.name = name
    obj.air_date = air_date
    obj.episode = episode
    obj.url = url
    obj.created = created
}

internal fun EpisodeObject.toModel() = Episode(
    id = id,
    name = name,
    air_date = air_date,
    episode = episode,
    url = url,
    created = created,
    characters = characters
)