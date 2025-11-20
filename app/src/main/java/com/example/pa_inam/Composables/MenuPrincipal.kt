package com.example.pa_inam.Composables

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pa_inam.R
import kotlinx.coroutines.launch

// ----- MODELOS DE ITEMS -----



data class DrawerItem(
    val id: String,
    @DrawableRes val icon: Int,
    val label: String
)

data class BottomItem(
    val id: String,
    val icon: Int,
    val label: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuPrincipalScreen(
    userName: String,
    userEmail: String,
    onLogout: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    var selectedBottom by remember { mutableStateOf("dashboard") }

    val drawerItems = listOf(
        DrawerItem("home",   R.drawable.home,        "Home"),
        DrawerItem("perfil", R.drawable.perfil,      "Perfil"),
        DrawerItem("ayuda",  R.drawable.help,        "Ayuda"),
        DrawerItem("ajuste", R.drawable.setting,    "Ajuste"),
        DrawerItem("salir",  R.drawable.close,   "Cerrar sesión")
    )

    val bottomItems = listOf(
        BottomItem("dashboard", R.drawable.analisis, "Dashboard"),
        BottomItem("consultas", R.drawable.consultas,     "Consultas"),
        BottomItem("reportes",  R.drawable.reportes, "Reportes")
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                userName = userName,
                userEmail = userEmail,
                items = drawerItems,
                onItemClick = { item ->
                    when (item.id) {
                        "home"   -> selectedBottom = "dashboard"
                        "perfil" -> selectedBottom = "perfil"
                        "ayuda"  -> selectedBottom = "ayuda"
                        "ajuste" -> selectedBottom = "ajuste"
                        "salir"  -> onLogout()
                    }
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("PA-INAM") },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    if (drawerState.isClosed) drawerState.open()
                                    else drawerState.close()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menú"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    bottomItems.forEach { item ->
                        NavigationBarItem(
                            selected = selectedBottom == item.id,
                            onClick = { selectedBottom = item.id },
                            icon = {
                                Icon(
                                    painter = painterResource(
                                        id = item.icon
                                    ),
                                    contentDescription = item.label,
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (selectedBottom) {
                    "dashboard" -> DashboardCuboScreen()
                    "consultas" -> ConsultasScreen()
                    "reportes"  -> ReportesScreen()
                    "perfil"    -> PerfilScreen(userName, userEmail)
                    "ayuda"     -> AyudaScreen()
                    "ajuste"    -> AjusteScreen()
                }
            }
        }
    }
}

@Composable
private fun DrawerContent(
    userName: String,
    userEmail: String,
    items: List<DrawerItem>,
    onItemClick: (DrawerItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(260.dp)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Header, similar a tu nav_header.xml
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(176.dp)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.BottomStart
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Image(
                    painter = painterResource(id = R.drawable.usuario),
                    contentDescription = "Usuario",
                    modifier = Modifier.size(60.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = userName,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = userEmail,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        items.forEach { item ->
            NavigationDrawerItem(
                label = { Text(item.label) },
                selected = false,
                onClick = { onItemClick(item) },
                icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }
    }
}