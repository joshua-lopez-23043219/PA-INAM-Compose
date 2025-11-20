package com.example.pa_inam.Composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pa_inam.network.GenderStats
import com.example.pa_inam.network.OlapRepository

data class GrupoGeneroConfig(
    val codeGroup: String,   // ej: "P1A-M"
    val titulo: String       // ej: "Primer grado A Mañana"
)

@Composable
fun GradoGeneroScreen(
    tituloCabecera: String,
    grupos: List<GrupoGeneroConfig>,
    onBack: () -> Unit
) {
    var statsMap by remember { mutableStateOf<Map<String, GenderStats>>(emptyMap()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // Cargar datos de TODOS los grupos al entrar
    LaunchedEffect(grupos) {
        loading = true
        error = null
        try {
            val map = mutableMapOf<String, GenderStats>()
            for (g in grupos) {
                val stats = OlapRepository.getGenderStatsForGroup(g.codeGroup)
                map[g.codeGroup] = stats
            }
            statsMap = map
        } catch (e: Exception) {
            error = e.message ?: "Error al obtener datos"
        } finally {
            loading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Barra azul
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0A4FA8))
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "<",
                color = Color.White,
                fontSize = 22.sp,
                modifier = Modifier
                    .padding(end = 12.dp)
                    .clickable { onBack() }
            )
            Text(
                text = tituloCabecera,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Banda gris
        Text(
            text = "Distribución por género por grupo",
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFE0E0E0))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            color = Color(0xFF0A4FA8),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            when {
                loading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(Modifier.height(8.dp))
                        Text("Cargando datos...")
                    }
                }

                error != null -> {
                    Text(
                        text = "Error: $error",
                        color = Color.Red,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                else -> {
                    grupos.forEach { cfg ->
                        val stats = statsMap[cfg.codeGroup]
                        GrupoGeneroCard(
                            titulo = cfg.titulo,
                            stats = stats
                        )
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun GrupoGeneroCard(
    titulo: String,
    stats: GenderStats?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = titulo,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Color(0xFF0A4FA8),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            if (stats == null) {
                Text("Sin datos (no se pudo leer el grupo)")
            } else {
                GeneroPieLikeChart(stats)
            }
        }
    }
}

@Composable
fun GeneroPieLikeChart(stats: GenderStats) {
    val total = stats.total
    val malesPercent = if (total > 0) (stats.males / total * 100).toInt() else 0
    val femalesPercent = if (total > 0) 100 - malesPercent else 0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (total > 0) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(26.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(malesPercent.toFloat())
                        .background(Color(0xFF1976D2))   // Hombres
                )
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(femalesPercent.toFloat())
                        .background(Color(0xFFF06292))   // Mujeres
                )
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            GeneroLegendItem(
                color = Color(0xFF1976D2),
                label = "Hombres",
                count = stats.males.toInt(),
                percent = malesPercent
            )
            GeneroLegendItem(
                color = Color(0xFFF06292),
                label = "Mujeres",
                count = stats.females.toInt(),
                percent = femalesPercent
            )
        }

        if (total == 0.0) {
            Text(
                text = "Total 0 registros para este grupo",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun GeneroLegendItem(
    color: Color,
    label: String,
    count: Int,
    percent: Int
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(color)
        )
        Text(
            text = "$label: $count ($percent%)",
            fontSize = 14.sp
        )
    }
}
