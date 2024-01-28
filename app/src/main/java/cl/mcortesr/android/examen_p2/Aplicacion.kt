package cl.mcortesr.android.examen_p2

import android.app.Application
import androidx.room.Room
import cl.mcortesr.android.examen_p2.data.BaseDatos


class Aplicacion : Application() {

    val db by lazy { Room.databaseBuilder(this, BaseDatos::class.java, "registros.db").build() }
    val registroDao by lazy { db.RegistroDao()}
}


