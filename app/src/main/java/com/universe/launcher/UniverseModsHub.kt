package com.universe.launcher

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

private val ModPurple = Color(0xFF9B55F5)
private val ModPink = Color(0xFFFF7BE5)
private val ModMuted = Color(0xFFB8A9D0)

private data class ModHit(
    val id: String,
    val title: String,
    val description: String,
    val downloads: Int
)

@Composable
fun UniverseModsHub(modsDirectory: File) {
    var query by remember { mutableStateOf("") }
    var version by remember { mutableStateOf("1.20.1") }
    var loader by remember { mutableStateOf("fabric") }
    var results by remember { mutableStateOf<List<ModHit>>(emptyList()) }
    var status by remember { mutableStateOf("Pesquise mods do Modrinth.") }
    var busy by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("CENTRAL DE MODS", color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.Black, letterSpacing = 1.4.sp)
        Text("Explore conteúdo compatível com sua instalação.", color = ModMuted, fontSize = 12.sp)
        Spacer(Modifier.height(14.dp))
        OutlinedTextField(
            value = query, onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text("Nome do mod") },
            trailingIcon = { Icon(Icons.Default.Search, null, tint = ModPink) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                focusedBorderColor = ModPurple, unfocusedBorderColor = Color(0xFF51416C),
                focusedLabelColor = ModPink, unfocusedLabelColor = ModMuted, cursorColor = ModPink
            )
        )
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = version, onValueChange = { version = it }, modifier = Modifier.weight(1f),
                label = { Text("Minecraft") }, singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = ModPurple, unfocusedBorderColor = Color(0xFF51416C), focusedLabelColor = ModPink, unfocusedLabelColor = ModMuted)
            )
            var expanded by remember { mutableStateOf(false) }
            Box(Modifier.weight(1f)) {
                OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) { Text(loader.uppercase(), color = ModPink) }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    listOf("fabric", "forge", "neoforge", "quilt").forEach { option ->
                        DropdownMenuItem(text = { Text(option.uppercase()) }, onClick = { loader = option; expanded = false })
                    }
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = {
                if (query.isBlank() || busy) return@Button
                busy = true
                status = "Buscando no Modrinth…"
                Thread {
                    try {
                        val encoded = URLEncoder.encode(query, "UTF-8")
                        val url = URL("https://api.modrinth.com/v2/search?query=$encoded&facets=%5B%5B%22project_type%3Amod%22%5D%5D&limit=30")
                        val connection = (url.openConnection() as HttpURLConnection).apply {
                            connectTimeout = 12000; readTimeout = 12000
                            setRequestProperty("User-Agent", "UniverseLauncher/0.3.0 (Android)")
                        }
                        val json = JSONObject(connection.inputStream.bufferedReader().use { it.readText() })
                        val hits = json.getJSONArray("hits")
                        val found = mutableListOf<ModHit>()
                        for (i in 0 until hits.length()) {
                            val item = hits.getJSONObject(i)
                            found += ModHit(item.optString("project_id"), item.optString("title"), item.optString("description"), item.optInt("downloads"))
                        }
                        connection.disconnect()
                        android.os.Handler(android.os.Looper.getMainLooper()).post {
                            results = found
                            status = if (found.isEmpty()) "Nenhum mod encontrado." else "${found.size} resultados encontrados."
                            busy = false
                        }
                    } catch (e: Exception) {
                        android.os.Handler(android.os.Looper.getMainLooper()).post {
                            status = "Não foi possível buscar. Verifique sua conexão e tente novamente."
                            busy = false
                        }
                    }
                }.start()
            },
            enabled = !busy,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = ModPurple, contentColor = Color.White)
        ) {
            Icon(Icons.Default.Search, null); Spacer(Modifier.width(8.dp)); Text(if (busy) "BUSCANDO…" else "BUSCAR MODS")
        }
        Text(status, color = ModMuted, fontSize = 11.sp, modifier = Modifier.padding(vertical = 10.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(9.dp)) {
            items(results, key = { it.id }) { mod ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF171021)),
                    shape = RoundedCornerShape(15.dp), modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(13.dp)) {
                        Text(mod.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(mod.description, color = ModMuted, fontSize = 11.sp, maxLines = 3)
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("${mod.downloads} downloads", color = ModPink, fontSize = 10.sp, modifier = Modifier.weight(1f))
                            TextButton(onClick = {
                                status = "Consultando versões de ${mod.title}…"
                                Thread {
                                    try {
                                        val endpoint = "https://api.modrinth.com/v2/project/${mod.id}/version?game_versions=%5B%22${URLEncoder.encode(version, "UTF-8")}%22%5D&loaders=%5B%22$loader%22%5D"
                                        val conn = (URL(endpoint).openConnection() as HttpURLConnection).apply {
                                            connectTimeout = 12000; readTimeout = 12000
                                            setRequestProperty("User-Agent", "UniverseLauncher/0.3.0 (Android)")
                                        }
                                        val versions = JSONArray(conn.inputStream.bufferedReader().use { it.readText() })
                                        if (versions.length() == 0) throw IllegalStateException("Sem versão compatível")
                                        val files = versions.getJSONObject(0).getJSONArray("files")
                                        val file = files.getJSONObject(0)
                                        val fileUrl = file.getString("url")
                                        val filename = file.optString("filename", "${mod.id}.jar").replace(Regex("[^A-Za-z0-9._-]"), "_")
                                        modsDirectory.mkdirs()
                                        val target = File(modsDirectory, filename)
                                        val download = (URL(fileUrl).openConnection() as HttpURLConnection).apply { connectTimeout = 15000; readTimeout = 30000 }
                                        download.inputStream.use { input -> target.outputStream().use { output -> input.copyTo(output) } }
                                        conn.disconnect(); download.disconnect()
                                        android.os.Handler(android.os.Looper.getMainLooper()).post {
                                            status = "Download concluído: $filename (armazenado no Universe)."
                                        }
                                    } catch (_: Exception) {
                                        android.os.Handler(android.os.Looper.getMainLooper()).post {
                                            status = "Não há arquivo compatível com essa versão/loader, ou o download falhou."
                                        }
                                    }
                                }.start()
                            }) {
                                Icon(Icons.Default.CloudDownload, null, tint = ModPink)
                                Text("BAIXAR", color = ModPink)
                            }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Text("Os arquivos ficam no armazenamento privado do app. A integração com a pasta de mods da instância Minecraft ainda precisa ser conectada ao motor do jogo.", color = ModMuted, fontSize = 10.sp)
    }
}
