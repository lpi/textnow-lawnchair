package com.android.launcher3.statuswidget

interface StatusWidgetDataSource {
    fun getStatusData(): StatusWidgetData
    fun getOffers(): List<OfferItem>
    fun getRewards(): List<LoyaltyReward>
    fun getQuests(): List<QuestItem>
}
