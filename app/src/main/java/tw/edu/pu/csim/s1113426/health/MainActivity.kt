package tw.edu.pu.csim.s1113426.health

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import tw.edu.pu.csim.s1113426.health.ui.theme.HealthTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.TextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.text.input.KeyboardType

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HealthTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    /*
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )

                     */
                    Birth(m = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun Birth(m: Modifier) {
    var userName by remember { mutableStateOf("")}
    var userWeight by remember { mutableStateOf("")}
    var userHeight by remember { mutableStateOf("")}
    var msg by remember { mutableStateOf("")}


    // BMI 計算函數
    fun calculateBMI(weight: Float, height: Float): Float {
        return weight / ((height / 100) * (height / 100)) // 身高轉換為米
    }

    // 判斷BMI範圍
    fun bmiCategory(bmi: Float): String {
        return when {
            bmi < 18.5 -> "體重過輕"
            bmi in 18.5..24.9 -> "正常範圍"
            bmi in 25.0..29.9 -> "過重"
            bmi in 30.0..34.9 -> "輕度肥胖"
            bmi in 35.0..39.9 -> "中度肥胖"
            else -> "重度肥胖"
        }
    }

    fun getBMIMessage(weight: String, height: String): String {
        val weightFloat = weight.toFloatOrNull()
        val heightFloat = height.toFloatOrNull()

        return if (weightFloat != null && heightFloat != null && weightFloat > 0f && heightFloat > 0f) {
            val bmi = calculateBMI(weightFloat, heightFloat)
            "您的BMI是：${"%.2f".format(bmi)}\n${bmiCategory(bmi)}"
        } else {
            "請輸入有效的體重和身高"
        }
    }

    // 計算每日建議飲水量
    fun getWater(weight: String): String {
        val weightFloat = weight.toFloatOrNull()
        return if (weightFloat != null && weightFloat > 0f) {
            // 以體重的 30 毫升/公斤來計算建議飲水量
            val waterIntake = weightFloat * 30
            "每日建議飲水量：${"%.2f".format(waterIntake)} 毫升"
        } else {
            "請輸入有效的體重以計算建議飲水量"
        }
    }

    Column {
        // 姓名輸入框
        TextField(
            value = userName,
            onValueChange = { newText ->
                userName = newText
            },
            modifier = m,
            label = { Text("姓名") },
            placeholder = { Text("請輸入您的姓名") }
        )

        // 體重輸入框
        TextField(
            value = userWeight,
            onValueChange = { newText ->
                userWeight = newText // 直接保存字符串
            },
            label = { Text("體重") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        // 身高輸入框
        TextField(
            value = userHeight,
            onValueChange = { newText ->
                userHeight = newText // 直接保存字符串
            },
            label = { Text("身高(公分)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )


        //輸入結果顯示
        Text(
            text = "${getBMIMessage(userWeight, userHeight)}\n${getWater(userWeight)}",

            modifier = m
        )
        Row {
            Button(onClick = { }) {
                Text("新增/修改資料")
            }
            Button(onClick = {  }) {
                Text("查詢資料")
            }
            Button(onClick = {  }) {
                Text("刪除資料")
            }
        }
        Text(text = msg)

    }
}
