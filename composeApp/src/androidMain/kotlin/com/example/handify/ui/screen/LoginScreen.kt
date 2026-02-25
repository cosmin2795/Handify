package com.example.handify.ui.screen

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.example.handify.R
import com.example.handify.presentation.auth.LoginViewModel
import com.example.handify.ui.theme.SocialButtonBackground
import com.example.handify.ui.theme.SocialButtonBorder
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

// Set to true once you have created a Facebook Developer App and filled in strings.xml
private const val FACEBOOK_ENABLED = false

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = koinViewModel()
) {
    val state = viewModel.state
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Facebook callback — only registered when Facebook is enabled
    val callbackManager = remember { CallbackManager.Factory.create() }
    if (FACEBOOK_ENABLED) {
        DisposableEffect(callbackManager) {
            LoginManager.getInstance().registerCallback(
                callbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(result: LoginResult) {
                        viewModel.loginWithFacebook(result.accessToken.token)
                    }
                    override fun onCancel() {}
                    override fun onError(error: FacebookException) {
                        viewModel.onError(error.message ?: "Facebook login failed")
                    }
                }
            )
            onDispose { LoginManager.getInstance().unregisterCallback(callbackManager) }
        }
    }

    // Navigate on successful login
    LaunchedEffect(state.isLoggedIn) {
        if (state.isLoggedIn) onLoginSuccess()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // App name
            Text(
                text = "Handify",
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Find the right person for the job",
                fontSize = 15.sp,
                color = Color(0xFF757575),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(56.dp))

            // ── Google button ─────────────────────────────────────────────
            SocialLoginButton(
                onClick = {
                    scope.launch {
                        signInWithGoogle(
                            context = context,
                            webClientId = context.getString(R.string.google_web_client_id),
                            onSuccess = { idToken -> viewModel.loginWithGoogle(idToken) },
                            onError = { viewModel.onError(it) }
                        )
                    }
                },
                enabled = !state.isLoading,
                iconRes = R.drawable.ic_google,
                text = "Log in with Google"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ── Facebook button ───────────────────────────────────────────
            SocialLoginButton(
                onClick = {
                    if (FACEBOOK_ENABLED) {
                        LoginManager.getInstance().logIn(
                            context as Activity,
                            callbackManager,
                            listOf("email", "public_profile")
                        )
                    }
                },
                enabled = FACEBOOK_ENABLED && !state.isLoading,
                iconRes = R.drawable.ic_facebook,
                text = if (FACEBOOK_ENABLED) "Log in with Facebook" else "Log in with Facebook  (coming soon)"
            )

            // ── Loading indicator ─────────────────────────────────────────
            if (state.isLoading) {
                Spacer(modifier = Modifier.height(24.dp))
                CircularProgressIndicator(modifier = Modifier.size(32.dp))
            }

            // ── Error message ─────────────────────────────────────────────
            state.error?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
                LaunchedEffect(error) {
                    kotlinx.coroutines.delay(4000)
                    viewModel.clearError()
                }
            }
        }
    }
}

@Composable
private fun SocialLoginButton(
    onClick: () -> Unit,
    enabled: Boolean,
    iconRes: Int,
    text: String,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = SocialButtonBackground,
            contentColor = Color(0xFF1A1A1A),
            disabledContainerColor = SocialButtonBackground,
            disabledContentColor = Color(0xFFAAAAAA)
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (enabled) SocialButtonBorder else Color(0xFFE8E8E8)
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(22.dp),
                alpha = if (enabled) 1f else 0.4f
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

private suspend fun signInWithGoogle(
    context: android.content.Context,
    webClientId: String,
    onSuccess: (String) -> Unit,
    onError: (String) -> Unit
) {
    val credentialManager = CredentialManager.create(context)
    val googleIdOption = GetGoogleIdOption.Builder()
        .setServerClientId(webClientId)
        .setFilterByAuthorizedAccounts(false)
        .setAutoSelectEnabled(false)
        .build()
    val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    try {
        val result = credentialManager.getCredential(context = context, request = request)
        val credential = result.credential
        if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
            onSuccess(googleCredential.idToken)
        } else {
            onError("Unexpected credential type")
        }
    } catch (e: GetCredentialException) {
        onError(e.errorMessage?.toString() ?: "Google sign-in failed")
    }
}
