package com.greenslocks.iptv

import org.junit.Assert.*
import org.junit.Test

class SportsTest {
    private val now = 1_700_000_000_000L
    private fun channel(id: String, name: String = "ESPN") = Entry(Kind.LIVE, id, name)

    @Test fun detectsSportsNetworksAndCategories() {
        assertTrue(isSportsChannel(channel("1", "US | ESPN2 HD"), "USA"))
        assertTrue(isSportsChannel(channel("2", "Arena 4K"), "Live Sports"))
        assertTrue(isSportsChannel(channel("3", "NFL Network"), "USA"))
        assertFalse(isSportsChannel(channel("4", "CNN"), "News"))
        assertFalse(isSportsChannel(Entry(Kind.MOVIE, "5", "Sports movie"), "Sports"))
    }

    @Test fun sportFiltersUseWordBoundaries() {
        assertTrue(Sport.BASKETBALL.matches("NBA: Boston vs. New York"))
        assertTrue(Sport.SOCCER.matches("UEFA Champions League"))
        assertTrue(Sport.RACING.matches("Formula One qualifying"))
        assertFalse(Sport.RACING.matches("F100 documentary"))
        assertFalse(Sport.FOOTBALL.matches("NFLIX movie night"))
    }

    @Test fun detectsMatchupsWithoutClaimingScores() {
        assertTrue(isSportsProgramme("Boston vs. New York"))
        assertTrue(isSportsProgramme("Cricket highlights"))
        assertFalse(isSportsProgramme("Evening news"))
    }

    @Test fun listsCurrentBeforeUpcomingAndDropsPastOrInvalidProgrammes() {
        val ch = channel("1")
        val live = Programme("NBA game", now - 1000, now + 1000)
        val next = Programme("NBA preview", now + 2000, now + 3000)
        val past = Programme("NBA earlier", now - 3000, now)
        val invalid = Programme("NBA invalid", now + 4000, now + 3500)
        val far = Programme("NBA tomorrow", now + 10 * 3_600_000, now + 11 * 3_600_000)
        assertEquals(listOf(live, next), sportsEvents(listOf(ch), mapOf(ch.id to listOf(next, past, invalid, far, live)), setOf(ch.id), now).map { it.programme })
    }

    @Test fun detectsSportsOnGeneralChannelsAndKeepsSportsTalkOnNetworks() {
        val general = channel("1", "ABC")
        val sports = channel("2")
        val guide = mapOf("1" to listOf(Programme("NBA Finals", now, now + 1000), Programme("Evening news", now + 1000, now + 2000)),
            "2" to listOf(Programme("Studio discussion", now, now + 1000)))
        assertEquals(listOf("NBA Finals", "Studio discussion"), sportsEvents(listOf(general, sports), guide, setOf("2"), now).map { it.programme.title })
    }

    @Test fun neverIncludesChannelsOutsideTheAllowedInput() {
        val guide = mapOf("locked" to listOf(Programme("NFL game", now, now + 1000)))
        assertTrue(sportsEvents(listOf(channel("visible")), guide, setOf("locked"), now).isEmpty())
    }

    @Test fun deduplicatesGuideRowsButPreservesStreamAlternatives() {
        val p = Programme("NFL game", now, now + 1000)
        val channels = listOf(channel("1", "ESPN HD"), channel("2", "ESPN 4K"))
        val result = sportsEvents(channels, mapOf("1" to listOf(p, p), "2" to listOf(p)), setOf("1", "2"), now)
        assertEquals(2, result.size)
        assertEquals(2, result.map { it.key }.toSet().size)
    }

    @Test fun missingGuideProducesNoInventedEvents() {
        assertTrue(sportsEvents(listOf(channel("1")), emptyMap(), setOf("1"), now).isEmpty())
    }
}
