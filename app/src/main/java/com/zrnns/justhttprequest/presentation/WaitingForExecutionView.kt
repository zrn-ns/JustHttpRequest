package com.zrnns.justhttprequest.presentation

import android.app.Application
import android.os.CountDownTimer
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.wear.compose.material.*
import com.zrnns.justhttprequest.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class MyCountDownTimer(millisInFuture: Long,
                       countDownInterval: Long,
                       val onTickHandler: ((p0: Long) -> Unit)? = null,
                       val onFinishHandler: (() -> Unit)? = null,
) : CountDownTimer(millisInFuture, countDownInterval) {
    override fun onTick(p0: Long) {
        onTickHandler?.let { it(p0) }
    }

    override fun onFinish() {
        onFinishHandler?.let { it() }
    }
}

class WaitingForExecutionViewModel(
): ViewModel(), DefaultLifecycleObserver {
    companion object {
        const val TOTAL_COUNT_MILLIS: Float = 3_000F
        const val INTERVAL_MILLIS: Float = 1_000F
    }

    var completionHandler: (() -> Unit)? = null
    var tappedSettingHandler: (() -> Unit)? = null

    var elapsedTimeMillis: Float = 0F
    var progress = MutableLiveData<Float>(0F)
        private set
    var myCountDownTimer: MyCountDownTimer? = null

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        startCountDownTimer()
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        stopAndResetTimer()
    }

    fun onSelectingSetting() {
        tappedSettingHandler?.let { it() }
    }

    private val scope = CoroutineScope(Job() + Dispatchers.Main)

    private fun startCountDownTimer() {
        myCountDownTimer?.cancel()
        myCountDownTimer = MyCountDownTimer(
            millisInFuture = INTERVAL_MILLIS.toLong(),
            countDownInterval = INTERVAL_MILLIS.toLong(),
            onTickHandler = {},
            onFinishHandler = {
                elapsedTimeMillis += INTERVAL_MILLIS.toLong()
                progress.value = elapsedTimeMillis / TOTAL_COUNT_MILLIS

                if (elapsedTimeMillis >= TOTAL_COUNT_MILLIS) {
                    completionHandler?.let { it() }
                } else {
                    startCountDownTimer()
                }
            }
        )
        myCountDownTimer?.start()
    }

    private fun stopAndResetTimer() {
        myCountDownTimer?.cancel()
        myCountDownTimer = null
    }
}

@Composable
fun WaitingForExecutionView(viewModel: WaitingForExecutionViewModel) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val progress = viewModel.progress.observeAsState()

    val progressAnimate by animateFloatAsState(
        targetValue = progress.value ?: 1F,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    )

    DisposableEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(viewModel)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(viewModel)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.fillMaxSize(),
            progress = progressAnimate,
            indicatorColor = MaterialTheme.colors.secondary
        )
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
            text = stringResource(R.string.ready),
        )
        Spacer(modifier = Modifier.height(height = 20.dp))
        Button(
            onClick = { viewModel.onSelectingSetting() },
        ) {
            Icon(
                Icons.Default.Settings,
                contentDescription = "Cancel"
            )
        }
    }
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun WaitingForExecutionViewPreview() {
    WaitingForExecutionView(viewModel = WaitingForExecutionViewModel())
}