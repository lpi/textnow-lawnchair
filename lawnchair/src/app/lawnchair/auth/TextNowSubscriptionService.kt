package app.lawnchair.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.GET

interface TextNowSubscriptionService {

    @GET("wireless/subscriptions/v4/active_user_subscription")
    suspend fun getActiveSubscription(): ActiveSubscriptionResponse

    companion object {
        fun create(apiClient: TextNowApiClient): TextNowSubscriptionService {
            return apiClient.retrofit.create(TextNowSubscriptionService::class.java)
        }
    }
}

@Serializable
data class ActiveSubscriptionResponse(
    val subscription: SubscriptionDto? = null,
)

@Serializable
data class SubscriptionDto(
    val id: String = "",
    val usage: UsageDto? = null,
    val status: String = "",
    val plan: PlanDto? = null,
)

@Serializable
data class UsageDto(
    val data: UsageInfoDto? = null,
)

@Serializable
data class UsageInfoDto(
    val unit: Int = 0,
    val total: Int = 0,
    val used: Int = 0,
)

@Serializable
data class PlanDto(
    val id: String = "",
    val name: String = "",
    @SerialName("description_short")
    val descriptionShort: String = "",
)
