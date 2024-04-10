package org.mathieu.cleanrmapi.domain.models.episode

import org.mathieu.cleanrmapi.domain.models.character.Character

data class Episode(
    val id: Int,
    val name: String,
    val air_date: String,
    val episode: String,
    val url: String,
    val created: String,
    val characters: List<Character>
)