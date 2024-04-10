package org.mathieu.cleanrmapi.data.local

import io.realm.kotlin.MutableRealm
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.types.TypedRealmObject
import org.mathieu.cleanrmapi.data.local.objects.CharacterObject
import kotlin.reflect.KClass

/**
 * Defines a `RMDatabase` class as an internal class extending `RealmDatabase`.
 */
internal class RMDatabase : RealmDatabase(
    "rick and morty", // Name of the database.
    // Set of classes that form the schema.
    setOf(
        CharacterObject::class
    ),
    1
)

/**
 * Open class `RealmDatabase` that serves as a base for Realm database configurations.
 */
open class RealmDatabase(name: String, schema: Set<KClass<out TypedRealmObject>>, schemaVersion: Long) {
    // Configuration for the Realm database.
    private val configuration = RealmConfiguration.Builder(schema)
        .name(name) // Sets the name of the database.
        .schemaVersion(schemaVersion) // Sets the version of the schema.
        //We'd rather prefer performing real schema migration in production environment, see : https://www.mongodb.com/developer/products/realm/realm-sdk-schema-migration-android/
        .deleteRealmIfMigrationNeeded()
        .build() // Builds the configuration object.

    private var realm: Realm? = null

    /**
     * A generic function to perform operations on the Realm instance.
     */
    suspend fun <R> use(block: suspend Realm.() -> R): R {
        realm = realm ?: Realm.open(configuration) // Opens the Realm with the configuration if not already opened.
        return try {
            block(realm!!) // Executes the given block of code with the Realm instance.
        } catch (e: Exception) {
            throw e // Rethrows any exception that occurs.
        }
    }

    /**
     * A generic function to perform write operations in the Realm database.
     */
    suspend fun <R> write(block: MutableRealm.() -> R): R = use { this.write(block) }

}