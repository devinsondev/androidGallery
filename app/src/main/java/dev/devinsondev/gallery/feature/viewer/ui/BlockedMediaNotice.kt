package dev.devinsondev.gallery.feature.viewer.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.devinsondev.gallery.R
import dev.devinsondev.gallery.core.media.SafetyIssue

@Composable
fun BlockedMediaNotice(issue: SafetyIssue?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Outlined.Lock,
            contentDescription = null,
            tint = Color.White,
        )
        Text(
            text = stringResource(R.string.blocked_media_title),
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp),
        )
        Text(
            text = stringResource(issue.messageRes()),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.LightGray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

@StringRes
private fun SafetyIssue?.messageRes(): Int = when (this) {
    SafetyIssue.UNSUPPORTED_TYPE -> R.string.blocked_media_unsupported
    SafetyIssue.TOO_LARGE -> R.string.blocked_media_size
    SafetyIssue.SUSPICIOUS_DIMENSIONS -> R.string.blocked_media_dimensions
    SafetyIssue.EXTREME_DURATION -> R.string.blocked_media_duration
    SafetyIssue.INVALID_URI -> R.string.blocked_media_invalid_uri
    null -> R.string.viewer_error
}
