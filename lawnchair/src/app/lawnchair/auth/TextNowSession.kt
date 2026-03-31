package app.lawnchair.auth

data class TextNowSession(
    val sessionId: String,
    val username: String,
    val guid: String,
)
