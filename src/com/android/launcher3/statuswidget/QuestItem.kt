package com.android.launcher3.statuswidget

data class QuestItem(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val label: String,
    val points: Long,
    val status: String,
    val currentProgress: Long,
    val requiredProgress: Long,
)
