package com.github.pjozsef.devicelister

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonSubTypes.*
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeInfo.*

data class ConnectedDeviceSummary(val androidList: List<Device>, val iosList: List<Device>)

@JsonTypeInfo(
        use = Id.NAME,
        include = As.PROPERTY,
        property = "type")
@JsonSubTypes(
        Type(value = Android::class, name = "android"),
        Type(value = iOS::class, name = "ios"))
sealed class Device

data class Android(
        val name: String,
        val status: String,
        val usb: String,
        val product: String?,
        val model: String?,
        val device: String?) : Device()

data class iOS(
        val name: String,
        val version: String?,
        val udid: String) : Device()
