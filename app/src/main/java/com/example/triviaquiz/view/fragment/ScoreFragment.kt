package com.example.triviaquiz.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.findNavController
import com.example.triviaquiz.*
import com.example.triviaquiz.databinding.FragmentScoreBinding
import com.example.triviaquiz.event.QuestionCall
import com.example.triviaquiz.model.Question
import com.example.triviaquiz.util.Constant.QUESTION_COUNT
import com.example.triviaquiz.util.Constant.QUESTION_DIFFICULTY_EASY
import com.example.triviaquiz.view.MainActivity
import com.example.triviaquiz.viewmodel.PlayerViewModel
import com.example.triviaquiz.viewmodel.PlayerViewModelFactory
import com.example.triviaquiz.viewmodel.QuestionViewModel
import com.example.triviaquiz.viewmodel.QuestionViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.CircularProgressIndicator
import kotlinx.coroutines.launch


class ScoreFragment : Fragment() {
    private lateinit var binding:FragmentScoreBinding
    private  var score = 0
    private  var level = 1
    private var username = ""
    private lateinit var loaderView: CircularProgressIndicator
    private val questionViewModel: QuestionViewModel by activityViewModels {
        QuestionViewModelFactory((activity?.application as TriviaQuizApplication).database.questionDao())
    }
    private val playerViewModel: PlayerViewModel by activityViewModels {
        PlayerViewModelFactory((activity?.application as TriviaQuizApplication).database.playerDao())
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                exitDialogue()
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScoreBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loaderView = CircularProgressIndicator(requireContext())
        score = requireArguments().getInt("score")
        username = (activity as MainActivity).pref.username
        level = requireArguments().getInt("level")
        if(requireArguments().getBoolean("highScore")){
            binding.apply {
                tvHighScoreName.text = username
                tvHighScoreLevel.text = "level: $level"
                tvHighScore.text = score.toString()
            }
        }else{
            lifecycle.coroutineScope.launch {
                playerViewModel.getHighScorePlayer().collect(){
                    binding.apply {
                        tvPlayerName.text = username
                        tvPlayerLevel.text =  "level: $level"
                        tvScore.text = score.toString()
                        tvHighScoreName.text = it.username
                        tvHighScoreLevel.text = "level: ${it.highLevel}"
                        tvHighScore.text = it.highScore.toString()
                    }
                }
            }
        }
        binding.btnTryAgain.setOnClickListener{
            (activity as MainActivity).initiateQuiz(QUESTION_COUNT,
                QUESTION_DIFFICULTY_EASY,object: QuestionCall {
                override fun onSuccess(questionList: MutableList<Question>) {

                    questionViewModel.clearQuestions()
                    questionList.forEach { q ->
                        questionViewModel.insertQuestion(q)
                    }
                        view.findNavController().navigate(R.id.action_scoreFragment_to_questionFragment)
                }

                override fun onError(error: String) {
                    questionViewModel.clearQuestions()
                    Toast.makeText(activity,"Failed to fetch questions.", Toast.LENGTH_SHORT).show()
                }
            })
        }
        binding.btnExit.setOnClickListener {
            exitDialogue()
        }


    }

    private fun exitDialogue(){
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Exit TriviaQuiz")
            .setMessage("Do you want to exit TriviaQuiz")
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Exit") { _, _ ->
                activity?.finish()
            }
            .show()
    }

}