package com.example.luxeval.data.remote

import com.example.luxeval.data.remote.RemoteItem
import com.example.luxeval.domain.*
import kotlinx.serialization.json.*

// ───────────────────────────── JsonObject read helpers ─────────────────────────────

private fun JsonObject.string(key: String): String =
    (this.getValue(key) as JsonPrimitive).content

private fun JsonObject.stringOrNull(key: String): String? {
    val v = this[key] ?: return null
    return if (v is JsonNull) null else (v as JsonPrimitive).content
}

private fun JsonObject.double(key: String): Double =
    (this.getValue(key) as JsonPrimitive).double

private fun JsonObject.doubleOrNull(key: String): Double? {
    val v = this[key] ?: return null
    return if (v is JsonNull) null else (v as JsonPrimitive).double
}

private fun JsonObject.int(key: String): Int =
    (this.getValue(key) as JsonPrimitive).int

private fun JsonObject.intOrNull(key: String): Int? {
    val v = this[key] ?: return null
    return if (v is JsonNull) null else (v as JsonPrimitive).int
}

private fun JsonObject.bool(key: String): Boolean =
    (this.getValue(key) as JsonPrimitive).boolean

private fun JsonObject.stringList(key: String): List<String> =
    (this.getValue(key) as JsonArray).map { (it as JsonPrimitive).content }

// ───────────────────────────── JsonObjectBuilder write helpers ─────────────────────

private fun JsonObjectBuilder.put(key: String, value: String?) {
    put(key, value?.let { JsonPrimitive(it) } ?: JsonNull)
}
private fun JsonObjectBuilder.put(key: String, value: Double?) {
    put(key, value?.let { JsonPrimitive(it) } ?: JsonNull)
}
private fun JsonObjectBuilder.put(key: String, value: Int?) {
    put(key, value?.let { JsonPrimitive(it) } ?: JsonNull)
}
private fun JsonObjectBuilder.put(key: String, value: Boolean) {
    put(key, JsonPrimitive(value))
}
private fun JsonObjectBuilder.putStringList(key: String, value: List<String>) {
    put(key, JsonArray(value.map { JsonPrimitive(it) }))
}

// ───────────────────────────── Metal / Gemstone (shared) ───────────────────────────

private fun MetalDetails.toJson(): JsonObject = buildJsonObject {
    put("metalType", metalType.name)
    put("purity", purity)
    put("color", color.name)
    put("netWeightGrams", netWeightGrams)
    put("isPlated", isPlated)
    put("platingBaseMetal", platingBaseMetal)
}

private fun JsonObject.parseMetal(): MetalDetails = MetalDetails(
    metalType = MetalType.valueOf(string("metalType")),
    purity = string("purity"),
    color = MetalColor.valueOf(string("color")),
    netWeightGrams = doubleOrNull("netWeightGrams"),
    isPlated = bool("isPlated"),
    platingBaseMetal = stringOrNull("platingBaseMetal")
)

private fun GemstoneDetails.toJson(): JsonObject = buildJsonObject {
    put("stoneType", stoneType.name)
    put("originType", originType.name)
    put("caratWeight", caratWeight)
    put("shape", shape.name)
    put("colorGrade", colorGrade)
    put("clarityGrade", clarityGrade)
    put("treatment", treatment.name)
    put("fluorescence", fluorescence?.name)
    put("settingType", settingType.name)
    put("stoneCount", stoneCount)
    put("certificationLab", certificationLab)
    put("certificationNumber", certificationNumber)
    put("measurementsMm", measurementsMm)
}

private fun JsonObject.parseGemstone(): GemstoneDetails = GemstoneDetails(
    stoneType = StoneType.valueOf(string("stoneType")),
    originType = StoneOriginType.valueOf(string("originType")),
    caratWeight = double("caratWeight"),
    shape = StoneShape.valueOf(string("shape")),
    colorGrade = stringOrNull("colorGrade"),
    clarityGrade = stringOrNull("clarityGrade"),
    treatment = StoneTreatment.valueOf(string("treatment")),
    fluorescence = stringOrNull("fluorescence")?.let { Fluorescence.valueOf(it) },
    settingType = SettingType.valueOf(string("settingType")),
    stoneCount = int("stoneCount"),
    certificationLab = stringOrNull("certificationLab"),
    certificationNumber = stringOrNull("certificationNumber"),
    measurementsMm = stringOrNull("measurementsMm")
)

// ───────────────────────────── detailsToJson: domain → JSON ────────────────────────

fun AppraisalItem.detailsToJson(): JsonElement = when (this) {

    is AppraisalItem.Ring -> buildJsonObject {
        put("ringSize", ringSize)
        put("bandWidthMm", bandWidthMm)
        put("bandStyle", bandStyle.name)
        put("shankType", shankType?.name)
        put("engraving", engraving)
        put("metal", metal.toJson())
        gemstone?.let { put("gemstone", it.toJson()) }
    }

    is AppraisalItem.Necklace -> buildJsonObject {
        put("lengthCm", lengthCm)
        put("chainStyle", chainStyle)
        put("claspType", claspType?.name)
        put("hasPendantAttached", hasPendantAttached)
        put("metal", metal.toJson())
        gemstone?.let { put("gemstone", it.toJson()) }
    }

    is AppraisalItem.Pendant -> buildJsonObject {
        put("bailType", bailType)
        put("heightMm", heightMm)
        put("widthMm", widthMm)
        put("soldWithChain", soldWithChain)
        put("metal", metal.toJson())
        gemstone?.let { put("gemstone", it.toJson()) }
    }

    is AppraisalItem.Bracelet -> buildJsonObject {
        put("lengthCm", lengthCm)
        put("style", style.name)
        put("claspType", claspType?.name)
        put("isAdjustable", isAdjustable)
        put("charmCount", charmCount)
        putStringList("charmDescriptions", charmDescriptions)
        put("metal", metal.toJson())
        gemstone?.let { put("gemstone", it.toJson()) }
    }

    is AppraisalItem.Earrings -> buildJsonObject {
        put("style", style.name)
        put("backing", backing.name)
        put("isConfirmedPair", isConfirmedPair)
        put("hoopDiameterMm", hoopDiameterMm)
        put("metal", metal.toJson())
        gemstone?.let { put("gemstone", it.toJson()) }
    }

    is AppraisalItem.Brooch -> buildJsonObject {
        put("pinMechanism", pinMechanism.name)
        put("heightMm", heightMm)
        put("widthMm", widthMm)
        put("motifDescription", motifDescription)
        put("metal", metal.toJson())
        gemstone?.let { put("gemstone", it.toJson()) }
    }

    is AppraisalItem.Watch -> buildJsonObject {
        put("brand", brand)
        put("model", model)
        put("referenceNumber", referenceNumber)
        put("movementType", movementType.name)
        put("caseMaterial", caseMaterial)
        put("caseSizeMm", caseSizeMm)
        put("dialColor", dialColor)
        putStringList("complications", complications.map { it.name })
        put("bandMaterial", bandMaterial)
        put("waterResistance", waterResistance)
        put("serialNumber", serialNumber)
        put("hasBox", hasBox)
        put("hasPapers", hasPapers)
        put("runningCondition", runningCondition.name)
        put("yearOfManufacture", yearOfManufacture)
    }

    is AppraisalItem.LooseGemstone -> buildJsonObject {
        put("gemstone", gemstone.toJson())
    }

    is AppraisalItem.PearlJewelry -> buildJsonObject {
        put("pearlType", pearlType.name)
        put("sizeMm", sizeMm)
        put("shape", shape.name)
        put("luster", luster.name)
        put("surfaceQuality", surfaceQuality.name)
        put("overtoneColor", overtoneColor)
        put("pearlCount", pearlCount)
        put("isMatchedStrand", isMatchedStrand)
        metal?.let { put("metal", it.toJson()) }
    }

    // Other has no detail table locally either (ItemRepository.saveItem throws
    // NotImplementedError for it) — mirror that limitation here rather than
    // silently pretending it's supported.
    is AppraisalItem.Other -> buildJsonObject { }
}

// ───────────────────────────── RemoteItem → domain: JSON → AppraisalItem ───────────

private data class ItemBaseFields(
    val id: String,
    val itemName: String,
    val appraisalId: String,
    val photoUris: List<String>,
    val condition: ItemCondition,
    val conditionNotes: String?,
    val grossWeightGrams: Double?,
    val hallmarks: String?,
    val estimatedValue: Double?,
    val currency: String,
    val valuationBasis: ValuationBasis,
    val valueSource: ValueSource,
    val notes: String?,
    val createdAt: Long,
    val updatedAt: Long
)

/** Returns null for OTHER (unsupported locally) or any category the mapper
 *  doesn't recognize — callers should skip nulls rather than crash a sync pass
 *  over one bad/legacy row. */
fun RemoteItem.toDomain(): AppraisalItem? {
    val category = runCatching { ItemCategory.valueOf(category) }.getOrNull() ?: return null
    if (category == ItemCategory.OTHER) return null

    val base = ItemBaseFields(
        id = id,
        itemName = itemName,
        appraisalId = appraisalId,
        photoUris = if (photoUris.isBlank()) emptyList() else photoUris.split("‖"),
        condition = ItemCondition.valueOf(condition),
        conditionNotes = conditionNotes,
        grossWeightGrams = grossWeightGrams,
        hallmarks = hallmarks,
        estimatedValue = estimatedValue,
        currency = currency,
        valuationBasis = ValuationBasis.valueOf(valuationBasis),
        valueSource = ValueSource.valueOf(valueSource),
        notes = notes,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
    val d = details.jsonObject

    return when (category) {
        ItemCategory.RING -> AppraisalItem.Ring(
            id = base.id, itemName = base.itemName, appraisalId = base.appraisalId,
            photoUris = base.photoUris, condition = base.condition, conditionNotes = base.conditionNotes,
            grossWeightGrams = base.grossWeightGrams, hallmarks = base.hallmarks,
            estimatedValue = base.estimatedValue, currency = base.currency,
            valuationBasis = base.valuationBasis, valueSource = base.valueSource, notes = base.notes,
            createdAt = base.createdAt, updatedAt = base.updatedAt,
            ringSize = d.stringOrNull("ringSize"),
            bandWidthMm = d.doubleOrNull("bandWidthMm"),
            bandStyle = RingBandStyle.valueOf(d.string("bandStyle")),
            shankType = d.stringOrNull("shankType")?.let { ShankType.valueOf(it) },
            engraving = d.stringOrNull("engraving"),
            metal = d["metal"]!!.jsonObject.parseMetal(),
            gemstone = d["gemstone"]?.jsonObject?.parseGemstone(),
            measurementUnit = MeasurementUnit.valueOf(measurementUnit),
        )

        ItemCategory.NECKLACE -> AppraisalItem.Necklace(
            id = base.id, itemName = base.itemName, appraisalId = base.appraisalId,
            photoUris = base.photoUris, condition = base.condition, conditionNotes = base.conditionNotes,
            grossWeightGrams = base.grossWeightGrams, hallmarks = base.hallmarks,
            estimatedValue = base.estimatedValue, currency = base.currency,
            valuationBasis = base.valuationBasis, valueSource = base.valueSource, notes = base.notes,
            createdAt = base.createdAt, updatedAt = base.updatedAt,
            lengthCm = d.doubleOrNull("lengthCm"),
            chainStyle = d.stringOrNull("chainStyle"),
            claspType = d.stringOrNull("claspType")?.let { ClaspType.valueOf(it) },
            hasPendantAttached = d.bool("hasPendantAttached"),
            metal = d["metal"]!!.jsonObject.parseMetal(),
            gemstone = d["gemstone"]?.jsonObject?.parseGemstone(),
            measurementUnit = MeasurementUnit.valueOf(measurementUnit),
        )

        ItemCategory.PENDANT -> AppraisalItem.Pendant(
            id = base.id, itemName = base.itemName, appraisalId = base.appraisalId,
            photoUris = base.photoUris, condition = base.condition, conditionNotes = base.conditionNotes,
            grossWeightGrams = base.grossWeightGrams, hallmarks = base.hallmarks,
            estimatedValue = base.estimatedValue, currency = base.currency,
            valuationBasis = base.valuationBasis, valueSource = base.valueSource, notes = base.notes,
            createdAt = base.createdAt, updatedAt = base.updatedAt,
            bailType = d.stringOrNull("bailType"),
            heightMm = d.doubleOrNull("heightMm"),
            widthMm = d.doubleOrNull("widthMm"),
            soldWithChain = d.bool("soldWithChain"),
            metal = d["metal"]!!.jsonObject.parseMetal(),
            gemstone = d["gemstone"]?.jsonObject?.parseGemstone(),
            measurementUnit = MeasurementUnit.valueOf(measurementUnit),
        )

        ItemCategory.BRACELET -> AppraisalItem.Bracelet(
            id = base.id, itemName = base.itemName, appraisalId = base.appraisalId,
            photoUris = base.photoUris, condition = base.condition, conditionNotes = base.conditionNotes,
            grossWeightGrams = base.grossWeightGrams, hallmarks = base.hallmarks,
            estimatedValue = base.estimatedValue, currency = base.currency,
            valuationBasis = base.valuationBasis, valueSource = base.valueSource, notes = base.notes,
            createdAt = base.createdAt, updatedAt = base.updatedAt,
            lengthCm = d.doubleOrNull("lengthCm"),
            style = BraceletStyle.valueOf(d.string("style")),
            claspType = d.stringOrNull("claspType")?.let { ClaspType.valueOf(it) },
            isAdjustable = d.bool("isAdjustable"),
            charmCount = d.intOrNull("charmCount"),
            charmDescriptions = d.stringList("charmDescriptions"),
            metal = d["metal"]!!.jsonObject.parseMetal(),
            gemstone = d["gemstone"]?.jsonObject?.parseGemstone(),
            measurementUnit = MeasurementUnit.valueOf(measurementUnit),
        )

        ItemCategory.EARRINGS -> AppraisalItem.Earrings(
            id = base.id, itemName = base.itemName, appraisalId = base.appraisalId,
            photoUris = base.photoUris, condition = base.condition, conditionNotes = base.conditionNotes,
            grossWeightGrams = base.grossWeightGrams, hallmarks = base.hallmarks,
            estimatedValue = base.estimatedValue, currency = base.currency,
            valuationBasis = base.valuationBasis, valueSource = base.valueSource, notes = base.notes,
            createdAt = base.createdAt, updatedAt = base.updatedAt,
            style = EarringStyle.valueOf(d.string("style")),
            backing = EarringBacking.valueOf(d.string("backing")),
            isConfirmedPair = d.bool("isConfirmedPair"),
            hoopDiameterMm = d.doubleOrNull("hoopDiameterMm"),
            metal = d["metal"]!!.jsonObject.parseMetal(),
            gemstone = d["gemstone"]?.jsonObject?.parseGemstone(),
            measurementUnit = MeasurementUnit.valueOf(measurementUnit),
        )

        ItemCategory.BROOCH -> AppraisalItem.Brooch(
            id = base.id, itemName = base.itemName, appraisalId = base.appraisalId,
            photoUris = base.photoUris, condition = base.condition, conditionNotes = base.conditionNotes,
            grossWeightGrams = base.grossWeightGrams, hallmarks = base.hallmarks,
            estimatedValue = base.estimatedValue, currency = base.currency,
            valuationBasis = base.valuationBasis, valueSource = base.valueSource, notes = base.notes,
            createdAt = base.createdAt, updatedAt = base.updatedAt,
            pinMechanism = BroochPinMechanism.valueOf(d.string("pinMechanism")),
            heightMm = d.doubleOrNull("heightMm"),
            widthMm = d.doubleOrNull("widthMm"),
            motifDescription = d.stringOrNull("motifDescription"),
            metal = d["metal"]!!.jsonObject.parseMetal(),
            gemstone = d["gemstone"]?.jsonObject?.parseGemstone(),
            measurementUnit = MeasurementUnit.valueOf(measurementUnit),
        )

        ItemCategory.WATCH -> AppraisalItem.Watch(
            id = base.id, itemName = base.itemName, appraisalId = base.appraisalId,
            photoUris = base.photoUris, condition = base.condition, conditionNotes = base.conditionNotes,
            grossWeightGrams = base.grossWeightGrams, hallmarks = base.hallmarks,
            estimatedValue = base.estimatedValue, currency = base.currency,
            valuationBasis = base.valuationBasis, valueSource = base.valueSource, notes = base.notes,
            createdAt = base.createdAt, updatedAt = base.updatedAt,
            brand = d.string("brand"),
            model = d.stringOrNull("model"),
            referenceNumber = d.stringOrNull("referenceNumber"),
            movementType = WatchMovementType.valueOf(d.string("movementType")),
            caseMaterial = d.string("caseMaterial"),
            caseSizeMm = d.doubleOrNull("caseSizeMm"),
            dialColor = d.stringOrNull("dialColor"),
            complications = d.stringList("complications").map { WatchComplication.valueOf(it) },
            bandMaterial = d.stringOrNull("bandMaterial"),
            waterResistance = d.stringOrNull("waterResistance"),
            serialNumber = d.stringOrNull("serialNumber"),
            hasBox = d.bool("hasBox"),
            hasPapers = d.bool("hasPapers"),
            runningCondition = WatchRunningCondition.valueOf(d.string("runningCondition")),
            yearOfManufacture = d.intOrNull("yearOfManufacture"),
            measurementUnit = MeasurementUnit.valueOf(measurementUnit),
        )

        ItemCategory.LOOSE_GEMSTONE -> AppraisalItem.LooseGemstone(
            id = base.id, itemName = base.itemName, appraisalId = base.appraisalId,
            photoUris = base.photoUris, condition = base.condition, conditionNotes = base.conditionNotes,
            grossWeightGrams = base.grossWeightGrams, hallmarks = base.hallmarks,
            estimatedValue = base.estimatedValue, currency = base.currency,
            valuationBasis = base.valuationBasis, valueSource = base.valueSource, notes = base.notes,
            createdAt = base.createdAt, updatedAt = base.updatedAt,
            gemstone = d["gemstone"]!!.jsonObject.parseGemstone(),
            measurementUnit = MeasurementUnit.valueOf(measurementUnit),
        )

        ItemCategory.PEARL_JEWELRY -> AppraisalItem.PearlJewelry(
            id = base.id, itemName = base.itemName, appraisalId = base.appraisalId,
            photoUris = base.photoUris, condition = base.condition, conditionNotes = base.conditionNotes,
            grossWeightGrams = base.grossWeightGrams, hallmarks = base.hallmarks,
            estimatedValue = base.estimatedValue, currency = base.currency,
            valuationBasis = base.valuationBasis, valueSource = base.valueSource, notes = base.notes,
            createdAt = base.createdAt, updatedAt = base.updatedAt,
            pearlType = PearlType.valueOf(d.string("pearlType")),
            sizeMm = d.doubleOrNull("sizeMm"),
            shape = PearlShape.valueOf(d.string("shape")),
            luster = PearlLuster.valueOf(d.string("luster")),
            surfaceQuality = PearlSurfaceQuality.valueOf(d.string("surfaceQuality")),
            overtoneColor = d.stringOrNull("overtoneColor"),
            pearlCount = d.int("pearlCount"),
            isMatchedStrand = d.bool("isMatchedStrand"),
            metal = d["metal"]?.jsonObject?.parseMetal(),
            measurementUnit = MeasurementUnit.valueOf(measurementUnit),
        )

        ItemCategory.OTHER -> null // unreachable — guarded above
    }
}