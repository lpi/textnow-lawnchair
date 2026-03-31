package app.lawnchair.auth

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

private const val SESSION_ID_HEADER = "client_id"
private const val GRPC_SESSION_ID = "tn-session-id"

class TextNowSessionInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val sessionId = TextNowSessionProvider.getSession(context)?.sessionId.orEmpty()

        return chain.request()
            .newBuilder()
            .addHeader(SESSION_ID_HEADER, sessionId)
            .addHeader(GRPC_SESSION_ID, sessionId)
            .build()
            .let(chain::proceed)
    }
}
