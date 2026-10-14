package com.greenslocks.iptv

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun SportsScreen(
    channels: List<Entry>,
    events: List<SportsEvent>,
    programmes: Map<String, List<Programme>>,
    now: Long,
    loading: Boolean,
    status: String?,
    query: String,
    onQuery: (String) -> Unit,
    sport: Sport?,
    onSport: (Sport?) -> Unit,
    view: Int,
    onView: (Int) -> Unit,
    favoritesOnly: Boolean,
    onFavoritesOnly: () -> Unit,
    favoriteKeys: Set<String>,
    reminderKeys: Set<String>,
    nameOf: (Entry) -> String,
    listState: LazyListState,
    selectedKey: String?,
    onSelected: (String) -> Unit,
    onPlay: (Entry) -> Unit,
    onRemind: (Entry, Programme) -> Unit,
    onFavorite: (Entry) -> Unit,
    onOptions: (Entry) -> Unit,
    onRefresh: () -> Unit,
    onGuide: () -> Unit,
    onMultiview: () -> Unit,
) {
    fun matches(ch: Entry, title: String): Boolean =
        (!favoritesOnly || ch.key() in favoriteKeys) &&
            (query.isBlank() || (nameOf(ch) + " " + title).contains(query.trim(), ignoreCase = true)) &&
            (sport == null || sport.matches(title) || sport.matches(nameOf(ch)))

    val shownEvents = events.filter { matches(it.channel, it.programme.title) &&
        if (view == 0) it.programme.start <= now else it.programme.start > now }
    val shownChannels = channels.filter { ch ->
        matches(ch, nowNext(programmes[ch.id].orEmpty(), now).first?.title.orEmpty())
    }
    val keys = if (view == 2) shownChannels.map { it.key() } else shownEvents.map { it.key }
    val targetKey = selectedKey?.takeIf { it in keys } ?: keys.firstOrNull()
    val first = remember { FocusRequester() }
    // Restore the last activated card after fullscreen playback; scrolling is owned by App.
    LaunchedEffect(view, sport, favoritesOnly, keys.isEmpty()) {
        if (query.isNotBlank() && selectedKey == null) return@LaunchedEffect
        val index = keys.indexOf(targetKey)
        if (index >= 0) listState.scrollToItem(index)
        repeat(15) {
            delay(80)
            if (runCatching { first.requestFocus() }.isSuccess) return@LaunchedEffect
        }
    }
    Column(Modifier.fillMaxSize().background(Brush.verticalGradient(
        listOf(accentWash(0.16f), Palette.bg, Palette.bg))).padding(horizontal = 24.dp, vertical = 16.dp)) {
        Text("Sports Hub", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Live TV and upcoming programs from your provider’s guide", color = Palette.muted,
            style = MaterialTheme.typography.bodySmall)
        LazyRow(Modifier.padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 4.dp)) {
            item { ActionButton("Refresh", primary = false, onClick = onRefresh) }
            item { ActionButton("Guide", primary = false, onClick = onGuide) }
            item { ActionButton("Multiview", primary = false, onClick = onMultiview) }
            item { Chip(if (favoritesOnly) "Favorites only" else "All channels", favoritesOnly, onClick = onFavoritesOnly) }
        }
        TvTextField(query, onQuery, label = "Find a team, program or channel", singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp))
        LazyRow(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 4.dp)) {
            item { Chip("All sports", sport == null) { onSport(null) } }
            items(Sport.entries) { s -> Chip(s.label, sport == s) { onSport(s) } }
        }
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(vertical = 4.dp)) {
            items(listOf(0 to "On now", 1 to "Coming up", 2 to "Sports channels")) { (index, label) ->
                Chip(label, view == index) { onView(index) }
            }
        }
        if (loading || status != null) Text(status ?: "Loading sports guide…", color = Palette.muted,
            style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(vertical = 4.dp))
        if (keys.isEmpty()) {
            Text(if (loading) "Finding your sports programming…" else when (view) {
                0 -> "No matching programs on now. Try Sports channels or clear your filters."
                1 -> "No matching upcoming programs in the next 9 hours. Try Sports channels."
                else -> "No matching sports channels. Try All sports or turn off Favorites only."
            }, color = Palette.muted, modifier = Modifier.padding(top = 16.dp))
        }
        LazyColumn(Modifier.weight(1f).padding(top = 8.dp), state = listState,
            verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(bottom = 20.dp)) {
            if (view == 2) items(shownChannels, key = { it.key() }) { ch ->
                val current = nowNext(programmes[ch.id].orEmpty(), now).first
                SportsCard(ch, current, nameOf(ch), now, favoriteKeys, reminderKeys,
                    if (ch.key() == targetKey) Modifier.focusRequester(first) else Modifier,
                    onOpen = { onSelected(ch.key()); onPlay(ch) }, onFavorite = { onFavorite(ch) }, onOptions = { onOptions(ch) })
            } else items(shownEvents, key = { it.key }) { event ->
                SportsCard(event.channel, event.programme, nameOf(event.channel), now, favoriteKeys, reminderKeys,
                    if (event.key == targetKey) Modifier.focusRequester(first) else Modifier,
                    onOpen = {
                        onSelected(event.key)
                        if (event.programme.start <= now) onPlay(event.channel) else onRemind(event.channel, event.programme)
                    }, onFavorite = { onFavorite(event.channel) }, onOptions = { onOptions(event.channel) })
            }
        }
        Text("Hold OK on a card for multiview and channel options. Listings may include talk and replays. Reminders require Vance TV to stay open.",
            color = Palette.muted, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun SportsCard(
    channel: Entry, programme: Programme?, name: String, now: Long,
    favorites: Set<String>, reminders: Set<String>, modifier: Modifier,
    onOpen: () -> Unit, onFavorite: () -> Unit, onOptions: () -> Unit,
) {
    val airing = programme != null && programme.start <= now && now < programme.stop
    val upcoming = programme != null && programme.start > now
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        FocusCard(modifier.weight(1f), RoundedCornerShape(12.dp), 1f, 2.dp, onClick = onOpen, onLongClick = onOptions) { focused ->
            Row(Modifier.fillMaxWidth().background(if (focused) Palette.accent.copy(alpha = 0.25f) else Palette.surface)
                .padding(14.dp), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Artwork(channel.icon, name, Modifier.size(72.dp, 48.dp), androidx.compose.ui.layout.ContentScale.Fit)
                Column(Modifier.weight(1f)) {
                    Text(programme?.title ?: name, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Text(name + if (channel.num > 0) " · Ch ${channel.num}" else "", color = Palette.muted,
                        style = MaterialTheme.typography.bodySmall)
                    Text(when {
                        airing -> "ON NOW · Until ${clockText(programme!!.stop)} · Watch"
                        upcoming -> "${clockText(programme!!.start)} – ${clockText(programme.stop)} · " +
                            if ("${channel.id}:${programme.start}" in reminders) "Remove reminder" else "Set reminder"
                        else -> "Guide unavailable · Watch channel"
                    }, color = Palette.accent, style = MaterialTheme.typography.bodySmall)
                    if (airing) ProgressLine(((now - programme!!.start).toFloat() /
                        (programme.stop - programme.start)).coerceIn(0f, 1f), Modifier.fillMaxWidth().padding(top = 5.dp))
                }
            }
        }
        ActionButton(if (channel.key() in favorites) "★" else "☆", primary = false, onClick = onFavorite)
    }
}
