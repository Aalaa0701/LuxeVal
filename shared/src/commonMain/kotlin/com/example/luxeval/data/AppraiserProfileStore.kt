package com.example.luxeval.data

import com.russhwolf.settings.Settings

class AppraiserProfileStore(private val settings: Settings) {

    fun getName(): String = settings.getString("appraiser_name", "")
    fun setName(value: String) = settings.putString("appraiser_name", value)

    fun getLicenseNumber(): String = settings.getString("license_number", "")
    fun setLicenseNumber(value: String) = settings.putString("license_number", value)

    fun getBusinessPhone(): String = settings.getString("business_phone", "")
    fun setBusinessPhone(value: String) = settings.putString("business_phone", value)

    fun getBusinessAddress(): String = settings.getString("business_address", "")
    fun setBusinessAddress(value: String) = settings.putString("business_address", value)

    fun getLetterheadText(): String = settings.getString("letterhead_text", "")
    fun setLetterheadText(value: String) = settings.putString("letterhead_text", value)

    fun getLogoPath(): String? = settings.getStringOrNull("logo_path")
    fun setLogoPath(value: String) = settings.putString("logo_path", value)

    fun getSignaturePath(): String? = settings.getStringOrNull("signature_path")
    fun setSignaturePath(value: String) = settings.putString("signature_path", value)

    fun isProfileComplete(): Boolean =
        getName().isNotBlank() &&
                getLicenseNumber().isNotBlank() &&
                !getLogoPath().isNullOrBlank() &&
                !getSignaturePath().isNullOrBlank()
}