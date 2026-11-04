package com.universe.launcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val SpaceBlack = Color(0xFF050812)
private val NebulaBlue = Color(0xFF174B78)
private val NebulaCyan = Color(0xFF42D6D0)
private val NebulaViolet = Color(0xFF7653A6)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { UniverseHome() } }
    }
}

@Composable
private fun UniverseHome() {
    Box(
        Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(SpaceBlack, Color(0xFF0A1728), Color(0xFF090B18)))
        )
    ) {
        StarField(Modifier.fillMaxSize())
        Column(
            Modifier.fillMaxSize().padding(horizontal = 22.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("UNIVERSE", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Black, letterSpacing = 3.sp)
                    Spacer(Modifier.weight(1f))
                    Icon(Icons.Default.Settings, contentDescription = "Configurações", tint = NebulaCyan)
                }
                Text("MINECRAFT JAVA • ANDROID", color = NebulaCyan, fontSize = 10.sp, letterSpacing = 2.sp)
            }
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Sua próxima\naventura começa aqui.", color = Color.White, fontSize = 31.sp, lineHeight = 37.sp, fontWeight = FontWeight.Bold)
                Text("Uma nova experiência para jogar Minecraft Java no celular.", color = Color(0xFFB7C6D8), fontSize = 14.sp)
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xCC101D30)),
                    shape = RoundedCornerShape(22.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(18.dp)) {
                        Text("PERFIL DE JOGO", color = NebulaCyan, fontSize = 10.sp, letterSpacing = 2.sp)
                        Spacer(Modifier.height(8.dp))
                        Text("Nenhuma versão selecionada", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
                        Text("A integração do motor Minecraft será adicionada nas próximas etapas.", color = Color(0xFFB7C6D8), fontSize = 12.sp)
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = {},
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = NebulaBlue),
                            shape = RoundedCornerShape(14.dp)
                        ) { Text("Preparar jogo", color = Color.White) }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    FeatureTile("Contas", Icons.Default.AccountCircle, Modifier.weight(1f))
                    FeatureTile("Arquivos", Icons.Default.Storage, Modifier.weight(1f))
                    FeatureTile("Música", Icons.Default.GraphicEq, Modifier.weight(1f))
                }
            }
            Text("UNIVERSE LAUNCHER  •  ALPHA 0.1.0", color = Color(0xFF8294A9), fontSize = 9.sp, letterSpacing = 1.5.sp)
        }
    }
}

@Composable
private fun FeatureTile(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier) {
    Card(modifier.height(82.dp), colors = CardDefaults.cardColors(containerColor = Color(0xB817263B)), shape = RoundedCornerShape(16.dp)) {
        Column(
            Modifier.fillMaxSize().padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = label, tint = NebulaCyan, modifier = Modifier.size(23.dp))
            Spacer(Modifier.height(5.dp))
            Text(label, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun StarField(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val stars = listOf(
            Offset(.08f, .12f), Offset(.22f, .25f), Offset(.81f, .13f), Offset(.92f, .31f),
            Offset(.68f, .39f), Offset(.12f, .56f), Offset(.89f, .66f), Offset(.37f, .73f),
            Offset(.56f, .84f), Offset(.18f, .91f), Offset(.76f, .94f), Offset(.47f, .18f)
        )
        stars.forEachIndexed { index, point ->
            drawCircle(
                color = if (index % 3 == 0) NebulaCyan.copy(alpha = .8f) else Color.White.copy(alpha = .65f),
                radius = if (index % 4 == 0) 2.2.dp.toPx() else 1.1.dp.toPx(),
                center = Offset(point.x * size.width, point.y * size.height)
            )
        }
        drawCircle(
            brush = Brush.radialGradient(listOf(NebulaBlue.copy(alpha = .22f), Color.Transparent)),
            radius = size.minDimension * .58f,
            center = Offset(size.width * .88f, size.height * .34f)
        )
        drawCircle(
            brush = Brush.radialGradient(listOf(NebulaViolet.copy(alpha = .16f), Color.Transparent)),
            radius = size.minDimension * .48f,
            center = Offset(size.width * .05f, size.height * .72f)
        )
    }
}
