package cl.mcortesr.android.examen_p2

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.mcortesr.android.examen_p2.clases.Registro
import cl.mcortesr.android.examen_p2.data.ListaRegistrosViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.LocalDate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import cl.mcortesr.android.examen_p2.clases.CategoriaIcono
import cl.mcortesr.android.examen_p2.data.BaseDatos


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppRegistrosUI()
        }
    }
}

@Composable
fun AppRegistrosUI(
    navController: NavHostController = rememberNavController(),
    vmListaRegistros: ListaRegistrosViewModel = viewModel(factory = ListaRegistrosViewModel.Factory)
) {
    NavHost(
        navController = navController,
        startDestination = "inicio")
    {
        composable("inicio") {
            PantallaListaRegistros(
                vmListaRegistros = vmListaRegistros,
                navController = navController
            )
        }
        composable("form") {
            PantallaFormRegistro(
                navigateBack = { navController.popBackStack()},
                vmListaRegistros = vmListaRegistros
            )
        }
    }
}

@Composable
fun OpcionesCategoriaUI(
    onCategoriaSelected: (String) -> Unit
) {
    val categorias = listOf("AGUA", "LUZ", "GAS")
    var categoriaSeleccionada by rememberSaveable { mutableStateOf(categorias[0]) }

    Column(Modifier.selectableGroup()) {
        categorias.forEach { categoria ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .selectable(
                        selected = (categoria == categoriaSeleccionada),
                        onClick = {
                            categoriaSeleccionada = categoria
                            onCategoriaSelected(categoria)
                        },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (categoria == categoriaSeleccionada),
                    onClick = null
                )
                Text(
                    text = categoria,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}


@Composable
fun PantallaFormRegistro(
    navigateBack: () -> Unit,
    vmListaRegistros: ListaRegistrosViewModel = viewModel(factory = ListaRegistrosViewModel.Factory)
){

    var monto by rememberSaveable { mutableIntStateOf(0) }
    var fecha by rememberSaveable { mutableStateOf("")}
    var categoriaSeleccionada by rememberSaveable { mutableStateOf("") }

    val contexto = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 80.dp, vertical = 50.dp)
    ) {
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 60.dp),
                text = (contexto.getString(R.string.app_name)),
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
            )
            Spacer(modifier = Modifier.height(20.dp))
            TextField(
                value = monto.toString(),
                onValueChange = { monto = it.toIntOrNull() ?: 0 },
                label = { Text("Medidor") },
                placeholder = { Text("10000") }
            )
            TextField(
                value = fecha,
                onValueChange = { fecha = it },
                label = { Text("Fecha") },
                placeholder = { Text("2023-01-01") }
            )
            Spacer(modifier = Modifier.height(30.dp))
            Text("Medidor de:")
            OpcionesCategoriaUI { categoria ->
                categoriaSeleccionada = categoria
            }
            Spacer(modifier = Modifier.height(30.dp))
            Button(
                onClick = {
                    vmListaRegistros.insertarRegistro(
                        Registro(
                            null,
                            monto.toLong(),
                            LocalDate.parse(fecha),
                            categoriaSeleccionada
                        )
                    )
                    vmListaRegistros.obtenerRegistros()
                    navigateBack()
                },
                modifier = Modifier.padding(horizontal = 50.dp)
            ) {
                Text(contexto.getString(R.string.btn_text_registrar))
            }
        }
    }
}

@Composable
fun PantallaListaRegistros(
    vmListaRegistros: ListaRegistrosViewModel,
    navController: NavHostController
) {
    val registros by vmListaRegistros.registros.collectAsState()
    val contexto = LocalContext.current

    val categoriasConIconos = listOf(
        CategoriaIcono("AGUA", R.drawable.agua_icon),
        CategoriaIcono("LUZ", R.drawable.luz_icon),
        CategoriaIcono("GAS", R.drawable.gas_icon)
    )

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("form") }) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = contexto.getString(R.string.btn_agregar)
                )
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier.padding(vertical = it.calculateTopPadding())
        ) {
            items(registros) { registro ->
                val categoriaIcono = categoriasConIconos.find { it.nombre == registro.categoria }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    categoriaIcono?.let {
                        Image(
                            painter = painterResource(id = it.icono),
                            contentDescription = "Icono de ${it.nombre}",
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = registro.categoria,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(80.dp)
                    )
                    Text(
                        text = String.format("%,d", registro.monto),
                        textAlign = TextAlign.End,
                        modifier = Modifier.width(100.dp)
                    )
                    Text(
                        text = registro.fecha.toString(),
                        textAlign = TextAlign.End,
                        modifier = Modifier.width(150.dp)
                    )
                }
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp),
                    color = Color.LightGray
                )
            }
        }
    }
}

