// presentation/itemEditor/common/PhotoGridEditor.kt
package com.example.luxeval.presentation.itemEditor.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.luxeval.presentation.theme.LuxeSpacing
import com.example.luxeval.presentation.util.PhotoImage
import luxeval.shared.generated.resources.Res
import luxeval.shared.generated.resources.action_add_photo
import luxeval.shared.generated.resources.cd_remove_photo
import luxeval.shared.generated.resources.ic_add
import luxeval.shared.generated.resources.ic_delete
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/**
 * Photo capture/selection is platform-specific (camera + gallery access differ on
 * Android vs iOS), so this composable stays dumb: it renders whatever URIs it's
 * given and delegates the "add" action upward. Wire `onAddPhoto` to an `expect/actual`
 * PhotoPicker once you get to that piece — for now it can push a placeholder URI.
 */
@Composable
fun PhotoGridEditor(
    photoUris: List<String>,
    onAddPhoto: () -> Unit,
    onRemovePhoto: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(LuxeSpacing.sm),
        verticalArrangement = Arrangement.spacedBy(LuxeSpacing.sm),
        modifier = modifier.heightIn(max = 300.dp)
    ) {
        items(photoUris) { uri ->
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                PhotoImage(
                    photoUri = uri,               // an absolute file path — Coil3 handles file:// and bare paths on both platforms
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                IconButton(onClick = { onRemovePhoto(uri) }, modifier = Modifier.align(Alignment.TopEnd)) {
                    Icon(painterResource(Res.drawable.ic_delete), contentDescription = stringResource(Res.string.cd_remove_photo))
                }
            }
        }
        item {
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
                    .clickable(onClick = onAddPhoto),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(painterResource(Res.drawable.ic_add), contentDescription = stringResource(Res.string.action_add_photo))
                    Text(stringResource(Res.string.action_add_photo), style = MaterialTheme.typography.labelSmall)

                }
            }
        }
    }
}