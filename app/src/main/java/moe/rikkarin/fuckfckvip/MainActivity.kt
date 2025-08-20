package moe.rikkarin.fuckfckvip

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import moe.rikkarin.fuckfckvip.ClipboardUtil.copyToClipboard
import moe.rikkarin.fuckfckvip.ui.theme.FuckFuckForVIPTheme
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class, ExperimentalSerializationApi::class)
@Composable
fun DeviceHashOrKeySection(
    deviceHashOrKey: String,
    setDeviceHashOrKey: (String) -> Unit,
) {
    var validationResult by remember { mutableStateOf(AnnotatedString("")) }

    val scope = rememberCoroutineScope()
    var validationJob by remember { mutableStateOf<Job?>(null) }

    val context = LocalContext.current

    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        shadowElevation = 4.dp,
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .animateContentSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Device Hash / Key",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                shadowElevation = 4.dp,
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(2.dp)
            ) {
                TextField(
                    value = deviceHashOrKey,
                    onValueChange = setDeviceHashOrKey,
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize()
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        deviceHashOrKey.copyToClipboard(context)
                        Toast.makeText(context, "Copied!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1.0f),
                    shape = MaterialTheme.shapes.large,
                ) {
                    Text(text = "Copy")
                }

                Button(
                    onClick = {
                        ClipboardUtil.getText(context)?.apply {
                            setDeviceHashOrKey(this)
                        }
                        Toast.makeText(context, "Pasted!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1.0f),
                    shape = MaterialTheme.shapes.large,
                ) {
                    Text(text = "Paste")
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        if (deviceHashOrKey.isBlank()) {
                            Toast.makeText(
                                context,
                                "Please input device hash or key",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }
                        if (deviceHashOrKey.length != 16) {
                            Toast.makeText(
                                context,
                                "Key must be 16 characters long",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }

                        validationJob = scope.launch {
                            validationResult = AnnotatedString("Validating...")
                            validationResult = performValidation(deviceHashOrKey)
                        }
                    },
                    modifier = Modifier.weight(1.0f),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.onTertiary
                    )
                ) {
                    Text(text = "Validate")
                }
                Button(
                    onClick = { setDeviceHashOrKey(Hasher.getCurrentDeviceHash()) },
                    modifier = Modifier.weight(1.0f),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    Text(text = "Reset")
                }
            }

            if (validationResult.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = MaterialTheme.shapes.extraLarge,
                    shadowElevation = 4.dp,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(8.dp)
                            .horizontalScroll(rememberScrollState())
                    ) {
                        SelectionContainer {
                            Text(
                                text = validationResult,
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (validationJob!!.isActive) {
                    Button(
                        onClick = {
                            validationJob!!.cancel(); validationResult = AnnotatedString("")
                        }
                    ) {
                        Text("Cancel")
                    }
                } else {
                    Button(
                        onClick = { validationResult = AnnotatedString("") }
                    ) {
                        Text("OK")
                    }
                }
            }
        }
    }
}

val prettyJson = Json { prettyPrint = true }

@OptIn(ExperimentalSerializationApi::class)
private suspend fun performValidation(deviceHashOrKey: String) = withContext(Dispatchers.IO) {
    @Serializable
    data class ValidateResponse(
        @SerialName("valid") val valid: Boolean,

        // found
        @SerialName("expiry") val expiry: Long? = null,
        @SerialName("remaining") val remaining: Long? = null,
        @SerialName("user_id") val userId: Long? = null,

        // not found
        @SerialName("reason") val reason: String? = null,
    )

    runCatching {
        val signature = Hasher.getSignature(deviceHashOrKey)
        val url = buildString {
            append("https://zyfzd.xyz/validate?key=")
            append(URLEncoder.encode(deviceHashOrKey, "UTF-8"))
            append("&signature=")
            append(URLEncoder.encode(signature, "UTF-8"))
        }

        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            connectTimeout = 5000
            readTimeout = 5000
            requestMethod = "GET"
        }

        val responseCode = connection.responseCode
        fun isBadResponseCode() = responseCode in 400..599

        val inputStream = if (isBadResponseCode()) {
            connection.errorStream
        } else {
            connection.inputStream
        }
        val resp: ValidateResponse = prettyJson.decodeFromStream(inputStream)

        buildAnnotatedString {
            append("Response code: ")
            val color = when {
                responseCode == 200 -> Color.Green
                isBadResponseCode() -> Color.Red
                else -> Color.Yellow
            }
            withStyle(SpanStyle(color = color)) {
                append(responseCode.toString())
            }

            append("\nValid: ")
            withStyle(SpanStyle(color = if (resp.valid) Color.Green else Color.Red)) {
                append(resp.valid.toString())
            }

            append("\n")

            resp.reason?.run {
                append("\nReason: ")
                append(this@run)
            }

            resp.expiry?.run {
                append("\nExpiry: ")
                append(
                    SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss",
                        Locale.getDefault()
                    ).format(this@run * 1000)
                )
            }

            resp.remaining?.run {
                append("\nRemaining: ")
                val totalSeconds = this@run
                val days = totalSeconds / (24 * 3600)
                val hours = (totalSeconds % (24 * 3600)) / 3600
                val minutes = (totalSeconds % 3600) / 60
                val seconds = totalSeconds % 60
                append("${days}d ${hours}h ${minutes}min ${seconds}s")
            }

            resp.userId?.run {
                append("\nUser ID: ")
                append(this@run.toString())
            }

            append("\n\nRaw response:\n")
            append(prettyJson.encodeToString(resp))
        }
    }.getOrElse { error ->
        val stackTrace = error.stackTraceToString()
        Log.e("PerformValidation", "performValidation error: $stackTrace")
        buildAnnotatedString {
            withStyle(SpanStyle(color = Color.Red)) {
                append("Error: ")
            }
            append(stackTrace.dropLastWhile { it == '\n' })
        }
    }
}


@Composable
fun SignatureSection(
    deviceHashOrKey: String,
) {
    var showFullSignature by remember { mutableStateOf(false) }
    val signature = Hasher.getSignature(deviceHashOrKey)

    val context = LocalContext.current

    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        shadowElevation = 4.dp,
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Signature",
                style = MaterialTheme.typography.headlineSmall
            )

            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                shadowElevation = 4.dp,
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .fillMaxWidth()
                        .animateContentSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SelectionContainer {
                        Text(
                            text = if (showFullSignature) signature else signature.take(16),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Text("Full")
                Checkbox(
                    checked = showFullSignature,
                    onCheckedChange = { showFullSignature = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary,
                        uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                Button(
                    onClick = {
                        signature.run { if (showFullSignature) this else take(16) }
                            .copyToClipboard(context)
                        Toast.makeText(context, "Copied!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = MaterialTheme.shapes.large,
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp)
                ) {
                    Text(text = "Copy")
                }
            }
        }
    }
}

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FuckFuckForVIPTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(title = { Text("Fuck Fuck for VIP") })
                    }
                ) { paddingValues ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                top = paddingValues.calculateTopPadding(),
                                bottom = paddingValues.calculateBottomPadding()
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        val (deviceHashOrKey, setDeviceHashOrKey) = remember { mutableStateOf(Hasher.getCurrentDeviceHash()) }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .imePadding()
                                .navigationBarsPadding()
                                .verticalScroll(rememberScrollState())
                        ) {
                            DeviceHashOrKeySection(
                                deviceHashOrKey = deviceHashOrKey,
                                setDeviceHashOrKey = setDeviceHashOrKey
                            )
                            SignatureSection(deviceHashOrKey = deviceHashOrKey)
                        }
                    }
                }
            }
        }
    }
}