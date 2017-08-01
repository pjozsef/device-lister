package com.github.pjozsef.devicelister.cli

import com.beust.jcommander.Parameter

sealed class Command {
    companion object {
        const val LIST = "list"
        const val DIFF = "diff"
    }
}

class ListCommand : Command() {
    val listAll: Boolean
        get() = !(listAndroid xor listIOS)

    @Parameter(names = arrayOf("-a", "--android"), description = "List android devices")
    var listAndroid: Boolean = false

    @Parameter(names = arrayOf("-i", "--ios"), description = "List iOS devices")
    var listIOS: Boolean = false
}

class DiffCommand : Command() {
    @Parameter(required = true, description = "Files to diff")
    lateinit var files: List<String>

    val before: String
        get() = files[0]

    val after: String
        get() = files[1]
}