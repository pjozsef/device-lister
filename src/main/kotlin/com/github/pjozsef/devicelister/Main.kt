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
        Command.DIFF -> listDifference(diffCommand)
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
    }
    println(Summary(devices).json)
}

private fun listDifference(diffCommand: DiffCommand) {
    val before = Summary.from(diffCommand.before)
    val after = Summary.from(diffCommand.after)

    val print: (SummaryDiff) -> Unit = { println(it.json) }

    val diff = SummaryDiff.of(before, after)
    if (diffCommand.silent) {
        if (diff.hasChanges) {
            print(diff)
        }
    } else {
        print(diff)
    }
}
