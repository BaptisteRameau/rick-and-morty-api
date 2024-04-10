package org.mathieu.cleanrmapi.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.HttpStatusCode
import org.mathieu.cleanrmapi.data.remote.responses.CharacterResponse
import org.mathieu.cleanrmapi.data.remote.responses.EpisodeResponse
import org.mathieu.cleanrmapi.data.remote.responses.PaginatedResponse

internal class CharacterApi(private val client: HttpClient) {

    /**
     * Fetches a list of characters from the API.
     *
     * If the page parameter is not provided, it defaults to fetching the first page.
     *
     * @param page The page number to fetch. If null, the first page is fetched by default.
     * @return A paginated response containing a list of [CharacterResponse] for the specified page.
     * @throws HttpException if the request fails or if the status code is not [HttpStatusCode.OK].
     */
    suspend fun getCharacters(page: Int?): PaginatedResponse<CharacterResponse> = client
        .get("character/") { // Effectue une requête GET sur l'endpoint `character/`.
            if (page != null) // Si un numéro de page est fourni,
                url {
                    parameter("page", page) // ajoute le paramètre `page` à l'URL de la requête.
                }
        }
        .accept(HttpStatusCode.OK) // Attend un code de réponse HTTP 200 OK.
        .body() // Extrait le corps de la réponse et le désérialise en `PaginatedResponse<CharacterResponse>`.

    /**
     * Fetches the details of a character with the given ID from the service.
     *
     * @param id The unique identifier of the character to retrieve.
     * @return The [CharacterResponse] representing the details of the character.
     * @throws HttpException if the request fails or if the status code is not [HttpStatusCode.OK].
     */
    suspend fun getCharacter(id: Int): CharacterResponse? = client
        .get("character/$id") // Effectue une requête GET sur l'endpoint `character/{id}`.
        .accept(HttpStatusCode.OK) // Attend un code de réponse HTTP 200 OK.
        .body() // Extrait le corps de la réponse et le désérialise en `CharacterResponse`.
}
