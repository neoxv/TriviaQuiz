package com.example.triviaquiz

import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.text.htmlEncode
import androidx.core.text.parseAsHtml
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.triviaquiz.databinding.FragmentQuestionBinding
import com.example.triviaquiz.db.Player
import com.example.triviaquiz.db.Question
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class QuestionFragment : Fragment(),View.OnClickListener {
    private lateinit var binding: FragmentQuestionBinding
    private var questionList = mutableListOf<Question>()
    private var currentQuestion = 1
    private var answerStatus = false
    private var score = 0
    private var lifeLoss = 0
    private var level = 1
    private var username = ""
    private var currentDifficulty = QuestionDifficulty.EASY
    private var gameStatus = true
    private val questionViewModel: QuestionViewModel by activityViewModels {
        QuestionViewModelFactory((activity?.application as TriviaQuizApplication).database.questionDao())
    }
    private val playerViewModel: PlayerViewModel by activityViewModels {
        PlayerViewModelFactory((activity?.application as TriviaQuizApplication).database.playerDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentQuestionBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycle.coroutineScope.launch {
            questionViewModel.getQuestions().collect(){
                questionList = it
                binding.progressBar.progress = currentQuestion
                binding.tvQuestionCount.text = "$currentQuestion/5"
                Log.i("t","${questionList.size.toString()}")
                if(it.size > 0){
                    drawQuestion(it[0])
                }
            }
        }
        binding.apply {
            btnAnswerOne.setOnClickListener(this@QuestionFragment)
            btnAnswerTwo.setOnClickListener(this@QuestionFragment)
            btnAnswerThree.setOnClickListener(this@QuestionFragment)
            btnAnswerFour.setOnClickListener(this@QuestionFragment)

            btnNext.setOnClickListener { questionView ->
                if (!gameStatus){
                    var highScore = false
                    lifecycle.coroutineScope.launch {
                        playerViewModel.getPlayerByName((activity as MainActivity).sf.getString("username","").toString()).collect(){player ->
                            if(player != null){
                            Log.i("t","player is not null")
                                if (player.highLevel < level){
                                    Log.i("t","player high level")

                                    player.highLevel = level
                                    player.highScore = score
                                    highScore = true
                                }else if(player.highLevel == level && player.highScore < score){
                                    Log.i("t","player high score" +
                                            "")
                                    player.highScore = score
                                    highScore = true
                                }
                                playerViewModel.updatePlayer(player)
                            }else{
                                var user = (activity as MainActivity).sf.getString("username","").toString()
                                Toast.makeText(context,"Failed to fetch $user",Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    var bundle = bundleOf("score" to score, "level" to level, "highScore" to highScore)
                    questionView.findNavController().navigate(R.id.action_questionFragment_to_scoreFragment,bundle)
                }else if (questionList.size > 0 && answerStatus && currentQuestion < questionList.size) {
                    clearQuestion()
                    answerStatus = false
                    currentQuestion += 1
                    binding.progressBar.progress = currentQuestion
                    binding.tvQuestionCount.text = "$currentQuestion/5"
                    var index = currentQuestion -1

                    drawQuestion(questionList[index])

                }else if (questionList.size > 0 && answerStatus && currentQuestion == questionList.size) {
                    currentDifficulty = when(currentDifficulty){
                        QuestionDifficulty.EASY -> QuestionDifficulty.MEDIUM
                        QuestionDifficulty.MEDIUM -> QuestionDifficulty.HARD
                        QuestionDifficulty.HARD -> QuestionDifficulty.ANY
                        else -> {
                            QuestionDifficulty.EASY
                        }
                    }
                    (activity as MainActivity).initiateQuiz(5, currentDifficulty,object: QuestionCall {
                        override fun onSuccess(newQuestionList: MutableList<Question>) {
                            questionViewModel.clearQuestions()
                            questionList = newQuestionList
                            questionList.forEach { q ->
                                questionViewModel.insertQuestion(q)
                            }
                            clearQuestion()
                            answerStatus = false
                            level += 1
                            currentQuestion = 1
                            binding.tvLevel.text = "Level: $level"
                            binding.progressBar.progress = currentQuestion
                            binding.tvQuestionCount.text = "$currentQuestion/5"
                            drawQuestion(questionList[0])
                        }

                        override fun onError(error: String) {
                            Toast.makeText(activity,"Failed to fetch questions.", Toast.LENGTH_SHORT).show()
                        }
                    })

                }else {
                    Toast.makeText(activity, "${(activity as MainActivity).sf.getString("username","")}choose an answer.", Toast.LENGTH_SHORT).show()
                }
            }
        }


    }

    private fun drawQuestion(question: Question){
        binding.apply {
            val questionText = question.question.parseAsHtml()
            if (currentQuestion == 1 && !answerStatus)
                drawScore(true)

            tvQuestion.text = questionText
            cpCategory.text = question.category
            cpDifficulty.text = question.difficulty

            var answers:MutableList<String> = emptyList<String>().toMutableList()
            answers.add(question.correct_answer)
            answers.addAll(question.incorrect_answers)
            answers = answers.shuffled() as MutableList<String>

            if(question.type == "boolean") {
                btnAnswerOne.visibility = View.VISIBLE
                btnAnswerTwo.visibility = View.VISIBLE
                btnAnswerThree.visibility = View.GONE
                btnAnswerFour.visibility = View.GONE

                btnAnswerOne.text = answers[0]
                btnAnswerTwo.text = answers[1]
            }else {
                btnAnswerOne.visibility = View.VISIBLE
                btnAnswerTwo.visibility = View.VISIBLE
                btnAnswerThree.visibility = View.VISIBLE
                btnAnswerFour.visibility = View.VISIBLE

                btnAnswerOne.text = answers[0].parseAsHtml()
                btnAnswerTwo.text = answers[1].parseAsHtml()
                btnAnswerThree.text = answers[2].parseAsHtml()
                btnAnswerFour.text = answers[3].parseAsHtml()
            }

        }
    }

    override fun onClick(btn: View?) {
        val btnMat = btn as MaterialButton
        if(!answerStatus){
            val answerBtn: Button = btn
            val answer = answerBtn.text.toString().htmlEncode()
            if (TextUtils.equals(answer, questionList[currentQuestion - 1].correct_answer)) {
                btnMat.strokeColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.beige))
                score += 1
                drawScore(true)
            } else {
                btnMat.strokeColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.claret))
                if(questionList[currentQuestion - 1].type != "boolean")
                    showAnswer()
                lifeLoss +=1
                drawScore(false)
            }
            answerStatus = true
        }else{
            Toast.makeText(context,"Please click next!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun drawScore(answer:Boolean){
        if (answer) {
            binding.tvLevelScore.text = "Score: $score"
            if (currentQuestion == 1 && !answerStatus)
                binding.tvLife.text = "❤ ❤ ❤"
        }else{
            when (lifeLoss) {
                1 -> binding.tvLife.text = "❤ ❤"
                2 -> binding.tvLife.text = "❤"
                3 -> {
                    binding.tvGameOver.text = "Game Over !"
                    gameStatus = false
                }
            }
        }
    }

    private fun clearQuestion(){
        binding.apply {
            val strokeColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),
                com.google.android.material.R.color.design_default_color_on_surface)).withAlpha(36)
            for (i in 0..llAnswers.childCount){
                if(llAnswers.getChildAt(i) is Button){
                    val child = llAnswers.getChildAt(i) as MaterialButton
                    child.strokeColor = strokeColor
                }
            }
        }
    }

    private  fun showAnswer(){
        binding.apply {
            for (i in 0 until llAnswers.childCount){
                    val child = llAnswers.getChildAt(i) as MaterialButton
                    if(TextUtils.equals(child.text, questionList[currentQuestion - 1].correct_answer.parseAsHtml())){
                        child.strokeColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.beige))
                        break
                    }
            }
        }
    }

}
