package com.goty.ejercicio.data.local

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Precarga las categorías y músculos iniciales con IDs fijos.
 *
 * onCreate solo se ejecuta la PRIMERA vez que se crea la BD en el dispositivo.
 *
 * Los valores se pasan como parámetros (`?` + bindArgs) en lugar de concatenarlos en el SQL:
 * así SQLite se encarga de escapar acentos, ñ, comillas y null, y no hay riesgo de romper la consulta.
 */
class PrepopulateCallback : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        db.beginTransaction()
        try {
            categorias.forEach { (id, nombre, icono) ->
                db.execSQL(
                    "INSERT INTO categorias (id, nombre, icono) VALUES (?, ?, ?)",
                    arrayOf<Any?>(id, nombre, icono),
                )
            }
            musculos.forEach { m ->
                db.execSQL(
                    "INSERT INTO musculos (id, categoriaId, nombre, grupo, foto) VALUES (?, ?, ?, ?, ?)",
                    arrayOf<Any?>(m.id, m.categoriaId, m.nombre, m.grupo, m.foto),
                )
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    private data class MusculoSeed(
        val id: Long,
        val categoriaId: Long,
        val nombre: String,
        val grupo: String?,
        val foto: String,
    )

    private companion object {

        val categorias = listOf(
            Triple(1L, "Pecho", "ic_cat_pecho"),
            Triple(2L, "Espalda", "ic_cat_espalda"),
            Triple(3L, "Hombros", "ic_cat_hombros"),
            Triple(4L, "Brazos", "ic_cat_brazos"),
            Triple(5L, "Piernas", "ic_cat_piernas"),
            Triple(6L, "Glúteos", "ic_cat_gluteos"),
            Triple(7L, "Core", "ic_cat_core"),
            Triple(8L, "Cuello", "ic_cat_cuello"),
        )

        val musculos = listOf(
            // Pecho
            MusculoSeed(1, 1, "Pectoral mayor porción superior", "Pectoral mayor", "ic_musculo_pectoral_superior"),
            MusculoSeed(2, 1, "Pectoral mayor porción media", "Pectoral mayor", "ic_musculo_pectoral_medio"),
            MusculoSeed(3, 1, "Pectoral mayor porción inferior", "Pectoral mayor", "ic_musculo_pectoral_inferior"),
            MusculoSeed(4, 1, "Pectoral menor", null, "ic_musculo_pectoral_menor"),
            MusculoSeed(5, 1, "Serrato anterior", null, "ic_musculo_serrato_anterior"),
            // Espalda
            MusculoSeed(6, 2, "Dorsal ancho", null, "ic_musculo_dorsal_ancho"),
            MusculoSeed(7, 2, "Trapecio superior", "Trapecio", "ic_musculo_trapecio_superior"),
            MusculoSeed(8, 2, "Trapecio medio", "Trapecio", "ic_musculo_trapecio_medio"),
            MusculoSeed(9, 2, "Trapecio inferior", "Trapecio", "ic_musculo_trapecio_inferior"),
            MusculoSeed(10, 2, "Romboides", null, "ic_musculo_romboides"),
            MusculoSeed(11, 2, "Redondo mayor", null, "ic_musculo_redondo_mayor"),
            MusculoSeed(12, 2, "Erectores espinales", null, "ic_musculo_erectores_espinales"),
            MusculoSeed(13, 2, "Cuadrado lumbar", null, "ic_musculo_cuadrado_lumbar"),
            // Hombros
            MusculoSeed(14, 3, "Deltoides anterior", "Deltoides", "ic_musculo_deltoides_anterior"),
            MusculoSeed(15, 3, "Deltoides lateral", "Deltoides", "ic_musculo_deltoides_lateral"),
            MusculoSeed(16, 3, "Deltoides posterior", "Deltoides", "ic_musculo_deltoides_posterior"),
            MusculoSeed(17, 3, "Supraespinoso", "Manguito rotador", "ic_musculo_supraespinoso"),
            MusculoSeed(18, 3, "Infraespinoso", "Manguito rotador", "ic_musculo_infraespinoso"),
            MusculoSeed(19, 3, "Redondo menor", "Manguito rotador", "ic_musculo_redondo_menor"),
            MusculoSeed(20, 3, "Subescapular", "Manguito rotador", "ic_musculo_subescapular"),
            // Brazos
            MusculoSeed(21, 4, "Bíceps braquial", "Bíceps", "ic_musculo_biceps_braquial"),
            MusculoSeed(22, 4, "Braquial", "Bíceps", "ic_musculo_braquial"),
            MusculoSeed(23, 4, "Tríceps cabeza larga", "Tríceps", "ic_musculo_triceps_larga"),
            MusculoSeed(24, 4, "Tríceps cabeza lateral", "Tríceps", "ic_musculo_triceps_lateral"),
            MusculoSeed(25, 4, "Tríceps cabeza medial", "Tríceps", "ic_musculo_triceps_medial"),
            MusculoSeed(26, 4, "Braquiorradial", "Antebrazo", "ic_musculo_braquiorradial"),
            MusculoSeed(27, 4, "Flexores de muñeca", "Antebrazo", "ic_musculo_flexores_muneca"),
            MusculoSeed(28, 4, "Extensores de muñeca", "Antebrazo", "ic_musculo_extensores_muneca"),
            // Piernas
            MusculoSeed(29, 5, "Recto femoral", "Cuádriceps", "ic_musculo_recto_femoral"),
            MusculoSeed(30, 5, "Vasto lateral", "Cuádriceps", "ic_musculo_vasto_lateral"),
            MusculoSeed(31, 5, "Vasto medial", "Cuádriceps", "ic_musculo_vasto_medial"),
            MusculoSeed(32, 5, "Vasto intermedio", "Cuádriceps", "ic_musculo_vasto_intermedio"),
            MusculoSeed(33, 5, "Bíceps femoral", "Isquiotibiales", "ic_musculo_biceps_femoral"),
            MusculoSeed(34, 5, "Semitendinoso", "Isquiotibiales", "ic_musculo_semitendinoso"),
            MusculoSeed(35, 5, "Semimembranoso", "Isquiotibiales", "ic_musculo_semimembranoso"),
            MusculoSeed(36, 5, "Aductor mayor", "Aductores", "ic_musculo_aductor_mayor"),
            MusculoSeed(37, 5, "Aductor largo", "Aductores", "ic_musculo_aductor_largo"),
            MusculoSeed(38, 5, "Aductor corto", "Aductores", "ic_musculo_aductor_corto"),
            MusculoSeed(39, 5, "Gastrocnemio", "Pantorrilla", "ic_musculo_gastrocnemio"),
            MusculoSeed(40, 5, "Sóleo", "Pantorrilla", "ic_musculo_soleo"),
            MusculoSeed(41, 5, "Tibial anterior", "Pantorrilla", "ic_musculo_tibial_anterior"),
            // Glúteos
            MusculoSeed(42, 6, "Glúteo mayor", null, "ic_musculo_gluteo_mayor"),
            MusculoSeed(43, 6, "Glúteo medio", null, "ic_musculo_gluteo_medio"),
            MusculoSeed(44, 6, "Glúteo menor", null, "ic_musculo_gluteo_menor"),
            MusculoSeed(45, 6, "Tensor de la fascia lata", null, "ic_musculo_tensor_fascia_lata"),
            // Core
            MusculoSeed(46, 7, "Recto abdominal", null, "ic_musculo_recto_abdominal"),
            MusculoSeed(47, 7, "Oblicuo externo", null, "ic_musculo_oblicuo_externo"),
            MusculoSeed(48, 7, "Oblicuo interno", null, "ic_musculo_oblicuo_interno"),
            MusculoSeed(49, 7, "Transverso abdominal", null, "ic_musculo_transverso_abdominal"),
            MusculoSeed(50, 7, "Psoas ilíaco", null, "ic_musculo_psoas_iliaco"),
            // Cuello
            MusculoSeed(51, 8, "Esternocleidomastoideo", null, "ic_musculo_esternocleidomastoideo"),
            MusculoSeed(52, 8, "Esplenio", null, "ic_musculo_esplenio"),
        )
    }
}
