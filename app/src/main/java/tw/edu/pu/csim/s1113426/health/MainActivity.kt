package tw.edu.pu.csim.s1113426.health

import android.os.Bundle
import android.util.Log
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
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore



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
    val db = Firebase.firestore
    val weight = userWeight.toFloatOrNull()
    val height = userHeight.toFloatOrNull()
    if (weight != null && height != null) {
        val user = Person(userName, weight, height)
        // 保存操作
    } else {
        msg = "體重或身高格式錯誤"
    }



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
            Button(onClick = {
                val user = Person(userName, userWeight.toFloat(), userHeight.toFloat())
                db.collection("users")
                    //.add(user)
                    .document(userName)
                    .set(user)
                    .addOnSuccessListener { documentReference ->
                        msg = "新增/異動資料成功"
                    }
                    .addOnFailureListener { e ->
                        Log.e("FirestoreError", "新增/異動資料失敗", e)
                        msg = "新增/異動資料失敗：" + e.toString()
                    }

            }) {
                Text("新增/修改資料")
            }
            Button(onClick = {
                db.collection("users")
                    .whereEqualTo("userName", userName)
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            msg = ""
                            for (document in task.result!!) {
                                msg += "文件id：" + document.id + "\n名字：" + document.data["userName"] +
                                        "\n體重：" + document.data["userWeight"].toString() + "\n\n"
                            }
                            if (msg == "") {
                                msg = "查無資料"
                            }
                        }
                    }

            }) {
                Text("查詢資料")
            }
            Button(onClick = {  }) {
                Text("刪除資料")
            }
        }
        Text(text = msg)

    }
}
data class Person(
    var userName: String,
    var userWeight: Float,
    var userHeight: Float
)
