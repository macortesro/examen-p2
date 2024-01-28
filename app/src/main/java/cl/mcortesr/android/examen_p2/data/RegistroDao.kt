package cl.mcortesr.android.examen_p2.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import cl.mcortesr.android.examen_p2.clases.Registro


@Dao
interface RegistroDao{
    @Query("SELECT * FROM Registro ORDER BY fecha DESC")
    suspend fun  obtenerTodos(): List<Registro>

    @Query("SELECT * FROM Registro WHERE id = :id")
    suspend fun obtenerPorId(id:Long): Registro

    @Insert
    suspend fun insertar(registro: Registro)

    @Update
    suspend fun modificar(registro: Registro)

    @Delete
    suspend fun eliminar(registro: Registro)
}

