package ru.fwnz.humblr.data

import android.app.PendingIntent
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import kotlinx.coroutines.*
import net.openid.appauth.*
import ru.fwnz.humblr.ui.intro.IntroActivity
import ru.fwnz.humblr.ui.main.MainActivity
import kotlin.coroutines.suspendCoroutine

private const val TAG = "AppAuthComponent"
class AppAuthComponent(
    private val context: Context,
//    private val aOAuthApi: RedditOAuthApi
) {

    private val scope = CoroutineScope(Dispatchers.IO + CoroutineName(SCOPE_NAME))
    private val authorizationServiceConfiguration = AuthorizationServiceConfiguration(
        AUTHORIZATION_ENDPOINT,
        TOKEN_ENDPOINT,
        null,
        LOGOUT_ENDPOINT
    )
    private val authorizationRequest = AuthorizationRequest.Builder(
        authorizationServiceConfiguration,
//        CLIENT_ID,
        ru.fwnz.humblr.BuildConfig.API_CLIENT_ID,
        ResponseTypeValues.CODE,
        POST_AUTHORIZATION_REDIRECT
    )
        .setAdditionalParameters(mapOf("duration" to "permanent"))
        .setScope(REQUIRED_SCOPES)
        .setState(STATE)
//        .setPrompt(PROMPT_POLICE)
        .build()
    private var authorizationService: AuthorizationService = AuthorizationService(context)
    var accessToken: String? = null
        private set
    var refreshToken: String? = null
        private set
//    var accessToken: String? = readAccessTokenState()
//        private set(value) {
//            field = value
//            if (value != null) {
//                val intent = Intent(context, MainActivity::class.java)
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                startActivity(
//                    context,
//                    intent,
//                    bundleOf()
//                )
//            }
//        }
    private fun setToken(
        newAccessToken: String?,
        newRefreshToken: String?,
        cls: Class<out AppCompatActivity> = MainActivity::class.java
    ) {
        accessToken = newAccessToken
        refreshToken = newRefreshToken

        val authPrefs: SharedPreferences = context.getSharedPreferences("auth", MODE_PRIVATE)
        val editor = authPrefs.edit()
        editor.putString("accessToken", accessToken)
        editor.putString("refreshToken", refreshToken)
        editor.apply()

        val intent = Intent(context, cls)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(
            context,
            intent,
            bundleOf()
        )
    }

    fun setToken(
        newAccessToken: String?,
    ) {
        accessToken = newAccessToken
        val authPrefs: SharedPreferences = context.getSharedPreferences("auth", MODE_PRIVATE)
        val editor = authPrefs.edit()
        editor.putString("accessToken", accessToken)
        editor.apply()
//        val intent = Intent(context, MainActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        startActivity(
//            context,
//            intent,
//            bundleOf()
//        )
    }

    private var authState: AuthState
    val isAuthorized get() = accessToken != null
    private var pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
        PendingIntent.FLAG_MUTABLE else 0

    init {
        val sP = context.getSharedPreferences("auth", MODE_PRIVATE)
        accessToken = sP.getString("accessToken", null)
        refreshToken = sP.getString("refreshToken", null)
        val stateJson = sP.getString("stateJson", null)
        authState = if (stateJson != null) {
            AuthState.jsonDeserialize(stateJson)
        } else {
            AuthState(authorizationServiceConfiguration)
        }
    }

    fun initAuthorization() {
        scope.launch {
            authorizationService.performAuthorizationRequest(
                authorizationRequest,
                PendingIntent.getActivity(
                    context,
                    0,
                    Intent(context, MainActivity::class.java).apply {
                        flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_TASK
                    },
                    pendingIntentFlags
                )
            )
        }
    }

    private fun readAccessTokenState(): String? {
        val sP = context.getSharedPreferences("auth", MODE_PRIVATE)
        val str = sP.getString("accessToken", null)
        return str
    }
//    private fun readAccessTokenState(): String? =
//        context.getSharedPreferences("auth", MODE_PRIVATE).getString("accessToken", null).also {
//            Log.d("RDA", "readAccessTokenState: $it")
//        }

    private fun writeAccessTokenState(accessToken: String?) {
        val authPrefs: SharedPreferences = context.getSharedPreferences("auth", MODE_PRIVATE)
        val editor = authPrefs.edit()
        editor.putString("accessToken", accessToken)
        editor.apply()
        this.accessToken = accessToken
    }

    private fun saveAuthState(authState: AuthState) {
        val authPreferences = context.getSharedPreferences("auth", MODE_PRIVATE)
        val editor = authPreferences.edit()
        editor.putString("stateJson", authState.jsonSerializeString())
        editor.apply()
    }

    private fun update(response: AuthorizationResponse?, ex: AuthorizationException?) {
        authState.update(response, ex)
        saveAuthState(authState)
        Log.d(TAG, "update: ${authState.accessToken}")
//        writeAccessTokenState(authState.accessToken)
    }

    private fun update(response: TokenResponse?, ex: AuthorizationException?) {
        authState.update(response, ex)
        saveAuthState(authState)
        Log.d(TAG, "update2: ${authState.accessToken}")
//        writeAccessTokenState(authState.accessToken)
    }

    fun getLogoutRequest(): Intent {
        val endSessionRequest = EndSessionRequest.Builder(authorizationServiceConfiguration)
            .setPostLogoutRedirectUri(POST_LOGOUT_REDIRECT)
            .build()
        val endSessionIntent = authorizationService.getEndSessionRequestIntent(endSessionRequest)
        setToken(null, null, IntroActivity::class.java)
        return endSessionIntent
    }


    fun prepareTokemRequest(intent: Intent): TokenRequest {
        val code = intent.dataString?.substringAfter("code=")?.substringBefore('#')
        Log.d(TAG, "prepareTokemRequest authorizationCode: $code")
        return TokenRequest.Builder(authorizationServiceConfiguration, CLIENT_ID)
            .setGrantType(GRANT_TYPE)
            .setAuthorizationCode(code)
            .setRedirectUri(POST_AUTHORIZATION_REDIRECT)
            .build()
    }

    fun performAccessTokenRequest(
        authorizationResponse: AuthorizationResponse,
        tokenExchangeRequest: TokenRequest,
        authorizationException: AuthorizationException?,
        failureCallback: () -> Unit
    ) {
        update(authorizationResponse, authorizationException)
        Log.d(TAG, "performAccessTokenRequest: ")



        authorizationService.performTokenRequest(
            tokenExchangeRequest,
//            ClientSecretBasic(SECRET_ID)
            ClientSecretBasic(ru.fwnz.humblr.BuildConfig.API_SECRET)
        ) { response, ex ->
            Log.d(TAG, "performAccessTokenRequest response: $response")
            Log.d(TAG, "performAccessTokenRequest ex: $ex")
            if (response != null) {
                update(response, ex)
                Log.d(TAG, "performAccessTokenRequest authState.accessToken: ${authState.accessToken}")
                Log.d(TAG, "performAccessTokenRequest authState.refreshToken: ${authState.refreshToken}")
                setToken(
                    authState.accessToken,
                    authState.refreshToken
                )

//                authState.performActionWithFreshTokens(authorizationService) { accessToken, idToken, exc ->
//                    Log.d(TAG, "performAccessTokenRequest: $accessToken")
//                    Log.d(TAG, "performAccessTokenRequest: $idToken")
//                    Log.d(TAG, "performAccessTokenRequest: $exc")
//                }
            } else {
                failureCallback.invoke()
            }
        }
    }

//    fun performAccessTokenRequest(
//        authorizationResponse: AuthorizationResponse,
//        tokenExchangeRequest: TokenRequest,
//        authorizationException: AuthorizationException?,
//        failureCallback: () -> Unit
//    ) {
//        update(authorizationResponse, authorizationException)
//        Log.d(TAG, "performAccessTokenRequest: ")
//        authorizationService.performTokenRequest(
//            tokenExchangeRequest,
////            ClientSecretBasic(SECRET_ID)
//            ClientSecretBasic(ru.fwnz.humblr.BuildConfig.API_SECRET)
//        ) { response, ex ->
//            Log.d(TAG, "performAccessTokenRequest response: $response")
//            Log.d(TAG, "performAccessTokenRequest ex: $ex")
//            if (response != null) {
//                update(response, ex)
//                Log.d(TAG, "performAccessTokenRequest authState.accessToken: ${authState.accessToken}")
//                Log.d(TAG, "performAccessTokenRequest authState.refreshToken: ${authState.refreshToken}")
//                setToken(
//                    authState.accessToken,
//                    authState.refreshToken
//                )
//
////                authState.performActionWithFreshTokens(authorizationService) { accessToken, idToken, exc ->
////                    Log.d(TAG, "performAccessTokenRequest: $accessToken")
////                    Log.d(TAG, "performAccessTokenRequest: $idToken")
////                    Log.d(TAG, "performAccessTokenRequest: $exc")
////                }
//            } else {
//                failureCallback.invoke()
//            }
//        }
//    }
//
//    private suspend fun performTokenRefreshSuspend() = suspendCoroutine<Unit> { continuation ->
//        authState.performActionWithFreshTokens(authorizationService) { accessToken, idToken, exc ->
//            when {
//                accessToken != null -> {
//                    Log.d(TAG, "performAccessTokenRequest: $accessToken")
//                    Log.d(TAG, "performAccessTokenRequest: $idToken")
//                    Log.d(TAG, "performAccessTokenRequest: $exc")
//                    setToken(accessToken)
//                }
//                else -> {
//                    Log.d(TAG, "performAccessTokenRequest: $accessToken")
//                    Log.d(TAG, "performAccessTokenRequest: $idToken")
//                    Log.d(TAG, "performAccessTokenRequest: $exc")
//                    refreshToken = null
//                    this.accessToken = null
//                    setToken(null, null)
//                    val intent = Intent(context, IntroActivity::class.java)
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//                    context.startActivity(intent)
//                    if (exc != null)
//                        continuation.resumeWith(Result.failure(exc))
//                    else
//                        continuation.resumeWith(Result.failure(Exception("Something go wrong")))
//                }
//            }
//        }
//    }
//
    private suspend fun performTokenRefreshSuspend()  {
        val clientAuth: ClientAuthentication = ClientSecretBasic(ru.fwnz.humblr.BuildConfig.API_SECRET)
        val tokenRequest = TokenRequest.Builder(authorizationServiceConfiguration, CLIENT_ID)
            .setGrantType(GrantTypeValues.REFRESH_TOKEN)
            .setRefreshToken(refreshToken)
            .build()
        return suspendCoroutine<Unit> { continuation ->
            Log.d(TAG, "performTokenRefreshSuspend: ")
            authorizationService.performTokenRequest(
                tokenRequest,
                clientAuth
            ) { response, ex ->
                Log.d(TAG, "performAccessTokenRequest response: $response")
                Log.d(TAG, "performAccessTokenRequest ex: $ex")
                if (response != null) {
                    update(response, ex)
                    Log.d(TAG, "performAccessTokenRequest authState.accessToken: ${authState.accessToken}")
                    Log.d(TAG, "performAccessTokenRequest authState.refreshToken: ${authState.refreshToken}")
                    setToken(authState.accessToken)
                    continuation.resumeWith(Result.success(Unit))
//                    setToken(
//                        authState.accessToken,
//                        authState.refreshToken
//                    )
                } else {
                    setToken(null, null, IntroActivity::class.java)
                    continuation.resumeWith(Result.failure(Exception(ex)))
                }
            }
        }
    }

    fun performTokenRefresh() = runBlocking {
        Log.d(TAG, "performTokenRefresh: ")
        try {
            performTokenRefreshSuspend()
            val intent = Intent(context, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(intent)
        } catch (e: Throwable) {
            refreshToken = null
            accessToken = null
            setToken(null, null)
            val intent = Intent(context, IntroActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(intent)
        }
    }

//    fun performTokenRefresh() = runBlocking {
//        Log.d(TAG, "performTokenRefresh: ")
//        try {
//            val authDto =  aOAuthApi.getRefreshToken()
//            authState.needsTokenRefresh
//            setToken(authDto.accessToken)
//        } catch (e: Throwable) {
//            refreshToken = null
//            accessToken = null
//            setToken(null, null)
//            val intent = Intent(context, IntroActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//            context.startActivity(intent)
//        }
//    }

//    fun performTokenRefresh() {
//        Log.d(TAG, "performTokenRefresh: ")
//        authState.performActionWithFreshTokens(authorizationService) { accessToken, idToken, exc ->
//            Log.d(TAG, "performAccessTokenRequest: $accessToken")
//            Log.d(TAG, "performAccessTokenRequest: $idToken")
//            Log.d(TAG, "performAccessTokenRequest: $exc")
//            if (exc == null) {
//                setToken(accessToken)
//            } else {
//                refreshToken = null
//                this.accessToken = null
//                setToken(null, null)
//                val intent = Intent(context, IntroActivity::class.java)
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//                context.startActivity(intent)
//            }
//        }
//    }
//
//    fun performAccessTokenRequest(
//        authorizationResponse: AuthorizationResponse,
//        authorizationException: AuthorizationException?,
//        failureCallback: () -> Unit
//    ) {
//        update(authorizationResponse, authorizationException)
//        val tokenExchangeRequest = authorizationResponse.createTokenExchangeRequest()
//        authorizationService.performTokenRequest(
//            tokenExchangeRequest,
//            ClientSecretPost(SECRET_ID)
////            ClientSecretPost(ru.fwnz.humblr.BuildConfig.API_SECRET)
//        ) { response, ex ->
//            if (response != null) {
//                update(response, ex)
//                Log.d(TAG, "performAccessTokenRequest: ${authState.accessToken}")
//                authState.performActionWithFreshTokens(authorizationService) { accessToken, idToken, exc ->
//                    Log.d(TAG, "performAccessTokenRequest: $accessToken")
//                    Log.d(TAG, "performAccessTokenRequest: $idToken")
//                    Log.d(TAG, "performAccessTokenRequest: $exc")
//                }
//            } else {
//                failureCallback.invoke()
//            }
//        }
//    }
//
    companion object {
        private const val SCOPE_NAME = "AppAuthScope"
        private val CLIENT_ID = "XXXXXXXXXXXXXXXXXXXXXX"
        private val SECRET_ID = ""
        private val STATE = "ru.fwnz.humblr:state"
        private val AUTHORIZATION_ENDPOINT = "https://old.reddit.com/api/v1/authorize".toUri()
        private val TOKEN_ENDPOINT = "https://www.reddit.com/api/v1/access_token".toUri()
        private val LOGOUT_ENDPOINT = "https://www.reddit.com/api/v1/revoke_token".toUri()
//        private val POST_AUTHORIZATION_REDIRECT = "https://oauth2redirect".toUri()
//        private val POST_LOGOUT_REDIRECT = "https://oauth2redirect".toUri()
        private val POST_AUTHORIZATION_REDIRECT = "ru.fwnz.humblr://oauth2redirect".toUri()
        private val POST_LOGOUT_REDIRECT = "ru.fwnz.humblr://oauth2redirect".toUri()
        private const val REQUIRED_SCOPES =
            "identity edit flair history modconfig modflair modlog modposts modwiki mysubreddits privatemessages read report save submit subscribe vote wikiedit wikiread"
//        private const val REQUIRED_SCOPES =
//            "read"
        private const val PROMPT_POLICE = "select_account"
        private val GRANT_TYPE = "authorization_code"
    }
}