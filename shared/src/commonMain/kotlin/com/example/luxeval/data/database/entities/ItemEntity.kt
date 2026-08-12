package com.example.luxeval.data.database.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey val id: String,
    val appraisalId: String,
    val category: String,
    val photoUris: List<String>,
    val condition: String,
    val conditionNotes: String? = null,
    val grossWeightGrams: Double? = null,
    val hallmarks: String? = null,
    val estimatedValue: Double? = null,
    val currency: String,
    val valuationBasis: String,        // ValuationBasis.name
    val valueSource: String,           // ValueSource.name
    val notes: String? = null,
    val createdAt: Long,
    val updatedAt: Long
)
data class MetalDetailsColumns(
    val metalType: String,             // MetalType.name
    val purity: String,
    val color: String,                 // MetalColor.name
    val netWeightGrams: Double? = null,
    val isPlated: Boolean = false,
    val platingBaseMetal: String? = null
)

data class GemstoneDetailsColumns(
    val stoneType: String,             // StoneType.name
    val originType: String,            // StoneOriginType.name
    val caratWeight: Double,
    val shape: String,                 // StoneShape.name
    val colorGrade: String? = null,
    val clarityGrade: String? = null,
    val treatment: String = "NONE",    // StoneTreatment.name
    val fluorescence: String? = null,  // Fluorescence.name
    val settingType: String = "NONE",  // SettingType.name
    val stoneCount: Int = 1,
    val certificationLab: String? = null,
    val certificationNumber: String? = null,
    val measurementsMm: String? = null
)
@Entity(
    tableName = "ring_details",
    foreignKeys = [ForeignKey(
        entity = ItemEntity::class,
        parentColumns = ["id"],
        childColumns = ["itemId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class RingDetailsEntity(
    @PrimaryKey val itemId: String,
    val ringSize: String? = null,
    val bandWidthMm: Double? = null,
    val bandStyle: String,              // RingBandStyle.name
    val shankType: String? = null,       // ShankType.name
    val engraving: String? = null,
    @Embedded(prefix = "metal_") val metal: MetalDetailsColumns,
    @Embedded(prefix = "stone_") val gemstone: GemstoneDetailsColumns? = null
)

// TODO (same pattern as Ring, just different own-columns):
@Entity(
    tableName = "necklace_details",
    foreignKeys = [ForeignKey(
        entity = ItemEntity::class,
        parentColumns = ["id"],
        childColumns = ["itemId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class NecklaceDetailsEntity(
    @PrimaryKey val itemId: String,
    val lengthCm: Double? = null,
    val chainStyle: String?,             // ChainStyle.name
    val claspType: String?,              // ClaspType.name
    val hasPendantAttached: Boolean = false,
    @Embedded(prefix = "metal_") val metal: MetalDetailsColumns,
    @Embedded(prefix = "stone_") val gemstone: GemstoneDetailsColumns? = null
)

@Entity(
    tableName = "pendant_details",
    foreignKeys = [ForeignKey(
        entity = ItemEntity::class,
        parentColumns = ["id"],
        childColumns = ["itemId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class PendantDetailsEntity(
    @PrimaryKey val itemId: String,
    val bailType: String?,               // BailType.name
    val heightMm: Double? = null,
    val widthMm: Double? = null,
    val soldWithChain: Boolean = false,
    @Embedded(prefix = "metal_") val metal: MetalDetailsColumns,
    @Embedded(prefix = "stone_") val gemstone: GemstoneDetailsColumns? = null
)

@Entity(
    tableName = "bracelet_details",
    foreignKeys = [ForeignKey(
        entity = ItemEntity::class,
        parentColumns = ["id"],
        childColumns = ["itemId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class BraceletDetailsEntity(
    @PrimaryKey val itemId: String,
    val lengthCm: Double? = null,
    val style: String,                  // BraceletStyle.name
    val claspType: String?,              // ClaspType.name
    val isAdjustable: Boolean = false,
    val charmCount: Int = 0,
    val charmDescriptions: String? = null,
    @Embedded(prefix = "metal_") val metal: MetalDetailsColumns,
    @Embedded(prefix = "stone_") val gemstone: GemstoneDetailsColumns? = null
)

@Entity(
    tableName = "earrings_details",
    foreignKeys = [ForeignKey(
        entity = ItemEntity::class,
        parentColumns = ["id"],
        childColumns = ["itemId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class EarringsDetailsEntity(
    @PrimaryKey val itemId: String,
    val style: String,                  // EarringStyle.name
    val backing: String,                // EarringBacking.name
    val isConfirmedPair: Boolean = false,
    val hoopDiameterMm: Double? = null,
    @Embedded(prefix = "metal_") val metal: MetalDetailsColumns,
    @Embedded(prefix = "stone_") val gemstone: GemstoneDetailsColumns? = null
)

@Entity(
    tableName = "brooch_details",
    foreignKeys = [ForeignKey(
        entity = ItemEntity::class,
        parentColumns = ["id"],
        childColumns = ["itemId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class BroochDetailsEntity(
    @PrimaryKey val itemId: String,
    val pinMechanism: String,            // PinMechanism.name
    val heightMm: Double? = null,
    val widthMm: Double? = null,
    val motifDescription: String? = null,
    @Embedded(prefix = "metal_") val metal: MetalDetailsColumns,
    @Embedded(prefix = "stone_") val gemstone: GemstoneDetailsColumns? = null
)

@Entity(
    tableName = "pearl_details",
    foreignKeys = [ForeignKey(
        entity = ItemEntity::class,
        parentColumns = ["id"],
        childColumns = ["itemId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class PearlDetailsEntity(
    @PrimaryKey val itemId: String,
    val pearlType: String,              // PearlType.name
    val sizeMm: Double? = null,
    val shape: String,                  // PearlShape.name
    val luster: String,                 // PearlLuster.name
    val surfaceQuality: String,         // PearlSurfaceQuality.name
    val overtoneColor: String? = null,
    val pearlCount: Int = 1,
    val isMatchedStrand: Boolean = false,
    @Embedded(prefix = "metal_") val metal: MetalDetailsColumns? = null
)
// NecklaceDetailsEntity   -> lengthCm, chainStyle, claspType, hasPendantAttached
// PendantDetailsEntity    -> bailType, heightMm, widthMm, soldWithChain
// BraceletDetailsEntity   -> lengthCm, style, claspType, isAdjustable, charmCount, charmDescriptions
// EarringsDetailsEntity   -> style, backing, isConfirmedPair, hoopDiameterMm
// BroochDetailsEntity     -> pinMechanism, heightMm, widthMm, motifDescription
// PearlDetailsEntity      -> pearlType, sizeMm, shape, luster, surfaceQuality, overtoneColor,
//                            pearlCount, isMatchedStrand, @Embedded metal (nullable, no gemstone block)

// ─────────────────────────────────────────────────────────────
// Pattern B — no metal/gemstone blocks at all (Watch).
// ─────────────────────────────────────────────────────────────

@Entity(
    tableName = "watch_details",
    foreignKeys = [ForeignKey(
        entity = ItemEntity::class,
        parentColumns = ["id"],
        childColumns = ["itemId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class WatchDetailsEntity(
    @PrimaryKey val itemId: String,
    val brand: String,
    val model: String? = null,
    val referenceNumber: String? = null,
    val movementType: String,            // WatchMovementType.name
    val caseMaterial: String,
    val caseSizeMm: Double? = null,
    val dialColor: String? = null,
    val complications: List<String> = emptyList(), // WatchComplication.name list, via converter
    val bandMaterial: String? = null,
    val waterResistance: String? = null,
    val serialNumber: String? = null,
    val hasBox: Boolean = false,
    val hasPapers: Boolean = false,
    val runningCondition: String,        // WatchRunningCondition.name
    val yearOfManufacture: Int? = null
)

// ─────────────────────────────────────────────────────────────
// Pattern C — the gemstone block *is* the whole record (LooseGemstone).
// ─────────────────────────────────────────────────────────────

@Entity(
    tableName = "loose_gemstone_details",
    foreignKeys = [ForeignKey(
        entity = ItemEntity::class,
        parentColumns = ["id"],
        childColumns = ["itemId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class LooseGemstoneDetailsEntity(
    @PrimaryKey val itemId: String,
    @Embedded(prefix = "stone_") val gemstone: GemstoneDetailsColumns
)