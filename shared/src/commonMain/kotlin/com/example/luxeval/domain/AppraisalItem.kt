package com.example.luxeval.domain

// ─────────────────────────────────────────────────────────────
// Enums — one per dropdown/choice in the "add item" forms.
// Keeping these as real enums (not raw strings) gives compile-time
// safety in the UI and in the report generator's `when` blocks.
// ─────────────────────────────────────────────────────────────

enum class ItemCategory {
    RING, NECKLACE, PENDANT, BRACELET, EARRINGS, BROOCH, WATCH,
    LOOSE_GEMSTONE, PEARL_JEWELRY, OTHER
}

enum class ItemCondition { EXCELLENT, GOOD, FAIR, POOR }

/** The same item appraises differently depending on why it's being appraised. */
enum class ValuationBasis { RETAIL_REPLACEMENT, FAIR_MARKET, LIQUIDATION }

enum class ValueSource { API_FETCHED, MANUAL }

enum class MetalType { GOLD, PLATINUM, SILVER, PALLADIUM, TITANIUM, STAINLESS_STEEL, MIXED_UNKNOWN }
enum class MetalColor { YELLOW, WHITE, ROSE, TWO_TONE }

enum class StoneType { DIAMOND, RUBY, SAPPHIRE, EMERALD, AMETHYST, TOPAZ, OPAL, OTHER }
enum class StoneOriginType { NATURAL, LAB_GROWN, SIMULANT }
enum class StoneShape { ROUND, PRINCESS, CUSHION, EMERALD_CUT, OVAL, PEAR, MARQUISE, RADIANT, CABOCHON, OTHER }
enum class StoneTreatment { NONE, HEAT_TREATED, IRRADIATED, CLARITY_ENHANCED, DYED, OTHER }
enum class Fluorescence { NONE, FAINT, MEDIUM, STRONG }
enum class SettingType { PRONG, BEZEL, PAVE, CHANNEL, HALO, TENSION, BEAD, NONE }

enum class RingBandStyle { SOLITAIRE, HALO, ETERNITY, THREE_STONE, SIGNET, WEDDING_BAND, COCKTAIL, OTHER }
enum class ShankType { FULL, SPLIT, CATHEDRAL }

enum class ClaspType { LOBSTER_CLAW, SPRING_RING, TOGGLE, BOX_CLASP, BARREL, OTHER }
enum class BraceletStyle { TENNIS, CHARM, BANGLE, CUFF, LINK, OTHER }

enum class EarringStyle { STUD, HOOP, DROP_DANGLE, HUGGIE, CHANDELIER, OTHER }
enum class EarringBacking { PUSH_BACK, SCREW_BACK, LEVER_BACK, CLIP_ON, FRENCH_WIRE, OTHER }

enum class BroochPinMechanism { C_CLASP, SAFETY_CATCH, HINGED_PIN }

enum class WatchMovementType { AUTOMATIC, MANUAL_WIND, QUARTZ }
enum class WatchComplication { NONE, CHRONOGRAPH, DATE, GMT, MOONPHASE, OTHER }
enum class WatchRunningCondition { RUNNING_ACCURATELY, RUNNING_NEEDS_SERVICE, NOT_RUNNING }

enum class PearlType { AKOYA, FRESHWATER, TAHITIAN, SOUTH_SEA, NATURAL }
enum class PearlShape { ROUND, SEMI_ROUND, BAROQUE, BUTTON }
enum class PearlLuster { HIGH, MEDIUM, LOW }
enum class PearlSurfaceQuality { CLEAN, LIGHTLY_BLEMISHED, BLEMISHED }

// ─────────────────────────────────────────────────────────────
// Reusable value objects — shared by every category that has a
// metal body and/or a set gemstone (i.e. everything except
// Watch and LooseGemstone, which are structured differently).
// ─────────────────────────────────────────────────────────────

data class MetalDetails(
    val metalType: MetalType,
    val purity: String,               // e.g. "18K", "925", "950"
    val color: MetalColor,
    val netWeightGrams: Double? = null,
    val isPlated: Boolean = false,
    val platingBaseMetal: String? = null
)

data class GemstoneDetails(
    val stoneType: StoneType,
    val originType: StoneOriginType,
    val caratWeight: Double,
    val shape: StoneShape,
    val colorGrade: String? = null,     // free text: "D" or "vivid pigeon-blood red"
    val clarityGrade: String? = null,   // free text: "VS1" or "eye-clean"
    val treatment: StoneTreatment = StoneTreatment.NONE,
    val fluorescence: Fluorescence? = null,
    val settingType: SettingType = SettingType.NONE,
    val stoneCount: Int = 1,
    val certificationLab: String? = null,
    val certificationNumber: String? = null,
    val measurementsMm: String? = null  // "6.5 x 6.5 x 4.0"
)

// ─────────────────────────────────────────────────────────────
// The sealed hierarchy. Every field common to all items is
// declared abstract on the base class; every category adds only
// what's actually relevant to it. Adding a new category later
// means adding one new subclass here — the compiler then forces
// you to handle it everywhere a `when(item)` exists (repository,
// UI router, report generator), so nothing gets silently missed.
// ─────────────────────────────────────────────────────────────

sealed class AppraisalItem {
    abstract val id: String
    abstract val itemName: String
    abstract val appraisalId: String
    abstract val photoUris: List<String>
    abstract val condition: ItemCondition
    abstract val conditionNotes: String?
    abstract val grossWeightGrams: Double?
    abstract val hallmarks: String?
    abstract val estimatedValue: Double?
    abstract val currency: String
    abstract val valuationBasis: ValuationBasis
    abstract val valueSource: ValueSource
    abstract val notes: String?
    abstract val createdAt: Long
    abstract val updatedAt: Long

    data class Ring(
        override val id: String,
        override val itemName: String,
        override val appraisalId: String,
        override val photoUris: List<String>,
        override val condition: ItemCondition,
        override val conditionNotes: String? = null,
        override val grossWeightGrams: Double? = null,
        override val hallmarks: String? = null,
        override val estimatedValue: Double? = null,
        override val currency: String,
        override val valuationBasis: ValuationBasis,
        override val valueSource: ValueSource,
        override val notes: String? = null,
        override val createdAt: Long,
        override val updatedAt: Long,
        val ringSize: String? = null,
        val bandWidthMm: Double? = null,
        val bandStyle: RingBandStyle,
        val shankType: ShankType? = null,
        val engraving: String? = null,
        val metal: MetalDetails,
        val gemstone: GemstoneDetails? = null
    ) : AppraisalItem()

    data class Necklace(
        override val id: String,
        override val itemName: String,
        override val appraisalId: String,
        override val photoUris: List<String>,
        override val condition: ItemCondition,
        override val conditionNotes: String? = null,
        override val grossWeightGrams: Double? = null,
        override val hallmarks: String? = null,
        override val estimatedValue: Double? = null,
        override val currency: String,
        override val valuationBasis: ValuationBasis,
        override val valueSource: ValueSource,
        override val notes: String? = null,
        override val createdAt: Long,
        override val updatedAt: Long,
        val lengthCm: Double? = null,
        val chainStyle: String? = null,
        val claspType: ClaspType? = null,
        val hasPendantAttached: Boolean = false,
        val metal: MetalDetails,
        val gemstone: GemstoneDetails? = null
    ) : AppraisalItem()

    data class Pendant(
        override val id: String,
        override val itemName: String,
        override val appraisalId: String,
        override val photoUris: List<String>,
        override val condition: ItemCondition,
        override val conditionNotes: String? = null,
        override val grossWeightGrams: Double? = null,
        override val hallmarks: String? = null,
        override val estimatedValue: Double? = null,
        override val currency: String,
        override val valuationBasis: ValuationBasis,
        override val valueSource: ValueSource,
        override val notes: String? = null,
        override val createdAt: Long,
        override val updatedAt: Long,
        val bailType: String? = null,
        val heightMm: Double? = null,
        val widthMm: Double? = null,
        val soldWithChain: Boolean = false,
        val metal: MetalDetails,
        val gemstone: GemstoneDetails? = null
    ) : AppraisalItem()

    data class Bracelet(
        override val id: String,
        override val itemName: String,
        override val appraisalId: String,
        override val photoUris: List<String>,
        override val condition: ItemCondition,
        override val conditionNotes: String? = null,
        override val grossWeightGrams: Double? = null,
        override val hallmarks: String? = null,
        override val estimatedValue: Double? = null,
        override val currency: String,
        override val valuationBasis: ValuationBasis,
        override val valueSource: ValueSource,
        override val notes: String? = null,
        override val createdAt: Long,
        override val updatedAt: Long,
        val lengthCm: Double? = null,
        val style: BraceletStyle,
        val claspType: ClaspType? = null,
        val isAdjustable: Boolean = false,
        val charmCount: Int? = null,
        val charmDescriptions: List<String> = emptyList(),
        val metal: MetalDetails,
        val gemstone: GemstoneDetails? = null
    ) : AppraisalItem()

    data class Earrings(
        override val id: String,
        override val itemName: String,
        override val appraisalId: String,
        override val photoUris: List<String>,
        override val condition: ItemCondition,
        override val conditionNotes: String? = null,
        override val grossWeightGrams: Double? = null,
        override val hallmarks: String? = null,
        override val estimatedValue: Double? = null,
        override val currency: String,
        override val valuationBasis: ValuationBasis,
        override val valueSource: ValueSource,
        override val notes: String? = null,
        override val createdAt: Long,
        override val updatedAt: Long,
        val style: EarringStyle,
        val backing: EarringBacking,
        val isConfirmedPair: Boolean = true,
        val hoopDiameterMm: Double? = null,
        val metal: MetalDetails,
        val gemstone: GemstoneDetails? = null
    ) : AppraisalItem()

    data class Brooch(
        override val id: String,
        override val itemName: String,
        override val appraisalId: String,
        override val photoUris: List<String>,
        override val condition: ItemCondition,
        override val conditionNotes: String? = null,
        override val grossWeightGrams: Double? = null,
        override val hallmarks: String? = null,
        override val estimatedValue: Double? = null,
        override val currency: String,
        override val valuationBasis: ValuationBasis,
        override val valueSource: ValueSource,
        override val notes: String? = null,
        override val createdAt: Long,
        override val updatedAt: Long,
        val pinMechanism: BroochPinMechanism,
        val heightMm: Double? = null,
        val widthMm: Double? = null,
        val motifDescription: String? = null,
        val metal: MetalDetails,
        val gemstone: GemstoneDetails? = null
    ) : AppraisalItem()

    /** No metal/gemstone blocks — watches are structured completely differently. */
    data class Watch(
        override val id: String,
        override val itemName: String,
        override val appraisalId: String,
        override val photoUris: List<String>,
        override val condition: ItemCondition,
        override val conditionNotes: String? = null,
        override val grossWeightGrams: Double? = null,
        override val hallmarks: String? = null,
        override val estimatedValue: Double? = null,
        override val currency: String,
        override val valuationBasis: ValuationBasis,
        override val valueSource: ValueSource,
        override val notes: String? = null,
        override val createdAt: Long,
        override val updatedAt: Long,
        val brand: String,
        val model: String? = null,
        val referenceNumber: String? = null,
        val movementType: WatchMovementType,
        val caseMaterial: String,
        val caseSizeMm: Double? = null,
        val dialColor: String? = null,
        val complications: List<WatchComplication> = emptyList(),
        val bandMaterial: String? = null,
        val waterResistance: String? = null,
        val serialNumber: String? = null,
        val hasBox: Boolean = false,
        val hasPapers: Boolean = false,
        val runningCondition: WatchRunningCondition,
        val yearOfManufacture: Int? = null
    ) : AppraisalItem()

    /** Gemstone block is the entire record — no metal, no setting. */
    data class LooseGemstone(
        override val id: String,
        override val itemName: String,
        override val appraisalId: String,
        override val photoUris: List<String>,
        override val condition: ItemCondition,
        override val conditionNotes: String? = null,
        override val grossWeightGrams: Double? = null,
        override val hallmarks: String? = null,
        override val estimatedValue: Double? = null,
        override val currency: String,
        override val valuationBasis: ValuationBasis,
        override val valueSource: ValueSource,
        override val notes: String? = null,
        override val createdAt: Long,
        override val updatedAt: Long,
        val gemstone: GemstoneDetails
    ) : AppraisalItem()

    /** Pearls are graded completely differently from gemstones — own pathway. */
    data class PearlJewelry(
        override val id: String,
        override val itemName: String,
        override val appraisalId: String,
        override val photoUris: List<String>,
        override val condition: ItemCondition,
        override val conditionNotes: String? = null,
        override val grossWeightGrams: Double? = null,
        override val hallmarks: String? = null,
        override val estimatedValue: Double? = null,
        override val currency: String,
        override val valuationBasis: ValuationBasis,
        override val valueSource: ValueSource,
        override val notes: String? = null,
        override val createdAt: Long,
        override val updatedAt: Long,
        val pearlType: PearlType,
        val sizeMm: Double? = null,
        val shape: PearlShape,
        val luster: PearlLuster,
        val surfaceQuality: PearlSurfaceQuality,
        val overtoneColor: String? = null,
        val pearlCount: Int = 1,
        val isMatchedStrand: Boolean = true,
        val metal: MetalDetails? = null // clasp/setting metal, optional
    ) : AppraisalItem()

    /** Escape hatch for anything that doesn't fit the categories above. */
    data class Other(
        override val id: String,
        override val itemName: String,
        override val appraisalId: String,
        override val photoUris: List<String>,
        override val condition: ItemCondition,
        override val conditionNotes: String? = null,
        override val grossWeightGrams: Double? = null,
        override val hallmarks: String? = null,
        override val estimatedValue: Double? = null,
        override val currency: String,
        override val valuationBasis: ValuationBasis,
        override val valueSource: ValueSource,
        override val notes: String? = null,
        override val createdAt: Long,
        override val updatedAt: Long,
        val description: String,
        val metal: MetalDetails? = null,
        val gemstone: GemstoneDetails? = null
    ) : AppraisalItem()

    /** Derive the category from the runtime type — one source of truth, can't drift out of sync. */
    val category: ItemCategory
        get() = when (this) {
            is Ring -> ItemCategory.RING
            is Necklace -> ItemCategory.NECKLACE
            is Pendant -> ItemCategory.PENDANT
            is Bracelet -> ItemCategory.BRACELET
            is Earrings -> ItemCategory.EARRINGS
            is Brooch -> ItemCategory.BROOCH
            is Watch -> ItemCategory.WATCH
            is LooseGemstone -> ItemCategory.LOOSE_GEMSTONE
            is PearlJewelry -> ItemCategory.PEARL_JEWELRY
            is Other -> ItemCategory.OTHER
        }

}
// domain/AppraisalItem.kt — add near the bottom
fun AppraisalItem.withPhotoUris(uris: List<String>): AppraisalItem = when (this) {
    is AppraisalItem.Ring -> copy(photoUris = uris)
    is AppraisalItem.Necklace -> copy(photoUris = uris)
    is AppraisalItem.Pendant -> copy(photoUris = uris)
    is AppraisalItem.Bracelet -> copy(photoUris = uris)
    is AppraisalItem.Earrings -> copy(photoUris = uris)
    is AppraisalItem.Brooch -> copy(photoUris = uris)
    is AppraisalItem.Watch -> copy(photoUris = uris)
    is AppraisalItem.LooseGemstone -> copy(photoUris = uris)
    is AppraisalItem.PearlJewelry -> copy(photoUris = uris)
    is AppraisalItem.Other -> copy(photoUris = uris)
}