package com.example.cost_of_electricity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cost_of_electricity.ui.theme.CostofelectricityTheme
import com.example.cost_of_electricity.ui.theme.MediumBlue
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CostofelectricityTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CostOfElectricity(
                        modifier = Modifier
                            .padding(innerPadding)
                            .background(color = MaterialTheme.colorScheme.background)
                    )
                }
            }
        }
    }
}

// main composable UI for electricity price calculation
@Composable
fun CostOfElectricity( modifier: Modifier = Modifier) {

    // state variables
    var input by remember {mutableStateOf("")}
    var sliderValue by remember { mutableStateOf(0.05f) } // initial value 0,05 e / kWh
    var vat10Selected by remember { mutableStateOf(true) } // initially chosen as that's current vat

    // price calculations using BigDecimal class for  accuracy and rounding when dealing with money
    // (avoids float rounding errors)
    val consumption = input.toBigDecimalOrNull() ?: BigDecimal.ZERO // if input is invalid, set to zero
    val cost = consumption * sliderValue.toBigDecimal() // consumption multiplied by price per kWh
    val costWithVAT = if (vat10Selected) cost * BigDecimal("1.10") else cost * BigDecimal("1.24") // calculate with VAT's
    val roundedCost = costWithVAT.setScale(0, RoundingMode.CEILING) // round up to nearest whole euro (no cents)

    // basic layout: column and row wrapping checkbox and its label
    // Text, OutlinedTextField, Text for price, Slider, Checkbox and Text label, Surface for result display
    Column(modifier = modifier
        .padding(12.dp)
    )
    {
        Text(
            text = "Cost of electricity",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(bottom = 14.dp)
        )
        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            label = {Text(text = "Consumption in kWh")},
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
                cursorColor = MaterialTheme.colorScheme.tertiary,
                focusedTextColor = MaterialTheme.colorScheme.primary,
                unfocusedTextColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
                .fillMaxWidth(),
        )
        Text(
            text = "Price/kWh ${"%.2f".format(sliderValue)} €",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
            )
        // this is default slider style, thumb is line instead of circle that could be done with drawable element
        Slider(
            value = sliderValue,
            // continuous slider value needs to be rounded to 2 decimal without showing steps in the UI
            // first converts the slider value to cents, rounds to nearest integer, then divides by 100 to get euros again
            onValueChange = { sliderValue = (it * 100).roundToInt() / 100f }, // 1 cent steps
            valueRange = -0.05f..1.0f,
            // if steps parameter were used: ((maxValue − minValue) / stepSize) - 1, eli ((1.00 − (−0.05)) / 0,01 ) - 1 = 104
            // steps = 104 -> 1 cent step for range, but these tick marks are visible on UI which is not wanted here
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.tertiary
            )
        )
        Row (
            verticalAlignment = Alignment.CenterVertically
        ){
            Checkbox(
                checked = vat10Selected,
                onCheckedChange = { vat10Selected = it },
                colors = CheckboxDefaults.colors(
                    checkedColor = MediumBlue
                )
                )
            Text(
                text = "VAT 10% (otherwise calculated with VAT 24%)",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        // surface is "card" like component with tonal elevation
        Surface(
            tonalElevation = 2.dp,
            shadowElevation = 4.dp,
            modifier = Modifier
                .align(Alignment.CenterHorizontally) // horizontally inside column
                .padding(all = 16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Text(
                text = "$roundedCost €",
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(10.dp)
            )

        }

    }
}

@Preview(showBackground = true)
@Composable
fun CostOfElectricityPreview() {
    CostofelectricityTheme {
        CostOfElectricity()
    }
}