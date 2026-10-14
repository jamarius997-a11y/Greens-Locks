package com.greenslocks.iptv

/** Provider guide titles are hints, not verified fixtures or scores. */
enum class Sport(val label: String, private val words: Regex) {
    FOOTBALL("Football", Regex("(?i)\\b(nfl|ncaaf|football|super bowl|gridiron)\\b")),
    BASKETBALL("Basketball", Regex("(?i)\\b(nba|wnba|ncaab|basketball)\\b")),
    BASEBALL("Baseball", Regex("(?i)\\b(mlb|baseball|softball)\\b")),
    HOCKEY("Hockey", Regex("(?i)\\b(nhl|hockey)\\b")),
    SOCCER("Soccer", Regex("(?i)\\b(soccer|premier league|champions league|la liga|bundesliga|serie a|mls|fifa|uefa)\\b")),
    COMBAT("Combat", Regex("(?i)\\b(ufc|mma|boxing|wrestling|wwe)\\b")),
    RACING("Racing", Regex("(?i)\\b(f1|formula 1|formula one|nascar|indycar|motogp|racing)\\b")),
    GOLF_TENNIS("Golf & tennis", Regex("(?i)\\b(golf|pga|lpga|tennis|atp|wta|wimbledon)\\b"));

    fun matches(text: String): Boolean = words.containsMatchIn(text)
}

private val sportsNetwork = Regex("(?i)\\b(espn\\d*|fox sports|fs[12]|sky sports|tnt sports|bt sport|bein|dazn|tsn\\d*|sportsnet|eurosport\\d*|sport|sports)\\b")
private val sportsEvent = Regex("(?i)\\b(olympics?|athletics|rugby|cricket|volleyball|cycling|sports?|game day)\\b")
private val matchup = Regex("(?i)\\b(?:vs\\.?|versus)\\s")

fun isSportsChannel(channel: Entry, categoryName: String): Boolean =
    channel.kind == Kind.LIVE && (sportsNetwork.containsMatchIn(channel.title + " " + categoryName) ||
        Sport.entries.any { it.matches(channel.title + " " + categoryName) })

fun isSportsProgramme(title: String): Boolean =
    Sport.entries.any { it.matches(title) } || sportsEvent.containsMatchIn(title) || matchup.containsMatchIn(title)

data class SportsEvent(val channel: Entry, val programme: Programme) {
    val key: String get() = "${channel.id}:${programme.start}:${programme.title}"
}

/** Keep channels separate: the same event can have different languages and stream qualities. */
fun sportsEvents(
    channels: List<Entry>,
    programmes: Map<String, List<Programme>>,
    sportsChannelIds: Set<String>,
    now: Long,
): List<SportsEvent> = channels.filter { it.kind == Kind.LIVE }.flatMap { ch ->
    programmes[ch.id].orEmpty().filter { p ->
        p.stop > p.start && p.stop > now && p.start < now + 9 * 3_600_000L &&
            (ch.id in sportsChannelIds || isSportsProgramme(p.title))
    }.map { SportsEvent(ch, it) }
}.distinctBy { it.key }.sortedWith(compareBy<SportsEvent> { it.programme.start > now }
    .thenBy { it.programme.start }.thenBy { it.channel.num }.thenBy { it.channel.title })
