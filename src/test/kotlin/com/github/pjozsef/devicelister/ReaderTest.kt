package com.github.pjozsef.devicelister

import org.junit.Assert.assertEquals
import org.junit.Test

class ReaderTest {

    val android1 = Android("id1", "device", "7642384X", "heroltexx", "SM_G930F", "herolte")
    val android2 = Android("id2", "device", "3342245160X")
    val android3 = Android("id1", "device", "1-4.4.4.1", "heroltexx", "SM_G930F", "herolte")
    val android4 = Android("id2", "device", "1-4.4.4.1")

    val androidList = listOf(android1, android2, android3, android4)

    val ios1 = iOS("ios1", "10.1.1", "5ac10199e2a03419")
    val ios2 = iOS("ios2", "9.0", "a4cd772f7a7115a")

    val iosList = listOf(ios1, ios2)

    @Test
    fun `devicesFromStream with Android input`() {
        val expected = androidList

        val stream = buildString {
            appendln("List of devices attached")
            appendln("id1\t\t  device usb:7642384X product:heroltexx model:SM_G930F device:herolte")
            appendln("id2      device usb:3342245160X")
            appendln("id1\t\t  device usb:1-4.4.4.1 product:heroltexx model:SM_G930F device:herolte")
            appendln("id2      device usb:1-4.4.4.1")
        }.byteInputStream()

        val result = devicesFromStream(stream, androidRegex, androidMapper)

        assertEquals(expected, result)
    }

    @Test
    fun `devicesFromStream with iOS input`() {
        val expected = iosList

        val stream = buildString {
            appendln("Known Devices:")
            appendln("ios1 (10.1.1) [5ac10199e2a03419]")
            appendln("ios2 (9.0) [a4cd772f7a7115a]")
        }.byteInputStream()

        val result = devicesFromStream(stream, iosRegex, iosMapper)

        assertEquals(expected, result)
    }
}