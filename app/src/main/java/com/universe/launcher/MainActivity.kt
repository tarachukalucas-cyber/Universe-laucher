package com.universe.launcher

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture

private val SpaceBlack = Color(0xFF050812)
private val NebulaBlue = Color(0xFF174B78)
private val NebulaCyan = Color(0xFF42D6D0)
private val NebulaViolet = Color(0xFF7653A6)

data class LocalTrack(val title: String, val artist: String, val uri: Uri)

class MainActivity : ComponentActivity() {
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var mediaController: MediaController? = null
    private val tracks = mutableStateListOf<LocalTrack>()
    private var permissionGranted by mutableStateOf(false)
    private var currentTrackTitle by mutableStateOf("")
    private var isPlaying by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionGranted = hasAudioPermission()
        if (permissionGranted) loadTracks()
        setContent {
            MaterialTheme {
                UniverseHome(
                    tracks = tracks,
                    permissionGranted = permissionGranted,
                    onRequestPermission = { requestAudioPermission() },
                    onRefresh = { loadTracks() },
                    onPlay = { track -> playTrack(track) },
                    currentTrackTitle = currentTrackTitle,
                    isPlaying = isPlaying,
                    onTogglePlayback = { togglePlayback() }
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val token = SessionToken(this, ComponentName(this, MusicPlaybackService::class.java))
        controllerFuture = MediaController.Builder(this, token).buildAsync()
        controllerFuture?.addListener({
            try { mediaController = controllerFuture?.get() } catch (_: Exception) { }
        }, ContextCompat.getMainExecutor(this))
    }

    override fun onStop() {
        mediaController = null
        controllerFuture?.let { MediaController.releaseFuture(it) }
        controllerFuture = null
        super.onStop()
    }

    private fun requiredAudioPermission(): String =
        if (Build.VERSION.SDK_INT >= 33) Manifest.permission.READ_MEDIA_AUDIO
        else Manifest.permission.READ_EXTERNAL_STORAGE

    private fun hasAudioPermission() =
        ContextCompat.checkSelfPermission(this, requiredAudioPermission()) == PackageManager.PERMISSION_GRANTED

    private fun requestAudioPermission() {
        registerPermissionLauncher.launch(requiredAudioPermission())
    }

    private val registerPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            permissionGranted = granted
            if (granted) loadTracks()
        }

    private fun loadTracks() {
        if (!hasAudioPermission()) return
        val found = mutableListOf<LocalTrack>()
        val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST
        )
        val selection: String? = null
        val sortOrder = "${MediaStore.Audio.Media.TITLE} COLLATE NOCASE ASC"
        contentResolver.query(collection, projection, selection, null, sortOrder)?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val title = cursor.getString(titleColumn) ?: "Faixa sem título"
                val artist = cursor.getString(artistColumn) ?: "Artista desconhecido"
                found += LocalTrack(title, artist, Uri.withAppendedPath(collection, id.toString()))
            }
        }
        tracks.clear()
        tracks.addAll(found)
    }

    private fun togglePlayback() {
        val player = mediaController ?: return
        if (player.isPlaying) { player.pause(); isPlaying = false }
        else { player.play(); isPlaying = true }
    }

    private fun playTrack(track: LocalTrack) {
        val player = mediaController ?: return
        player.setMediaItem(MediaItem.fromUri(track.uri))
        player.prepare()
        player.play()
        currentTrackTitle = track.title
        isPlaying = true
    }
}

@Composable
private fun UniverseHome(
    tracks: List<LocalTrack>,
    permissionGranted: Boolean,
    onRequestPermission: () -> Unit,
    onRefresh: () -> Unit,
    onPlay: (LocalTrack) -> Unit,
    currentTrackTitle: String,
    isPlaying: Boolean,
    onTogglePlayback: () -> Unit
) {
    Box(
        Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(SpaceBlack, Color(0xFF0A1728), Color(0xFF090B18)))
        )
    ) {
        Column(Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("UNIVERSE", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Black, letterSpacing = 3.sp)
                    Text("MINECRAFT JAVA • ANDROID", color = NebulaCyan, fontSize = 10.sp, letterSpacing = 2.sp)
                }
                Icon(Icons.Default.GraphicEq, contentDescription = "Universe Music", tint = NebulaCyan)
            }
            Spacer(Modifier.height(24.dp))
            Text("Sua aventura começa aqui.", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Bold)
            Text("Launcher espacial para Minecraft Java.", color = Color(0xFFB7C6D8), fontSize = 13.sp)
            Spacer(Modifier.height(24.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xCC101D30)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth().weight(1f)
            ) {
                Column(Modifier.fillMaxSize().padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.MusicNote, contentDescription = null, tint = NebulaCyan)
                        Spacer(Modifier.width(8.dp))
                        Text("MÚSICAS DO DISPOSITIVO", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.weight(1f))
                        IconButton(onClick = onRefresh) {
                            Icon(Icons.Default.Refresh, contentDescription = "Atualizar biblioteca", tint = NebulaCyan)
                        }
                    }
                    Text(
                        "Encontre e toque arquivos de áudio locais. A reprodução continua em segundo plano enquanto você usa outros apps.",
                        color = Color(0xFFB7C6D8), fontSize = 11.sp
                    )
                    Spacer(Modifier.height(12.dp))
                    if (!permissionGranted) {
                        Text("Permita o acesso a músicas e áudios para listar os arquivos do dispositivo.", color = Color.White, fontSize = 13.sp)
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = onRequestPermission, colors = ButtonDefaults.buttonColors(containerColor = NebulaBlue)) {
                            Text("Encontrar minhas músicas")
                        }
                    } else if (tracks.isEmpty()) {
                        Text("Nenhuma música encontrada. Baixe arquivos de áudio ou atualize a biblioteca.", color = Color(0xFFB7C6D8), fontSize = 13.sp)
                    } else {
                        Text("${tracks.size} faixa(s) encontrada(s)", color = NebulaCyan, fontSize = 11.sp)
                        Spacer(Modifier.height(8.dp))
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(tracks) { track ->
                                Row(
                                    Modifier.fillMaxWidth().background(Color(0x8825364B), RoundedCornerShape(12.dp))
                                        .clickable { onPlay(track) }.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.MusicNote, contentDescription = null, tint = NebulaCyan)
                                    Spacer(Modifier.width(10.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(track.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                                        Text(track.artist, color = Color(0xFFB7C6D8), fontSize = 11.sp, maxLines = 1)
                                    }
                                    Icon(Icons.Default.PlayArrow, contentDescription = "Reproduzir", tint = NebulaCyan)
                                }
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            if (currentTrackTitle.isNotBlank()) {
                Row(Modifier.fillMaxWidth().background(Color(0xCC101D30), RoundedCornerShape(12.dp)).clickable { onTogglePlayback() }.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(if (isPlaying) Icons.Default.GraphicEq else Icons.Default.PlayArrow, contentDescription = null, tint = NebulaCyan)
                    Spacer(Modifier.width(10.dp))
                    Text(currentTrackTitle, color = Color.White, fontSize = 12.sp, modifier = Modifier.weight(1f), maxLines = 1)
                    Text(if (isPlaying) "PAUSAR" else "CONTINUAR", color = NebulaCyan, fontSize = 10.sp)
                }
                Spacer(Modifier.height(8.dp))
            }
            Text("UNIVERSE LAUNCHER • ALPHA 0.2.0", color = Color(0xFF8294A9), fontSize = 9.sp, letterSpacing = 1.5.sp)
        }
    }
}
