package com.rtknits.rt_knits_samplefinder

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

@Composable
fun NavGraph(nc : NavHostController) {
    NavHost(navController = nc, startDestination = Screen.Home.route ) {
        composable(route = Screen.Home.route){
            HomeScreen(nc = nc)
        }
        composable(
            route = Screen.Scan.route,
            arguments = listOf(navArgument("sampleID") { type = NavType.StringType })
        ){ backStackEntry->
            ScanScreen(backStackEntry.arguments?.getString("sampleID") ?: "SAMPLEID NOT PASSED")
        }
    }
}