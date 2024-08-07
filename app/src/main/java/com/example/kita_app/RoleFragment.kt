package com.example.kita_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.kita_app.databinding.FragmentRolesBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RoleFragment : Fragment() {

    private var _binding: FragmentRolesBinding? = null
    private val binding get() = _binding!!
    private lateinit var username: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRolesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve the username from the arguments
        username = arguments?.getString("username") ?: "username"

        binding.setRoleButton.setOnClickListener {
            val targetUsername = binding.usernameRoleEditText.text.toString()
            val newRole = binding.newRoleEditText.text.toString()

            val request = SetRoleRequest(username, targetUsername, newRole)
            RetrofitClient.instance.setRole(request).enqueue(object : Callback<ResponseMessage> {
                override fun onResponse(call: Call<ResponseMessage>, response: Response<ResponseMessage>) {
                    if (response.isSuccessful) {
                        Toast.makeText(context, response.body()?.message, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Failed to set role", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                    Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}