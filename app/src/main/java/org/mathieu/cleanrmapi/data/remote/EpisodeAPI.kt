package org.mathieu.cleanrmapi.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.HttpStatusCode
import org.mathieu.cleanrmapi.data.remote.responses.CharacterResponse
import org.mathieu.cleanrmapi.data.remote.responses.EpisodeResponse
import org.mathieu.cleanrmapi.data.remote.responses.PaginatedResponse

internal class EpisodeAPI(private val client: HttpClient) {
    /**
     * Fonction suspendue pour récupérer une liste d'épisodes à partir d'une page spécifique.
     */
    suspend fun getEpisodes(page: Int?): PaginatedResponse<EpisodeResponse> = client
        .get("episode/") { // Démarre une requête GET à l'endpoint `episode/`.
            if (page != null) // Si la variable `page` n'est pas nulle, ajoute le paramètre de page à l'URL de la requête.
                url {
                    parameter("page", page)
                }
        }
        .accept(HttpStatusCode.OK) // Configure la requête pour accepter seulement un code de réponse HTTP 200 OK.
        .body() // Récupère le corps de la réponse HTTP et le convertit en `PaginatedResponse<EpisodeResponse>`.

    /**
     * Fonction suspendue pour obtenir les détails d'un épisode spécifique par son identifiant.
     */
    suspend fun getEpisode(id: Int): EpisodeResponse? = client
        .get("episode/$id") // Effectue une requête GET à l'endpoint `episode/{id}`.
        .accept(HttpStatusCode.OK) // Configure la requête pour accepter seulement un code de réponse HTTP 200 OK.
        .body() // Récupère le corps de la réponse HTTP et le convertit en `EpisodeResponse`.
}
