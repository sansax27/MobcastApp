package com.mobcast.discussion.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobcast.R
import com.mobcast.data.utils.ResultState
import com.mobcast.data.utils.UIState
import com.mobcast.databinding.FragmentDiscussionBinding
import com.mobcast.discussion.DiscussionFragmentViewModel
import com.mobcast.discussion.DiscussionItemAdapter
import com.mobcast.discussion.models.DiscussionReply
import com.mobcast.discussion.models.Employee
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class DiscussionFragment @Inject constructor() :
    Fragment() {

    private lateinit var _binding: FragmentDiscussionBinding
    private val binding: FragmentDiscussionBinding get() = _binding
    private val viewModel: DiscussionFragmentViewModel by viewModels()
    private val employeesList = ArrayList<Employee>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = FragmentDiscussionBinding.inflate(layoutInflater)
        val discussionItemAdapter =  DiscussionItemAdapter{
            if (it.isNullOrEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.noCommentsToShow), Toast.LENGTH_SHORT).show()
            } else {
                DiscussionReplyFragment.newInstance(ArrayList<DiscussionReply>(it.size).apply { addAll(it) }, employeesList).show(parentFragmentManager, "Replies")
            }
        }
        binding.discussionItemsRV.apply {
            adapter = discussionItemAdapter
            layoutManager = LinearLayoutManager(requireContext()).apply {
                orientation = RecyclerView.VERTICAL
            }
        }
        viewModel.discussionItemLiveData.observe(this) {
            if (binding.discussionItemPB.visibility == View.VISIBLE && it!=null) {
                binding.discussionItemPB.visibility = View.GONE
                binding.discussionItemsRV.visibility = View.VISIBLE
            }
            viewModel.viewModelScope.launch {
                discussionItemAdapter.submitData(it)
            }
        }
        viewModel.getEmployeesStatus.observe(this) {
            when(it) {
                is UIState.Success -> {
                    it.data.employees?.let {
                        employeesList.clear()
                        employeesList.addAll(it)
                    }
                }
                is UIState.Empty -> {}
                is UIState.Loading -> {}
                is UIState.Failure -> {
                    if (it.message=="Network Error") {
                        Toast.makeText(requireContext(), getString(R.string.networkError), Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }
}