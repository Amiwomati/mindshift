package com.mindshift.anxiety.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mindshift.anxiety.ui.theme.AnxietyRed
import com.mindshift.anxiety.ui.theme.SyncBlue
import com.mindshift.anxiety.viewmodel.AnxietyViewModel
import com.mindshift.anxiety.viewmodel.SyncState

@Composable
fun AnxietyScreen(
    onLogout: () -> Unit,
    viewModel: AnxietyViewModel = hiltViewModel()
) {
    val unsyncedCount by viewModel.unsyncedCount.collectAsState()
    val syncState by viewModel.syncState.collectAsState()
    val lastSync by viewModel.lastSync.collectAsState()
    val userName by viewModel.userName.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Cerrar sesión") },
            text = {
                Text("Tienes $unsyncedCount ${if (unsyncedCount == 1) "clic pendiente" else "clics pendientes"} de sincronizar. Si cierras sesión ahora, se perderán.")
            },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    onLogout()
                }) {
                    Text("Cerrar sesión", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    onClick = { if (unsyncedCount > 0) showLogoutDialog = true else onLogout() },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Cerrar sesión",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            Text(
                text = "Hola, ${userName ?: "Paciente"}",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "¿Cómo te sientes hoy?",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { viewModel.recordClick() },
                modifier = Modifier.size(220.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AnxietyRed,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Text(
                    text = "Tengo\nansiedad",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    lineHeight = 30.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (unsyncedCount > 0) {
                Text(
                    text = "$unsyncedCount ${if (unsyncedCount == 1) "clic pendiente" else "clics pendientes"} de sincronizar",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            } else {
                Text(
                    text = "Todo sincronizado ✓",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF4CAF50)
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            if (lastSync != null) {
                Text(
                    text = "Última sync: ${lastSync!!.take(19).replace("T", " ")}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (syncState is SyncState.Error) {
                Text(
                    text = (syncState as SyncState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (syncState is SyncState.Success) {
                val synced = (syncState as SyncState.Success).synced
                Text(
                    text = "$synced clics sincronizados exitosamente",
                    color = Color(0xFF4CAF50),
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = { viewModel.syncNow() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SyncBlue),
                enabled = syncState !is SyncState.Syncing && unsyncedCount > 0
            ) {
                if (syncState is SyncState.Syncing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sincronizando...")
                } else {
                    Text("Sincronizar", fontSize = 16.sp)
                }
            }
        }
    }
}
