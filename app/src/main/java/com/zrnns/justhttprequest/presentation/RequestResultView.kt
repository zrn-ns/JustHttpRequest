package com.zrnns.justhttprequest.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.zrnns.justhttprequest.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class RequestResultViewModel(
    val statusCode: Int,
    val isSucceeded: Boolean,
): LifecycleObserver {
    companion object {
        const val EXIT_AFTER_MSEC: Long = 3_000
    }

    var needsToExit = MutableLiveData<Boolean>(false)
        private set

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    suspend fun onResume() {
        startDismissTimer()
    }

    private suspend fun startDismissTimer() {
        withContext(Dispatchers.IO) {
            delay(EXIT_AFTER_MSEC)
            needsToExit.value = true
        }
    }
}

@Composable
fun RequestResultView(viewModel: RequestResultViewModel = RequestResultViewModel(statusCode = 200, isSucceeded = true)) {
    val needsToExit = viewModel.needsToExit.observeAsState()

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
    RequestResultView(RequestResultViewModel(statusCode = 200, isSucceeded = true))
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun RequestResultViewPreviewFailed() {
    RequestResultView(RequestResultViewModel(statusCode = 404, isSucceeded = false))
}