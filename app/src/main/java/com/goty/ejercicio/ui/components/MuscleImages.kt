package com.goty.ejercicio.ui.components

import androidx.annotation.DrawableRes
import com.goty.ejercicio.R

/**
 * Traduce el nombre guardado en Musculo.foto (ej. "ic_musculo_biceps_braquial") a su drawable.
 * Se usa un mapa con R.drawable (y no getIdentifier) para que el compilador valide que existan
 * y R8 no los elimine al reducir recursos en release.
 */
private val muscleDrawables: Map<String, Int> = mapOf(
    // Pecho
    "ic_musculo_pectoral_superior" to R.drawable.ic_musculo_pectoral_superior,
    "ic_musculo_pectoral_medio" to R.drawable.ic_musculo_pectoral_medio,
    "ic_musculo_pectoral_inferior" to R.drawable.ic_musculo_pectoral_inferior,
    "ic_musculo_pectoral_menor" to R.drawable.ic_musculo_pectoral_menor,
    "ic_musculo_serrato_anterior" to R.drawable.ic_musculo_serrato_anterior,
    // Espalda
    "ic_musculo_dorsal_ancho" to R.drawable.ic_musculo_dorsal_ancho,
    "ic_musculo_trapecio_superior" to R.drawable.ic_musculo_trapecio_superior,
    "ic_musculo_trapecio_medio" to R.drawable.ic_musculo_trapecio_medio,
    "ic_musculo_trapecio_inferior" to R.drawable.ic_musculo_trapecio_inferior,
    "ic_musculo_romboides" to R.drawable.ic_musculo_romboides,
    "ic_musculo_redondo_mayor" to R.drawable.ic_musculo_redondo_mayor,
    "ic_musculo_erectores_espinales" to R.drawable.ic_musculo_erectores_espinales,
    "ic_musculo_cuadrado_lumbar" to R.drawable.ic_musculo_cuadrado_lumbar,
    // Hombros
    "ic_musculo_deltoides_anterior" to R.drawable.ic_musculo_deltoides_anterior,
    "ic_musculo_deltoides_lateral" to R.drawable.ic_musculo_deltoides_lateral,
    "ic_musculo_deltoides_posterior" to R.drawable.ic_musculo_deltoides_posterior,
    "ic_musculo_supraespinoso" to R.drawable.ic_musculo_supraespinoso,
    "ic_musculo_infraespinoso" to R.drawable.ic_musculo_infraespinoso,
    "ic_musculo_redondo_menor" to R.drawable.ic_musculo_redondo_menor,
    "ic_musculo_subescapular" to R.drawable.ic_musculo_subescapular,
    // Brazos
    "ic_musculo_biceps_braquial" to R.drawable.ic_musculo_biceps_braquial,
    "ic_musculo_braquial" to R.drawable.ic_musculo_braquial,
    "ic_musculo_triceps_larga" to R.drawable.ic_musculo_triceps_larga,
    "ic_musculo_triceps_lateral" to R.drawable.ic_musculo_triceps_lateral,
    "ic_musculo_triceps_medial" to R.drawable.ic_musculo_triceps_medial,
    "ic_musculo_braquiorradial" to R.drawable.ic_musculo_braquiorradial,
    "ic_musculo_flexores_muneca" to R.drawable.ic_musculo_flexores_muneca,
    "ic_musculo_extensores_muneca" to R.drawable.ic_musculo_extensores_muneca,
    // Piernas
    "ic_musculo_recto_femoral" to R.drawable.ic_musculo_recto_femoral,
    "ic_musculo_vasto_lateral" to R.drawable.ic_musculo_vasto_lateral,
    "ic_musculo_vasto_medial" to R.drawable.ic_musculo_vasto_medial,
    "ic_musculo_vasto_intermedio" to R.drawable.ic_musculo_vasto_intermedio,
    "ic_musculo_biceps_femoral" to R.drawable.ic_musculo_biceps_femoral,
    "ic_musculo_semitendinoso" to R.drawable.ic_musculo_semitendinoso,
    "ic_musculo_semimembranoso" to R.drawable.ic_musculo_semimembranoso,
    "ic_musculo_aductor_mayor" to R.drawable.ic_musculo_aductor_mayor,
    "ic_musculo_aductor_largo" to R.drawable.ic_musculo_aductor_largo,
    "ic_musculo_aductor_corto" to R.drawable.ic_musculo_aductor_corto,
    "ic_musculo_gastrocnemio" to R.drawable.ic_musculo_gastrocnemio,
    "ic_musculo_soleo" to R.drawable.ic_musculo_soleo,
    "ic_musculo_tibial_anterior" to R.drawable.ic_musculo_tibial_anterior,
    // Glúteos
    "ic_musculo_gluteo_mayor" to R.drawable.ic_musculo_gluteo_mayor,
    "ic_musculo_gluteo_medio" to R.drawable.ic_musculo_gluteo_medio,
    "ic_musculo_gluteo_menor" to R.drawable.ic_musculo_gluteo_menor,
    "ic_musculo_tensor_fascia_lata" to R.drawable.ic_musculo_tensor_fascia_lata,
    // Core
    "ic_musculo_recto_abdominal" to R.drawable.ic_musculo_recto_abdominal,
    "ic_musculo_oblicuo_externo" to R.drawable.ic_musculo_oblicuo_externo,
    "ic_musculo_oblicuo_interno" to R.drawable.ic_musculo_oblicuo_interno,
    "ic_musculo_transverso_abdominal" to R.drawable.ic_musculo_transverso_abdominal,
    "ic_musculo_psoas_iliaco" to R.drawable.ic_musculo_psoas_iliaco,
    // Cuello
    "ic_musculo_esternocleidomastoideo" to R.drawable.ic_musculo_esternocleidomastoideo,
    "ic_musculo_esplenio" to R.drawable.ic_musculo_esplenio,
)

/** Devuelve el drawable del músculo, o null si no hay imagen para ese nombre. */
@DrawableRes
fun muscleDrawable(photo: String?): Int? = photo?.let { muscleDrawables[it] }
