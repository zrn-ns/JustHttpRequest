package com.zrnns.justhttprequest.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
import com.zrnns.justhttprequest.R

@Composable
fun WaitingForExecutionView(onSelectingCancel: (() -> Unit) = {}, progress: Float) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.fillMaxSize(),
            progress = progress,
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
            onClick = onSelectingCancel,
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
fun WaitingForExecutionViewPreview() {
    WaitingForExecutionView(progress = 0.2f)
}