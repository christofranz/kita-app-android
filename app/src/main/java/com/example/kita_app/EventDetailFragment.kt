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

class EventDetailFragment : Fragment() {

    private var _binding: FragmentEventDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var event: Event
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

        return view
    }

    private fun postFeedback() {
        val sharedPreferences = getEncryptedSharedPreferences(requireContext())
        val id = sharedPreferences.getString("id", null)
        val feedback = Feedback(child_id = "66cb30233d6250639c4f7815")
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

    companion object {
        fun newInstance(event: Event): EventDetailFragment {
            val fragment = EventDetailFragment()
            fragment.event = event
            return fragment
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
