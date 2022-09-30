package com.example.triviaquiz

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.findNavController
import com.example.triviaquiz.databinding.FragmentScoreBinding
import com.example.triviaquiz.db.Player
import com.example.triviaquiz.db.Question
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.CircularProgressIndicator
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.ArrayList


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
        username = (activity as MainActivity).sf.getString("username","").toString()
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
            (activity as MainActivity).initiateQuiz(5,QuestionDifficulty.EASY,object: QuestionCall {
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