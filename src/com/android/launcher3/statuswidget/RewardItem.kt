package com.android.launcher3.statuswidget

data class LoyaltyReward(
    val id: String,
    val type: String,
    val points: Long,
    val duration: String,
    val canRedeem: Boolean,
)
