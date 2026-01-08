package com.example.smartqr.ui.generate

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.smartqr.utils.QrUtils

@Composable
fun GenerateScreen() {
    var text by remember { mutableStateOf("") }
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var selectedType by remember { mutableStateOf(QrType.TEXT) }
    
    // Wifi specific vars
    var wifiSsid by remember { mutableStateOf("") }
    var wifiPass by remember { mutableStateOf("") }

    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Generate QR Code", style = MaterialTheme.typography.headlineMedium)

        // Type Selection
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            QrType.values().forEach { type ->
                FilterChip(
                    selected = selectedType == type,
                    onClick = { selectedType = type },
                    label = { Text(type.name) }
                )
            }
        }

        // Inputs
        when (selectedType) {
            QrType.WIFI -> {
                OutlinedTextField(
                    value = wifiSsid,
                    onValueChange = { wifiSsid = it },
                    label = { Text("Network Name (SSID)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = wifiPass,
                    onValueChange = { wifiPass = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
            }
            QrType.URL -> {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("https://example.com") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
                )
            }
            QrType.TEXT -> {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Enter text") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        }

        Button(
            onClick = {
                val content = when(selectedType) {
                    QrType.WIFI -> "WIFI:T:WPA;S:$wifiSsid;P:$wifiPass;;"
                    QrType.URL -> if(text.startsWith("http")) text else "https://$text"
                    QrType.TEXT -> text
                }
                qrBitmap = QrUtils.generateQrBitmap(content)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Generate QR")
        }

        // Result
        qrBitmap?.let { bitmap ->
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .size(300.dp)
                    .border(1.dp, Color.LightGray, RoundedCornerShape(16.dp))
            ) {
                Box(modifier = Modifier.fillMaxSize().background(Color.White), contentAlignment = Alignment.Center) {
                     Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "QR Code",
                        modifier = Modifier.size(250.dp)
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = {
                        val path = MediaStore.Images.Media.insertImage(
                            context.contentResolver, 
                            bitmap, 
                            "QR_Code_${System.currentTimeMillis()}", 
                            null
                        )
                        val uri = Uri.parse(path)
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "image/jpeg"
                            putExtra(Intent.EXTRA_STREAM, uri)
                        }
                        context.startActivity(Intent.createChooser(intent, "Share QR Code"))
                    }
                ) {
                    Icon(Icons.Default.Share, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Share")
                }
            }
        }
    }
}

enum class QrType {
    TEXT, URL, WIFI
}