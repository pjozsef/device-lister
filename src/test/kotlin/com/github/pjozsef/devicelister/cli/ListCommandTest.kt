package com.github.pjozsef.devicelister.cli

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ListCommandTest {

    @Test
    fun testListAll_noArgs() {
        assertTrue(listCommand(false, false).listAll)
    }

    @Test
    fun testListAll_android() {
        assertFalse(listCommand(true, false).listAll)
    }

    @Test
    fun testListAll_ios() {
        assertFalse(listCommand(false, true).listAll)
    }

    @Test
    fun testListAll_android_ios() {
        assertTrue(listCommand(true, true).listAll)
    }

    private fun listCommand(android: Boolean, ios: Boolean) = ListCommand().apply {
        listAndroid = android
        listIOS = ios
    }
}