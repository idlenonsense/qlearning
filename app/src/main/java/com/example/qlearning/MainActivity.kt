package com.example.qlearning

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.qlearning.core.QLearning
import com.example.qlearning.ui.theme.JostRegular
import com.example.qlearning.ui.theme.QlearningTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            QLearningApp()
        }
    }
}

@Composable
fun QLearningApp() {
    val ql = remember { QLearning() }

    Column(
        modifier = Modifier.fillMaxSize().padding(top = 132.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
            Text(
                text = ql.info,
                textAlign = TextAlign.Center,
                fontSize = 32.sp,
                modifier = Modifier.size(284.dp, 92.dp),
                fontFamily = JostRegular
            )


        Grid(5, 5, ql.rewardCoordinates, ql.agentCoordinates, ql.penaltyCoordinates)

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            modifier = Modifier
                .size(248.dp, 56.dp)
                .padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
            onClick = {
            ql.step(0.15)
        }) {
            Text(
                text = "Шаг",
                fontSize = 18.sp
            )
        }

        Button(
            modifier = Modifier
                .size(248.dp, 56.dp)
                .padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
            onClick = {
            ql.episode(0.5)
        }) {
            Text(
                text = "Эпизод",
                fontSize = 18.sp
            )
        }

        Button(
            modifier = Modifier
                .size(248.dp, 56.dp)
                .padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            onClick = {
            ql.train(0.65)
        }) {
            Text(
                text = "Тренировка",
                fontSize = 18.sp
            )
        }
    }
}

@Composable
fun Grid(
    rows: Int,
    columns: Int,
    rewardCoordinates: Pair<Int, Int>,
    minerCoordinates: Pair<Int, Int>,
    penaltyCoordinates: List<Pair<Int, Int>>
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(start = 82.dp, top = 16.dp)
    ) {
        Column {
            repeat(rows) { row ->
                Row {
                    repeat(columns) { column ->
                        val isReward = Pair(row, column) == rewardCoordinates
                        val isPenalty = penaltyCoordinates.contains(Pair(row, column))
                        val imageResource = if (isReward) R.drawable.diamond_cell else if (isPenalty) R.drawable.hole_cell else R.drawable.empty_cell

                        Box {
                            Image(
                                painter = painterResource(id = imageResource),
                                contentDescription = "Cell $row, $column",
                                modifier = Modifier.size(50.dp)
                            )
                            if (Pair(row, column) == minerCoordinates) {
                                Image(
                                    painter = painterResource(id = R.drawable.miner),
                                    contentDescription = "Miner",
                                    modifier = Modifier.size(50.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    QlearningTheme {
        QLearningApp()
    }
}