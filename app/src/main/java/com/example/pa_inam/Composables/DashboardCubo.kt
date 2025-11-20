package com.example.pa_inam.Composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pa_inam.ui.theme.Red


import androidx.compose.material3.*

import androidx.compose.runtime.*

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel


import com.example.pa_inam.network.CategoryValue
import com.example.pa_inam.network.PassFailStats



@Composable
fun DashboardCuboScreen(
    viewModel: DashboardCuboViewModel  = viewModel()
) {
    val loading by viewModel.isLoading
    val error by viewModel.errorMessage

    val yearSeries by viewModel.yearSeries
    val shiftSeries by viewModel.shiftSeries
    val passFail by viewModel.passFail

    var selectedYear by remember { mutableStateOf<CategoryValue?>(null) }
    var selectedShift by remember { mutableStateOf<CategoryValue?>(null) }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ======================== LOADING ==========================
            if (loading) {
                Spacer(Modifier.height(40.dp))
                CircularProgressIndicator()
                Spacer(Modifier.height(12.dp))
                Text("Cargando datos del cubo...")
                return@Column
            }

            // ========================= ERROR ===========================
            if (error != null) {
                Spacer(Modifier.height(40.dp))
                Text(
                    text = "Error: $error",
                    color = Color.Red,
                    textAlign = TextAlign.Center
                )
                return@Column
            }

            // ======================= GRAFICO 1 =========================
            ChartCard(title = "Registros por Año") {
                BarChartSimple(
                    data = yearSeries,
                    barColor = Color(0xFF1565C0),
                    onBarSelected = { selectedYear = it }
                )

                selectedYear?.let {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Año ${it.label}: ${it.value.toInt()} registros",
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )
                }
            }

            // ======================= GRAFICO 2 =========================
            ChartCard(title = "Registros por Turno") {
                BarChartSimple(
                    data = shiftSeries,
                    barColor = Color(0xFF00897B),
                    onBarSelected = { selectedShift = it }
                )

                selectedShift?.let {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "${it.label}: ${it.value.toInt()} registros",
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )
                }
            }

            // ======================= GRAFICO 3 =========================
            ChartCard(title = "Aprobados vs Reprobados") {
                passFail?.let {
                    PassFailPieChart(it)
                } ?: Text("No hay datos para mostrar")
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = Color(0xFF0A4FA8),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        textAlign = TextAlign.Start
    )
}

/**
 * Gráfico de barras sencillo hecho solo con filas y columnas.
 * Cada categoría es una columna con una barra proporcional a su valor.
 */
@Composable
fun BarChartSimple(
    data: List<CategoryValue>,
    barColor: Color,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(220.dp),
    onBarSelected: (CategoryValue) -> Unit = {}
) {
    if (data.isEmpty()) {
        Text("No hay datos para mostrar")
        return
    }

    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    val maxValue = (data.maxOfOrNull { it.value } ?: 0.0).takeIf { it > 0 } ?: 1.0

    Box(
        modifier = modifier
            .padding(horizontal = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            data.forEachIndexed { index, item ->
                val ratio = (item.value / maxValue).toFloat().coerceIn(0f, 1f)
                val isSelected = selectedIndex == index

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(vertical = 4.dp)
                        .clickable {
                            selectedIndex = index
                            onBarSelected(item)
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    // Barra
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(if (isSelected) 0.7f else 0.55f)
                            .fillMaxHeight(ratio)
                            .background(
                                if (isSelected) barColor.copy(alpha = 0.85f)
                                else barColor.copy(alpha = 0.65f)
                            )
                    )

                    Spacer(Modifier.height(4.dp))

                    // Valor
                    Text(
                        text = item.value.toInt().toString(),
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(4.dp))

                    // Etiqueta
                    Text(
                        text = item.label,
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center,
                        maxLines = 2
                    )
                }
            }
        }
    }
}

/**
 * Gráfico de pastel muy simple (2 porciones: aprobados y reprobados)
 * Hecho con layout, sin Canvas complicado: usamos barras horizontales
 * para que sea 100% estable y entendible.
 */
@Composable
fun PassFailPieChart(
    stats: PassFailStats,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(160.dp)
) {
    val total = stats.passed + stats.failed
    if (total <= 0.0) {
        Text("No hay datos para mostrar")
        return
    }

    val passedPercent = (stats.passed / total * 100).toInt()
    val failedPercent = 100 - passedPercent

    Column(
        modifier = modifier
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // Barra apilada simulando pastel 100%
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(26.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(passedPercent.toFloat())
                    .background(Color(0xFF4CAF50))
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(failedPercent.toFloat())
                    .background(Color(0xFFF44336))
            )
        }

        // Leyenda
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            LegendItem(
                color = Color(0xFF4CAF50),
                label = "Aprobados: $passedPercent%"
            )
            LegendItem(
                color = Color(0xFFF44336),
                label = "Reprobados: $failedPercent%"
            )
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .background(color)
        )
        Text(
            text = label,
            fontSize = 12.sp
        )
    }

}




@Composable
private fun ChartCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0x22000000)),   // borde muy suave/transparente
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp                        // pequeño relieve
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            content = {
                SectionTitle(title)
                Spacer(Modifier.height(8.dp))
                content()
            }
        )
    }
}