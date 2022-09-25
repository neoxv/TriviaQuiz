package com.example.triviaquiz

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.triviaquiz.databinding.FragmentScoreBinding
import com.example.triviaquiz.db.Player
import com.example.triviaquiz.db.Question
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.CircularProgressIndicator
import java.util.ArrayList


class ScoreFragment : Fragment() {
    private lateinit var binding:FragmentScoreBinding
    private  var score = 0
    private var username = ""
    private lateinit var loaderView: CircularProgressIndicator
    private val questionViewModel: QuestionViewModel by activityViewModels {
        QuestionViewModelFactory((activity?.application as TriviaQuizApplication).database.questionDao())
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
        score = requireArguments().getInt("score")
        username = requireArguments().getString("username").toString()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loaderView = CircularProgressIndicator(requireContext())
        binding.apply {
            tvScore.text = "$score/10"
            tvUsername.text = "Player: $username"
            btnTryAgain.setOnClickListener{
                loaderView.isIndeterminate = true
//                binding.llScorePage.addView(loaderView)

                (activity as MainActivity).initiateQuiz(5,QuestionDifficulty.ANY,object: QuestionCall {
                    //clear questions
                    //set new questions

                    override fun onSuccess(questionList: MutableList<Question>) {
                        questionViewModel.clearQuestions()
                        questionList.forEach { q ->
                            questionViewModel.insertQuestion(q)
                        }
                        it.findNavController().navigate(R.id.action_scoreFragment_to_questionFragment)
                    }

                    override fun onError(error: String) {
                        questionViewModel.clearQuestions()
//                        binding.llScorePage.removeView(loaderView)
                        Toast.makeText(activity,"Failed to fetch questions.", Toast.LENGTH_SHORT).show()
                    }
                })
            }
            btnExit.setOnClickListener {
                exitDialogue()
            }
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