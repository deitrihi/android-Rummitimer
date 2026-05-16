// 게임 결과 화면 — 플레이어 랭킹과 점수/패널티를 표시하고 광고 후 초기화
package com.deitrihi.rummitimer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

private data class PlayerResult(
    val originalIndex: Int,
    val fruitEmoji: String,
    val score: Int?,
    val penaltyCount: Int,
    val rank: Int,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    playerCount: Int,
    fruitIndices: List<Int>,
    scores: List<Int?>,
    penalties: List<Int>,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val ranked = (0 until playerCount).map { i ->
        PlayerResult(i, FRUIT_EMOJIS[fruitIndices[i]], scores[i], penalties[i], 0)
    }.sortedWith(compareBy<PlayerResult, Int?>(nullsLast()) { it.score }.thenBy { it.penaltyCount })
        .mapIndexed { idx, p -> p.copy(rank = idx + 1) }

    val totalPenalty = (0 until playerCount).sumOf { penalties[it] }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.result_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 8.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.rank_label),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(2f)
                )
                Text(
                    text = stringResource(R.string.score_label),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.penalty_label),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End
                )
            }
            HorizontalDivider()

            ranked.forEach { player ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(2f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = when (player.rank) {
                                1 -> "🥇"; 2 -> "🥈"; 3 -> "🥉"
                                else -> "${player.rank}위"
                            },
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(text = player.fruitEmoji, style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "P${player.originalIndex + 1}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Text(
                        text = if (player.score != null) "${player.score}점" else "-",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = stringResource(R.string.penalty_count_format, player.penaltyCount),
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (player.penaltyCount > 0) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.End
                    )
                }
                HorizontalDivider()
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.low_score_wins),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.total_penalty_format, totalPenalty),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onComplete,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = stringResource(R.string.btn_complete_reset),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
