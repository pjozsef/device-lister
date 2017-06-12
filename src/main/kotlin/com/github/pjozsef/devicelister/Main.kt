package com.github.pjozsef.devicelister

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import kotlin.system.measureTimeMillis

typealias Mapper = (MatchGroupCollection) -> Device

val mapper = ObjectMapper().registerModule(KotlinModule())

val androidCommand = "adb devices -l"
val iosCommand = "instruments -s devices | grep -v Simulator"

val androidRegex = Regex("^(?<name>\\w+)\\s+(?<status>[a-z]+)\\s+usb:(?<usb>\\w+)(\\s+product:(?<product>[-\\w]+)\\s+model:(?<model>[-\\w]+)\\s+device:(?<device>[-\\w]+))?$")
val iosRegex = Regex("^(?<name>.+)\\s+\\((?<version>[\\d\\.]+)\\)\\s*\\[(?<udid>[\\w-]+)\\]$")

val androidMapper: Mapper = {
    val name = requireNotNull(it["name"]?.value)
    val status = requireNotNull(it["status"]?.value)
    val usb = requireNotNull(it["usb"]?.value)
    val product = it["product"]?.value
    val model = it["model"]?.value
    val device = it["device"]?.value
    Android(name, status, usb, product, model, device)
}

val iosMapper: Mapper = {
    val name = requireNotNull(it["name"]?.value)
    val version = it["version"]?.value
    val udid = requireNotNull(it["udid"]?.value)
    iOS(name, version, udid)
}

fun devices(command: String, regex: Regex, mapper: (MatchGroupCollection) -> Device) =
        Runtime.getRuntime()
                .exec(command)
                .inputStream.bufferedReader()
                .useLines { lines ->
                    lines.mapNotNull { line ->
                        regex.matchEntire(line)?.groups
                    }.map(mapper).toList()
                }

fun main(args: Array<String>) {
    val summary = ConnectedDeviceSummary(
            devices(androidCommand, androidRegex, androidMapper),
            devices(iosCommand, iosRegex, iosMapper))
    val json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(summary)
    println(json)
}