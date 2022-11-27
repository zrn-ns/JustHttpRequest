package com.zrnns.justhttprequest.presentation

import android.app.Application
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.android.volley.Request.Method
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.zrnns.justhttprequest.R
import com.zrnns.justhttprequest.storage.StoreManager


class RequestingViewModel(
    val urlText: String,
    val method: StoreManager.HttpMethod,
    application: Application
): AndroidViewModel(application), DefaultLifecycleObserver {
    var completionHandler: ((statusCode: Int, isSucceeded: Boolean) -> Unit)? = null
    var cancelHandler: (() -> Unit)? = null

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)

        startRequesting()
    }

    fun tappedCancel() {
        cancelHandler?.let { it() }
    }

    private fun startRequesting() {
        val queue = Volley.newRequestQueue(getApplication())

        // Request a string response from the provided URL.
        val request = StringRequest(method.toVolleyRequestMethod(), urlText,
            { response ->
                Log.i(null, response.toString())
                completionHandler?.let { it(200, true) }
            },
            { error ->
                error.localizedMessage?.let { Log.e(null, it) }
                error.networkResponse?.also {
                    completionHandler?.let { it(error.networkResponse.statusCode, false) }
                } ?: run {
                    // 不正なURLが渡された場合などはこちらに入ってくる
                    completionHandler?.let { it(-1, false) }
                }
            })

        queue.add(request)
    }
}

@Composable
fun RequestingView(viewModel: RequestingViewModel = RequestingViewModel(urlText = "https://zrn-ns.com/", method = StoreManager.HttpMethod.GET, application = Application())) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(viewModel)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(viewModel)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.primary,
            text = stringResource(R.string.requesting),
        )
        Spacer(modifier = Modifier.height(height = 20.dp))
        Button(
            onClick = { viewModel.tappedCancel() },
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Cancel"
            )
        }
    }
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun RequestingViewPreview() {
    RequestingView()
}