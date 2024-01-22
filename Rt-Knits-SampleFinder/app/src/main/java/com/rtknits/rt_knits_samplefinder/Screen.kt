package com.rtknits.rt_knits_samplefinder

import androidx.navigation.NavHostController

sealed class Screen(val route: String) {
    object Home: Screen(route = "home");
    object ScanSingle: Screen(route = "scanSingle/{sampleID}");
    object ScanMultiple: Screen(route = "scanMultiple/{sampleIDs}");
}

fun gotoHome(nc: NavHostController){
    return nc.navigate(
        Screen.Home.route
    )
}

private fun gotoScanSingle(nc: NavHostController, sampleId: String){
    return nc.navigate(
        Screen.ScanSingle.route.replace(
            "{sampleID}", sampleId
        )
    )
}

private fun gotoScanMultiple(nc: NavHostController, sampleIds:List<String>){
    return nc.navigate(
        Screen.ScanMultiple.route.replace(
            "{sampleIDs}", sampleIds.joinToString(separator = ",")
        )
    )
}

fun gotoScan(nc: NavHostController, sampleIds: List<String>){
    when (sampleIds.size) {
        1 -> {
            gotoScanSingle(nc, sampleIds[0])
        }
        else -> {
            gotoScanMultiple(nc, sampleIds)
        }
    }
}