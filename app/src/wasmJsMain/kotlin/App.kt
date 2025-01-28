import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import compatibility.corsForbidden
import io.github.aakira.napier.Napier
import kotlinx.browser.window
import navigation.Destinations
import org.jetbrains.compose.resources.stringResource
import stgin_proyecto_final.app.generated.resources.*

@Composable
fun App() {
    val navController = rememberNavController()

    val startDestination = remember {
        val path = window.location.pathname.trim('/')
        val destination = Destinations.entries.find { it.path.trim('/') == path }
        if (destination == null) {
            Napier.w { "Could not find destination for \"$path\"" }
            Destinations.NotFound
        } else {
            Napier.d { "Path: $path. Destination: ${destination::class.simpleName}" }
            destination
        }
    }

    CORSWarning()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CORSWarning() {
    var showCORSDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        corsForbidden.then { result ->
            showCORSDialog = result.unsafeCast<JsBoolean>().toBoolean()
            null
        }
    }

    if (showCORSDialog) {
        BasicAlertDialog(
            onDismissRequest = {}
        ) {
            Column {
                Text(
                    text = stringResource(Res.string.error_cors_text),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.error
                )
                Text(
                    text = stringResource(Res.string.error_cors_server, SERVER_URL),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 8.dp),
                    color = MaterialTheme.colorScheme.error
                )
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    onClick = { window.location.reload() }
                ) { Text(stringResource(Res.string.reload)) }
            }
        }
    }
}
