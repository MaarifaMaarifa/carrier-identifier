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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardCapitalization
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
    Column (Modifier.padding(5.dp)){
        ApplicationBanner(modifier = modifier)
        Spacer(modifier = modifier.height(20.dp))
        NumberInput(modifier = modifier)
    }

}

@Composable
fun ApplicationBanner(modifier: Modifier) {
    Surface (shape = MaterialTheme.shapes.large, color = MaterialTheme.colorScheme.secondary){
        Row {
            Image(painter = painterResource(id = R.drawable.search), contentDescription = "Application Icon", modifier = modifier.padding(10.dp))
            Column (modifier = modifier.padding(10.dp)){
                Text(text = "Carrier Identifier", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleLarge)
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
    var isErrored by remember {
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
            return
        }

        val rawNumber = RawNumber(textInput)

        try {
            identifiedNumber = rawNumber.identifyNumber()
            isErrored = false
            errorText = ""

            try {
                identifiedNumber!!.verifyLength()
            } catch (e: NumberTooShort) {
                isErrored = true
                errorText = "The provided number is too short"
            } catch (e: NumberTooLong) {
                isErrored = true
                errorText = "The provided number is too long"
            }
        } catch (e: UnknownCarrier) {
            errorText = "The number provided is of unknown carrier"
            isErrored = true
        } catch (e: InvalidNumberType) {
            errorText = "The number provided starts with invalid digits"
            isErrored = true
        } catch (e: NumberTooShort) {
            isErrored = true
            errorText = "The provided number is too short"        }
    }

    @Composable
    fun placeholder() {
        Text (text = "Input phone number (i.e 07xxxx)")
    }

    Surface (shape = MaterialTheme.shapes.large, color = MaterialTheme.colorScheme.secondary, modifier = modifier.height(IntrinsicSize.Min)) {
        Column (modifier = modifier.fillMaxSize().padding(5.dp), horizontalAlignment = Alignment.CenterHorizontally){
            TextField(value = textInput, onValueChange = {text -> onValueChange(text)}, placeholder = {placeholder()}, isError = isErrored)
            Spacer(modifier = modifier.height(10.dp))
            IdentifiedNumberText(identifiedNumber)
            Spacer(modifier = modifier.height(10.dp))
            ErrorText(errorText = errorText)
        }
    }

}

@Composable
fun IdentifiedNumberText(identifiedNumber: IdentifiedNumber?) {
    if (identifiedNumber != null) {
        Text (text = "Number ${identifiedNumber.numberStr} is ${identifiedNumber.carrier}")
    } 
}

@Composable
fun ErrorText(errorText: String) {
    if (errorText.isNotEmpty()) {
        Text(text = errorText, color = Color.Red)
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CarrierIdentifierTheme {
        View(modifier = Modifier)
    }
}