package app.lawnchair.auth

import android.content.Context
import android.os.Build
import java.util.Locale
import java.util.UUID
import okhttp3.Interceptor
import okhttp3.Response

private const val SESSION_ID_HEADER = "client_id"
private const val GRPC_SESSION_ID = "tn-session-id"
private const val GRPC_USER_AGENT = "tn-user-agent"
private const val GRPC_CLIENT_TYPE = "client_type"
private const val GRPC_REQUEST_ID = "tn-request-id"

class TextNowSessionInterceptor(private val context: Context) : Interceptor {

    private val userAgent: String by lazy {
        val appName = "TextNow"
        val osVersion = "Android OS ${Build.VERSION.RELEASE}"
        val deviceInfo = "${Build.MODEL}; $osVersion; ${Locale.getDefault()}"
        "$appName ($deviceInfo)"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val sessionId = TextNowSessionProvider.getSession(context)?.sessionId.orEmpty()

        return chain.request()
            .newBuilder()
            .addHeader(SESSION_ID_HEADER, sessionId)
            .addHeader(GRPC_SESSION_ID, sessionId)
            .addHeader(GRPC_USER_AGENT, userAgent)
            .addHeader(GRPC_CLIENT_TYPE, "TN_ANDROID")
            .addHeader(GRPC_REQUEST_ID, UUID.randomUUID().toString())
            .build()
            .let(chain::proceed)
    }
}
