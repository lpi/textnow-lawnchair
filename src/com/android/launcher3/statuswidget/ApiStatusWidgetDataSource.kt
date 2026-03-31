package com.android.launcher3.statuswidget

import android.content.Context
import android.util.Log
import app.lawnchair.auth.TextNowApiClient
import app.lawnchair.auth.TextNowSubscriptionService
import kotlinx.coroutines.runBlocking
import me.textnow.api.loyalty.v1.GetLoyaltyInfoRequest
import me.textnow.api.loyalty.v1.GetLoyaltyInfoResponse
import me.textnow.api.loyalty.v1.QuestAction
import me.textnow.api.loyalty.v1.QuestStatus
import me.textnow.api.loyalty.v1.RewardDuration
import me.textnow.api.loyalty.v1.RewardItem

class ApiStatusWidgetDataSource(context: Context) : StatusWidgetDataSource {

    private val fallback = DemoStatusWidgetDataSource()
    private val apiClient = TextNowApiClient.getInstance(context)
    private val subscriptionService = TextNowSubscriptionService.create(apiClient)

    override fun getStatusData(): StatusWidgetData {
        return try {
            val dataUsage = fetchDataUsage()
            val loyaltyInfo = fetchLoyaltyInfo()

            StatusWidgetData(
                dataUsedBytes = dataUsage.first,
                dataTotalBytes = dataUsage.second,
                loyaltyPoints = loyaltyInfo?.balance?.toInt() ?: 0,
                offersAvailable = loyaltyInfo?.available_offers?.size ?: 0,
            )
        } catch (e: Exception) {
            Log.w(TAG, "Failed to fetch status data", e)
            fallback.getStatusData()
        }
    }

    override fun getOffers(): List<OfferItem> {
        return try {
            val loyaltyInfo = fetchLoyaltyInfo() ?: return fallback.getOffers()
            loyaltyInfo.available_offers.map { offer ->
                OfferItem(
                    id = offer.offer_id,
                    title = offer.title,
                    description = offer.subtitle,
                    expiryLabel = "",
                )
            }.ifEmpty { fallback.getOffers() }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to fetch offers", e)
            fallback.getOffers()
        }
    }

    override fun getRewards(): List<LoyaltyReward> {
        return try {
            val loyaltyInfo = fetchLoyaltyInfo() ?: return emptyList()
            val balance = loyaltyInfo.balance
            loyaltyInfo.available_rewards
                .filter { !it.disabled && it.reward_item != RewardItem.REWARD_ITEM_UNKNOWN }
                .map { reward ->
                    LoyaltyReward(
                        id = reward.reward_id,
                        type = reward.reward_item.toTypeString(),
                        points = reward.points,
                        duration = reward.duration.toDurationString(),
                        canRedeem = balance >= reward.points,
                    )
                }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to fetch rewards", e)
            emptyList()
        }
    }

    override fun getQuests(): List<QuestItem> {
        return try {
            val loyaltyInfo = fetchLoyaltyInfo() ?: return emptyList()
            loyaltyInfo.quests
                .filter { quest ->
                    quest.actions.none { action ->
                        action.action in EXCLUDED_QUEST_ACTIONS
                    }
                }
                .map { quest ->
                    QuestItem(
                        id = quest.quest_id,
                        title = quest.title,
                        description = quest.description,
                        imageUrl = quest.image_url,
                        label = quest.label,
                        points = quest.points,
                        status = quest.status.toStatusString(),
                        currentProgress = quest.actions.sumOf { it.count },
                        requiredProgress = quest.actions.sumOf { it.target },
                    )
                }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to fetch quests", e)
            emptyList()
        }
    }

    private fun fetchDataUsage(): Pair<Long, Long> = runBlocking {
        try {
            val response = subscriptionService.getActiveSubscription()
            val usageInfo = response.subscription?.usage?.data
            val usedBytes = (usageInfo?.used?.toLong() ?: 0L) * MB_TO_BYTES
            val totalBytes = (usageInfo?.total?.toLong() ?: 0L) * MB_TO_BYTES
            Pair(usedBytes, totalBytes)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to fetch subscription data", e)
            Pair(0L, 0L)
        }
    }

    private fun fetchLoyaltyInfo(): GetLoyaltyInfoResponse? = runBlocking {
        try {
            val response = apiClient.loyaltyService.GetLoyaltyInfo()
                .execute(GetLoyaltyInfoRequest())
            Log.d(TAG, "Loyalty info: balance=${response.balance}, quests=${response.quests.size}, rewards=${response.available_rewards.size}, offers=${response.available_offers.size}")
            response
        } catch (e: Exception) {
            Log.w(TAG, "Failed to fetch loyalty info", e)
            null
        }
    }

    companion object {
        private const val TAG = "ApiStatusWidgetDataSrc"
        private const val MB_TO_BYTES = 1_000_000L

        private val EXCLUDED_QUEST_ACTIONS = setOf(
            QuestAction.QUEST_ACTION_CURRENT_FINANCIAL_SIGNUP,
            QuestAction.QUEST_ACTION_LOYALTY_OPT_IN,
            QuestAction.QUEST_ACTION_UNKNOWN,
        )

        private fun RewardItem.toTypeString(): String = when (this) {
            RewardItem.REWARD_ITEM_LOCK_IN_NUMBER -> "LIN"
            RewardItem.REWARD_ITEM_SIM_CARD -> "SIM_CARD"
            RewardItem.REWARD_ITEM_ADFREE_PLUS -> "ADFREE_PLUS"
            RewardItem.REWARD_ITEM_HOUR_BROWSE_PASS -> "HOUR_PASS"
            RewardItem.REWARD_ITEM_VERIFICATION_CODE -> "VERIFICATION_CODES"
            RewardItem.REWARD_ITEM_SHOW_CALLER -> "SHOW_CALLER"
            RewardItem.REWARD_ITEM_VOICEMAIL_TRANSCRIPTION -> "VOICEMAIL_TRANSCRIPTION"
            RewardItem.REWARD_ITEM_APP_ICON_MINIMALIST,
            RewardItem.REWARD_ITEM_APP_ICON_EYE_CANDY_PURPLE,
            RewardItem.REWARD_ITEM_APP_ICON_EYE_CANDY_IOS,
            RewardItem.REWARD_ITEM_APP_ICON_EYE_CANDY_AQUA,
            RewardItem.REWARD_ITEM_APP_ICON_TIGER,
            RewardItem.REWARD_ITEM_APP_ICON_PINK_CAMO,
            RewardItem.REWARD_ITEM_APP_ICON_GREEN_CAMO,
            RewardItem.REWARD_ITEM_APP_ICON_PRIDE,
            RewardItem.REWARD_ITEM_APP_ICON_VINTAGE_TEXTNOW,
            RewardItem.REWARD_ITEM_APP_ICON_ALL_HANDS_2025,
            RewardItem.REWARD_ITEM_APP_ICON_4TH_OF_JULY_2025,
            RewardItem.REWARD_ITEM_APP_ICON_HALLOWEEN,
            RewardItem.REWARD_ITEM_APP_ICON_WINTER,
            RewardItem.REWARD_ITEM_APP_ICON_SPRING_BLOOM -> "APP_ICON"
            else -> "UNKNOWN"
        }

        private fun RewardDuration.toDurationString(): String = when (this) {
            RewardDuration.REWARD_DURATION_PERMANENT -> "PERMANENT"
            RewardDuration.REWARD_DURATION_1_MONTH -> "ONE_MONTH"
            RewardDuration.REWARD_DURATION_1_WEEK -> "ONE_WEEK"
            RewardDuration.REWARD_DURATION_1_DAY -> "ONE_DAY"
            RewardDuration.REWARD_DURATION_1_HOUR -> "ONE_HOUR"
            RewardDuration.REWARD_DURATION_SINGLE_USE -> "SINGLE_USE"
            RewardDuration.REWARD_DURATION_30_MINUTES -> "THIRTY_MINUTES"
            else -> "UNKNOWN"
        }

        private fun QuestStatus.toStatusString(): String = when (this) {
            QuestStatus.QUEST_STATUS_IN_PROGRESS -> "IN_PROGRESS"
            QuestStatus.QUEST_STATUS_READY_FOR_COMPLETION -> "READY_TO_COMPLETE"
            QuestStatus.QUEST_STATUS_COMPLETED -> "COMPLETED"
            else -> "UNKNOWN"
        }
    }
}
