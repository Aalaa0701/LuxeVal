package com.example.luxeval.data.mappers

import com.example.luxeval.data.database.entities.BraceletDetailsEntity
import com.example.luxeval.data.database.entities.BroochDetailsEntity
import com.example.luxeval.data.database.entities.EarringsDetailsEntity
import com.example.luxeval.data.database.entities.GemstoneDetailsColumns
import com.example.luxeval.data.database.entities.ItemEntity
import com.example.luxeval.data.database.entities.LooseGemstoneDetailsEntity
import com.example.luxeval.data.database.entities.MetalDetailsColumns
import com.example.luxeval.data.database.entities.NecklaceDetailsEntity
import com.example.luxeval.data.database.entities.PearlDetailsEntity
import com.example.luxeval.data.database.entities.PendantDetailsEntity
import com.example.luxeval.data.database.entities.RingDetailsEntity
import com.example.luxeval.data.database.entities.WatchDetailsEntity
import com.example.luxeval.domain.AppraisalItem
import com.example.luxeval.domain.BraceletStyle
import com.example.luxeval.domain.BroochPinMechanism
import com.example.luxeval.domain.ClaspType
import com.example.luxeval.domain.EarringBacking
import com.example.luxeval.domain.EarringStyle
import com.example.luxeval.domain.Fluorescence
import com.example.luxeval.domain.GemstoneDetails
import com.example.luxeval.domain.ItemCondition
import com.example.luxeval.domain.MetalColor
import com.example.luxeval.domain.MetalDetails
import com.example.luxeval.domain.MetalType
import com.example.luxeval.domain.PearlLuster
import com.example.luxeval.domain.PearlShape
import com.example.luxeval.domain.PearlSurfaceQuality
import com.example.luxeval.domain.PearlType
import com.example.luxeval.domain.RingBandStyle
import com.example.luxeval.domain.SettingType
import com.example.luxeval.domain.ShankType
import com.example.luxeval.domain.StoneOriginType
import com.example.luxeval.domain.StoneShape
import com.example.luxeval.domain.StoneTreatment
import com.example.luxeval.domain.StoneType
import com.example.luxeval.domain.ValuationBasis
import com.example.luxeval.domain.ValueSource
import com.example.luxeval.domain.WatchComplication
import com.example.luxeval.domain.WatchMovementType
import com.example.luxeval.domain.WatchRunningCondition

// ── shared helpers ──────────────────────────────────────────

private fun MetalDetailsColumns.toDomain() = MetalDetails(
    metalType = MetalType.valueOf(metalType),
    purity = purity,
    color = MetalColor.valueOf(color),
    netWeightGrams = netWeightGrams,
    isPlated = isPlated,
    platingBaseMetal = platingBaseMetal
)

private fun MetalDetails.toColumns() = MetalDetailsColumns(
    metalType = metalType.name,
    purity = purity,
    color = color.name,
    netWeightGrams = netWeightGrams,
    isPlated = isPlated,
    platingBaseMetal = platingBaseMetal
)

private fun GemstoneDetailsColumns.toDomain() = GemstoneDetails(
    stoneType = StoneType.valueOf(stoneType),
    originType = StoneOriginType.valueOf(originType),
    caratWeight = caratWeight,
    shape = StoneShape.valueOf(shape),
    colorGrade = colorGrade,
    clarityGrade = clarityGrade,
    treatment = StoneTreatment.valueOf(treatment),
    fluorescence = fluorescence?.let { Fluorescence.valueOf(it) },
    settingType = SettingType.valueOf(settingType),
    stoneCount = stoneCount,
    certificationLab = certificationLab,
    certificationNumber = certificationNumber,
    measurementsMm = measurementsMm
)

private fun GemstoneDetails.toColumns() = GemstoneDetailsColumns(
    stoneType = stoneType.name,
    originType = originType.name,
    caratWeight = caratWeight,
    shape = shape.name,
    colorGrade = colorGrade,
    clarityGrade = clarityGrade,
    treatment = treatment.name,
    fluorescence = fluorescence?.name,
    settingType = settingType.name,
    stoneCount = stoneCount,
    certificationLab = certificationLab,
    certificationNumber = certificationNumber,
    measurementsMm = measurementsMm
)

/** Every AppraisalItem subtype maps to the same base ItemEntity columns. */
fun AppraisalItem.toItemEntity() = ItemEntity(
    id = id,
    appraisalId = appraisalId,
    category = category.name,
    photoUris = photoUris,
    condition = condition.name,
    conditionNotes = conditionNotes,
    grossWeightGrams = grossWeightGrams,
    hallmarks = hallmarks,
    estimatedValue = estimatedValue,
    currency = currency,
    valuationBasis = valuationBasis.name,
    valueSource = valueSource.name,
    notes = notes,
    createdAt = createdAt,
    updatedAt = updatedAt
)

// ── Ring ─────────────────────────────────────────────────────

fun AppraisalItem.Ring.toDetailsEntity() = RingDetailsEntity(
    itemId = id,
    ringSize = ringSize,
    bandWidthMm = bandWidthMm,
    bandStyle = bandStyle.name,
    shankType = shankType?.name,
    engraving = engraving,
    metal = metal.toColumns(),
    gemstone = gemstone?.toColumns()
)

fun RingDetailsEntity.toDomain(item: ItemEntity) = AppraisalItem.Ring(
    id = item.id,
    appraisalId = item.appraisalId,
    photoUris = item.photoUris,
    condition = ItemCondition.valueOf(item.condition),
    conditionNotes = item.conditionNotes,
    grossWeightGrams = item.grossWeightGrams,
    hallmarks = item.hallmarks,
    estimatedValue = item.estimatedValue,
    currency = item.currency,
    valuationBasis = ValuationBasis.valueOf(item.valuationBasis),
    valueSource = ValueSource.valueOf(item.valueSource),
    notes = item.notes,
    createdAt = item.createdAt,
    updatedAt = item.updatedAt,
    ringSize = ringSize,
    bandWidthMm = bandWidthMm,
    bandStyle = RingBandStyle.valueOf(bandStyle),
    shankType = shankType?.let { ShankType.valueOf(it) },
    engraving = engraving,
    metal = metal.toDomain(),
    gemstone = gemstone?.toDomain()
)

// ── Watch ────────────────────────────────────────────────────

fun AppraisalItem.Watch.toDetailsEntity() = WatchDetailsEntity(
    itemId = id,
    brand = brand,
    model = model,
    referenceNumber = referenceNumber,
    movementType = movementType.name,
    caseMaterial = caseMaterial,
    caseSizeMm = caseSizeMm,
    dialColor = dialColor,
    complications = complications.map { it.name },
    bandMaterial = bandMaterial,
    waterResistance = waterResistance,
    serialNumber = serialNumber,
    hasBox = hasBox,
    hasPapers = hasPapers,
    runningCondition = runningCondition.name,
    yearOfManufacture = yearOfManufacture
)

fun WatchDetailsEntity.toDomain(item: ItemEntity) = AppraisalItem.Watch(
    id = item.id,
    appraisalId = item.appraisalId,
    photoUris = item.photoUris,
    condition = ItemCondition.valueOf(item.condition),
    conditionNotes = item.conditionNotes,
    grossWeightGrams = item.grossWeightGrams,
    hallmarks = item.hallmarks,
    estimatedValue = item.estimatedValue,
    currency = item.currency,
    valuationBasis = ValuationBasis.valueOf(item.valuationBasis),
    valueSource = ValueSource.valueOf(item.valueSource),
    notes = item.notes,
    createdAt = item.createdAt,
    updatedAt = item.updatedAt,
    brand = brand,
    model = model,
    referenceNumber = referenceNumber,
    movementType = WatchMovementType.valueOf(movementType),
    caseMaterial = caseMaterial,
    caseSizeMm = caseSizeMm,
    dialColor = dialColor,
    complications = complications.map { WatchComplication.valueOf(it) },
    bandMaterial = bandMaterial,
    waterResistance = waterResistance,
    serialNumber = serialNumber,
    hasBox = hasBox,
    hasPapers = hasPapers,
    runningCondition = WatchRunningCondition.valueOf(runningCondition),
    yearOfManufacture = yearOfManufacture
)

// ── Loose Gemstone ─────────────────────────────────────────────

fun AppraisalItem.LooseGemstone.toDetailsEntity() = LooseGemstoneDetailsEntity(
    itemId = id,
    gemstone = gemstone.toColumns()
)

fun LooseGemstoneDetailsEntity.toDomain(item: ItemEntity) = AppraisalItem.LooseGemstone(
    id = item.id,
    appraisalId = item.appraisalId,
    photoUris = item.photoUris,
    condition = ItemCondition.valueOf(item.condition),
    conditionNotes = item.conditionNotes,
    grossWeightGrams = item.grossWeightGrams,
    hallmarks = item.hallmarks,
    estimatedValue = item.estimatedValue,
    currency = item.currency,
    valuationBasis = ValuationBasis.valueOf(item.valuationBasis),
    valueSource = ValueSource.valueOf(item.valueSource),
    notes = item.notes,
    createdAt = item.createdAt,
    updatedAt = item.updatedAt,
    gemstone = gemstone.toDomain()
)

// ── Necklace ─────────────────────────────────────────────

fun AppraisalItem.Necklace.toDetailsEntity() = NecklaceDetailsEntity(
    itemId = id,
    lengthCm = lengthCm,
    chainStyle = chainStyle ,
    claspType = claspType?.name,
    hasPendantAttached = hasPendantAttached,
    metal = metal.toColumns(),
    gemstone = gemstone?.toColumns()
)

fun NecklaceDetailsEntity.toDomain(item: ItemEntity) = AppraisalItem.Necklace(
    id = item.id,
    appraisalId = item.appraisalId,
    photoUris = item.photoUris,
    condition = ItemCondition.valueOf(item.condition),
    conditionNotes = item.conditionNotes,
    grossWeightGrams = item.grossWeightGrams,
    hallmarks = item.hallmarks,
    estimatedValue = item.estimatedValue,
    currency = item.currency,
    valuationBasis = ValuationBasis.valueOf(item.valuationBasis),
    valueSource = ValueSource.valueOf(item.valueSource),
    notes = item.notes,
    createdAt = item.createdAt,
    updatedAt = item.updatedAt,
    lengthCm = lengthCm,
    chainStyle = chainStyle,
    claspType = claspType?.let { ClaspType.valueOf(it) },
    hasPendantAttached = hasPendantAttached,
    metal = metal.toDomain(),
    gemstone = gemstone?.toDomain()
)

// ── Pendant ─────────────────────────────────────────────────────

fun AppraisalItem.Pendant.toDetailsEntity() = PendantDetailsEntity(
    itemId = id,
    bailType = bailType,
    heightMm = heightMm,
    widthMm = widthMm,
    soldWithChain = soldWithChain,
    metal = metal.toColumns(),
    gemstone = gemstone?.toColumns()
)

fun PendantDetailsEntity.toDomain(item: ItemEntity) = AppraisalItem.Pendant(
    id = item.id,
    appraisalId = item.appraisalId,
    photoUris = item.photoUris,
    condition = ItemCondition.valueOf(item.condition),
    conditionNotes = item.conditionNotes,
    grossWeightGrams = item.grossWeightGrams,
    hallmarks = item.hallmarks,
    estimatedValue = item.estimatedValue,
    currency = item.currency,
    valuationBasis = ValuationBasis.valueOf(item.valuationBasis),
    valueSource = ValueSource.valueOf(item.valueSource),
    notes = item.notes,
    createdAt = item.createdAt,
    updatedAt = item.updatedAt,
    bailType = bailType,
    heightMm = heightMm,
    widthMm = widthMm,
    soldWithChain = soldWithChain,
    metal = metal.toDomain(),
    gemstone = gemstone?.toDomain()
)

// ── Bracelet ────────────────────────────────────────────────────

fun AppraisalItem.Bracelet.toDetailsEntity() = BraceletDetailsEntity(
    itemId = id,
    lengthCm = lengthCm,
    style = style.name,
    claspType = claspType?.name,
    isAdjustable = isAdjustable,
    charmCount = charmCount ?: 0,
    charmDescriptions = charmDescriptions.joinToString("\n"),
    metal = metal.toColumns(),
    gemstone = gemstone?.toColumns()
)

fun BraceletDetailsEntity.toDomain(item: ItemEntity) = AppraisalItem.Bracelet(
    id = item.id,
    appraisalId = item.appraisalId,
    photoUris = item.photoUris,
    condition = ItemCondition.valueOf(item.condition),
    conditionNotes = item.conditionNotes,
    grossWeightGrams = item.grossWeightGrams,
    hallmarks = item.hallmarks,
    estimatedValue = item.estimatedValue,
    currency = item.currency,
    valuationBasis = ValuationBasis.valueOf(item.valuationBasis),
    valueSource = ValueSource.valueOf(item.valueSource),
    notes = item.notes,
    createdAt = item.createdAt,
    updatedAt = item.updatedAt,
    lengthCm = lengthCm,
    style = BraceletStyle.valueOf(style),
    claspType = claspType?.let { ClaspType.valueOf(it) },
    isAdjustable = isAdjustable,
    charmCount = charmCount,
    charmDescriptions =
        if (charmDescriptions.isNullOrEmpty()) {
            emptyList()
        } else {
            charmDescriptions.split("\n")
        },
    metal = metal.toDomain(),
    gemstone = gemstone?.toDomain()
)

// ── Earrings ────────────────────────────────────────────────────

fun AppraisalItem.Earrings.toDetailsEntity() = EarringsDetailsEntity(
    itemId = id,
    style = style.name,
    backing = backing.name,
    isConfirmedPair = isConfirmedPair,
    hoopDiameterMm = hoopDiameterMm,
    metal = metal.toColumns(),
    gemstone = gemstone?.toColumns()
)

fun EarringsDetailsEntity.toDomain(item: ItemEntity) = AppraisalItem.Earrings(
    id = item.id,
    appraisalId = item.appraisalId,
    photoUris = item.photoUris,
    condition = ItemCondition.valueOf(item.condition),
    conditionNotes = item.conditionNotes,
    grossWeightGrams = item.grossWeightGrams,
    hallmarks = item.hallmarks,
    estimatedValue = item.estimatedValue,
    currency = item.currency,
    valuationBasis = ValuationBasis.valueOf(item.valuationBasis),
    valueSource = ValueSource.valueOf(item.valueSource),
    notes = item.notes,
    createdAt = item.createdAt,
    updatedAt = item.updatedAt,
    style = EarringStyle.valueOf(style),
    backing = EarringBacking.valueOf(backing),
    isConfirmedPair = isConfirmedPair,
    hoopDiameterMm = hoopDiameterMm,
    metal = metal.toDomain(),
    gemstone = gemstone?.toDomain()
)

// ── Brooch ──────────────────────────────────────────────────────

fun AppraisalItem.Brooch.toDetailsEntity() = BroochDetailsEntity(
    itemId = id,
    pinMechanism = pinMechanism.name,
    heightMm = heightMm,
    widthMm = widthMm,
    motifDescription = motifDescription,
    metal = metal.toColumns(),
    gemstone = gemstone?.toColumns()
)

fun BroochDetailsEntity.toDomain(item: ItemEntity) = AppraisalItem.Brooch(
    id = item.id,
    appraisalId = item.appraisalId,
    photoUris = item.photoUris,
    condition = ItemCondition.valueOf(item.condition),
    conditionNotes = item.conditionNotes,
    grossWeightGrams = item.grossWeightGrams,
    hallmarks = item.hallmarks,
    estimatedValue = item.estimatedValue,
    currency = item.currency,
    valuationBasis = ValuationBasis.valueOf(item.valuationBasis),
    valueSource = ValueSource.valueOf(item.valueSource),
    notes = item.notes,
    createdAt = item.createdAt,
    updatedAt = item.updatedAt,
    pinMechanism = BroochPinMechanism.valueOf(pinMechanism),
    heightMm = heightMm,
    widthMm = widthMm,
    motifDescription = motifDescription,
    metal = metal.toDomain(),
    gemstone = gemstone?.toDomain()
)

// ── Pearl Jewelry ───────────────────────────────────────────────

fun AppraisalItem.PearlJewelry.toDetailsEntity() = PearlDetailsEntity(
    itemId = id,
    pearlType = pearlType.name,
    sizeMm = sizeMm,
    shape = shape.name,
    luster = luster.name,
    surfaceQuality = surfaceQuality.name,
    overtoneColor = overtoneColor,
    pearlCount = pearlCount,
    isMatchedStrand = isMatchedStrand,
    metal = metal?.toColumns()
)

fun PearlDetailsEntity.toDomain(item: ItemEntity) = AppraisalItem.PearlJewelry(
    id = item.id,
    appraisalId = item.appraisalId,
    photoUris = item.photoUris,
    condition = ItemCondition.valueOf(item.condition),
    conditionNotes = item.conditionNotes,
    grossWeightGrams = item.grossWeightGrams,
    hallmarks = item.hallmarks,
    estimatedValue = item.estimatedValue,
    currency = item.currency,
    valuationBasis = ValuationBasis.valueOf(item.valuationBasis),
    valueSource = ValueSource.valueOf(item.valueSource),
    notes = item.notes,
    createdAt = item.createdAt,
    updatedAt = item.updatedAt,
    pearlType = PearlType.valueOf(pearlType),
    sizeMm = sizeMm,
    shape = PearlShape.valueOf(shape),
    luster = PearlLuster.valueOf(luster),
    surfaceQuality = PearlSurfaceQuality.valueOf(surfaceQuality),
    overtoneColor = overtoneColor,
    pearlCount = pearlCount,
    isMatchedStrand = isMatchedStrand,
    metal = metal?.toDomain()
)
