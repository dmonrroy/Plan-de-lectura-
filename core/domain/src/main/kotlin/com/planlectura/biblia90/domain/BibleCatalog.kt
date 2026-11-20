package com.planlectura.biblia90.domain

enum class Testament { OLD, NEW }

data class Book(
    val id: String,
    val name: String,
    val abbreviation: String,
    val testament: Testament,
    val chapters: Int,
)

/** Referencia a un capítulo concreto, la unidad atómica de progreso. */
data class ChapterRef(val bookId: String, val chapter: Int)

/** Catálogo canónico protestante de 66 libros (1.189 capítulos). */
object BibleCatalog {
    val books: List<Book> = listOf(
        Book("GEN", "Génesis", "Gn", Testament.OLD, 50),
        Book("EXO", "Éxodo", "Éx", Testament.OLD, 40),
        Book("LEV", "Levítico", "Lv", Testament.OLD, 27),
        Book("NUM", "Números", "Nm", Testament.OLD, 36),
        Book("DEU", "Deuteronomio", "Dt", Testament.OLD, 34),
        Book("JOS", "Josué", "Jos", Testament.OLD, 24),
        Book("JDG", "Jueces", "Jue", Testament.OLD, 21),
        Book("RUT", "Rut", "Rt", Testament.OLD, 4),
        Book("1SA", "1 Samuel", "1 S", Testament.OLD, 31),
        Book("2SA", "2 Samuel", "2 S", Testament.OLD, 24),
        Book("1KI", "1 Reyes", "1 R", Testament.OLD, 22),
        Book("2KI", "2 Reyes", "2 R", Testament.OLD, 25),
        Book("1CH", "1 Crónicas", "1 Cr", Testament.OLD, 29),
        Book("2CH", "2 Crónicas", "2 Cr", Testament.OLD, 36),
        Book("EZR", "Esdras", "Esd", Testament.OLD, 10),
        Book("NEH", "Nehemías", "Neh", Testament.OLD, 13),
        Book("EST", "Ester", "Est", Testament.OLD, 10),
        Book("JOB", "Job", "Job", Testament.OLD, 42),
        Book("PSA", "Salmos", "Sal", Testament.OLD, 150),
        Book("PRO", "Proverbios", "Pr", Testament.OLD, 31),
        Book("ECC", "Eclesiastés", "Ec", Testament.OLD, 12),
        Book("SNG", "Cantares", "Cnt", Testament.OLD, 8),
        Book("ISA", "Isaías", "Is", Testament.OLD, 66),
        Book("JER", "Jeremías", "Jer", Testament.OLD, 52),
        Book("LAM", "Lamentaciones", "Lm", Testament.OLD, 5),
        Book("EZK", "Ezequiel", "Ez", Testament.OLD, 48),
        Book("DAN", "Daniel", "Dn", Testament.OLD, 12),
        Book("HOS", "Oseas", "Os", Testament.OLD, 14),
        Book("JOL", "Joel", "Jl", Testament.OLD, 3),
        Book("AMO", "Amós", "Am", Testament.OLD, 9),
        Book("OBA", "Abdías", "Abd", Testament.OLD, 1),
        Book("JON", "Jonás", "Jon", Testament.OLD, 4),
        Book("MIC", "Miqueas", "Mi", Testament.OLD, 7),
        Book("NAM", "Nahúm", "Nah", Testament.OLD, 3),
        Book("HAB", "Habacuc", "Hab", Testament.OLD, 3),
        Book("ZEP", "Sofonías", "Sof", Testament.OLD, 3),
        Book("HAG", "Hageo", "Hag", Testament.OLD, 2),
        Book("ZEC", "Zacarías", "Zac", Testament.OLD, 14),
        Book("MAL", "Malaquías", "Mal", Testament.OLD, 4),
        Book("MAT", "Mateo", "Mt", Testament.NEW, 28),
        Book("MRK", "Marcos", "Mr", Testament.NEW, 16),
        Book("LUK", "Lucas", "Lc", Testament.NEW, 24),
        Book("JHN", "Juan", "Jn", Testament.NEW, 21),
        Book("ACT", "Hechos", "Hch", Testament.NEW, 28),
        Book("ROM", "Romanos", "Ro", Testament.NEW, 16),
        Book("1CO", "1 Corintios", "1 Co", Testament.NEW, 16),
        Book("2CO", "2 Corintios", "2 Co", Testament.NEW, 13),
        Book("GAL", "Gálatas", "Gá", Testament.NEW, 6),
        Book("EPH", "Efesios", "Ef", Testament.NEW, 6),
        Book("PHP", "Filipenses", "Fil", Testament.NEW, 4),
        Book("COL", "Colosenses", "Col", Testament.NEW, 4),
        Book("1TH", "1 Tesalonicenses", "1 Ts", Testament.NEW, 5),
        Book("2TH", "2 Tesalonicenses", "2 Ts", Testament.NEW, 3),
        Book("1TI", "1 Timoteo", "1 Ti", Testament.NEW, 6),
        Book("2TI", "2 Timoteo", "2 Ti", Testament.NEW, 4),
        Book("TIT", "Tito", "Tit", Testament.NEW, 3),
        Book("PHM", "Filemón", "Flm", Testament.NEW, 1),
        Book("HEB", "Hebreos", "He", Testament.NEW, 13),
        Book("JAS", "Santiago", "Stg", Testament.NEW, 5),
        Book("1PE", "1 Pedro", "1 P", Testament.NEW, 5),
        Book("2PE", "2 Pedro", "2 P", Testament.NEW, 3),
        Book("1JN", "1 Juan", "1 Jn", Testament.NEW, 5),
        Book("2JN", "2 Juan", "2 Jn", Testament.NEW, 1),
        Book("3JN", "3 Juan", "3 Jn", Testament.NEW, 1),
        Book("JUD", "Judas", "Jud", Testament.NEW, 1),
        Book("REV", "Apocalipsis", "Ap", Testament.NEW, 22),
    )

    private val byId: Map<String, Book> = books.associateBy { it.id }

    val totalChapters: Int = books.sumOf { it.chapters }

    fun book(id: String): Book? = byId[id]

    /** Todos los capítulos de la Biblia en orden canónico. */
    fun allChapters(): List<ChapterRef> =
        books.flatMap { b -> (1..b.chapters).map { ChapterRef(b.id, it) } }
}
