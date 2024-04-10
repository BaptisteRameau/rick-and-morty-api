package org.mathieu.cleanrmapi.di

import io.ktor.client.HttpClient
import org.koin.dsl.module
import org.mathieu.cleanrmapi.data.local.CharacterLocal
import org.mathieu.cleanrmapi.data.local.EpisodeLocal
import org.mathieu.cleanrmapi.data.local.RMDatabase
import org.mathieu.cleanrmapi.data.local.RealmDatabase
import org.mathieu.cleanrmapi.data.remote.CharacterApi
import org.mathieu.cleanrmapi.data.remote.EpisodeAPI
import org.mathieu.cleanrmapi.data.remote.createHttpClient
import org.mathieu.cleanrmapi.data.repositories.CharacterRepositoryImpl
import org.mathieu.cleanrmapi.data.repositories.EpisodeRepositoryImpl
import org.mathieu.cleanrmapi.domain.repositories.CharacterRepository
import org.mathieu.cleanrmapi.domain.repositories.EpisodeRepository

//https://rickandmortyapi.com/documentation/#rest
private const val RMAPI_URL = "https://rickandmortyapi.com/api/"

val dataModule = module {

    single<HttpClient> {
        createHttpClient(
            baseUrl = RMAPI_URL
        )
    }

    single<RealmDatabase> { RMDatabase() }

    /**
     * Add Injection dependancies for Character
     */

    single { CharacterLocal(get()) }

    single { CharacterApi(get()) }

    single<CharacterRepository> { CharacterRepositoryImpl(get(), get(), get()) }

    /**
     * Add Injection dependancies for Episode
     */

    single { EpisodeLocal(get()) }

    single { EpisodeAPI(get()) }

    single<EpisodeRepository> { EpisodeRepositoryImpl(get(), get(), get()) }

}