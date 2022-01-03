package com.example.mysplit

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mysplit.components.InputField
import com.example.mysplit.ui.theme.MySplitTheme
import com.example.mysplit.widgets.RoundIconButton

class MainActivity : ComponentActivity() {
    @ExperimentalComposeUiApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MySplitTheme(darkTheme = true) {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {


                    BillForm()

                }
            }
        }
    }
}

@Composable
fun TopHeader(totalPerPerson: Double = 132.0){

    Column(
        modifier = Modifier.padding(2.dp)
    ) {

        Card(
            modifier = Modifier
                .padding(top = 20.dp)
                .padding(22.dp)
                .fillMaxWidth()
                .height(150.dp),
            border = BorderStroke(2.dp, Color.LightGray),
            backgroundColor = Color(0xFFE9D7F7),
            elevation = 2.dp,
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                val total = "%.2f".format(totalPerPerson)

                Text(
                    text = "Total Per Person",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    color = Color.Black
                )
                Spacer(modifier = Modifier.padding(0.5.dp))
                Text(
                    text = "₹$total",
                    style = TextStyle(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                    ),
                    color = Color.Black
                )
            }
        }
    }
}


@ExperimentalComposeUiApi
@Composable
fun BillForm(
    onValueChange: (String) -> Unit = {}
){

    val totalBillState = remember {
        mutableStateOf("")
    }

    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    val sliderPositionState = remember {
        mutableStateOf(0f)
    }
    val tipPercentage = (sliderPositionState.value * 100).toInt()

    val splitByState = remember {
        mutableStateOf(1)
    }

    val tipAmountState = remember {
        mutableStateOf(0.0)
    }

    val totalPerPersonState = remember {
        mutableStateOf(0.0)
    }

    val focusManager = LocalFocusManager.current

    Column(modifier = Modifier.pointerInput(Unit) {
        detectTapGestures(onTap = {
            focusManager.clearFocus()
        })
        }) {

        TopHeader(totalPerPerson = totalPerPersonState.value)
        Spacer(modifier = Modifier.padding(10.dp))

        Surface(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(corner = CornerSize(8.dp)),
            border = BorderStroke(width = 1.dp, color = Color.LightGray),
            elevation = 4.dp
        ) {

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {

                InputField(
                    valueState = totalBillState,
                    labelId = "Enter Bill",
                    enabled = true,
                    isSingleLine = true,
                    onAction = KeyboardActions{
                        if(!validState) return@KeyboardActions

                        onValueChange(totalBillState.value.trim())
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    },

                )

                Spacer(modifier = Modifier.height(10.dp))
            if(validState){
                Row(
                    modifier = Modifier.padding(3.dp),
                    horizontalArrangement = Arrangement.Start
                ){

                    Text(
                        text = "Split",
                        modifier = Modifier
                            .align(alignment = Alignment.CenterVertically)
                            .padding(start = 12.dp),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold

                    )
                    Spacer(modifier = Modifier.width(120.dp))

                    Row(
                        modifier = Modifier.padding(horizontal = 3.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        RoundIconButton(
                            imageVector = Icons.Default.Remove,
                            onClick = {
                                splitByState.value =
                                    if(splitByState.value > 1) splitByState.value -1
                                    else 1
                                totalPerPersonState.value =
                                    calculateTotalPerPerson(
                                        totalBill = totalBillState.value.toDouble(),
                                        splitBy = splitByState.value,
                                        tipPercentage = tipPercentage
                                    )
                            }
                        )
                        Text(
                            text = "${splitByState.value}",
                            modifier = Modifier
                                .align(alignment = Alignment.CenterVertically)
                                .padding(horizontal = 9.dp),
                            fontSize = 18.sp
                        )
                        RoundIconButton(
                            imageVector = Icons.Default.Add,
                            onClick = {
                                if(splitByState.value < 20) splitByState.value++
                                totalPerPersonState.value =
                                    calculateTotalPerPerson(
                                        totalBill = totalBillState.value.toDouble(),
                                        splitBy = splitByState.value,
                                        tipPercentage = tipPercentage
                                    )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.padding(3.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "Tip",
                        modifier = Modifier
                            .align(alignment = Alignment.CenterVertically)
                            .padding(start = 16.dp),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(150.dp))
                    Text(
                        text = "₹${tipAmountState.value}",
                        modifier = Modifier
                            .align(alignment = Alignment.CenterVertically)
                            .padding(start = 16.dp),
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(25.dp))
                Column(
                    modifier = Modifier.padding(3.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "$tipPercentage %",
                        modifier = Modifier
                            .align(alignment = Alignment.CenterHorizontally),
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))

                    //Slider
                    Slider(
                        value = sliderPositionState.value,
                        onValueChange = { newVal ->
                            sliderPositionState.value = newVal
                            tipAmountState.value =
                                calculateTotalTip(
                                    totalBill = totalBillState.value.toDouble(),
                                    tipPercentage =  tipPercentage
                                )
                            totalPerPersonState.value =
                                calculateTotalPerPerson(
                                    totalBill = totalBillState.value.toDouble(),
                                    splitBy = splitByState.value,
                                    tipPercentage = tipPercentage
                                )

                        },
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                        onValueChangeFinished = {
                            //Log.d("TAG", "BillForm: Finished...")
                        },
                        steps = 19
                    )



                }









            }
            else{
                Box {}
            }
            }
        }

    }




}

fun calculateTotalTip(totalBill: Double,
                      tipPercentage: Int): Double {

    return if (totalBill > 1 && totalBill.toString().isNotEmpty())
        (totalBill * tipPercentage)/100
    else
        0.0

}

fun calculateTotalPerPerson(
    totalBill: Double,
    splitBy: Int,
    tipPercentage: Int
): Double{
    val bill = calculateTotalTip(
        totalBill = totalBill,
        tipPercentage = tipPercentage
    ) + totalBill

    return (bill/splitBy)
}


@ExperimentalComposeUiApi
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showSystemUi = true)
@Composable
fun DefaultPreview() {
    MySplitTheme {
        BillForm()
    }
}