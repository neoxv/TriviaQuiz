package com.example.triviaquiz.view.fragment

import android.os.Bundle
import android.system.Os.remove
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.findNavController
import com.example.triviaquiz.*
import com.example.triviaquiz.databinding.FragmentHomeBinding
import com.example.triviaquiz.event.QuestionCall
import com.example.triviaquiz.room.entity.Player
import com.example.triviaquiz.model.Question
import com.example.triviaquiz.util.Constant.QUESTION_COUNT
import com.example.triviaquiz.util.Constant.QUESTION_DIFFICULTY_EASY
import com.example.triviaquiz.view.MainActivity
import com.example.triviaquiz.viewmodel.PlayerViewModel
import com.example.triviaquiz.viewmodel.PlayerViewModelFactory
import com.example.triviaquiz.viewmodel.QuestionViewModel
import com.example.triviaquiz.viewmodel.QuestionViewModelFactory
import com.google.android.material.chip.Chip
import com.google.android.material.progressindicator.CircularProgressIndicator
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var loaderView: CircularProgressIndicator
    private val playerViewModel: PlayerViewModel by activityViewModels {
        PlayerViewModelFactory((activity?.application as TriviaQuizApplication).database.playerDao())
    }
    private val questionViewModel: QuestionViewModel by activityViewModels {
        QuestionViewModelFactory((activity?.application as TriviaQuizApplication).database.questionDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        lifecycle.coroutineScope.launch {
            playerViewModel.getPlayers().collect() {
                it.forEach { player ->
                    var playerChip = createChip(player.username)
                    binding.cgPlayerChips.addView(playerChip)
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loaderView = CircularProgressIndicator(requireContext())
        binding.cgPlayerChips.setOnCheckedStateChangeListener { group, checkedIds ->
            loaderView.isIndeterminate = true
            binding.llHome.addView(loaderView)

            (activity as MainActivity).initiateQuiz(QUESTION_COUNT,
                QUESTION_DIFFICULTY_EASY, object : QuestionCall {
                    override fun onSuccess(questionList: MutableList<Question>) {
                        questionViewModel.clearQuestions()
                        questionList.forEach { q ->
                            questionViewModel.insertQuestion(q)
                        }
                        (activity as MainActivity).pref.username = binding.etName.text.toString()

                        view.findNavController()
                            .navigate(R.id.action_homeFragment_to_questionFragment)
                    }

                    override fun onError(error: String) {
                        binding.llHome.removeView(loaderView)
                        Toast.makeText(activity, "Failed to fetch questions.", Toast.LENGTH_SHORT)
                            .show()
                    }
                })
        }

        binding.btnSubmit.setOnClickListener { homeView ->
            if (!TextUtils.isEmpty(binding.etName.text.toString())) {
                loaderView.isIndeterminate = true
                binding.llHome.addView(loaderView)

                (activity as MainActivity).initiateQuiz(QUESTION_COUNT,
                    QUESTION_DIFFICULTY_EASY, object : QuestionCall {
                        override fun onSuccess(questionList: MutableList<Question>) {
                            val player = Player(binding.etName.text.toString())
                            playerViewModel.insertPlayer(player)
                            questionViewModel.clearQuestions()

                            questionList.forEach { q ->
                                questionViewModel.insertQuestion(q)
                            }
                            (activity as MainActivity).pref.username = binding.etName.text.toString()

                            homeView.findNavController()
                                .navigate(R.id.action_homeFragment_to_questionFragment)
                        }

                        override fun onError(error: String) {
                            binding.llHome.removeView(loaderView)
                            Toast.makeText(
                                activity,
                                "Failed to fetch questions.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
            } else {
                Toast.makeText(activity, "Please enter your name.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createChip(label: String): Chip {
        return Chip(context).apply {
            id = View.generateViewId()
            text = label
            isClickable = true
            isCheckedIconVisible = false
            isFocusable = true
            isCheckable = true
        }

    }
}