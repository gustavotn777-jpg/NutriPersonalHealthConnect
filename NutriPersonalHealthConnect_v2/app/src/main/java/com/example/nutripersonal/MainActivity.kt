package com.example.nutripersonal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.*
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    private lateinit var hc: HealthConnectClient

    private val permissions = setOf(
        HealthPermission.getReadPermission(ExerciseSessionRecord::class),
        HealthPermission.getReadPermission(HeartRateRecord::class),
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class),
        HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class),
        HealthPermission.getReadPermission(WeightRecord::class),
        HealthPermission.getReadPermission(DistanceRecord::class)
    )

    private val permissionLauncher =
        registerForActivityResult(PermissionController.createRequestPermissionResultContract()) {
            // UI can refresh after permissions are granted.
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (HealthConnectClient.getSdkStatus(this) == HealthConnectClient.SDK_AVAILABLE) {
            hc = HealthConnectClient.getOrCreate(this)
        }

        setContent {
            MaterialTheme {
                AppScreen(
                    onConnect = {
                        if (::hc.isInitialized) permissionLauncher.launch(permissions)
                    },
                    onRead = {
                        // Full read/sync repository is the next production layer.
                    }
                )
            }
        }
    }
}

@Composable
private fun AppScreen(onConnect: () -> Unit, onRead: () -> Unit) {
    var status by remember { mutableStateOf("Não conectado") }

    Surface(color = Color(0xFFF3F7FB), modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Nutricionista / Personal", style = MaterialTheme.typography.headlineMedium, color = Color(0xFF17324D))
                Text("Health Connect • versão 0.2", color = Color.Gray)
            }
            item {
                Card {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Conexão", style = MaterialTheme.typography.titleLarge)
                        Text("🔵 $status")
                        Button(onClick = { status = "Permissões solicitadas"; onConnect() }) {
                            Text("Conectar ao Health Connect")
                        }
                        OutlinedButton(onClick = onRead) {
                            Text("Ler dados agora")
                        }
                    }
                }
            }
            item {
                Card {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                        Text("Dados que serão consolidados", style = MaterialTheme.typography.titleLarge)
                        Text("🏃 Sessões de exercício")
                        Text("❤️ Frequência cardíaca")
                        Text("🚶 Passos")
                        Text("🔥 Calorias")
                        Text("⚖️ Peso")
                        Text("📏 Distância")
                    }
                }
            }
            item {
                Card {
                    Column(Modifier.padding(16.dp)) {
                        Text("Sincronização", style = MaterialTheme.typography.titleLarge)
                        Text("A arquitetura usa leitura por intervalo e está preparada para sincronização incremental por ChangeLogs, que o Health Connect fornece para registros inseridos, alterados ou excluídos.")
                    }
                }
            }
        }
    }
}
