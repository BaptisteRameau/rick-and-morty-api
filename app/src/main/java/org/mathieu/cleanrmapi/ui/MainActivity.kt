package org.mathieu.cleanrmapi.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import org.mathieu.cleanrmapi.ui.core.Destination
import org.mathieu.cleanrmapi.ui.core.composable
import org.mathieu.cleanrmapi.ui.core.theme.LeTheme
import org.mathieu.cleanrmapi.ui.screens.characterdetails.CharacterDetailsScreen
import org.mathieu.cleanrmapi.ui.screens.characters.CharactersScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                    content = { MainContent() }
                )
            }
        }
    }
}

// A composable function defining the main content of the app.
@Composable
private fun MainContent() {
    // Creates a navigation controller for managing navigation in the app.
    val navController = rememberNavController()

    // Sets up a navigation host for managing navigation between composables.
    NavHost(navController = navController, startDestination = "characters") {
        // Defines a navigation route for the characters list screen.
        composable(Destination.Characters) { CharactersScreen(navController) }

        // Defines a navigation route for character details, with a dynamic ID parameter.
        composable(
            destination = Destination.CharacterDetails()
        ) { backStackEntry ->
            // Displays the character details screen, getting the character ID from navigation arguments.
            CharacterDetailsScreen(
                navController = navController,
                id = backStackEntry.arguments?.getInt("characterId") ?: -1
            )

        }

    }

}
