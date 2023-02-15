package com.example.test12

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.test12.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var msgAdapter: MsgAdapter
    private lateinit var answer: String
    private val SPEECH_REQUEST_CODE = 0
    //建立消息数据列表
    private var msgList: MutableList<Msg> = ArrayList()//建立可改變的list

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initMsg()   //初始化
        initRv() //RecyclerView初始化
        displaySpeechRecognizer()//語音辨識
        setListener()//發送訊息與ai對話
    }
    private fun setListener() {
        binding.run {
            sendButton.setOnClickListener {
                val content: String = editText.text.toString()   //获取输入框的文本
                if (content.isNotEmpty()) {
                    msgList.add(Msg(content, Msg.RIGHT))    //将输入的消息及其类型添加进消息数据列表中
                    aichat()
                    editText.hideKeyboard() //收起鍵盤
                    editText.setText("")    //清空输入框文本
                }
            }
        }
    }

    private fun initRv() {
        binding.recyclerView.apply {
            msgAdapter = MsgAdapter(msgList)   //建立适配器实例
            layoutManager = LinearLayoutManager(
                this@MainActivity,
                LinearLayoutManager.VERTICAL,
                false
            )  //布局为线性垂直
            adapter = msgAdapter
        }
    }

    private fun initMsg() {
        msgList.add(Msg("發送訊息以獲得回覆", Msg.LEFT))
    }

    private fun aichat() {
        val API_KEY = "Bearer $MY_API_KEY" //獲得api-key
        val openAI = OpenAI(API_KEY)
        var prompt = editText.text.toString()//要和ai說的話
        val message = binding.editText.text.toString()
        CoroutineScope(Dispatchers.IO).launch {
            prompt += "\n\nHuman: $message \nAI:"
            try {
                val response = openAI.createCompletion(
                    model = "text-davinci-003",
                    prompt = prompt,
                    temperature = 0.3,
                    max_tokens = 200,
                    top_p = 1,
                    frequency_penalty = 0.0,
                    presence_penalty = 0.0,
                    stop = listOf(" Human:", " AI:")
                )//ai相關參數
                if (response.isSuccessful) {
                    answer = response.body()?.choices?.first()?.text.toString()//ai回答的話
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(this@MainActivity, answer, Toast.LENGTH_SHORT).show()
                        msgList.add(Msg(answer, Msg.LEFT))//讓ai說的話顯示出來
                        msgAdapter.notifyDataSetChanged()   //为RecyclerView添加末尾子项
                    }
                } else {
                    Log.d("RESPONSE", "Error: ${response.code()} ${response.message()}")
                }
            } catch (e: Exception) {
                Log.d("RESPONSE", "Error: $e")
            }
        }
    }

    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }//打完字自動把鍵盤收起來

    private fun displaySpeechRecognizer() {
        button.setOnClickListener{
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            }
            // This starts the activity and populates the intent with the speech text.
            startActivityForResult(intent, SPEECH_REQUEST_CODE)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val spokenText: String = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.let { results ->
                    results[0]
                }.toString()
            binding.editText.setText(spokenText)
            // Do something with spokenText.
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}


