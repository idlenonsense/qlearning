package com.example.qlearning.core

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.qlearning.R
import kotlin.random.Random

class QLearning(private val context: Context) {
    private val qTable = Array(25) { DoubleArray(4) { 0.0 } }
    private val learningRate = 0.98
    private val discountFactor = 0.6
    val rewardCoordinates = Pair(4, 4)
    var agentCoordinates by mutableStateOf(Pair(0, 0))
    var info by mutableStateOf(context.getString(R.string.info_idle))
    private val allCoordinates = (0 until 5).flatMap { x -> (0 until 5).map { y -> Pair(x, y) } }
    private var nonRewardAndAgentCoordinates: SnapshotStateList<Pair<Int, Int>> = mutableStateListOf()
    var penaltyCoordinates: SnapshotStateList<Pair<Int, Int>> = mutableStateListOf()

    fun shufflePenalty() {
        nonRewardAndAgentCoordinates.clear()
        nonRewardAndAgentCoordinates.addAll(allCoordinates.filterNot { it == rewardCoordinates || it == agentCoordinates })

        penaltyCoordinates.clear()
        penaltyCoordinates.addAll((1..8).map { _ -> nonRewardAndAgentCoordinates.random() }.distinct())

        info = context.getString(R.string.info_obstacles_placed)
    }

    fun removeCoordinates() {
        penaltyCoordinates.clear()
        info = context.getString(R.string.info_obstacles_cleared)
    }

    fun updateInfo(language: String) {
        if (language == "ru") {
            info = context.getString(R.string.switch_language_ru)
        } else {
            info = context.getString(R.string.switch_language_en)
        }
    }

    fun step(epsilon: Double) {
        performStep(epsilon)
    }

    fun episode(epsilon: Double) {
        agentCoordinates = Pair(0, 0)
        while (true) {
            if (!performStep(epsilon)) break
        }
    }

    fun train(epsilon: Double) {
        repeat(1000) {
            agentCoordinates = Pair(0, 0)
            while (true) {
                if (!performStep(epsilon)) break
            }
        }
        info = context.getString(R.string.info_training_finished)
    }

    private fun performStep(epsilon: Double): Boolean {
        val state = (agentCoordinates.first * 5) + agentCoordinates.second
        val action = if (Random.nextDouble() < epsilon) {
            Random.nextInt(0, 4)
        } else {
            argmax(state)
        }

        val newState = move(agentCoordinates, action)
        if (newState == -1) {
            performStep(epsilon)
        } else {
            val reward = isReward(newState)
            qTable[state][action] = qTable[state][action] + learningRate * (reward + discountFactor * max(qTable[newState]) - qTable[state][action])

            when (reward) {
                -100.0 -> {
                    agentCoordinates = Pair(0, 0)
                    info = context.getString(R.string.info_agent_fell)
                    return false
                }
                250.0 -> {
                    agentCoordinates = Pair(0, 0)
                    info = context.getString(R.string.info_agent_reached_goal)
                    return false
                }
                else -> {
                    val x = newState % 5
                    val y = newState / 5
                    agentCoordinates = Pair(y, x)
                    info = context.getString(R.string.info_agent_moved, y, x)
                }
            }
        }
        return true
    }

    private fun argmax(state: Int): Int {
        return qTable[state].asList().indexOf(qTable[state].maxOrNull() ?: 0.0)
    }

    private fun max(array: DoubleArray): Double {
        return array.maxOrNull() ?: 0.0
    }

    private fun move(cords: Pair<Int, Int>, action: Int): Int {
        val newCords = when (action) {
            0 -> cords.copy(first = cords.first - 1).takeIf { it.first >= 0 }
            1 -> cords.copy(second = cords.second + 1).takeIf { it.second <= 4 }
            2 -> cords.copy(first = cords.first + 1).takeIf { it.first <= 4 }
            3 -> cords.copy(second = cords.second - 1).takeIf { it.second >= 0 }
            else -> null
        }
        return newCords?.let { (newCords.first * 5) + newCords.second } ?: -1
    }

    private fun isReward(state: Int): Double {
        val x = state % 5
        val y = state / 5

        return when {
            rewardCoordinates.first == y && rewardCoordinates.second == x -> 250.0
            penaltyCoordinates.any { it.first == y && it.second == x } -> -100.0
            else -> 0.0
        }
    }
}