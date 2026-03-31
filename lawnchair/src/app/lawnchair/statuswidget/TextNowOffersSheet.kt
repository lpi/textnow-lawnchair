package app.lawnchair.statuswidget

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.lawnchair.views.ComposeBottomSheet
import com.android.launcher3.R
import com.android.launcher3.statuswidget.OfferItem
import com.android.launcher3.statuswidget.QuestItem
import com.android.launcher3.statuswidget.LoyaltyReward
import com.android.launcher3.statuswidget.StatusWidgetRepository
import com.android.launcher3.views.ActivityContext

object TextNowOffersSheet {

    fun <T> show(context: T) where T : Context, T : ActivityContext {
        ComposeBottomSheet.show(
            context,
            contentPaddings = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        ) {
            SheetContent()
        }
    }

    @Composable
    private fun SheetContent() {
        val context = LocalContext.current
        val offers = StatusWidgetRepository.getOffers(context)
        val quests = StatusWidgetRepository.getQuests(context)
        val rewards = StatusWidgetRepository.getRewards(context)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
        ) {
            if (quests.isNotEmpty()) {
                SectionHeader(stringResource(R.string.textnow_quests_title))
                quests.forEach { quest ->
                    QuestCard(quest)
                    Spacer(modifier = Modifier.height(12.dp))
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (rewards.isNotEmpty()) {
                SectionHeader(stringResource(R.string.textnow_rewards_title))
                rewards.forEach { reward ->
                    RewardCard(reward)
                    Spacer(modifier = Modifier.height(12.dp))
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (offers.isNotEmpty()) {
                SectionHeader(stringResource(R.string.textnow_status_offers_title))
                offers.forEach { offer ->
                    OfferCard(offer)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }

    @Composable
    private fun SectionHeader(title: String) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(16.dp))
    }

    @Composable
    private fun QuestCard(quest: QuestItem) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = quest.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.textnow_quest_points, quest.points),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }

                if (quest.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = quest.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                if (quest.requiredProgress > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    val progress = if (quest.requiredProgress > 0) {
                        quest.currentProgress.toFloat() / quest.requiredProgress.toFloat()
                    } else 0f
                    LinearProgressIndicator(
                        progress = { progress.coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.small),
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${quest.currentProgress}/${quest.requiredProgress}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                if (quest.status == "READY_TO_COMPLETE") {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.textnow_quest_ready),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }

    @Composable
    private fun RewardCard(reward: LoyaltyReward) {
        val alpha = if (reward.canRedeem) 1f else 0.6f
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = alpha),
            ),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = reward.type.rewardDisplayName(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = reward.duration.durationDisplayName(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    text = stringResource(R.string.textnow_reward_cost, reward.points),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (reward.canRedeem) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                )
            }
        }
    }

    @Composable
    private fun OfferCard(offer: OfferItem) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = offer.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = offer.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (offer.expiryLabel.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = offer.expiryLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }

    private fun String.rewardDisplayName(): String = when (this) {
        "LIN" -> "Lock-in Number"
        "SIM_CARD" -> "Free SIM Card"
        "ADFREE_PLUS" -> "Ad-Free Plus"
        "HOUR_PASS" -> "Hour Browse Pass"
        "VERIFICATION_CODES" -> "Verification Codes"
        "SHOW_CALLER" -> "See Who's Calling"
        "VOICEMAIL_TRANSCRIPTION" -> "Voicemail Transcription"
        "APP_ICON" -> "App Icon"
        else -> this
    }

    private fun String.durationDisplayName(): String = when (this) {
        "PERMANENT" -> "Permanent"
        "ONE_MONTH" -> "1 Month"
        "ONE_WEEK" -> "1 Week"
        "ONE_DAY" -> "1 Day"
        "ONE_HOUR" -> "1 Hour"
        "SINGLE_USE" -> "Single Use"
        "THIRTY_MINUTES" -> "30 Minutes"
        else -> this
    }
}
