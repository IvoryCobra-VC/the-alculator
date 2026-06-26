@file:OptIn(ExperimentalMaterial3Api::class)

package com.alculator.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.alculator.data.Drink
import com.alculator.data.VolumePreset
import com.alculator.data.VolumeUnit
import com.alculator.data.lookupBarcode
import com.alculator.ui.theme.*
import java.util.UUID
import kotlinx.coroutines.launch

private val QUANTITIES = listOf(1, 2, 4, 6, 8, 12)

private fun fmtNum(d: Double): String =
    if (d == d.toLong().toDouble()) d.toLong().toString() else d.toString()

@Composable
fun AddDrinkSheet(
    onSave: (Drink) -> Unit,
    onDismiss: () -> Unit,
    editing: Drink? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf(editing?.name ?: "") }
    var price by remember { mutableStateOf(editing?.price?.let { fmtNum(it) } ?: "") }
    var abv by remember { mutableStateOf(editing?.abv?.let { fmtNum(it) } ?: "") }
    var volume by remember { mutableStateOf(editing?.singleVolumeMl?.let { fmtNum(it) } ?: "") }
    var selectedUnit by remember { mutableStateOf(VolumeUnit.ML) }
    var selectedQuantity by remember { mutableIntStateOf(editing?.quantity ?: 1) }
    var unitMenuExpanded by remember { mutableStateOf(false) }
    var showScanner by remember { mutableStateOf(false) }
    var isLooking by remember { mutableStateOf(false) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> if (granted) showScanner = true }

    fun launchScanner() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) showScanner = true
        else cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    fun onBarcodeDetected(barcode: String) {
        showScanner = false
        isLooking = true
        scope.launch {
            val info = lookupBarcode(barcode)
            isLooking = false
            if (info != null) {
                info.name?.let { name = it }
                info.abv?.let { abv = fmtNum(it) }
                info.volumeMl?.let { volume = fmtNum(it); selectedUnit = VolumeUnit.ML }
            }
        }
    }

    val canAdd = price.toDoubleOrNull()?.let { it > 0 } == true &&
            abv.toDoubleOrNull()?.let { it > 0 } == true &&
            volume.toDoubleOrNull()?.let { it > 0 } == true

    val singleVolMl = volume.toDoubleOrNull()?.let { selectedUnit.toMl(it) }
    val totalVolMl = singleVolMl?.let { it * selectedQuantity }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = ChalkLight,
        dragHandle = { BottomSheetDefaults.DragHandle(color = Hairline) }
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(bottom = 36.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (editing != null) "Edit Drink" else "Add a Drink",
                    color = Espresso,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                if (isLooking) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Clay,
                        strokeWidth = 2.dp
                    )
                } else {
                    IconButton(onClick = { launchScanner() }) {
                        Icon(
                            Icons.Default.QrCodeScanner,
                            contentDescription = "Scan barcode",
                            tint = Clay
                        )
                    }
                }
            }

            if (showScanner) {
                BarcodeScannerDialog(
                    onDetected = { onBarcodeDetected(it) },
                    onDismiss = { showScanner = false }
                )
            }

            // Name
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name (optional)") },
                placeholder = { Text("e.g. Stella Artois") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = inputColors()
            )

            // Price + ABV
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price") },
                    prefix = { Text("£", color = Taupe) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    colors = inputColors()
                )
                OutlinedTextField(
                    value = abv,
                    onValueChange = { abv = it },
                    label = { Text("ABV") },
                    suffix = { Text("%", color = Taupe) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    colors = inputColors()
                )
            }

            // Volume + unit selector
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = volume,
                    onValueChange = { volume = it },
                    label = { Text("Volume") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    colors = inputColors()
                )
                Box {
                    OutlinedButton(
                        onClick = { unitMenuExpanded = true },
                        modifier = Modifier.width(90.dp),
                        border = BorderStroke(1.dp, Clay.copy(alpha = 0.45f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Clay)
                    ) {
                        Text(selectedUnit.label, fontSize = 13.sp)
                        Icon(Icons.Default.ArrowDropDown, null, Modifier.size(16.dp))
                    }
                    DropdownMenu(
                        expanded = unitMenuExpanded,
                        onDismissRequest = { unitMenuExpanded = false }
                    ) {
                        VolumeUnit.entries.forEach { unit ->
                            DropdownMenuItem(
                                text = { Text(unit.label, color = Espresso) },
                                onClick = { selectedUnit = unit; unitMenuExpanded = false }
                            )
                        }
                    }
                }
            }

            // Quick-fill presets
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Quick fill", color = Taupe, fontSize = 11.sp, letterSpacing = 0.5.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    VolumePreset.entries.forEach { preset ->
                        PresetChip(
                            label = preset.label,
                            selected = volume == preset.ml.toString() && selectedUnit == VolumeUnit.ML,
                            onClick = {
                                volume = preset.ml.toString()
                                selectedUnit = VolumeUnit.ML
                            }
                        )
                    }
                }
            }

            // Quantity selector (multipack)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Quantity", color = Taupe, fontSize = 11.sp, letterSpacing = 0.5.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    QUANTITIES.forEach { qty ->
                        PresetChip(
                            label = "×$qty",
                            selected = selectedQuantity == qty,
                            onClick = { selectedQuantity = qty }
                        )
                    }
                }
                // Show total volume hint when multipack
                if (selectedQuantity > 1 && totalVolMl != null) {
                    Text(
                        "${"%.0f".format(singleVolMl)}ml × $selectedQuantity = ${"%.0f".format(totalVolMl)}ml total",
                        color = Clay.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(Modifier.height(2.dp))

            Button(
                onClick = {
                    val p = price.toDoubleOrNull() ?: return@Button
                    val a = abv.toDoubleOrNull() ?: return@Button
                    val v = volume.toDoubleOrNull() ?: return@Button
                    val singleMl = selectedUnit.toMl(v)
                    onSave(
                        Drink(
                            id = editing?.id ?: UUID.randomUUID().toString(),
                            name = name.trim(),
                            price = p,
                            abv = a,
                            volumeMl = singleMl * selectedQuantity,
                            singleVolumeMl = singleMl,
                            quantity = selectedQuantity
                        )
                    )
                },
                enabled = canAdd,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Clay,
                    contentColor = ChalkLight,
                    disabledContainerColor = Clay.copy(alpha = 0.2f),
                    disabledContentColor = ChalkLight.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, null)
                Spacer(Modifier.width(8.dp))
                Text(
                    if (editing != null) "Save Changes" else "Add to Comparison",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
private fun PresetChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = if (selected) Clay else Sand,
        border = BorderStroke(0.5.dp, if (selected) Clay else Hairline),
        modifier = Modifier.height(32.dp)
    ) {
        Box(Modifier.padding(horizontal = 12.dp), contentAlignment = Alignment.Center) {
            Text(
                label,
                color = if (selected) ChalkLight else Taupe,
                fontSize = 12.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}

@Composable
private fun inputColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Clay,
    focusedLabelColor = Clay,
    cursorColor = Clay,
    focusedTextColor = Espresso,
    unfocusedTextColor = Espresso,
    unfocusedBorderColor = Hairline,
    unfocusedLabelColor = Taupe,
    focusedPlaceholderColor = Taupe,
    unfocusedPlaceholderColor = Taupe.copy(alpha = 0.6f)
)
