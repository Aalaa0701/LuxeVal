package com.example.luxeval.presentation.appraisalDetail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.luxeval.domain.ItemCategory
import com.example.luxeval.presentation.theme.LuxeShapes
import com.example.luxeval.presentation.theme.LuxeSpacing
import luxeval.shared.generated.resources.Res
import luxeval.shared.generated.resources.ic_brooch
import luxeval.shared.generated.resources.ic_earrings
import luxeval.shared.generated.resources.ic_gamstone
import luxeval.shared.generated.resources.ic_necklace
import luxeval.shared.generated.resources.ic_other_jewelry
import luxeval.shared.generated.resources.ic_pearl
import luxeval.shared.generated.resources.ic_pendant
import luxeval.shared.generated.resources.ic_ring
import luxeval.shared.generated.resources.ic_watch
import luxeval.shared.generated.resources.ic_bracelet
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryPickerSheet(
    onCategorySelected: (ItemCategory) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(LuxeSpacing.md)) {
            Text("Add Jewelry Item", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(
                "Choose a category to continue.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(LuxeSpacing.md))
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(LuxeSpacing.sm),
                verticalArrangement = Arrangement.spacedBy(LuxeSpacing.sm),
                modifier = Modifier.heightIn(max = 400.dp)
            ) {
                items(ItemCategory.entries.filter { it != ItemCategory.OTHER }) { category ->
                    CategoryTile(category = category, onClick = { onCategorySelected(category) })
                }
            }
            Spacer(Modifier.height(LuxeSpacing.md))
        }
    }
}

@Composable
private fun CategoryTile(category: ItemCategory, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = LuxeShapes.md,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = LuxeSpacing.md),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(painterResource(category.icon()), contentDescription = category.label(), tint = MaterialTheme.colorScheme.secondary)
            Spacer(Modifier.height(LuxeSpacing.xs))
            Text(category.label(), style = MaterialTheme.typography.labelMedium, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}

fun ItemCategory.label(): String = when (this) {
    ItemCategory.RING -> "Ring"
    ItemCategory.NECKLACE -> "Necklace"
    ItemCategory.PENDANT -> "Pendant"
    ItemCategory.BRACELET -> "Bracelet"
    ItemCategory.EARRINGS -> "Earrings"
    ItemCategory.BROOCH -> "Brooch"
    ItemCategory.WATCH -> "Watch"
    ItemCategory.LOOSE_GEMSTONE -> "Gemstone"
    ItemCategory.PEARL_JEWELRY -> "Pearl Jewelry"
    ItemCategory.OTHER -> "Other"
}

fun ItemCategory.icon(): DrawableResource = when (this) {
    ItemCategory.RING -> Res.drawable.ic_ring
    ItemCategory.NECKLACE -> Res.drawable.ic_necklace
    ItemCategory.PENDANT -> Res.drawable.ic_pendant
    ItemCategory.BRACELET -> Res.drawable.ic_bracelet
    ItemCategory.EARRINGS -> Res.drawable.ic_earrings
    ItemCategory.BROOCH -> Res.drawable.ic_brooch
    ItemCategory.WATCH -> Res.drawable.ic_watch
    ItemCategory.LOOSE_GEMSTONE -> Res.drawable.ic_gamstone
    ItemCategory.PEARL_JEWELRY -> Res.drawable.ic_pearl
    ItemCategory.OTHER -> Res.drawable.ic_other_jewelry
}