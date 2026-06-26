package com.alculator.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alculator.data.Drink
import com.alculator.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrinkCard(
    rank: Int,
    drink: Drink,
    bestUnitsPerPound: Double,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val valueRatio = if (bestUnitsPerPound > 0.0) {
        (drink.unitsPerPound / bestUnitsPerPound).toFloat().coerceIn(0f, 1f)
    } else 0f

    val animatedRatio by animateFloatAsState(
        targetValue = valueRatio,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "value_bar"
    )

    val rankColor = when (rank) {
        1 -> MedalGold
        2 -> MedalSilver
        3 -> MedalBronze
        else -> Taupe
    }

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { it == SwipeToDismissBoxValue.EndToStart }
    )

    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) onDelete()
    }

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Clay),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = ChalkLight,
                    modifier = Modifier.padding(end = 24.dp)
                )
            }
        }
    ) {
        Surface(
            onClick = onEdit,
            shape = RoundedCornerShape(18.dp),
            color = ChalkLight,
            border = BorderStroke(0.5.dp, Hairline),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = if (rank == 1) 8.dp else 4.dp,
                    shape = RoundedCornerShape(18.dp),
                    spotColor = ClayDim.copy(alpha = 0.30f),
                    ambientColor = Espresso.copy(alpha = 0.18f)
                )
        ) {
            Column(Modifier.padding(18.dp)) {

                // — Top row: rank · name+chips · score —
                Row(verticalAlignment = Alignment.Top) {

                    Text(
                        text = if (rank < 10) "0$rank" else "$rank",
                        color = rankColor,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Thin,
                        modifier = Modifier.width(46.dp)
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = drink.name.ifBlank { "Drink $rank" },
                            color = Espresso,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = (-0.2).sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.height(7.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                            StatChip("£${"%.2f".format(drink.price)}")
                            StatChip(volumeDisplay(drink))
                            StatChip("${drink.abv}%")
                        }
                    }

                    Spacer(Modifier.width(10.dp))

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "%.2f".format(drink.unitsPerPound),
                            color = Clay,
                            fontSize = 27.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = "UNITS / £",
                            color = Taupe,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 1.sp,
                            textAlign = TextAlign.End
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // — Value bar —
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(ValueBarBg)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedRatio)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(3.dp))
                            .background(Brush.horizontalGradient(listOf(ClayDim, Clay)))
                    )
                }

                Spacer(Modifier.height(11.dp))

                // — Footer —
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (rank == 1) {
                        Text(
                            text = "BEST VALUE",
                            color = Clay,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                    } else {
                        Spacer(Modifier.weight(1f))
                    }
                    Text(
                        text = "£${"%.2f".format(drink.costPerUnit)} / unit",
                        color = Taupe,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun StatChip(text: String) {
    Surface(
        shape = RoundedCornerShape(5.dp),
        color = Sand,
        border = BorderStroke(0.5.dp, Hairline)
    ) {
        Text(
            text = text,
            color = Taupe,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
        )
    }
}

private fun volumeDisplay(drink: Drink): String {
    val single = formatMl(drink.singleVolumeMl)
    return if (drink.quantity > 1) "$single ×${drink.quantity}" else single
}

private fun formatMl(ml: Double): String = when {
    ml >= 1000.0 -> "${"%.1f".format(ml / 1000.0)}L"
    ml == ml.toLong().toDouble() -> "${ml.toLong()}ml"
    else -> "${"%.0f".format(ml)}ml"
}
