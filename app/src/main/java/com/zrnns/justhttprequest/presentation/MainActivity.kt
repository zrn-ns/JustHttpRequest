/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.zrnns.justhttprequest.presentation

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.zrnns.justhttprequest.presentation.theme.JustHttpRequestTheme
import com.zrnns.justhttprequest.storage.StoreManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WearApp()
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}

@Composable
fun WearApp() {
    val application = LocalContext.current.applicationContext as Application
    val activity = (LocalContext.current as? Activity)

    JustHttpRequestTheme {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "waitingForExecution") {
            composable("waitingForExecution") {
                val viewModel = WaitingForExecutionViewModel()
                viewModel.completionHandler = {
                    navController.navigate("requesting")
                }
                viewModel.tappedSettingHandler = {
                    navController.navigate("settings")
                }
                WaitingForExecutionView(viewModel = viewModel)
            }
            composable("settings") {
                SettingsView(SettingsViewModel(application))
            }
            composable("requesting") {
                val viewModel = RequestingViewModel(application = application, urlText = StoreManager(application).getURL())
                viewModel.completionHandler = { statusCode, isSucceeded ->
                    navController.navigate("requestResult/$statusCode/$isSucceeded")
                }
                viewModel.cancelHandler = {
                    activity?.finish()
                }
                RequestingView(viewModel = viewModel)
            }
            composable("requestResult/{statusCode}/{isSucceeded}",
                arguments = listOf(
                    navArgument("statusCode") { type = NavType.IntType },
                    navArgument("isSucceeded") { type = NavType.BoolType },
                )
            ) {
                val viewModel = RequestResultViewModel(
                    statusCode = it.arguments?.getInt("statusCode") ?: -1,
                    isSucceeded = it.arguments?.getBoolean("isSucceeded") ?: false,
                    application = application
                )
                viewModel.completionHandler = {
                    activity?.finish()
                }

                RequestResultView(viewModel)
            }
        }
    }
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp()
}