package com.example.luxeval.domain

// ─────────────────────────────────────────────────────────────
// EnumLabels.kt
//
// One label() extension per enum used in a FormDropdown. Each
// returns a StringResource (not a String) so call sites resolve
// it with stringResource(it.label()) inside a @Composable, the
// same pattern already used for ItemCategory in CategoryPicker.kt.
//
// Drop this file in: shared/src/commonMain/kotlin/com/example/luxeval/domain/
// It only needs imports from your generated Res object — add the
// matching keys to strings.xml (all 4 languages) first, or the
// build will fail to resolve Res.string.xxx.
// ─────────────────────────────────────────────────────────────

import luxeval.shared.generated.resources.Res
import org.jetbrains.compose.resources.StringResource
import luxeval.shared.generated.resources.*

fun MetalType.label(): StringResource = when (this) {
    MetalType.GOLD -> Res.string.metal_type_gold
    MetalType.PLATINUM -> Res.string.metal_type_platinum
    MetalType.SILVER -> Res.string.metal_type_silver
    MetalType.PALLADIUM -> Res.string.metal_type_palladium
    MetalType.TITANIUM -> Res.string.metal_type_titanium
    MetalType.STAINLESS_STEEL -> Res.string.metal_type_stainless_steel
    MetalType.MIXED_UNKNOWN -> Res.string.metal_type_mixed_unknown
}

fun MetalColor.label(): StringResource = when (this) {
    MetalColor.YELLOW -> Res.string.metal_color_yellow
    MetalColor.WHITE -> Res.string.metal_color_white
    MetalColor.ROSE -> Res.string.metal_color_rose
    MetalColor.TWO_TONE -> Res.string.metal_color_two_tone
}

fun StoneType.label(): StringResource = when (this) {
    StoneType.DIAMOND -> Res.string.stone_type_diamond
    StoneType.RUBY -> Res.string.stone_type_ruby
    StoneType.SAPPHIRE -> Res.string.stone_type_sapphire
    StoneType.EMERALD -> Res.string.stone_type_emerald
    StoneType.AMETHYST -> Res.string.stone_type_amethyst
    StoneType.TOPAZ -> Res.string.stone_type_topaz
    StoneType.OPAL -> Res.string.stone_type_opal
    StoneType.OTHER -> Res.string.stone_type_other
}

fun StoneOriginType.label(): StringResource = when (this) {
    StoneOriginType.NATURAL -> Res.string.stone_origin_natural
    StoneOriginType.LAB_GROWN -> Res.string.stone_origin_lab_grown
    StoneOriginType.SIMULANT -> Res.string.stone_origin_simulant
}

fun StoneShape.label(): StringResource = when (this) {
    StoneShape.ROUND -> Res.string.stone_shape_round
    StoneShape.PRINCESS -> Res.string.stone_shape_princess
    StoneShape.CUSHION -> Res.string.stone_shape_cushion
    StoneShape.EMERALD_CUT -> Res.string.stone_shape_emerald_cut
    StoneShape.OVAL -> Res.string.stone_shape_oval
    StoneShape.PEAR -> Res.string.stone_shape_pear
    StoneShape.MARQUISE -> Res.string.stone_shape_marquise
    StoneShape.RADIANT -> Res.string.stone_shape_radiant
    StoneShape.CABOCHON -> Res.string.stone_shape_cabochon
    StoneShape.OTHER -> Res.string.stone_shape_other
}

fun StoneTreatment.label(): StringResource = when (this) {
    StoneTreatment.NONE -> Res.string.stone_treatment_none
    StoneTreatment.HEAT_TREATED -> Res.string.stone_treatment_heat_treated
    StoneTreatment.IRRADIATED -> Res.string.stone_treatment_irradiated
    StoneTreatment.CLARITY_ENHANCED -> Res.string.stone_treatment_clarity_enhanced
    StoneTreatment.DYED -> Res.string.stone_treatment_dyed
    StoneTreatment.OTHER -> Res.string.stone_treatment_other
}

fun Fluorescence.label(): StringResource = when (this) {
    Fluorescence.NONE -> Res.string.fluorescence_none
    Fluorescence.FAINT -> Res.string.fluorescence_faint
    Fluorescence.MEDIUM -> Res.string.fluorescence_medium
    Fluorescence.STRONG -> Res.string.fluorescence_strong
}

fun SettingType.label(): StringResource = when (this) {
    SettingType.PRONG -> Res.string.setting_type_prong
    SettingType.BEZEL -> Res.string.setting_type_bezel
    SettingType.PAVE -> Res.string.setting_type_pave
    SettingType.CHANNEL -> Res.string.setting_type_channel
    SettingType.HALO -> Res.string.setting_type_halo
    SettingType.TENSION -> Res.string.setting_type_tension
    SettingType.BEAD -> Res.string.setting_type_bead
    SettingType.NONE -> Res.string.setting_type_none
}

fun RingBandStyle.label(): StringResource = when (this) {
    RingBandStyle.SOLITAIRE -> Res.string.ring_band_style_solitaire
    RingBandStyle.HALO -> Res.string.ring_band_style_halo
    RingBandStyle.ETERNITY -> Res.string.ring_band_style_eternity
    RingBandStyle.THREE_STONE -> Res.string.ring_band_style_three_stone
    RingBandStyle.SIGNET -> Res.string.ring_band_style_signet
    RingBandStyle.WEDDING_BAND -> Res.string.ring_band_style_wedding_band
    RingBandStyle.COCKTAIL -> Res.string.ring_band_style_cocktail
    RingBandStyle.OTHER -> Res.string.ring_band_style_other
}

fun ShankType.label(): StringResource = when (this) {
    ShankType.FULL -> Res.string.shank_type_full
    ShankType.SPLIT -> Res.string.shank_type_split
    ShankType.CATHEDRAL -> Res.string.shank_type_cathedral
}

fun ClaspType.label(): StringResource = when (this) {
    ClaspType.LOBSTER_CLAW -> Res.string.clasp_type_lobster_claw
    ClaspType.SPRING_RING -> Res.string.clasp_type_spring_ring
    ClaspType.TOGGLE -> Res.string.clasp_type_toggle
    ClaspType.BOX_CLASP -> Res.string.clasp_type_box_clasp
    ClaspType.BARREL -> Res.string.clasp_type_barrel
    ClaspType.OTHER -> Res.string.clasp_type_other
}

fun BraceletStyle.label(): StringResource = when (this) {
    BraceletStyle.TENNIS -> Res.string.bracelet_style_tennis
    BraceletStyle.CHARM -> Res.string.bracelet_style_charm
    BraceletStyle.BANGLE -> Res.string.bracelet_style_bangle
    BraceletStyle.CUFF -> Res.string.bracelet_style_cuff
    BraceletStyle.LINK -> Res.string.bracelet_style_link
    BraceletStyle.OTHER -> Res.string.bracelet_style_other
}

fun EarringStyle.label(): StringResource = when (this) {
    EarringStyle.STUD -> Res.string.earring_style_stud
    EarringStyle.HOOP -> Res.string.earring_style_hoop
    EarringStyle.DROP_DANGLE -> Res.string.earring_style_drop_dangle
    EarringStyle.HUGGIE -> Res.string.earring_style_huggie
    EarringStyle.CHANDELIER -> Res.string.earring_style_chandelier
    EarringStyle.OTHER -> Res.string.earring_style_other
}

fun EarringBacking.label(): StringResource = when (this) {
    EarringBacking.PUSH_BACK -> Res.string.earring_backing_push_back
    EarringBacking.SCREW_BACK -> Res.string.earring_backing_screw_back
    EarringBacking.LEVER_BACK -> Res.string.earring_backing_lever_back
    EarringBacking.CLIP_ON -> Res.string.earring_backing_clip_on
    EarringBacking.FRENCH_WIRE -> Res.string.earring_backing_french_wire
    EarringBacking.OTHER -> Res.string.earring_backing_other
}

fun BroochPinMechanism.label(): StringResource = when (this) {
    BroochPinMechanism.C_CLASP -> Res.string.brooch_pin_c_clasp
    BroochPinMechanism.SAFETY_CATCH -> Res.string.brooch_pin_safety_catch
    BroochPinMechanism.HINGED_PIN -> Res.string.brooch_pin_hinged_pin
}

fun WatchMovementType.label(): StringResource = when (this) {
    WatchMovementType.AUTOMATIC -> Res.string.watch_movement_automatic
    WatchMovementType.MANUAL_WIND -> Res.string.watch_movement_manual_wind
    WatchMovementType.QUARTZ -> Res.string.watch_movement_quartz
}

fun WatchComplication.label(): StringResource = when (this) {
    WatchComplication.NONE -> Res.string.watch_complication_none
    WatchComplication.CHRONOGRAPH -> Res.string.watch_complication_chronograph
    WatchComplication.DATE -> Res.string.watch_complication_date
    WatchComplication.GMT -> Res.string.watch_complication_gmt
    WatchComplication.MOONPHASE -> Res.string.watch_complication_moonphase
    WatchComplication.OTHER -> Res.string.watch_complication_other
}

fun WatchRunningCondition.label(): StringResource = when (this) {
    WatchRunningCondition.RUNNING_ACCURATELY -> Res.string.watch_running_accurately
    WatchRunningCondition.RUNNING_NEEDS_SERVICE -> Res.string.watch_running_needs_service
    WatchRunningCondition.NOT_RUNNING -> Res.string.watch_running_not_running
}

fun PearlType.label(): StringResource = when (this) {
    PearlType.AKOYA -> Res.string.pearl_type_akoya
    PearlType.FRESHWATER -> Res.string.pearl_type_freshwater
    PearlType.TAHITIAN -> Res.string.pearl_type_tahitian
    PearlType.SOUTH_SEA -> Res.string.pearl_type_south_sea
    PearlType.NATURAL -> Res.string.pearl_type_natural
}

fun PearlShape.label(): StringResource = when (this) {
    PearlShape.ROUND -> Res.string.pearl_shape_round
    PearlShape.SEMI_ROUND -> Res.string.pearl_shape_semi_round
    PearlShape.BAROQUE -> Res.string.pearl_shape_baroque
    PearlShape.BUTTON -> Res.string.pearl_shape_button
}

fun PearlLuster.label(): StringResource = when (this) {
    PearlLuster.HIGH -> Res.string.pearl_luster_high
    PearlLuster.MEDIUM -> Res.string.pearl_luster_medium
    PearlLuster.LOW -> Res.string.pearl_luster_low
}

fun PearlSurfaceQuality.label(): StringResource = when (this) {
    PearlSurfaceQuality.CLEAN -> Res.string.pearl_surface_clean
    PearlSurfaceQuality.LIGHTLY_BLEMISHED -> Res.string.pearl_surface_lightly_blemished
    PearlSurfaceQuality.BLEMISHED -> Res.string.pearl_surface_blemished
}

fun ItemCondition.label(): StringResource = when (this) {
    ItemCondition.EXCELLENT -> Res.string.item_condition_excellent
    ItemCondition.GOOD -> Res.string.item_condition_good
    ItemCondition.FAIR -> Res.string.item_condition_fair
    ItemCondition.POOR -> Res.string.item_condition_poor
}