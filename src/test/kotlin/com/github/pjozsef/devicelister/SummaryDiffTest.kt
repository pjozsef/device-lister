package com.github.pjozsef.devicelister

import org.junit.Assert.assertEquals
import org.junit.Test

class SummaryDiffTest {

    val android1 = Android("id1", "device", "7642384X", "heroltexx", "SM_G930F", "herolte")
    val android2 = Android("id2", "device", "3342245160X")

    val androidList = listOf(android1, android2)

    val ios1 = iOS("ios1", "10.1.1", "5ac10199e2a03419")
    val ios2 = iOS("ios2", "9.0", "a4cd772f7a7115a")

    val iosList = listOf(ios1, ios2)

    val baseSummary = Summary(androidList + iosList)

    @Test
    fun `diff with empty summaries`() {
        val expected = emptyDiff()

        val result = SummaryDiff.of(emptySummary(), emptySummary())

        assertEquals(expected, result)
    }


    @Test
    fun `diff without any change`() {
        val expected = emptyDiff()

        val result = SummaryDiff.of(baseSummary, baseSummary)

        assertEquals(expected, result)
    }

    @Test
    fun `diff with new devices`() {
        val before = emptySummary()
        val after = baseSummary

        val expected = SummaryDiff(emptyList(), androidList, emptyList(), iosList)

        val result = SummaryDiff.of(before, after)

        assertEquals(expected, result)
    }

    @Test
    fun `diff with missing devices`() {
        val before = baseSummary
        val after = emptySummary()

        val expected = SummaryDiff(androidList, emptyList(), iosList, emptyList())

        val result = SummaryDiff.of(before, after)

        assertEquals(expected, result)
    }

    @Test
    fun `diff with missing and new devices`() {
        val before = Summary(
                listOf(android1) + listOf(ios1))
        val after = Summary(
                listOf(android2) + listOf(ios2))

        val expected = SummaryDiff(
                listOf(android1),
                listOf(android2),
                listOf(ios1),
                listOf(ios2))

        val result = SummaryDiff.of(before, after)

        assertEquals(expected, result)
    }

    private fun emptySummary() = Summary(emptyList())

    private fun emptyDiff() = SummaryDiff(emptyList(), emptyList(), emptyList(), emptyList())
}
