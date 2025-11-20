package com.example.pa_inam.Composables

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pa_inam.ui.theme.Azul_f
import com.example.pa_inam.R


// ---------------- ENUM PARA CONTROLAR LAS VISTAS ----------------

enum class ReporteView {
    MENU,        // Pantalla principal con 6 botones
    GRUPOS,
    MAESTROS,
    ASIGNATURAS,
    NOTAS,
    MATRICULAS,
    ESTUDIANTES
}

// ---------------- PANTALLA PRINCIPAL DE REPORTES ----------------

@Composable
fun ReportesScreen() {
    var currentView by remember { mutableStateOf(ReporteView.MENU) }

    when (currentView) {
        ReporteView.MENU -> ReportesMenuScreen(
            onGruposClick = { currentView = ReporteView.GRUPOS },
            onMaestrosClick = { currentView = ReporteView.MAESTROS },
            onAsignaturasClick = { currentView = ReporteView.ASIGNATURAS },
            onNotasClick = { currentView = ReporteView.NOTAS },
            onMatriculasClick = { currentView = ReporteView.MATRICULAS },
            onEstudiantesClick = { currentView = ReporteView.ESTUDIANTES }
        )

        ReporteView.GRUPOS -> ReporteGruposScreen { currentView = ReporteView.MENU }
        ReporteView.MAESTROS -> ReporteMaestrosScreen { currentView = ReporteView.MENU }
        ReporteView.ASIGNATURAS -> ReporteAsignaturasScreen { currentView = ReporteView.MENU }
        ReporteView.NOTAS -> ReporteNotasScreen { currentView = ReporteView.MENU }
        ReporteView.MATRICULAS -> ReporteMatriculasScreen { currentView = ReporteView.MENU }
        ReporteView.ESTUDIANTES -> ReporteEstudiantesScreen { currentView = ReporteView.MENU }
    }
}



data class ReporteItem(
    @DrawableRes val icon: Int,
    @StringRes val text: Int
)

@Composable
private fun ReportesMenuScreen(
    onGruposClick: () -> Unit,
    onMaestrosClick: () -> Unit,
    onAsignaturasClick: () -> Unit,
    onNotasClick: () -> Unit,
    onMatriculasClick: () -> Unit,
    onEstudiantesClick: () -> Unit
) {
    val items = listOf(
        ReporteItem(R.drawable.grupos,     R.string.grupo),
        ReporteItem(R.drawable.maestro,    R.string.maestros),
        ReporteItem(R.drawable.asignatura, R.string.asignaturas),
        ReporteItem(R.drawable.notas,      R.string.notas),
        ReporteItem(R.drawable.matriculas, R.string.matriculas),
        ReporteItem(R.drawable.estudiante, R.string.estudiantes)
    )

    val acciones: List<() -> Unit> = listOf(
        onGruposClick,
        onMaestrosClick,
        onAsignaturasClick,
        onNotasClick,
        onMatriculasClick,
        onEstudiantesClick
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Título azul (Gestión Académica y Administrativa -> ahora Reportes)
        Text(
            text = stringResource(id = R.string.tituloreporte),
            modifier = Modifier
                .fillMaxWidth()
                .background(Azul_f)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            color = Color.White,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(items.indices.toList()) { index ->
                val item = items[index]
                ReportCard(
                    iconRes = item.icon,
                    textRes = item.text,
                    onClick = acciones[index]
                )
            }
        }
    }
}



// ---------------- CARD (IGUAL ESTILO QUE CONSULTAS) ----------------

@Composable
fun ReportCard(
    @DrawableRes iconRes: Int,
    @StringRes textRes: Int,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(150.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(2.dp, Blue, RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(44.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(id = textRes),
            color = Color.Black,
            fontSize = 20.sp
        )
    }
}



@Composable
fun ReporteDetalleScreen(
    titulo: String,
    subtitulo: String,
    onBack: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Barra azul con flecha y título
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Azul_f)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "<",
                color = Color.White,
                fontSize = 22.sp,
                modifier = Modifier
                    .clickable { onBack() }
                    .padding(end = 12.dp)
            )
            Text(
                text = titulo,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Banda gris con el nombre de la pestaña
        Text(
            text = subtitulo,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFE0E0E0))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            color = Azul_f,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )

        // Contenido propio del reporte
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            content = content
        )
    }
}



// ---------------- PANTALLAS SIMPLES DE EJEMPLO PARA CADA REPORTE ----------------
// (solo para que compile; luego las reemplazas por el diseño real de cada reporte)

@Composable
fun ReporteGruposScreen(onBack: () -> Unit) {
    ReporteDetalleScreen(
        titulo = "Grupos",
        subtitulo = "Grupos de Clases por año",
        onBack = onBack
    ) {
        // TODO: aquí va el contenido del reporte de grupos
    }
}

@Composable
fun ReporteMaestrosScreen(onBack: () -> Unit) {
    ReporteDetalleScreen(
        titulo = "Maestros",
        subtitulo = "Maestros por áreas o asignaturas",
        onBack = onBack
    ) {
        // TODO: contenido reporte maestros
    }
}

@Composable
fun ReporteAsignaturasScreen(onBack: () -> Unit) {
    ReporteDetalleScreen(
        titulo = "Asignaturas",
        subtitulo = "Asignaturas registradas",
        onBack = onBack
    ) {
        // TODO: contenido reporte asignaturas
    }
}

@Composable
fun ReporteNotasScreen(onBack: () -> Unit) {
    ReporteDetalleScreen(
        titulo = "Notas",
        subtitulo = "Notas por grupo o período",
        onBack = onBack
    ) {
        // TODO: contenido reporte notas
    }
}

@Composable
fun ReporteMatriculasScreen(onBack: () -> Unit) {
    ReporteDetalleScreen(
        titulo = "Matrículas",
        subtitulo = "Matrículas de estudiantes por año",
        onBack = onBack
    ) {
        // TODO: contenido reporte matrículas
    }
}

@Composable
fun ReporteEstudiantesScreen(onBack: () -> Unit) {
    ReporteDetalleScreen(
        titulo = "Estudiantes",
        subtitulo = "Listado de estudiantes",
        onBack = onBack
    ) {
        // TODO: contenido reporte estudiantes
    }
}




//
//@Preview(showBackground = true)
//@Composable
//fun ReportesScreenPreview() {
//    ReportesScreen()
//}