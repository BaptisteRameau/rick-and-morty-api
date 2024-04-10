package org.mathieu.cleanrmapi.data.remote.responses

/**
 * Represents an episode, typically derived from a data source or API.
 *
 * @property id The unique identifier for the character.
 * @property name The name of the episode
 * @property air_date The air date of the episode
 * @property episode The number of saison + number of episode
 * @property url The url of the episode
 * @property created The timestamp indicating when the episode was added to the database.
 */
data class EpisodeResponse(
    val id: Int,
    val name: String,
    val air_date: String,
    val episode: String,
    val characters: List<String>,
    val url: String,
    val created: String
)