package com.github.pjozsef.devicelister

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonSubTypes.Type
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeInfo.As
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.io.File

private val mapper = ObjectMapper().registerModule(KotlinModule())

@JsonIgnoreProperties(ignoreUnknown = true)
data class Summary(val devices: List<Device>) {
    val android: List<Device> by lazy {
        devices.filter { it is Android }
    }

    val androidCount: Int
        get() = android.size

    val ios: List<Device> by lazy {
        devices.filter { it is iOS }
    }

    val iosCount: Int
        get() = ios.size

    companion object {
        fun from(path: String): Summary = mapper.readValue(File(path), Summary::class.java) ?: throw IllegalArgumentException("Could not parse from $path!")
    }
}

data class SummaryExport(val devices: List<Device>)

@JsonTypeInfo(
        use = Id.NAME,
        include = As.PROPERTY,
        property = "type")
@JsonSubTypes(
        Type(value = Android::class, name = "android"),
        Type(value = iOS::class, name = "ios")
)
sealed class Device

data class Android(
        val name: String,
        val status: String,
        val usb: String,
        val product: String? = null,
        val model: String? = null,
        val device: String? = null) : Device()

data class iOS(
        val name: String,
        val version: String?,
        val udid: String) : Device()

data class SummaryDiff(
        val androidMissing: List<Device>,
        val androidNew: List<Device>,
        val iosMissing: List<Device>,
        val iosNew: List<Device>) {

    companion object {
        fun of(before: Summary, after: Summary): SummaryDiff {
            val androidMissing = before.android - after.android
            val androidNew = after.android - before.android
            val iosMissing = before.ios - after.ios
            val iosNew = after.ios - before.ios
            return SummaryDiff(androidMissing, androidNew, iosMissing, iosNew)
        }
    }
}

val SummaryDiff.hasChanges: Boolean
    get() = androidMissing.size + androidNew.size + iosMissing.size + iosNew.size > 0

val Any.json: String
    get() = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this)

val Summary.json: String
    get() = SummaryExport(devices).json