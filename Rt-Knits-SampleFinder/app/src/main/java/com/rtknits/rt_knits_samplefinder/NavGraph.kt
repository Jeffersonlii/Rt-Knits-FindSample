package com.rtknits.rt_knits_samplefinder

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

val NavControllerContext = compositionLocalOf<NavHostController?> { null }
@Composable
fun NavGraph(nc : NavHostController) {
    CompositionLocalProvider(NavControllerContext provides nc) {
        NavHost(navController = nc, startDestination = Screen.Home.route ) {
            composable(route = Screen.Home.route){
                HomeScreen()
            }
            composable(
                route = Screen.ScanMultiple.route,
                arguments = listOf(navArgument("sampleIDs") { type = NavType.StringArrayType })
            ){ backStackEntry->
                val sampleIDsString = backStackEntry.arguments?.getStringArray("sampleIDs")?.get(0) ?: ""
                val sampleIDs = sampleIDsString.split(',').toTypedArray();
                ScanMultiScreen(sampleIDs)
            }
            composable(
                route = Screen.ScanSingle.route,
                arguments = listOf(navArgument("sampleID") { type = NavType.StringType })
            ){ backStackEntry->
                ScanSingleScreen(backStackEntry.arguments?.getString("sampleID") ?: "SAMPLEID NOT PASSED")
            }
        }
    }
}