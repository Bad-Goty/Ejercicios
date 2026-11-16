package com.goty.ejercicio.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * v1 -> v2
 * - ejercicios: nuevas columnas `series` y `repeticiones` (los ejercicios que ya existían
 *   quedan con 4 series de 12 repeticiones).
 * - Se elimina `rutina_ejercicio` (las series/reps ahora viven en el ejercicio).
 * - Nueva tabla `rutina_dia`: qué categoría(s) toca cada día de la rutina.
 * No se pierden ejercicios, músculos ni categorías.
 */
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `ejercicios` ADD COLUMN `series` INTEGER NOT NULL DEFAULT 4")
        db.execSQL("ALTER TABLE `ejercicios` ADD COLUMN `repeticiones` INTEGER NOT NULL DEFAULT 12")

        db.execSQL("DROP TABLE IF EXISTS `rutina_ejercicio`")

        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `rutina_dia` (" +
                "`rutinaId` INTEGER NOT NULL, " +
                "`diaSemana` INTEGER NOT NULL, " +
                "`categoriaId` INTEGER NOT NULL, " +
                "PRIMARY KEY(`rutinaId`, `diaSemana`, `categoriaId`), " +
                "FOREIGN KEY(`rutinaId`) REFERENCES `rutinas`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , " +
                "FOREIGN KEY(`categoriaId`) REFERENCES `categorias`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )"
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_rutina_dia_categoriaId` ON `rutina_dia` (`categoriaId`)")
    }
}

/**
 * v2 -> v3
 * - Nueva tabla `serie_completada`: registro de las series hechas cada día
 *   (para recordar el progreso del día y para historial/estadísticas).
 */
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `serie_completada` (" +
                "`ejercicioId` INTEGER NOT NULL, " +
                "`fecha` TEXT NOT NULL, " +
                "`numeroSerie` INTEGER NOT NULL, " +
                "`repeticiones` INTEGER NOT NULL, " +
                "`completadaEn` INTEGER NOT NULL, " +
                "PRIMARY KEY(`ejercicioId`, `fecha`, `numeroSerie`), " +
                "FOREIGN KEY(`ejercicioId`) REFERENCES `ejercicios`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )"
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_serie_completada_fecha` ON `serie_completada` (`fecha`)")
    }
}
