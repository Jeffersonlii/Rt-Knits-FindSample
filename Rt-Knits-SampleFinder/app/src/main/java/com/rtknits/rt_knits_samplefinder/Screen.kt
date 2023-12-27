package com.rtknits.rt_knits_samplefinder

sealed class Screen(val route: String) {
    object Home: Screen(route = "home");
    object Scan: Screen(route = "scan/{sampleID}");
}