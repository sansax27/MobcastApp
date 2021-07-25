package com.mobcast.discussion.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobcast.databinding.FragmentDiscussionBinding
import com.mobcast.discussion.DiscussionFragmentViewModel
import com.mobcast.discussion.DiscussionItemAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class DiscussionFragment @Inject constructor(private val discussionItemAdapter: DiscussionItemAdapter) :
    Fragment() {

    private lateinit var _binding: FragmentDiscussionBinding
    private val binding: FragmentDiscussionBinding get() = _binding
    private val viewModel: DiscussionFragmentViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        _binding = FragmentDiscussionBinding.inflate(layoutInflater)
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
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }
}