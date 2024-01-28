package cl.mcortesr.android.examen_p2.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import cl.mcortesr.android.examen_p2.clases.Registro


@Database(entities = [Registro::class], version = 1)
@TypeConverters(LocalDateConverter::class)
abstract class BaseDatos : RoomDatabase() {
    abstract fun RegistroDao(): RegistroDao


}

