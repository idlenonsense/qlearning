package com.example.qlearning.core

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlin.math.sqrt
import kotlin.random.Random

class QLearning {
    private var qTable = Array(25) { DoubleArray(4) { 0.0 } }
    private val learningRate = 0.98
    private val discountFactor = 0.6
    private val matrixSize: Int = sqrt(qTable.size.toDouble()).toInt()
    val rewardCoordinates = Pair(matrixSize - 1, matrixSize - 1)
    var penaltyCoordinates: List<Pair<Int, Int>> = listOf()
    var agentCoordinates by mutableStateOf(Pair(0,0))
    var info by mutableStateOf("idleNaNsense Q-learning app v1.0")

    init {
        val penaltyCount = (matrixSize * matrixSize) / 3 + 1
        val allCoordinates = (0 until matrixSize).flatMap { x -> (0 until matrixSize).map { y -> Pair(x, y) } }
        val nonRewardAndStartCoordinates = allCoordinates.filterNot { it == rewardCoordinates || it == Pair(0, 0) }
        penaltyCoordinates = (1..penaltyCount).map { _ -> nonRewardAndStartCoordinates.random() }.distinct()
    }

    fun step(epsilon: Double) {
        val state = (agentCoordinates.first * matrixSize) + agentCoordinates.second

        val action = if (Random.nextDouble() < epsilon) {
            Random.nextInt(0, 4)
        } else {
            argmax(state)
        }

        val newState = move(agentCoordinates, action)
        if (newState == -1) {
            agentCoordinates = Pair(agentCoordinates.first, agentCoordinates.second)
            step(epsilon)
        } else {
            when (val reward = this.isReward(newState)) {
                -100.0 -> {
                    agentCoordinates = Pair(0,0)
                    qTable[state][action] = qTable[state][action] + learningRate * (reward + discountFactor * max(qTable[newState]) - qTable[state][action])
                    info = "The agent fell into the pit"
                }
                250.0 -> {
                    agentCoordinates = Pair(0,0)
                    qTable[state][action] = qTable[state][action] + learningRate * (reward + discountFactor * max(qTable[newState]) - qTable[state][action])
                    info = "The agent has reached the goal!"
                }
                else -> {
                    qTable[state][action] = qTable[state][action] + learningRate * (reward + discountFactor * max(qTable[newState]) - qTable[state][action])
                    val x = newState % matrixSize
                    val y = newState / matrixSize
                    agentCoordinates = Pair(y, x)
                    info = "Agent moved to $y, $x"
                }
            }
        }
    }

    fun episode(epsilon: Double) {
        agentCoordinates = Pair(0, 0)
        while (true) {
            val state = (agentCoordinates.first * matrixSize) + agentCoordinates.second

            val action = if (Random.nextDouble() < epsilon) {
                Random.nextInt(0, 4)
            } else {
                argmax(state)
            }

            val newState = move(agentCoordinates, action)
            if (newState == -1) {
                agentCoordinates = Pair(agentCoordinates.first, agentCoordinates.second)
            } else {
                val reward = this.isReward(newState)
                if (reward == -100.0) {
                    agentCoordinates = Pair(0,0)
                    qTable[state][action] = qTable[state][action] + learningRate * (reward + discountFactor * max(qTable[newState]) - qTable[state][action])
                    info = "Episode ended: The agent fell into the pit"
                    break
                } else if (reward == 250.0) {
                    agentCoordinates = Pair(0,0)
                    qTable[state][action] = qTable[state][action] + learningRate * (reward + discountFactor * max(qTable[newState]) - qTable[state][action])
                    info = "Episode ended: The agent has reached the goal!"
                    break
                } else {
                    qTable[state][action] = qTable[state][action] + learningRate * (reward + discountFactor * max(qTable[newState]) - qTable[state][action])
                    val x = newState % matrixSize
                    val y = newState / matrixSize
                    agentCoordinates = Pair(y, x)
                }
            }
        }
    }

    fun train(epsilon: Double) {
        for (i in 1..1000) {
            agentCoordinates = Pair(0, 0)
            while (true) {
                val state = (agentCoordinates.first * matrixSize) + agentCoordinates.second

                val action = if (Random.nextDouble() < epsilon) {
                    Random.nextInt(0, 4)
                } else {
                    argmax(state)
                }

                val newState = move(agentCoordinates, action)
                if (newState == -1) {
                    agentCoordinates = Pair(agentCoordinates.first, agentCoordinates.second)
                } else {
                    val reward = this.isReward(newState)
                    if (reward == -100.0) {
                        agentCoordinates = Pair(0,0)
                        qTable[state][action] = qTable[state][action] + learningRate * (reward + discountFactor * max(qTable[newState]) - qTable[state][action])
                        break
                    } else if (reward == 250.0) {
                        agentCoordinates = Pair(0,0)
                        qTable[state][action] = qTable[state][action] + learningRate * (reward + discountFactor * max(qTable[newState]) - qTable[state][action])
                        break
                    } else {
                        qTable[state][action] = qTable[state][action] + learningRate * (reward + discountFactor * max(qTable[newState]) - qTable[state][action])
                        val x = newState % matrixSize
                        val y = newState / matrixSize
                        agentCoordinates = Pair(y, x)
                    }
                }
            }
        }
        info = "The training is finished"
    }

    private fun argmax(state: Int): Int {
        return qTable[state].asList().indexOf(qTable[state].maxOrNull() ?: 0.0)
    }

    private fun max(array: DoubleArray): Double {
        return array.maxOrNull() ?: 0.0
    }

    private fun move(cords: Pair<Int, Int>, action: Int): Int {
        val newCords = when (action) {
            0 -> if (cords.first > 0) Pair(cords.first - 1, cords.second) else Pair(-1, -1)
            1 -> if (cords.second < 4) Pair(cords.first, cords.second + 1) else Pair(-1, -1)
            2 -> if (cords.first < 4) Pair(cords.first + 1, cords.second) else Pair(-1, -1)
            3 -> if (cords.second > 0) Pair(cords.first, cords.second - 1) else Pair(-1, -1)
            else -> Pair(-1, -1)
        }
        return if (newCords == Pair(-1, -1)) {
            -1
        } else {
            (newCords.first * matrixSize) + newCords.second
        }
    }

    private fun isReward(state: Int): Double {
        val x = state % 5
        val y = state / 5

        if (rewardCoordinates.first == y && rewardCoordinates.second == x) {
            return 250.0
        }

        if (penaltyCoordinates.any { it.first == y && it.second == x }) {
            return -100.0
        }

        return 0.0
    }
}