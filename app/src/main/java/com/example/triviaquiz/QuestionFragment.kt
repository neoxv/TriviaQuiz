package com.example.triviaquiz

import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils
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
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.triviaquiz.databinding.FragmentQuestionBinding
import com.example.triviaquiz.db.Question
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class QuestionFragment : Fragment(),View.OnClickListener {
    private lateinit var binding: FragmentQuestionBinding
    private var questionList = mutableListOf<Question>()
    private var currentQuestion = 1
    private var answerStatus = false
    private var score = 0
    private var lifeLoss = 0
    private var level = 1
    private var username = ""
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
                questionList = it as MutableList<Question>
                binding.apply {
                    btnAnswerOne.setOnClickListener(this@QuestionFragment)
                    btnAnswerTwo.setOnClickListener(this@QuestionFragment)
                    btnAnswerThree.setOnClickListener(this@QuestionFragment)
                    btnAnswerFour.setOnClickListener(this@QuestionFragment)

                    btnNext.setOnClickListener {
                        if (answerStatus && currentQuestion < questionList.size) {
                            clearQuestion()
                            answerStatus = false
                            currentQuestion += 1
                            var index = currentQuestion -1

                            drawQuestion(questionList[index])
                        }else if (answerStatus && currentQuestion == questionList.size) {
                            //and refetch questions
                            //reset progress
                            // increment level
                            // redraw level
                            level += 1
                        }else {
                            setFragmentResultListener("user") { _, bundle ->
                                username = bundle.getString("name").toString()
                            }
                            Toast.makeText(activity, "$username choose an answer.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                drawQuestion(questionList[0])
            }
        }


    }

    private fun drawQuestion(question: Question){
        binding.apply {
            val questionText = question.question.parseAsHtml()
            tvLife.text = "❤ ❤ ❤"
            tvLevelScore.text = "Score: 0"
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
            //update progress and question count
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
        if (answer){
            binding.tvLevelScore.text = "Score: $score"
        }else{
            when (lifeLoss) {
                1 -> binding.tvLife.text = "❤ ❤"
                2 -> binding.tvLife.text = "❤"
                3 -> {
                    binding.tvGameOver.text = "Game Over !"
                    lifecycle.coroutineScope.launch {
                        playerViewModel.getPlayerByName((activity as MainActivity).sf.getString("username","").toString()).collect{
                            if(it != null){
                                if (it.highLevel < level){
                                    it.highLevel = level
                                    it.highScore = score
                                }else if(it.highLevel == level && it.highScore < score){
                                    it.highScore = score
                                }
                                it.score = score
                                playerViewModel.updatePlayer(it)
                            }
                        }
                    }
                    requireParentFragment().findNavController().navigate(R.id.action_questionFragment_to_scoreFragment)
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
