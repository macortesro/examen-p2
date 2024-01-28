package cl.mcortesr.android.examen_p2.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cl.mcortesr.android.examen_p2.Aplicacion
import cl.mcortesr.android.examen_p2.clases.Registro
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListaRegistrosViewModel(
    private val registroDao: RegistroDao
) : ViewModel() {

    private val _registros = MutableStateFlow<List<Registro>>(emptyList())
    val registros: StateFlow<List<Registro>> = _registros

    init {
         obtenerRegistros()
    }

    fun obtenerRegistros() {
        viewModelScope.launch {
            _registros.value = registroDao.obtenerTodos()
        }
    }

    fun insertarRegistro(registro: Registro) {
        viewModelScope.launch {
            registroDao.insertar(registro)
           obtenerRegistros()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val savedStateHandle = createSavedStateHandle()
                val aplicacion = (this[APPLICATION_KEY] as Aplicacion)
                ListaRegistrosViewModel(aplicacion.registroDao)
            }
        }
    }
}