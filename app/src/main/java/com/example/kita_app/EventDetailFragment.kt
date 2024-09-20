package com.example.kita_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.kita_app.databinding.FragmentEventDetailBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log


class EventDetailFragment : Fragment() {

    private var _binding: FragmentEventDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var event: Event
    private lateinit var childId: String
    private var isChildStayingHome: Boolean = false
    private lateinit var apiService: Api

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEventDetailBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.eventDetailText.text = "${event.event_type} on ${event.date}\n" +
                "Max Children Allowed: ${event.max_children_allowed}\n" +
                "Children Staying Home: ${event.children_staying_home.size}"

        binding.stayHomeButton.setOnClickListener {
            postFeedback()
        }
        // Get feedback on whether the child is staying home
        checkIfChildStayingHome()

        // Set up the withdraw button
        binding.buttonWithdrawFeedback.setOnClickListener {
            withdrawFeedback()
        }

        return view
    }

    private fun postFeedback() {

        val feedback = Feedback(child_id = childId)
        context?.let {
            apiService = RetrofitClient.getInstance(it).create(Api::class.java)
        }
        apiService.postEventFeedback(event._id, feedback)
            .enqueue(object : Callback<ResponseMessage> {
                override fun onResponse(call: Call<ResponseMessage>, response: Response<ResponseMessage>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Feedback submitted!", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.popBackStack()
                    } else {
                        Toast.makeText(requireContext(), "Failed to submit feedback", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun checkIfChildStayingHome() {
        context?.let {
            apiService = RetrofitClient.getInstance(it).create(Api::class.java)
        }
        apiService.getFeedback(event._id, childId).enqueue(object : Callback<FeedbackResponse> {
            override fun onResponse(call: Call<FeedbackResponse>, response: Response<FeedbackResponse>) {
                if (response.isSuccessful) {
                    isChildStayingHome = response.body()?.staying_home ?: false
                    updateFeedbackStatus()
                } else {
                    Toast.makeText(requireContext(), "Failed to check feedback status", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<FeedbackResponse>, t: Throwable) {
                // Handle failure
            }
        })
    }

    private fun updateFeedbackStatus() {
        if (isChildStayingHome) {
            binding.textViewFeedbackStatus.text = "Your child is staying home for this event."
            binding.buttonWithdrawFeedback.visibility = View.VISIBLE
            binding.stayHomeButton.visibility = View.GONE
        } else {
            binding.textViewFeedbackStatus.text = "Your child is attending this event."
            binding.buttonWithdrawFeedback.visibility = View.GONE
            binding.stayHomeButton.visibility = View.VISIBLE
        }
    }

    private fun withdrawFeedback() {
        context?.let {
            apiService = RetrofitClient.getInstance(it).create(Api::class.java)
        }
        apiService.withdrawFeedback(event._id, childId).enqueue(object : Callback<FeedbackResponse> {
            override fun onResponse(call: Call<FeedbackResponse>, response: Response<FeedbackResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Feedback withdrawn!", Toast.LENGTH_SHORT).show()
                    isChildStayingHome = false
                    updateFeedbackStatus()
                } else {
                    Toast.makeText(requireContext(), "Failed to withdraw feedback", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<FeedbackResponse>, t: Throwable) {
                // Handle failure
            }
        })
    }

    companion object {
        fun newInstance(event: Event, childId: String): EventDetailFragment {
            val fragment = EventDetailFragment()
            fragment.event = event
            fragment.childId = childId
            return fragment
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}