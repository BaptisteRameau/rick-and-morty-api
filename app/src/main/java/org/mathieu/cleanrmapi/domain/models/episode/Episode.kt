package org.mathieu.cleanrmapi.domain.models.episode

import org.mathieu.cleanrmapi.domain.models.character.Character

/**
 * Represents an episode, typically derived from a data source or API.
 *
 * @property id The unique identifier for the character.
 * @property name The name of the episode
 * @property air_date The air date of the episode
 * @property episode The number of saison + number of episode
 * @property url The url of the episode
 * @property created The date of creation of the episode
 * @property characters The list of characters presents in the episode
 */
data class Episode(
    val id: Int,
    val name: String,
    val air_date: String,
    val episode: String,
    val url: String,
    val created: String,
    val characters: List<Character>
)