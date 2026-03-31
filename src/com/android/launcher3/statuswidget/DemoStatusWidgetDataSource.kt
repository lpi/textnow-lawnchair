package com.android.launcher3.statuswidget

class DemoStatusWidgetDataSource : StatusWidgetDataSource {

    override fun getStatusData(): StatusWidgetData = StatusWidgetData(
        dataUsedBytes = 2_500_000_000L,
        dataTotalBytes = 5_000_000_000L,
        loyaltyPoints = 1250,
        offersAvailable = 3,
    )

    override fun getOffers(): List<OfferItem> = listOf(
        OfferItem(
            id = "offer_1",
            title = "Double Data Weekend",
            description = "Get double data this weekend only. Automatically applied to your account.",
            expiryLabel = "Expires Sun",
        ),
        OfferItem(
            id = "offer_2",
            title = "500 Bonus Points",
            description = "Earn 500 bonus loyalty points when you refer a friend this month.",
            expiryLabel = "Expires Mar 31",
        ),
        OfferItem(
            id = "offer_3",
            title = "Free 1 GB Add-On",
            description = "Claim a free 1 GB data add-on as a thank you for being a loyal member.",
            expiryLabel = "Expires Apr 15",
        ),
    )

    override fun getRewards(): List<LoyaltyReward> = emptyList()

    override fun getQuests(): List<QuestItem> = emptyList()
}
