package com.planlectura.biblia90.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.planlectura.biblia90.MainUiState
import com.planlectura.biblia90.domain.BookProgress
import com.planlectura.biblia90.domain.Testament
import com.planlectura.biblia90.ui.mediumLabel
import com.planlectura.biblia90.ui.percentLabel

@Composable
fun ProgressScreen(state: MainUiState.Ready) {
    val s = state.snapshot
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 64.dp),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        fullWidth {
            Card {
                Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Tu avance", style = MaterialTheme.typography.titleMedium)
                    Text(s.fraction.percentLabel(), style = MaterialTheme.typography.displaySmall)
                    LinearProgressIndicator(
                        progress = { s.fraction },
                        modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)),
                    )
                    Text("${s.chaptersRead} de ${s.totalChapters} capítulos")
                    val completedDays = s.days.count { it.chaptersRead == it.totalChapters }
                    Text("$completedDays de ${state.plan.totalDays} días completos")
                    Text("Fecha prevista: ${s.plannedFinish.mediumLabel()}")
                    s.projectedFinish?.let { Text("A tu ritmo actual: ${it.mediumLabel()}") }
                }
            }
        }
        bookSection("Antiguo Testamento", s.books.filter { it.book.testament == Testament.OLD })
        bookSection("Nuevo Testamento", s.books.filter { it.book.testament == Testament.NEW })
    }
}

private fun LazyGridScope.fullWidth(content: @Composable () -> Unit) {
    item(span = { GridItemSpan(maxLineSpan) }) { content() }
}

private fun LazyGridScope.bookSection(title: String, books: List<BookProgress>) {
    fullWidth {
        val done = books.count { it.chaptersRead == it.book.chapters }
        Text(
            "$title · $done/${books.size} libros",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 16.dp),
        )
    }
    items(books, key = { it.book.id }) { BookCell(it) }
}

/** Cada libro "se ilumina" a medida que se leen sus capítulos. */
@Composable
private fun BookCell(progress: BookProgress) {
    val colors = MaterialTheme.colorScheme
    val complete = progress.chaptersRead == progress.book.chapters
    val background = lerp(colors.surfaceVariant, colors.primary, progress.fraction)
    val content = if (progress.fraction > 0.5f) colors.onPrimary else colors.onSurfaceVariant
    Column(
        Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(background)
            .padding(4.dp)
            .semantics {
                contentDescription = "${progress.book.name}: ${progress.chaptersRead} de ${progress.book.chapters} capítulos"
            },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(progress.book.abbreviation, style = MaterialTheme.typography.titleSmall, color = content)
        Text(
            if (complete) "✓" else progress.fraction.percentLabel(),
            style = MaterialTheme.typography.labelSmall,
            color = content,
            textAlign = TextAlign.Center,
        )
    }
}
