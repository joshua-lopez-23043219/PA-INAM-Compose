package com.example.pa_inam.Composables

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.pa_inam.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pa_inam.ui.theme.Gris
import com.example.pa_inam.ui.theme.Azul_f


data class ConsultaItem(
    @DrawableRes val icon: Int,
    @StringRes val text: Int
)

enum class ConsultaView {
    MENU,        // Pantalla principal con 6 botones
    GRUPOS,      // Pantalla "Grupos de Clases por año"
    MAESTROS,     // Pantalla "Maestros por Áreas o Asignaturas"
    PRIMER_GRADO,
    SEGUNDO_GRADO
}



@Composable
fun ConsultasScreen() {
    var currentView by remember { mutableStateOf(ConsultaView.MENU) }

    when (currentView) {
        ConsultaView.MENU -> ConsultasMenuScreen(
            onGruposClick = { currentView = ConsultaView.GRUPOS },
            onMaestrosClick = { currentView = ConsultaView.MAESTROS },
            onAsignaturasClick = { /*TODO: más adelante*/ },
            onNotasClick = { /* TODO */ },
            onMatriculasClick = { /* TODO */ },
            onEstudiantesClick = { /* TODO */ }
        )

        ConsultaView.GRUPOS -> GruposScreen(
            onBack = { currentView = ConsultaView.MENU },
            onPrimerGradoClick = { currentView = ConsultaView.PRIMER_GRADO },
            onSegundoGradoClick = { currentView = ConsultaView.SEGUNDO_GRADO }
        )

        ConsultaView.PRIMER_GRADO -> GradoGeneroScreen(
            tituloCabecera = "Primer grado",
            grupos = listOf(
                GrupoGeneroConfig("P1A-M", "Primer grado A Mañana"),
                GrupoGeneroConfig("P1A-T", "Primer grado A Tarde"),
                GrupoGeneroConfig("P1B-M", "Primer grado B Mañana"),
                GrupoGeneroConfig("P1B-T", "Primer grado B Tarde"),
                GrupoGeneroConfig("P1C-M", "Primer grado C Mañana"),
                GrupoGeneroConfig("P1C-T", "Primer grado C Tarde"),
                GrupoGeneroConfig("P1D-M", "Primer grado D Mañana"),
                GrupoGeneroConfig("P1D-T", "Primer grado D Tarde")
            ),
            onBack = { currentView = ConsultaView.GRUPOS }
        )

        ConsultaView.SEGUNDO_GRADO -> GradoGeneroScreen(
            tituloCabecera = "Segundo grado",
            grupos = listOf(
                GrupoGeneroConfig("P2A-M", "Segundo grado A Mañana"),
                GrupoGeneroConfig("P2A-T", "Segundo grado A Tarde")
                // aquí agregas P2B-M, P2B-T, etc. cuando los tengas
            ),
            onBack = { currentView = ConsultaView.GRUPOS }
        )

        ConsultaView.MAESTROS -> MaestrosScreen(
            onBack = { currentView = ConsultaView.MENU }
        )
    }
}


@Composable
private fun GruposScreen(
    onBack: () -> Unit,
    onPrimerGradoClick: () -> Unit,
    onSegundoGradoClick: () -> Unit
) {


    val grupos = listOf(
        ConsultaItem(R.drawable.grupos, R.string.primaria1),
        ConsultaItem(R.drawable.grupos, R.string.primaria2),
        ConsultaItem(R.drawable.grupos, R.string.primaria3),
        ConsultaItem(R.drawable.grupos, R.string.primaria4),
        ConsultaItem(R.drawable.grupos, R.string.primaria5),
        ConsultaItem(R.drawable.grupos, R.string.primaria6),
        ConsultaItem(R.drawable.grupos, R.string.grupo1),
        ConsultaItem(R.drawable.grupos, R.string.grupo2),
        ConsultaItem(R.drawable.grupos, R.string.grupo3),
        ConsultaItem(R.drawable.grupos, R.string.grupo4),
        ConsultaItem(R.drawable.grupos, R.string.grupo5),

    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5FBFF))
    ) {
        // Barra azul con botón atrás y título "Grupos"
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
                text = "Grupos",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Banda gris
        Text(
            text = "Grupos de Clases por año",
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFE0E0E0))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            color = Color(0xFF0A4FA8),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(12.dp))

        // Grid con las tarjetas
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(grupos) { index, item ->
                ConsultaCard(
                    iconRes = item.icon,
                    textRes = item.text,
                    onClick = {
                        when (index) {
                            0 -> onPrimerGradoClick()
                            1 -> onSegundoGradoClick()
                            // 2 -> onTercerGradoClick()   // cuando lo quieras
                        }
                    }
                )
            }
        }
    }
}





@Composable
private fun ConsultasMenuScreen(
    onGruposClick: () -> Unit,
    onMaestrosClick: () -> Unit,
    onAsignaturasClick: () -> Unit,
    onNotasClick: () -> Unit,
    onMatriculasClick: () -> Unit,
    onEstudiantesClick: () -> Unit
) {


    val items = listOf(
        ConsultaItem(R.drawable.grupos,     R.string.grupo),
        ConsultaItem(R.drawable.maestro,    R.string.maestros),
        ConsultaItem(R.drawable.asignatura, R.string.asignaturas),
        ConsultaItem(R.drawable.notas,      R.string.notas),
        ConsultaItem(R.drawable.matriculas, R.string.matriculas),
        ConsultaItem(R.drawable.estudiante, R.string.estudiantes)
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
        // Título azul (Gestión Académica y Administrativa)
        Text(
            text = stringResource(id = R.string.tituloconsulta),
            modifier = Modifier
                .fillMaxWidth()
                .background(Azul_f)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
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
                ConsultaCard(
                    iconRes = item.icon,
                    textRes = item.text,
                    onClick = acciones[index]
                )
            }
        }
    }
}



@Composable
private fun MaestrosScreen(
    onBack: () -> Unit
) {


    val areas = listOf(
        ConsultaItem(R.drawable.maestro, R.string.area1),
        ConsultaItem(R.drawable.maestro, R.string.area2),
        ConsultaItem(R.drawable.maestro, R.string.area3),
        ConsultaItem(R.drawable.maestro, R.string.area4),
        ConsultaItem(R.drawable.maestro, R.string.area5),
        ConsultaItem(R.drawable.maestro, R.string.area6)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Barra azul con botón "<" y título "Maestros"
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Azul_f)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text("<", color = Color.White, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(id = R.string.maestros),
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Subtítulo gris "Maestros por Áreas o Asignaturas"
        Text(
            text = stringResource(id = R.string.titulomaestros),
            modifier = Modifier
                .fillMaxWidth()
                .background(Gris)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            color = Azul_f,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
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
            items(areas) { item ->
                ConsultaCard(
                    iconRes = item.icon,
                    textRes = item.text,
                    onClick = { /* aquí luego harás la consulta para esa área */ }
                )
            }
        }
    }
}


//@Preview(showBackground = true)
//@Composable
//fun ConsultasScreenPreview() {
//    ConsultasScreen()
//}



