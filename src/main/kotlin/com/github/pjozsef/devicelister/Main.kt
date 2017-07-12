package com.github.pjozsef.devicelister


fun main(args: Array<String>) {
    if (args.isEmpty()) {
        listDevices()
    } else if (args.size == 2) {
        listDifference(args[0], args[1]);
    }
}

private fun listDevices() {
    val summary = Summary(androidDevices(), iosDevices())
    println(summary.json)
}

private fun listDifference(beforeFile: String, afterFile: String) {
    val before = Summary.from(beforeFile)
    val after = Summary.from(afterFile)

    val diff = SummaryDiff.of(before, after)

    println("Android missing: ${diff.json}")
}