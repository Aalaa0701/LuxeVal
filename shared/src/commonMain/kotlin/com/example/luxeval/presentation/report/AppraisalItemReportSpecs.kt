package com.example.luxeval.presentation.report

import com.example.luxeval.domain.*

data class SpecLine(val label: String, val value: String)

/** Flattens any category's unique fields into display-ready label/value pairs, in the
 *  order they should appear on the report. Metal/gemstone (shared across 6 categories)
 *  are handled once here rather than duplicated per branch. */
fun AppraisalItem.reportSpecs(): List<SpecLine> {
    val specs = mutableListOf<SpecLine>()

    when (this) {
        is AppraisalItem.Ring -> {
            ringSize?.let { specs += SpecLine("Ring Size", it) }
            bandWidthMm?.let { specs += SpecLine("Band Width", "${it} mm") }
            specs += SpecLine("Band Style", bandStyle.displayName())
            shankType?.let { specs += SpecLine("Shank Type", it.displayName()) }
            engraving?.let { specs += SpecLine("Engraving", it) }
            specs += metal.toSpecLines()
            gemstone?.let { specs += it.toSpecLines() }
        }
        is AppraisalItem.Necklace -> {
            lengthCm?.let { specs += SpecLine("Length", "${it} cm") }
            chainStyle?.let { specs += SpecLine("Chain Style", it) }
            claspType?.let { specs += SpecLine("Clasp Type", it.displayName()) }
            specs += SpecLine("Pendant Attached", if (hasPendantAttached) "Yes" else "No")
            specs += metal.toSpecLines()
            gemstone?.let { specs += it.toSpecLines() }
        }
        is AppraisalItem.Pendant -> {
            bailType?.let { specs += SpecLine("Bail Type", it) }
            heightMm?.let { specs += SpecLine("Height", "${it} mm") }
            widthMm?.let { specs += SpecLine("Width", "${it} mm") }
            specs += SpecLine("Sold With Chain", if (soldWithChain) "Yes" else "No")
            specs += metal.toSpecLines()
            gemstone?.let { specs += it.toSpecLines() }
        }
        is AppraisalItem.Bracelet -> {
            lengthCm?.let { specs += SpecLine("Length", "${it} cm") }
            specs += SpecLine("Style", style.displayName())
            claspType?.let { specs += SpecLine("Clasp Type", it.displayName()) }
            specs += SpecLine("Adjustable", if (isAdjustable) "Yes" else "No")
            charmCount?.let { specs += SpecLine("Charm Count", it.toString()) }
            if (charmDescriptions.isNotEmpty()) {
                specs += SpecLine("Charms", charmDescriptions.joinToString(", "))
            }
            specs += metal.toSpecLines()
            gemstone?.let { specs += it.toSpecLines() }
        }
        is AppraisalItem.Earrings -> {
            specs += SpecLine("Style", style.displayName())
            specs += SpecLine("Backing", backing.displayName())
            specs += SpecLine("Confirmed Pair", if (isConfirmedPair) "Yes" else "No")
            hoopDiameterMm?.let { specs += SpecLine("Hoop Diameter", "${it} mm") }
            specs += metal.toSpecLines()
            gemstone?.let { specs += it.toSpecLines() }
        }
        is AppraisalItem.Brooch -> {
            specs += SpecLine("Pin Mechanism", pinMechanism.displayName())
            heightMm?.let { specs += SpecLine("Height", "${it} mm") }
            widthMm?.let { specs += SpecLine("Width", "${it} mm") }
            motifDescription?.let { specs += SpecLine("Motif", it) }
            specs += metal.toSpecLines()
            gemstone?.let { specs += it.toSpecLines() }
        }
        is AppraisalItem.Watch -> {
            specs += SpecLine("Brand", brand)
            model?.let { specs += SpecLine("Model", it) }
            referenceNumber?.let { specs += SpecLine("Reference #", it) }
            serialNumber?.let { specs += SpecLine("Serial #", it) }
            specs += SpecLine("Movement", movementType.displayName())
            specs += SpecLine("Case Material", caseMaterial)
            caseSizeMm?.let { specs += SpecLine("Case Size", "${it} mm") }
            dialColor?.let { specs += SpecLine("Dial Color", it) }
            bandMaterial?.let { specs += SpecLine("Band Material", it) }
            waterResistance?.let { specs += SpecLine("Water Resistance", it) }
            if (complications.isNotEmpty() && complications != listOf(WatchComplication.NONE)) {
                specs += SpecLine("Complications", complications.joinToString(", ") { it.displayName() })
            }
            specs += SpecLine("Running Condition", runningCondition.displayName())
            specs += SpecLine("Box / Papers", listOfNotNull(
                "Box".takeIf { hasBox }, "Papers".takeIf { hasPapers }
            ).ifEmpty { listOf("Neither") }.joinToString(" & "))
            yearOfManufacture?.let { specs += SpecLine("Year", it.toString()) }
        }
        is AppraisalItem.LooseGemstone -> {
            specs += gemstone.toSpecLines()
        }
        is AppraisalItem.PearlJewelry -> {
            specs += SpecLine("Pearl Type", pearlType.displayName())
            sizeMm?.let { specs += SpecLine("Size", "${it} mm") }
            specs += SpecLine("Shape", shape.displayName())
            specs += SpecLine("Luster", luster.displayName())
            specs += SpecLine("Surface Quality", surfaceQuality.displayName())
            overtoneColor?.let { specs += SpecLine("Overtone", it) }
            specs += SpecLine("Pearl Count", pearlCount.toString())
            specs += SpecLine("Matched Strand", if (isMatchedStrand) "Yes" else "No")
            metal?.let { specs += it.toSpecLines() }
        }
        is AppraisalItem.Other -> {
            specs += SpecLine("Description", description)
            metal?.let { specs += it.toSpecLines() }
            gemstone?.let { specs += it.toSpecLines() }
        }
    }

    grossWeightGrams?.let { specs += SpecLine("Gross Weight", "${it} g") }
    hallmarks?.let { specs += SpecLine("Hallmarks", it) }

    return specs
}

private fun MetalDetails.toSpecLines(): List<SpecLine> = buildList {
    add(SpecLine("Metal", "${metalType.displayName()} ${purity}, ${color.displayName()}"))
    netWeightGrams?.let { add(SpecLine("Net Metal Weight", "${it} g")) }
    if (isPlated) add(SpecLine("Plating", platingBaseMetal?.let { "Over $it" } ?: "Plated"))
}

private fun GemstoneDetails.toSpecLines(): List<SpecLine> = buildList {
    add(SpecLine("Stone", "${stoneType.displayName()}, ${originType.displayName()}"))
    add(SpecLine("Carat Weight", "$caratWeight ct" + if (stoneCount > 1) " ($stoneCount stones)" else ""))
    add(SpecLine("Shape", shape.displayName()))
    colorGrade?.let { add(SpecLine("Color Grade", it)) }
    clarityGrade?.let { add(SpecLine("Clarity Grade", it)) }
    if (treatment != StoneTreatment.NONE) add(SpecLine("Treatment", treatment.displayName()))
    fluorescence?.let { add(SpecLine("Fluorescence", it.displayName())) }
    if (settingType != SettingType.NONE) add(SpecLine("Setting", settingType.displayName()))
    measurementsMm?.let { add(SpecLine("Measurements", "$it mm")) }
    certificationLab?.let { lab ->
        add(SpecLine("Certification", certificationNumber?.let { "$lab #$it" } ?: lab))
    }
}

// Shared enum-label helper — every enum in your domain uses SCREAMING_SNAKE, so one
// function covers all of them instead of copy-pasting the same replace/lowercase chain.
private fun Enum<*>.displayName(): String =
    name.replace("_", " ").lowercase().replaceFirstChar(Char::uppercase)