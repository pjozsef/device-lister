package com.github.pjozsef.devicelister

import java.io.InputStream

private typealias Mapper = (MatchGroupCollection) -> Device

val androidCommand = "adb devices -l"
val iosCommand = "instruments -s devices | grep -v Simulator"

val androidRegex = Regex("^(?<name>\\w+)\\s+(?<status>[a-z]+)\\s+usb:(?<usb>[\\w\\.\\-]+)(\\s+product:(?<product>[-\\w]+)\\s+model:(?<model>[-\\w]+)\\s+device:(?<device>[-\\w]+))?$")
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

fun androidDevices(): List<Device> =
        devicesFromCommand(androidCommand, androidRegex, androidMapper)

fun iosDevices(): List<Device> =
        devicesFromCommand(iosCommand, iosRegex, iosMapper)

fun devicesFromCommand(command: String, regex: Regex, mapper: (MatchGroupCollection) -> Device): List<Device> {
    val stream = Runtime.getRuntime()
            .exec(command)
            .inputStream
    return devicesFromStream(stream, regex, mapper)
}

fun devicesFromStream(stream: InputStream, regex: Regex, mapper: (MatchGroupCollection) -> Device): List<Device> {
    return stream.bufferedReader()
            .useLines { lines ->
                lines.mapNotNull { line ->
                    regex.matchEntire(line)?.groups
                }.map(mapper).toList()
            }
}
