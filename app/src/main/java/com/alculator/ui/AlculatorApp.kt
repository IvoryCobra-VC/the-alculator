package com.alculator.ui

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alculator.data.Drink
import com.alculator.ui.theme.*
import com.alculator.viewmodel.DrinkViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AlculatorApp(viewModel: DrinkViewModel) {
    val drinks by viewModel.drinks.collectAsStateWithLifecycle()
    val ranked = remember(drinks) { drinks.sortedByDescending { it.unitsPerPound } }
    var showSheet by remember { mutableStateOf(false) }
    var editingDrink by remember { mutableStateOf<Drink?>(null) }
    var showClearDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box(Modifier.fillMaxSize().limewashBackground()) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "the Alculator",
                            color = Espresso,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp,
                            letterSpacing = (-0.3).sp
                        )
                    },
                    actions = {
                        if (drinks.isNotEmpty()) {
                            IconButton(onClick = { shareRankings(context, ranked) }) {
                                Icon(Icons.Default.Share, "Share", tint = Taupe)
                            }
                            IconButton(onClick = { showClearDialog = true }) {
                                Icon(Icons.Default.DeleteSweep, "Clear all", tint = Taupe)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    text = { Text("Add Drink", fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.Add, null) },
                    onClick = { editingDrink = null; showSheet = true },
                    containerColor = Clay,
                    contentColor = ChalkLight
                )
            }
        ) { padding ->
            if (drinks.isEmpty()) {
                EmptyState(Modifier.padding(padding))
            } else {
                LazyColumn(
                    modifier = Modifier.padding(padding),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val best = ranked.firstOrNull()?.unitsPerPound ?: 1.0
                    itemsIndexed(ranked, key = { _, d -> d.id }) { index, drink ->
                        DrinkCard(
                            rank = index + 1,
                            drink = drink,
                            bestUnitsPerPound = best,
                            onDelete = { viewModel.remove(drink.id) },
                            onEdit = { editingDrink = drink; showSheet = true },
                            modifier = Modifier.animateItemPlacement()
                        )
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }

    if (showSheet) {
        AddDrinkSheet(
            editing = editingDrink,
            onSave = {
                if (editingDrink != null) viewModel.update(it) else viewModel.add(it)
                showSheet = false
                editingDrink = null
            },
            onDismiss = { showSheet = false; editingDrink = null }
        )
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            containerColor = ChalkLight,
            title = { Text("Clear all?", color = Espresso) },
            text = {
                Text(
                    "Remove all ${drinks.size} drink${if (drinks.size != 1) "s" else ""} from the comparison?",
                    color = Taupe
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.clear(); showClearDialog = false }) {
                    Text("Clear", color = Clay, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel", color = Taupe)
                }
            }
        )
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize().padding(horizontal = 40.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "the\nAlculator",
                color = Espresso,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Normal,
                fontSize = 46.sp,
                lineHeight = 48.sp,
                letterSpacing = (-0.5).sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(Modifier.height(20.dp))
            Box(
                Modifier
                    .width(48.dp)
                    .height(2.dp)
                    .background(Clay)
            )
            Spacer(Modifier.height(20.dp))
            Text(
                text = "Add your first drink to compare value by the unit",
                color = Taupe,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

private fun shareRankings(context: Context, ranked: List<Drink>) {
    val medals = listOf("🥇", "🥈", "🥉")
    val lines = ranked.mapIndexed { i, d ->
        val medal = medals.getOrElse(i) { "${i + 1}." }
        val name = d.name.ifBlank { "Drink ${i + 1}" }
        val vol = if (d.quantity > 1) "${d.singleVolumeMl.toLong()}ml ×${d.quantity}" else "${d.volumeMl.toLong()}ml"
        "$medal $name ($vol, ${d.abv}%) — ${"%.2f".format(d.unitsPerPound)} units/£"
    }.joinToString("\n")

    val text = "the Alculator Rankings\n\n$lines\n\nRanked by units of alcohol per £"
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "Share rankings"))
}
