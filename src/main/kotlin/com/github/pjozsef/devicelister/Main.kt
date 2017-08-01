package com.github.pjozsef.devicelister

import com.beust.jcommander.JCommander
import com.github.pjozsef.devicelister.cli.Command
import com.github.pjozsef.devicelister.cli.DiffCommand
import com.github.pjozsef.devicelister.cli.ListCommand


fun main(args: Array<String>) {
    val listCommand = ListCommand()
    val diffCommand = DiffCommand()
    val jc = JCommander.newBuilder()
            .addCommand(Command.LIST, listCommand)
            .addCommand(Command.DIFF, diffCommand)
            .build()

    jc.parse(*args)

    when (jc.parsedCommand) {
        Command.LIST -> listDevices(listCommand)
        Command.DIFF -> listDifference(diffCommand.before, diffCommand.after)
        else -> jc.usage()
    }
}

private fun listDevices(listCommand: ListCommand) {
    val devices = ArrayList<Device>()
    if (listCommand.listAll) {
        devices += androidDevices()
        devices += iosDevices()
    } else {
        if (listCommand.listAndroid) {
            devices += androidDevices()
        }
        if (listCommand.listIOS) {
            devices += iosDevices()
        }
        when (true) {
            listCommand.listAndroid -> println("android")
            listCommand.listIOS -> println("ios")
        }
    }
    println(Summary(devices).json)
}

private fun listDifference(beforePath: String, afterPath: String) {
    val before = Summary.from(beforePath)
    val after = Summary.from(afterPath)

    println(SummaryDiff.of(before, after))
}