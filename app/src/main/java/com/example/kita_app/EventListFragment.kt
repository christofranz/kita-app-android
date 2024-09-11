package com.example.kita_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kita_app.databinding.FragmentEventListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventListFragment : Fragment() {

    private var _binding: FragmentEventListBinding? = null
    private val binding get() = _binding!!
    private lateinit var apiService: Api

    private lateinit var eventAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEventListBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.eventRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        fetchEvents()

        return view
    }

    private fun fetchEvents() {
        context?.let {
            apiService = RetrofitClient.getInstance(it).create(Api::class.java)
        }

        apiService.getUpcomingEvents().enqueue(object : Callback<List<ChildEvents>> {
                override fun onResponse(call: Call<List<ChildEvents>>, response: Response<List<ChildEvents>>) {
                    if (response.isSuccessful) {
                        val events = response.body() ?: emptyList()
                        val flatEventList = events.flatMap { it.events } ?: listOf()
                        eventAdapter = EventAdapter(flatEventList) { event ->
                            openEventDetail(event)
                        }
                        binding.eventRecyclerView.adapter = eventAdapter
                    }
                }

                override fun onFailure(call: Call<List<ChildEvents>>, t: Throwable) {
                    // Handle error
                }
        })
    }

    private fun openEventDetail(event: Event) {
        val fragment = EventDetailFragment.newInstance(event)
        parentFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
