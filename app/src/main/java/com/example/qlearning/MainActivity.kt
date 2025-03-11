package com.example.qlearning

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Clear
import androidx.compose.material.icons.sharp.Refresh
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.qlearning.core.QLearning
import com.example.qlearning.ui.theme.JostRegular
import com.example.qlearning.ui.theme.QlearningTheme
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            QLearningApp()
        }
    }

    fun updateLocale(context: Context, language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        context.createConfigurationContext(config)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }
}

@Composable
fun QLearningApp() {
    val context = LocalContext.current
    val ql = remember { QLearning(context) }
    var language by remember { mutableStateOf("en") }

    InfoText(ql.info)

    Column(
        modifier = Modifier.fillMaxSize().padding(bottom = 52.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Grid(5, 5, ql.rewardCoordinates, ql.agentCoordinates, ql.penaltyCoordinates, ql)
        ActionButtons(ql)
        Image(painter =
            if (language == "ru") {
                painterResource(R.drawable.russian_lang)
            } else {
                painterResource(R.drawable.english_lang)
            }, contentDescription = null, modifier = Modifier.size(52.dp).clickable {
            language = if (language == "en") "ru" else "en"
            ql.updateInfo(language)
            (context as MainActivity).updateLocale(context, language)
        })
    }

    BottomButtons(ql, context)
}

@Composable
fun InfoText(
    info: String
) {
    Box(
        modifier = Modifier.fillMaxWidth().fillMaxSize().padding(top = 48.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Text(
            text = info,
            textAlign = TextAlign.Center,
            fontSize = 32.sp,
            modifier = Modifier.width(302.dp),
            fontFamily = JostRegular
        )
    }
}

@Composable
fun ActionButtons(ql: QLearning) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(bottom = 16.dp)
    ) {
        ActionButton(stringResource(R.string.button_step), Color.Gray) { ql.step(0.15) }
        ActionButton(stringResource(R.string.button_episode), Color.Blue) { ql.episode(0.5) }
        ActionButton(stringResource(R.string.button_train), Color.Red) { ql.train(0.65) }
    }
}

@Composable
fun ActionButton(text: String, color: Color, onClick: () -> Unit) {
    Button(
        modifier = Modifier.size(248.dp, 56.dp).padding(bottom = 16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        onClick = onClick
    ) {
        Text(text = text, fontSize = 18.sp)
    }
}

@Composable
fun BottomButtons(ql: QLearning, context: Context) {

    Box(
        modifier = Modifier.fillMaxWidth().fillMaxSize().padding(bottom = 48.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row {
                IconButton(Icons.Sharp.Refresh, Color.Gray) { ql.shufflePenalty() }
                IconButton(Icons.Sharp.Clear, Color.Red) { ql.removeCoordinates() }

            }
            Text(
                text = "IdleNaNsense's Q-Learning app \nversion 1.1",
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun IconButton(icon: ImageVector, tint: Color, onClick: () -> Unit) {
    Button(
        modifier = Modifier.padding(bottom = 4.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        onClick = onClick
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.scale(2F))
    }
}

@Composable
fun Grid(
    rows: Int,
    columns: Int,
    rewardCoordinates: Pair<Int, Int>,
    minerCoordinates: Pair<Int, Int>,
    penaltyCoordinates: List<Pair<Int, Int>>,
    qLearning: QLearning
) {
    Box(
        modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(bottom = 16.dp, top = 36.dp),
        contentAlignment = Alignment.Center
    ) {
        Column {
            repeat(rows) { row ->
                Row {
                    repeat(columns) { column ->
                        Cell(row, column, rewardCoordinates, penaltyCoordinates, minerCoordinates, qLearning)
                    }
                }
            }
        }
    }
}

@Composable
fun Cell(
    row: Int,
    column: Int,
    rewardCoordinates: Pair<Int, Int>,
    penaltyCoordinates: List<Pair<Int, Int>>,
    minerCoordinates: Pair<Int, Int>,
    qLearning: QLearning
) {
    val isReward = Pair(row, column) == rewardCoordinates
    val isPenalty = penaltyCoordinates.contains(Pair(row, column))
    val imageResource = if (isReward) R.drawable.diamond_cell else if (isPenalty) R.drawable.hole_cell else R.drawable.empty_cell

    Box {
        Image(
            painter = painterResource(id = imageResource),
            contentDescription = "Cell $row, $column",
            modifier = Modifier.size(50.dp).clickable {
                if (qLearning.penaltyCoordinates.contains(Pair(row, column))) {
                    qLearning.penaltyCoordinates.remove(Pair(row, column))
                } else {
                    qLearning.penaltyCoordinates.add(Pair(row, column))
                }
            }
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

@Preview(showBackground = true, device = "id:Nexus 7")
@Composable
fun AppPreview() {
    QlearningTheme {
        QLearningApp()
    }
}