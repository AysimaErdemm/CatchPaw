package com.aysimaerdem.catchpaw.domain.engine

import com.aysimaerdem.catchpaw.domain.model.GameConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GameEngineTest {

    private lateinit var engine: GameEngine

    @Before
    fun setup() {
        engine = GameEngine()
    }

    @Test
    fun `first catch gives 1 point`() {
        val points = engine.onMouseCaught(1000L)
        assertEquals(1, points)
        assertEquals(1, engine.score)
    }

    @Test
    fun `fast consecutive catches build combo`() {
        engine.onMouseCaught(1000L)
        val points = engine.onMouseCaught(1500L) // within 1200ms window
        assertEquals(2, points)
        assertEquals(2, engine.combo)
        assertEquals(3, engine.score) // 1 + 2
    }

    @Test
    fun `slow catch resets combo`() {
        engine.onMouseCaught(1000L)
        engine.onMouseCaught(1500L) // combo = 2
        val points = engine.onMouseCaught(5000L) // too slow, combo resets
        assertEquals(1, points)
        assertEquals(1, engine.combo)
    }

    @Test
    fun `missed mouse resets combo and increments missed count`() {
        engine.onMouseCaught(1000L)
        engine.onMouseCaught(1500L) // combo = 2
        engine.onMouseMissed()
        assertEquals(0, engine.combo)
        assertEquals(1, engine.missedCount)
    }

    @Test
    fun `spawn interval starts easy and gets harder with score`() {
        val intervalAtZero = engine.calculateSpawnInterval()
        assertEquals(GameConfig.EASY_SPAWN_INTERVAL_MS, intervalAtZero)

        // Score a few points (no combo — large time gaps)
        repeat(10) { engine.onMouseCaught(it * 5000L) }
        val intervalMid = engine.calculateSpawnInterval()

        // Score up to cap
        repeat(25) { engine.onMouseCaught(100000L + it * 5000L) }
        val intervalHard = engine.calculateSpawnInterval()

        assertTrue(intervalAtZero > intervalMid)
        assertTrue(intervalMid > intervalHard)
        assertEquals(GameConfig.HARD_SPAWN_INTERVAL_MS, intervalHard)
    }

    @Test
    fun `mouse lifetime starts long and gets shorter with score`() {
        val lifetimeEasy = engine.calculateMouseLifetime()
        assertEquals(GameConfig.EASY_MOUSE_LIFETIME_MS, lifetimeEasy)

        // Score enough to hit cap (no combo)
        repeat(35) { engine.onMouseCaught(it * 5000L) }
        val lifetimeHard = engine.calculateMouseLifetime()

        assertTrue(lifetimeEasy > lifetimeHard)
        assertEquals(GameConfig.HARD_MOUSE_LIFETIME_MS, lifetimeHard)
    }

    @Test
    fun `max mice starts at 1 and increases with score`() {
        val maxEasy = engine.calculateMaxMice()
        assertEquals(GameConfig.EASY_MAX_MICE, maxEasy)
        assertEquals(1, maxEasy)

        repeat(35) { engine.onMouseCaught(it * 5000L) }
        val maxHard = engine.calculateMaxMice()

        assertTrue(maxHard > maxEasy)
        assertTrue(maxHard <= GameConfig.HARD_MAX_MICE)
    }

    @Test
    fun `difficulty progress is clamped between 0 and 1`() {
        assertEquals(0f, engine.difficultyProgress(), 0.001f)

        repeat(50) { engine.onMouseCaught(it * 5000L) }
        assertEquals(1f, engine.difficultyProgress(), 0.001f)
    }

    @Test
    fun `game result detects new best score`() {
        engine.onMouseCaught(1000L)
        engine.onMouseCaught(1500L)
        engine.onMouseCaught(2000L)

        val result = engine.buildGameResult(bestScore = 0)
        assertTrue(result.isNewBest)
        assertEquals(engine.score, result.bestScore)
    }

    @Test
    fun `game result does not flag new best when score is lower`() {
        engine.onMouseCaught(1000L) // 1 point

        val result = engine.buildGameResult(bestScore = 100)
        assertFalse(result.isNewBest)
        assertEquals(100, result.bestScore)
    }

    @Test
    fun `reset clears all state`() {
        engine.onMouseCaught(1000L)
        engine.onMouseCaught(1500L)
        engine.onMouseMissed()
        engine.reset()

        assertEquals(0, engine.score)
        assertEquals(0, engine.missedCount)
        assertEquals(0, engine.combo)
        assertEquals(0L, engine.lastCatchTime)
    }

    @Test
    fun `mouse created within container bounds`() {
        val width = 500f
        val height = 800f
        val mouse = engine.createMouse(width, height)

        assertTrue(mouse.x >= GameConfig.MOUSE_SIZE / 2)
        assertTrue(mouse.x <= width - GameConfig.MOUSE_SIZE * 1.5f)
        assertTrue(mouse.y >= GameConfig.TOP_BAR_HEIGHT)
        assertTrue(mouse.y <= height - GameConfig.MOUSE_SIZE * 2)
    }

    @Test
    fun `mouse ids are sequential`() {
        val m1 = engine.createMouse(500f, 800f)
        val m2 = engine.createMouse(500f, 800f)
        assertEquals(m1.id + 1, m2.id)
    }

    @Test
    fun `triple combo gives correct points`() {
        engine.onMouseCaught(1000L)  // 1 point, combo=1
        engine.onMouseCaught(1500L)  // 2 points, combo=2
        val points = engine.onMouseCaught(2000L) // 3 points, combo=3
        assertEquals(3, points)
        assertEquals(6, engine.score) // 1+2+3
    }
}
