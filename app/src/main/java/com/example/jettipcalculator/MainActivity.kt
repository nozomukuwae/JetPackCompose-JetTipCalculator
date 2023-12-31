package com.example.jettipcalculator

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jettipcalculator.components.InputField
import com.example.jettipcalculator.ui.theme.JetTipCalculatorTheme
import com.example.jettipcalculator.widgets.RoundIconButton
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TipCalculatorContainer {
                TipCalculatorContent()
            }
        }
    }
}

@Composable
fun TipCalculatorContainer(content: @Composable () -> Unit) {
    JetTipCalculatorTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            content()
        }
    }
}

@Composable
fun TipCalculatorContent() {
    var amountPerPerson by remember {
        mutableStateOf(0.0)
    }

    Column(
        verticalArrangement = Arrangement.Top
    ) {
        TopHeader(amountPerPerson = amountPerPerson)
        BillForm() {
            amountPerPerson = it
            Log.d("", it.toString())
        }
    }
}

@Composable
fun TopHeader(amountPerPerson: Double) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(12.dp)
            .clip(RoundedCornerShape(12.dp)),
        color = Color(0xFFE9D7F7)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Total Per Person",
                style = MaterialTheme.typography.h5
            )
            Text(
                text = "$${String.format("%.2f", amountPerPerson)}",
                fontWeight = FontWeight.ExtraBold,
                style = MaterialTheme.typography.h4
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BillForm(
    modifier: Modifier = Modifier,
    onValueChange: (Double) -> Unit = {}
) {
    val totalBillTextState = remember {
        mutableStateOf("")
    }
    val totalBillParsed = totalBillTextState.value.toDoubleOrNull()
    val totalBill = ((totalBillParsed ?: 0.0) * 100.0).roundToInt().toDouble() / 100.0
    var peopleCount by remember {
        mutableStateOf(1)
    }
    var sliderPosition by remember {
        mutableStateOf(0.15f)
    }
    val tipPercentage = remember(key1 = sliderPosition) {
        (sliderPosition * 100).roundToInt()
    }
    val tipAmount = remember(key1 = totalBill, key2 = tipPercentage) {
        (totalBill * tipPercentage.toDouble()).roundToInt().toDouble() / 100.0
    }
    val keyboardController = LocalSoftwareKeyboardController.current

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(width = 1.dp, color = Color.LightGray)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            InputField(
                modifier = Modifier.fillMaxWidth(),
                valueState = totalBillTextState,
                labelText = "Enter Bill",
                keyboardActions = KeyboardActions {
                    if (totalBillParsed == null) return@KeyboardActions

                    onValueChange(amountPerPerson(totalBill, tipAmount, peopleCount))
                    keyboardController?.hide()
                }
            )

            if (totalBillParsed == null) {
                Box() {}
            } else {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Split",
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.width(120.dp))
                    Row(
                        modifier = Modifier.padding(horizontal = 3.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RoundIconButton(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "minus",
                            onClick = {
                                if (peopleCount > 1) {
                                    peopleCount--
                                }
                                onValueChange(amountPerPerson(totalBill, tipAmount, peopleCount))
                            }
                        )
                        Text(
                            text = peopleCount.toString(),
                            modifier = Modifier.padding(9.dp)
                        )
                        RoundIconButton(
                            imageVector = Icons.Default.Add,
                            contentDescription = "plus",
                            onClick = {
                                peopleCount++
                                onValueChange(amountPerPerson(totalBill, tipAmount, peopleCount))
                            }
                        )
                    }
                }

                Row(
                    modifier = Modifier.padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Tip")
                    Spacer(modifier = Modifier.width(200.dp))
                    Text(text = "$${String.format("%.2f", tipAmount)}")
                }

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "$tipPercentage%")
                    Spacer(modifier = Modifier.height(14.dp))
                    Slider(
                        value = sliderPosition,
                        onValueChange = {
                            sliderPosition = it
                            onValueChange(amountPerPerson(totalBill, tipAmount, peopleCount))
                        },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

fun amountPerPerson(totalBill: Double, tipAmount: Double, peopleCount: Int): Double {
    return ((totalBill + tipAmount) / peopleCount.toDouble() * 100.0)
        .roundToInt().toDouble() / 100.0
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TipCalculatorContainer {
        TipCalculatorContent()
    }
}
