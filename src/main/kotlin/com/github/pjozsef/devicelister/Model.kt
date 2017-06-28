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
data class Summary(val androidList: List<Device>, val iosList: List<Device>) {
    val androidCount: Int
        get() = androidList.size
    val iosCount: Int
        get() = iosList.size

    companion object {
        fun from(path: String): Summary = mapper.readValue(File(path), Summary::class.java) ?: throw IllegalArgumentException("Could not parse from $path!")
    }
}

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
            val androidMissing = before.androidList - after.androidList
            val androidNew = after.androidList - before.androidList
            val iosMissing = before.iosList - after.iosList
            val iosNew = after.iosList - before.iosList
            return SummaryDiff(androidMissing, androidNew, iosMissing, iosNew)
        }
    }
}

val Any.json: String
    get() = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this)