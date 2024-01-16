package com.rtknits.rt_knits_samplefinder

sealed class Screen(val route: String) {
    object Home: Screen(route = "home");
    object ScanSingle: Screen(route = "scanSingle/{sampleID}");
    object ScanMultiple: Screen(route = "scanMultiple/{sampleIDs}");
}