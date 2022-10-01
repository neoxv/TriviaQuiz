package com.example.triviaquiz.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.navigation.fragment.NavHostFragment
import com.example.triviaquiz.R
import com.example.triviaquiz.databinding.ActivityMainBinding
import com.example.triviaquiz.event.QuestionCall
import com.example.triviaquiz.model.Question
import com.example.triviaquiz.model.Questions
import com.example.triviaquiz.service.QuestionService
import com.example.triviaquiz.service.RetrofitInstance
import com.example.triviaquiz.util.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    /* A variable that is used to bind the layout to the activity. */
    private lateinit var binding: ActivityMainBinding
    lateinit var pref: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /* Used to bind the layout to the activity. */
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /* Used to initialize the `PrefManager` class. */
        pref = PrefManager(this)

       /* This is used to initialize the navigation graph. */
         val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
        navController.graph = navGraph
    }

    fun initiateQuiz(count: Int, difficulty: String, callback: QuestionCall) {
        val questionService: QuestionService = RetrofitInstance.getRetrofitInstance()
            .create(QuestionService::class.java)
        questionService.getQuestions(count, difficulty).enqueue(object : Callback<Questions> {
            override fun onResponse(call: Call<Questions>, response: Response<Questions>) {
                if (response.body() != null && response?.code() == 200) {
                    val fetchedResult = response.body()?.results
                    if (fetchedResult != null) {
                        callback.onSuccess(fetchedResult as MutableList<Question>)
                    } else {
                        callback.onError("Question list is empty.")
                    }
                } else {
                    callback.onError("Response is empty")
                }
            }

            override fun onFailure(call: Call<Questions>, t: Throwable) {
                Log.e("api", t.message.toString())
                callback.onError(t.message.toString())
            }
        })
    }
}