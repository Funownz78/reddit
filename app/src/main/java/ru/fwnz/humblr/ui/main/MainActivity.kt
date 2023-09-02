package ru.fwnz.humblr.ui.main

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Base64
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import ru.fwnz.humblr.App
import ru.fwnz.humblr.R
import ru.fwnz.humblr.data.AppAuthComponent
import ru.fwnz.humblr.databinding.ActivityMainBinding
import ru.fwnz.humblr.ui.intro.IntroActivity

private const val TAG = "AppAuthComponent"
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var appAuthComponent: AppAuthComponent


    override fun onCreate(savedInstanceState: Bundle?) {

//        val authPrefs: SharedPreferences = getSharedPreferences("auth", MODE_PRIVATE)
//        val editor = authPrefs.edit()
//        editor.putString("accessToken", "2094123231851-bnD-rIQEYMLXPrSRda0J5pbMxyemOQ")
//        editor.putString("refreshToken", "2094123231851-RXTYuOmUuFroKPH3IqePgk-6W4bk6Q")
//        editor.putString("stateJson", "{&quot;refreshToken&quot;:&quot;2094123231851-RXTYuOmUuFroKPH3IqePgk-6W4bk6Q&quot;,&quot;scope&quot;:&quot;wikiedit save wikiread modwiki edit vote mysubreddits subscribe privatemessages modconfig read modlog modposts modflair report flair submit identity history&quot;,&quot;lastAuthorizationResponse&quot;:{&quot;request&quot;:{&quot;configuration&quot;:{&quot;authorizationEndpoint&quot;:&quot;https:\\/\\/old.reddit.com\\/api\\/v1\\/authorize&quot;,&quot;tokenEndpoint&quot;:&quot;https:\\/\\/www.reddit.com\\/api\\/v1\\/access_token&quot;,&quot;endSessionEndpoint&quot;:&quot;https:\\/\\/www.reddit.com\\/api\\/v1\\/revoke_token&quot;},&quot;clientId&quot;:&quot;vbYbH-upnoaJtMKnzfKKNA&quot;,&quot;responseType&quot;:&quot;code&quot;,&quot;redirectUri&quot;:&quot;ru.fwnz.humblr:\\/\\/oauth2redirect&quot;,&quot;scope&quot;:&quot;identity edit flair history modconfig modflair modlog modposts modwiki mysubreddits privatemessages read report save submit subscribe vote wikiedit wikiread&quot;,&quot;state&quot;:&quot;ru.fwnz.humblr:state&quot;,&quot;nonce&quot;:&quot;fpEVR1utXIoqvTGNrj2MSQ&quot;,&quot;codeVerifier&quot;:&quot;HBfDbWnr45_S4nAohMoo-q_XPUP83mhEazcpL3Ej9Vfj1U6KfVVPdgQWVAp9Xz8qze0X3zFS_zPqhKvDL-he-A&quot;,&quot;codeVerifierChallenge&quot;:&quot;ndLcpkI2scT-YSva_HvPITOush78WQfdXcnZEnv7TMk&quot;,&quot;codeVerifierChallengeMethod&quot;:&quot;S256&quot;,&quot;additionalParameters&quot;:{&quot;duration&quot;:&quot;permanent&quot;}},&quot;state&quot;:&quot;ru.fwnz.humblr:state&quot;,&quot;code&quot;:&quot;CIIaokHlsN7qkPRWPPMVRGTh5zeMRA&quot;,&quot;additional_parameters&quot;:{}},&quot;mLastTokenResponse&quot;:{&quot;request&quot;:{&quot;configuration&quot;:{&quot;authorizationEndpoint&quot;:&quot;https:\\/\\/old.reddit.com\\/api\\/v1\\/authorize&quot;,&quot;tokenEndpoint&quot;:&quot;https:\\/\\/www.reddit.com\\/api\\/v1\\/access_token&quot;,&quot;endSessionEndpoint&quot;:&quot;https:\\/\\/www.reddit.com\\/api\\/v1\\/revoke_token&quot;},&quot;clientId&quot;:&quot;vbYbH-upnoaJtMKnzfKKNA&quot;,&quot;grantType&quot;:&quot;refresh_token&quot;,&quot;refreshToken&quot;:&quot;2094123231851-RXTYuOmUuFroKPH3IqePgk-6W4bk6Q&quot;,&quot;additionalParameters&quot;:{}},&quot;token_type&quot;:&quot;bearer&quot;,&quot;access_token&quot;:&quot;2094123231851-bnD-rIQEYMLXPrSRda0J5pbMxyemOQ&quot;,&quot;expires_at&quot;:1683619743039,&quot;refresh_token&quot;:&quot;2094123231851-RXTYuOmUuFroKPH3IqePgk-6W4bk6Q&quot;,&quot;scope&quot;:&quot;wikiedit save wikiread modwiki edit vote mysubreddits subscribe privatemessages modconfig read modlog modposts modflair report flair submit identity history&quot;,&quot;additionalParameters&quot;:{}}}")
//        editor.apply()
//        finish()

        super.onCreate(savedInstanceState)

        appAuthComponent = App.daggerComponent.getAppAuthComponent()


        val authorizationResponse = AuthorizationResponse.fromIntent(intent)
        val authorizationException = AuthorizationException.fromIntent(intent)
        Log.d(TAG, "onCreate: $authorizationResponse")
        val id64 = Base64.encodeToString(
            "vbYbH-upnoaJtMKnzfKKNA:".toByteArray(),
            Base64.NO_WRAP
        )
        Log.d(TAG, "onCreate id64: $id64")
        Log.d(TAG, "onCreate authorizationCode: ${authorizationResponse?.authorizationCode}")
        authorizationResponse?.authorizationCode?.let {
            val acode64 = Base64.encodeToString(
                authorizationResponse.authorizationCode?.toByteArray(),
                Base64.NO_WRAP
            )
            Log.d(TAG, "onCreate authorizationCode64: $acode64")
        }
        Log.d(TAG, "onCreate accessToken: ${authorizationResponse?.accessToken}")
        Log.d(TAG, "onCreate idToken: ${authorizationResponse?.idToken}")
        Log.d(TAG, "onCreate scope: ${authorizationResponse?.scope}")
        Log.d(TAG, "onCreate tokenType: ${authorizationResponse?.tokenType}")
        Log.d(TAG, "onCreate state: ${authorizationResponse?.state}")
        Log.d(TAG, "onCreate accessTokenExpirationTime: ${authorizationResponse?.accessTokenExpirationTime}")
        Log.d(TAG, "onCreate request: ${authorizationResponse?.request}")
        Log.d(TAG, "onCreate additionalParameters: ${authorizationResponse?.additionalParameters.toString()}")
        Log.d(TAG, "onCreate: $authorizationException")

        if (!appAuthComponent.isAuthorized) {
            if (authorizationResponse == null && authorizationException == null) {
                Log.d(TAG, "onCreate: authorizationResponse == null && authorizationException == null")
                val intent = Intent(this@MainActivity, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            } else {
                if (authorizationResponse != null) {
                    Log.d(TAG, "onCreate: authorizationResponse")
                    val tokenExchangeRequest = appAuthComponent.prepareTokemRequest(intent)
                    appAuthComponent.performAccessTokenRequest(
                        authorizationResponse,
                        tokenExchangeRequest,
                        authorizationException
                    ) {
                        val intent = Intent(this@MainActivity, IntroActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                    }
                } else {
                    Log.d(TAG, "onCreate: authorizationException")
                    val intent = Intent(this@MainActivity, IntroActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                }
            }
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}