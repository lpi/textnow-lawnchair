package com.android.launcher3.statuswidget

import android.content.Context

object StatusWidgetRepository {

    @Volatile
    private var dataSource: StatusWidgetDataSource? = null

    private fun getDataSource(context: Context): StatusWidgetDataSource {
        return dataSource ?: synchronized(this) {
            dataSource ?: ApiStatusWidgetDataSource(context.applicationContext).also { dataSource = it }
        }
    }

    fun getStatusData(context: Context): StatusWidgetData = getDataSource(context).getStatusData()

    fun getOffers(context: Context): List<OfferItem> = getDataSource(context).getOffers()

    fun getRewards(context: Context): List<LoyaltyReward> = getDataSource(context).getRewards()

    fun getQuests(context: Context): List<QuestItem> = getDataSource(context).getQuests()
}
