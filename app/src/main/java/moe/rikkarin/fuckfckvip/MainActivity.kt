package moe.rikkarin.fuckfckvip

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import moe.rikkarin.fuckfckvip.ClipboardUtil.copyToClipboard
import moe.rikkarin.fuckfckvip.ui.theme.FuckFuckForVIPTheme

@Composable
fun ClickToCopyText(
    textToCopy: String,
    context: Context
) {
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
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = textToCopy,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    textToCopy.copyToClipboard(context)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceHashSection(
    deviceHash: String,
    setDeviceHash: (String) -> Unit,
    context: Context
) {
    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        shadowElevation = 4.dp,
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Device Hash",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                shadowElevation = 4.dp,
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(2.dp)
            ) {
                TextField(
                    value = deviceHash,
                    onValueChange = setDeviceHash,
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
                    onClick = { setDeviceHash(DeviceInfoHasher.getCurrentDeviceHash()) },
                    modifier = Modifier.weight(1.0f),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.onTertiary
                    )
                ) {
                    Text(text = "Reset")
                }

                Button(
                    onClick = {
                        deviceHash.copyToClipboard(context)
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
                            setDeviceHash(this)
                        }
                        Toast.makeText(context, "Pasted!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1.0f),
                    shape = MaterialTheme.shapes.large,
                ) {
                    Text(text = "Paste")
                }
            }
        }
    }
}

@Composable
fun KeySection(
    deviceHash: String,
    context: Context
) {
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
                "Key",
                style = MaterialTheme.typography.headlineSmall
            )
            ClickToCopyText(DeviceInfoHasher.getKeyByDeviceHash(deviceHash), context)
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
                        val (deviceHash, setDeviceHash) = remember { mutableStateOf(DeviceInfoHasher.getCurrentDeviceHash()) }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .imePadding()
                                .navigationBarsPadding()
                                .verticalScroll(rememberScrollState())
                        ) {
                            DeviceHashSection(deviceHash = deviceHash, setDeviceHash = setDeviceHash, context = this@MainActivity.applicationContext)
                            KeySection(deviceHash = deviceHash, context = this@MainActivity.applicationContext)
                        }
                    }
                }
            }
        }
    }
}