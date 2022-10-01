package com.example.triviaquiz.view

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.example.triviaquiz.databinding.ActivityMainBinding
import com.example.triviaquiz.event.QuestionCall
import com.example.triviaquiz.model.Question
import com.example.triviaquiz.model.Questions
import com.example.triviaquiz.service.QuestionService
import com.example.triviaquiz.service.RetrofitInstance
import com.example.triviaquiz.util.QuestionDifficulty
import com.example.triviaquiz.viewmodel.PlayerViewModel
import com.example.triviaquiz.viewmodel.QuestionViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var sf:SharedPreferences
    lateinit var sfEdit:SharedPreferences.Editor
    val questionViewModel: QuestionViewModel by viewModels()
    val playerViewModel: PlayerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sf = getSharedPreferences("sf_demo", MODE_PRIVATE)
        sfEdit = sf.edit()
    }

    fun initiateQuiz(count:Int, difficulty: QuestionDifficulty, callback: QuestionCall) {
        val questionService: QuestionService = RetrofitInstance.getRetrofitInstance()
            .create(QuestionService::class.java)
        questionService.getQuestions(count,difficulty.difficulty).enqueue(object: Callback<Questions> {
            override fun onResponse(call: Call<Questions>, response: Response<Questions>) {
                if (response.body() != null && response?.code() == 200) {
                    val fetchedResult = response.body()?.results
                    if (fetchedResult != null){
                        callback.onSuccess(fetchedResult as MutableList<Question>)
                    }else{
                        callback.onError("Question list is empty.")
                    }
                }else{
                    callback.onError("Response is empty")
                }
            }
            override fun onFailure(call: Call<Questions>, t: Throwable) {
               Log.e("api",t.message.toString())
                callback.onError(t.message.toString())
            }
        })
    }

}