package com.zrnns.justhttprequest.presentation

import android.app.Application
import android.content.Context
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.zrnns.justhttprequest.R
import kotlinx.coroutines.*

class RequestResultViewModel(
    val statusCode: Int,
    val isSucceeded: Boolean,
    application: Application
): AndroidViewModel(application), DefaultLifecycleObserver {
    companion object {
        const val EXIT_AFTER_MILLIS: Long = 3_000
    }

    var completionHandler: (() -> Unit)? = null

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)

        scope.launch {
            startDismissTimer()
        }
        val vibrator = ContextCompat.getSystemService(getApplication(), Vibrator::class.java)!!
        vibrator.vibrate(if (isSucceeded) 100 else 1_000)
    }

    private val scope = CoroutineScope(Job() + Dispatchers.Main)

    private suspend fun startDismissTimer() {
        withContext(Dispatchers.IO) {
            delay(EXIT_AFTER_MILLIS)
            completionHandler?.let { it() }
        }
    }
}

@Composable
fun RequestResultView(viewModel: RequestResultViewModel = RequestResultViewModel(statusCode = 200, isSucceeded = true, Application())) {
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
            color = if (viewModel.isSucceeded) MaterialTheme.colors.primary else MaterialTheme.colors.error,
            text = stringResource(if (viewModel.isSucceeded) R.string.succeeded else R.string.error),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.fillMaxWidth().height(5.dp))
        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.primary,
            text = stringResource(R.string.resultTextWithStatusCode, viewModel.statusCode),
        )
    }
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun RequestResultViewPreviewSucceeded() {
    RequestResultView(RequestResultViewModel(statusCode = 200, isSucceeded = true, Application()))
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun RequestResultViewPreviewFailed() {
    RequestResultView(RequestResultViewModel(statusCode = 404, isSucceeded = false, Application()))
}