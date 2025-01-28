package screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import model.AuthModel

@Composable
fun AuthScreen(
    onAlreadyLoggedIn: () -> Unit,
    model: AuthModel = viewModel { AuthModel() }
) {
    LaunchedEffect(Unit) {
        model.load(onAlreadyLoggedIn = onAlreadyLoggedIn)
    }

    AuthScreen(
        isLoading = model.isLoading.collectAsState().value,
        onLoginRequested = { u, p ->
            model.login(u, p, onAlreadyLoggedIn)
        },
        onRegisterRequested = model::register,
    )
}

private const val IDX_LOGIN = 0
private const val IDX_REGISTER = 1

@Composable
private fun AuthScreen(
    isLoading: Boolean,
    onLoginRequested: (username: String, password: String) -> Unit,
    onRegisterRequested: (username: String, password: String, onSuccess: () -> Unit) -> Unit,
) {
    Scaffold { paddingValues ->
        val scope = rememberCoroutineScope()
        val pagerState = rememberPagerState { 2 }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            userScrollEnabled = false,
        ) { page ->
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .widthIn(max = 800.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 32.dp)
                ) {
                    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                        when (page) {
                            IDX_LOGIN -> LoginPage(
                                isLoading,
                                onLoginRequested = onLoginRequested,
                                onRegisterRequested = {
                                    scope.launch { pagerState.animateScrollToPage(IDX_REGISTER) }
                                }
                            )

                            IDX_REGISTER -> RegisterPage(
                                isLoading,
                                onRegisterRequested = { u, p ->
                                    onRegisterRequested(u, p) {
                                        scope.launch { pagerState.animateScrollToPage(IDX_LOGIN) }
                                    }
                                },
                                onLoginRequested = {
                                    scope.launch { pagerState.animateScrollToPage(IDX_LOGIN) }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Form(
    title: String,
    altAction: Pair<String, () -> Unit>,
    submit: Pair<String, (username: String, password: String) -> Unit>,
    isLoading : Boolean,
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var showingPassword by remember { mutableStateOf(false) }

    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp)
        )

        Spacer(Modifier.height(4.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
            singleLine = true,
            enabled = !isLoading,
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
            singleLine = true,
            enabled = !isLoading,
            visualTransformation = if (showingPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(
                    onClick = { showingPassword = !showingPassword }
                ) {
                    Icon(
                        if (showingPassword) {
                            Icons.Default.VisibilityOff
                        } else {
                            Icons.Default.Visibility
                        },
                        null
                    )
                }
            }
        )

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = altAction.first,
                modifier = Modifier.padding(8.dp).clickable(enabled = !isLoading, onClick = altAction.second)
            )
            OutlinedButton(
                onClick = { submit.second(username, password) },
                enabled = !isLoading && username.isNotBlank() && password.isNotBlank()
            ) { Text(submit.first) }
        }
    }
}

@Composable
fun LoginPage(
    isLoading: Boolean,
    onLoginRequested: (username: String, password: String) -> Unit,
    onRegisterRequested: () -> Unit,
) {
    Form(
        title = "Login",
        altAction = "Don't have an account?" to onRegisterRequested,
        submit = "Login" to onLoginRequested,
        isLoading = isLoading,
    )
}

@Composable
fun RegisterPage(
    isLoading: Boolean,
    onLoginRequested: () -> Unit,
    onRegisterRequested: (username: String, password: String) -> Unit,
) {
    Form(
        title = "Register",
        altAction = "Already have an account?" to onLoginRequested,
        submit = "Register" to onRegisterRequested,
        isLoading = isLoading,
    )
}
