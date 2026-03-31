package app.lawnchair.auth

import android.content.Context
import android.util.Log

object TextNowSessionProvider {

    private const val TAG = "TextNowSessionProvider"

    fun getSession(context: Context): TextNowSession? {
        return try {
            val bundle = context.contentResolver.call(
                TextNowSessionContract.CONTENT_URI,
                TextNowSessionContract.METHOD_GET_SESSION,
                null,
                null,
            ) ?: return null

            val loggedIn = bundle.getBoolean(TextNowSessionContract.KEY_LOGGED_IN, false)
            if (!loggedIn) return null

            val sessionId = bundle.getString(TextNowSessionContract.KEY_SESSION_ID) ?: return null
            val username = bundle.getString(TextNowSessionContract.KEY_USERNAME) ?: return null
            val guid = bundle.getString(TextNowSessionContract.KEY_GUID) ?: return null

            TextNowSession(sessionId = sessionId, username = username, guid = guid)
        } catch (e: SecurityException) {
            Log.w(TAG, "Signature mismatch — cannot read TextNow session", e)
            null
        } catch (e: Exception) {
            Log.w(TAG, "Failed to read TextNow session", e)
            null
        }
    }
}
