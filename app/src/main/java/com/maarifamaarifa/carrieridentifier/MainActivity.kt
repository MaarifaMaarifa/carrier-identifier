package com.maarifamaarifa.carrieridentifier

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.maarifamaarifa.carrieridentifier.ui.theme.CarrierIdentifierTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CarrierIdentifierTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    View(modifier = Modifier)
                }
            }
        }
    }
}

@Composable
fun View(modifier: Modifier ) {
    Column (Modifier.padding(10.dp)){
        ApplicationBanner(modifier = modifier)
        Spacer(modifier = modifier.height(20.dp))
        NumberInput(modifier = modifier)
    }

}

@Composable
fun ApplicationBanner(modifier: Modifier) {
    CardView (modifier){
        Row {
            Image(painter = painterResource(id = R.drawable.search), contentDescription = "Application Icon", modifier = modifier
                .padding(10.dp)
                .weight(1.0F))
            Column (modifier = modifier
                .padding(10.dp)
                .weight(2.0F)){
                Text(text = "Carrier Identifier", color = MaterialTheme.colorScheme.background, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "Identify Tanzanian phone numbers starting with +255, 255 or 0", style = MaterialTheme.typography.bodyMedium)
            }

        }
    }
}

@Composable
fun NumberInput(modifier: Modifier) {
    var textInput by remember {
        mutableStateOf ("")
    }
    var isError by remember {
        mutableStateOf(false)
    }
    var identifiedNumber: IdentifiedNumber? by remember {
        mutableStateOf(null)
    }

    var errorText by remember {
        mutableStateOf("")
    }

    fun onValueChange(text: String) {
        textInput = text

        if (textInput.isBlank()) {
            identifiedNumber = null
            isError = false
            errorText = ""
            return
        }

        val rawNumber = RawNumber(textInput)

        try {
            identifiedNumber = rawNumber.identifyNumber()
            isError = false
            errorText = ""

            try {
                identifiedNumber!!.verifyLength()
            } catch (e: NumberTooShort) {
                isError = true
                errorText = "The provided number is too short"
            } catch (e: NumberTooLong) {
                isError = true
                errorText = "The provided number is too long"
            }
        } catch (e: UnknownCarrier) {
            errorText = "The number provided is of unknown carrier"
            isError = true
        } catch (e: InvalidNumberType) {
            errorText = "The number provided starts with invalid digits"
            isError = true
        } catch (e: NumberTooShort) {
            isError = true
            errorText = "The provided number is too short"        }
    }

    @Composable
    fun placeholder() {
        Text (text = "Input phone number (i.e 07xxxx)")
    }

    @Composable
    fun TextInputLeadingIcon() {
        Icon(painterResource(id = R.drawable.baseline_add_call_24), contentDescription = "text input leading icon")
    }

    CardView(modifier = modifier) {
        Column (modifier = modifier
            .fillMaxSize()
            .padding(5.dp), horizontalAlignment = Alignment.CenterHorizontally){
            TextField(value = textInput, onValueChange = {text -> onValueChange(text)}, placeholder = {placeholder()}, isError = isError, leadingIcon = {TextInputLeadingIcon()})
            Spacer(modifier = modifier.height(10.dp))
            IdentifiedNumberText(identifiedNumber)
            Spacer(modifier = modifier.height(10.dp))
            ErrorText(errorText = errorText, modifier = modifier)
        }
    }
}

@Composable
fun CardView(modifier: Modifier, view: @Composable () -> Unit) {
    Surface (shape = MaterialTheme.shapes.large, color = MaterialTheme.colorScheme.tertiary, modifier = modifier.height(IntrinsicSize.Min)) {
        view()
    }
}
@Composable
fun IdentifiedNumberText(identifiedNumber: IdentifiedNumber?) {
    if (identifiedNumber != null) {
        Text (text = "Number ${identifiedNumber.numberStr} is ${identifiedNumber.carrier}")
    }
}

@Composable
fun ErrorText(errorText: String, modifier: Modifier) {
    @Composable
    fun ErrorIcon() {
        Icon(painter = painterResource(id = R.drawable.baseline_warning_amber_24), contentDescription = "warning icon", tint = MaterialTheme.colorScheme.onError)
    }

    if (errorText.isNotEmpty()) {
        Row {
            ErrorIcon()
            Spacer(modifier.height(10.dp))
            Text(text = errorText, color = Color.Red, style = MaterialTheme.typography.bodyMedium)
        }

    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CarrierIdentifierTheme {
        View(modifier = Modifier)
    }
}