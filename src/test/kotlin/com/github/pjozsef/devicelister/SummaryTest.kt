package com.github.pjozsef.devicelister

import org.junit.Assert.assertEquals
import org.junit.Test

class SummaryTest {

    val android1 = Android("id1", "device", "7642384X", "heroltexx", "SM_G930F", "herolte")
    val android2 = Android("id2", "device", "3342245160X")

    val androidList = listOf(android1, android2)

    val ios1 = iOS("ios1", "10.1.1", "5ac10199e2a03419")
    val ios2 = iOS("ios2", "9.0", "a4cd772f7a7115a")

    val iosList = listOf(ios1, ios2)

    val baseSummary = Summary(androidList, iosList)

    @Test
    fun `Summary#of with correct file`() {
        val expected = baseSummary

        val result = Summary.from(javaClass.getResource("summary.json").path)

        assertEquals(expected, result)
    }

}