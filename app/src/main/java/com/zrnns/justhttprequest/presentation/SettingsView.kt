package com.zrnns.justhttprequest.presentation

import android.app.Application
import android.app.RemoteInput
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TitleCard
import androidx.wear.input.RemoteInputIntentHelper
import androidx.wear.input.wearableExtender
import com.zrnns.justhttprequest.R
import com.zrnns.justhttprequest.storage.StoreManager

class SettingsViewModel(
    application: Application
): AndroidViewModel(application), DefaultLifecycleObserver {
    private val storeManager = StoreManager(application)
    var url = MutableLiveData<String>(storeManager.getURL())

    fun updatedURL(url: String) {
        storeManager.saveURL(url)
        this.url.value = url
    }
}

@Composable
fun SettingsView(viewModel: SettingsViewModel) {
    val context = LocalContext.current
    val url = viewModel.url.observeAsState()
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it.data?.let { data ->
                val results: Bundle = RemoteInput.getResultsFromIntent(data)
                val urlString: CharSequence? = results.getCharSequence("url_string")
                viewModel.updatedURL(urlString as String)
            }
        }

    ScalingLazyColumn(
        modifier = Modifier.fillMaxWidth(),
        autoCentering = true
    ) {
        item {
            ListHeader() {
                Text(text = "Settings")
            }
        }
        item {
            TitleCard(
                onClick = {
                    Toast.makeText(context, "Currently, GET is the only supported method.", Toast.LENGTH_SHORT).show()
                },
                title = { Text(stringResource(R.string.method)) },
                content = { Text("GET") }
            )
        }
        item {
            TitleCard(
                onClick = {
                    val intent: Intent = RemoteInputIntentHelper.createActionRemoteInputIntent();
                    val remoteInputs: List<RemoteInput> = listOf(
                        RemoteInput.Builder("url_string")
                            .setLabel("URL")
                            .wearableExtender {
                                setEmojisAllowed(false)
                                setInputActionType(EditorInfo.IME_ACTION_DONE)
                            }.build()
                    )
                    RemoteInputIntentHelper.putRemoteInputsExtra(intent, remoteInputs)
                    launcher.launch(intent)
                },
                title = { Text(stringResource(R.string.url)) }
            ) {
                Text(url.value ?: "")
            }
        }
    }
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun SettingsViewPreview() {
    SettingsView(SettingsViewModel(Application()))
}