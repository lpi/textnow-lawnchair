package app.lawnchair.auth

import android.net.Uri

object TextNowSessionContract {
    const val AUTHORITY = "com.enflick.android.TextNow.launchersession"
    val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY")
    const val METHOD_GET_SESSION = "getSession"
    const val KEY_SESSION_ID = "session_id"
    const val KEY_USERNAME = "username"
    const val KEY_GUID = "guid"
    const val KEY_LOGGED_IN = "logged_in"
}
