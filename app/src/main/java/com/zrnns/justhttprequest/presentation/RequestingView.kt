package com.zrnns.justhttprequest.presentation

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.zrnns.justhttprequest.R
import java.util.concurrent.TimeUnit

class RequestingViewModel(
    val urlText: String
): ViewModel() {

    var needsToExit = MutableLiveData<Boolean>(false)

    fun tappedCancel(activity: Activity?) {
        needsToExit.value = true
    }

    fun startRequesting(context: Context) {
        val queue = Volley.newRequestQueue(context)

        // Request a string response from the provided URL.
        val stringRequest = StringRequest(
            Request.Method.GET, urlText,
            { response ->
                Log.i(null, response)
                needsToExit.value = true
            },
            { error ->
                error.localizedMessage?.let { Log.e(null, it) }
                needsToExit.value = true
            })

        queue.add(stringRequest)
    }
}

@Composable
fun RequestingView(viewModel: RequestingViewModel = RequestingViewModel(urlText = "https://zrn-ns.com/")) {
    val activity = (LocalContext.current as? Activity)
    val currentContext = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val needsToExit = viewModel.needsToExit.observeAsState()

    val observer = remember {
        LifecycleEventObserver { _, event ->
            when(event) {
                Lifecycle.Event.ON_RESUME -> {
                    viewModel.startRequesting(currentContext)
                }
            }
        }
    }

    DisposableEffect(Unit) {
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
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
            onClick = { viewModel.tappedCancel(activity) },
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Cancel"
            )
        }
        needsToExit.value?.let { needsToExit ->
            if (needsToExit) {
                activity?.finish()
            }
        }
    }
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun RequestingViewPreview() {
    RequestingView()
}