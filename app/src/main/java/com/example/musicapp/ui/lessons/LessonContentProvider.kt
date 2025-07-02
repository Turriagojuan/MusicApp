package com.example.musicapp.ui.lessons

import androidx.annotation.DrawableRes
import com.example.musicapp.R

// Modelo para el contenido de una lección
data class LessonContent(
    val lessonId: String,
    val title: String,
    val pages: List<LessonPage>
)

// Modelo para una página dentro de una lección
data class LessonPage(
    val text: String,
    @DrawableRes val imageResId: Int? = null // ID del recurso de imagen (opcional)
)

object LessonContentProvider {

    fun getLesson(lessonId: String): LessonContent? {
        return allLessons.find { it.lessonId == lessonId }
    }

    private val allLessons = listOf(
        // MÓDULO 1: RITMO
        LessonContent(
            lessonId = "1.1",
            title = "El Pulso y el Compás",
            pages = listOf(
                LessonPage(
                    text = "¡Bienvenido al mundo del ritmo! El pulso es como el latido del corazón de la música. Es una serie de golpes constantes que marcan el tiempo. ¡Intenta dar palmas siguiendo un ritmo constante!",
                    // TODO: Añadir imagen de un corazón latiendo o similar en res/drawable
                ),
                LessonPage(
                    text = "El compás agrupa estos pulsos. El más común es el de 4/4, que significa que hay 4 pulsos en cada 'cajita' musical o compás. El primer pulso suele ser el más fuerte: UNO, dos, tres, cuatro."
                )
            )
        ),
        LessonContent(
            lessonId = "1.2",
            title = "Figuras de Nota",
            pages = listOf(
                LessonPage(
                    text = "Las figuras nos dicen cuánto tiempo debe durar un sonido.\n\nLa REDONDA dura 4 pulsos. ¡Es la más larga!",
                ),
                LessonPage(
                    text = "La BLANCA dura la mitad de una redonda, es decir, 2 pulsos.",
                ),
                LessonPage(
                    text = "La NEGRA dura 1 pulso. Es la figura más común para seguir el ritmo.",
                ),
                LessonPage(
                    text = "La CORCHEA dura medio pulso. ¡Es más rápida! Se necesitan dos corcheas para llenar un pulso de negra.",
                )
            )
        ),
        LessonContent(
            lessonId = "1.3",
            title = "Los Silencios",
            pages = listOf(
                LessonPage(
                    text = "La música también está hecha de silencios. Cada figura de nota tiene su propio silencio, que dura exactamente lo mismo.\n\nEl silencio de redonda también dura 4 pulsos de silencio."
                ),
                LessonPage(
                    text = "El silencio de blanca dura 2 pulsos. El de negra, 1 pulso, y el de corchea, medio pulso. ¡El silencio es tan importante como el sonido!"
                )
            )
        ),
        // MÓDULO 2: PENTAGRAMA
        LessonContent(
            lessonId = "2.1",
            title = "¿Qué es el Pentagrama?",
            pages = listOf(
                LessonPage(text = "El pentagrama son esas 5 líneas y 4 espacios donde escribimos la música. ¡Es como el renglón de las notas musicales!")
            )
        ),
        LessonContent(
            lessonId = "2.2",
            title = "La Clave de Sol",
            pages = listOf(
                LessonPage(text = "La Clave de Sol es un símbolo que se pone al principio del pentagrama. Su función es muy importante: ¡le da nombre a las notas! La 'pancita' de la clave envuelve la segunda línea, y nos dice que esa línea se llama SOL.")
            )
        ),
        LessonContent(
            lessonId = "2.3",
            title = "Notas en las Líneas",
            pages = listOf(
                LessonPage(text = "Las notas en las líneas, contando desde abajo hacia arriba, son: MI, SOL, SI, RE, FA.")
            )
        ),
        LessonContent(
            lessonId = "2.4",
            title = "Notas en los Espacios",
            pages = listOf(
                LessonPage(text = "Las notas en los espacios, contando desde abajo hacia arriba, forman la palabra: FA, LA, DO, MI.")
            )
        )
    )
}