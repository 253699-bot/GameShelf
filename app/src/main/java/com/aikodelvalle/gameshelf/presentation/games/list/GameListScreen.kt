package com.aikodelvalle.gameshelf.presentation.games.list

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aikodelvalle.gameshelf.domain.model.Game

@Composable
fun GameListScreen(
    viewModel: GameListViewModel,
    onNavigateToCreateGame: () -> Unit,
    onNavigateToEditGame: (Int) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadGames()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Mi colección",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            text = "Tus videojuegos en un solo lugar",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.secondary,
                        )
                    }
                    OutlinedButton(
                        onClick = onLogout,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.secondary,
                        ),
                    ) {
                        Text("Cerrar sesión", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "${uiState.games.size} videojuegos",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )

                    Button(
                        onClick = onNavigateToCreateGame,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                        ),
                    ) {
                        Text("+ Agregar videojuego", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Content
                when {
                    uiState.isLoading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                    }

                    uiState.errorMessage != null -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = uiState.errorMessage!!,
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 14.sp,
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = { viewModel.loadGames() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                    ),
                                ) {
                                    Text("Reintentar")
                                }
                            }
                        }
                    }

                    uiState.games.isEmpty() -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Todavía no tienes videojuegos en tu colección",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onBackground,
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = onNavigateToCreateGame,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                    ),
                                ) {
                                    Text("Agregar el primero")
                                }
                            }
                        }
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            items(
                                items = uiState.games,
                                key = { game -> game.id },
                            ) { game ->
                                GameCard(
                                    game = game,
                                    isOwner = (game.userId == uiState.currentUserId),
                                    onEditClick = { onNavigateToEditGame(game.id) },
                                    onDeleteClick = { viewModel.onDeleteClick(game) },
                                )
                            }
                        }
                    }
                }
            }

            // Delete Confirmation Dialog
            if (uiState.gameToDelete != null) {
                AlertDialog(
                    onDismissRequest = { viewModel.onCancelDelete() },
                    title = {
                        Text(
                            text = "¿Eliminar videojuego?",
                            fontWeight = FontWeight.Bold,
                        )
                    },
                    text = {
                        Text(
                            text = "¿Estás seguro de que deseas eliminar '${uiState.gameToDelete!!.title}' de tu colección?",
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = { viewModel.confirmDelete() },
                            enabled = !uiState.isDeleting,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                            ),
                        ) {
                            if (uiState.isDeleting) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = MaterialTheme.colorScheme.onError,
                                    strokeWidth = 2.dp,
                                )
                            } else {
                                Text("Eliminar")
                            }
                        }
                    },
                    dismissButton = {
                        OutlinedButton(
                            onClick = { viewModel.onCancelDelete() },
                            enabled = !uiState.isDeleting,
                        ) {
                            Text("Cancelar")
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.surface,
                )
            }
        }
    }
}

@Composable
private fun GameCard(
    game: Game,
    isOwner: Boolean,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = game.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f),
                )

                Spacer(modifier = Modifier.width(8.dp))

                val statusText = if (game.completed) "Completado" else "Pendiente"
                val statusBg = if (game.completed) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline
                Box(
                    modifier = Modifier
                        .background(
                            color = statusBg.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(12.dp),
                        )
                        .border(
                            width = 1.dp,
                            color = statusBg,
                            shape = RoundedCornerShape(12.dp),
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Text(
                        text = statusText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${game.platform} • ${game.genre} (${game.releaseYear})",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground,
            )

            if (game.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Notas: ${game.notes}",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.secondary,
                )
            }

            if (isOwner) {
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = onEditClick) {
                        Text("Editar", color = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = onDeleteClick) {
                        Text("Eliminar", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}
