package com.mobcast.discussion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mobcast.R
import com.mobcast.databinding.FragmentCommentBinding
import com.mobcast.discussion.models.DiscussionReply


class DiscussionReplyFragment : BottomSheetDialogFragment() {

    private lateinit var _binding:FragmentCommentBinding
    private val binding:FragmentCommentBinding get() = _binding
    private val discussionReplyAdapter = DiscussionReplyAdapter(true)


    override fun onCreate(savedInstanceState: Bundle?) {
        _binding = FragmentCommentBinding.inflate(layoutInflater)
        arguments?.getSerializable("replies")?.let {
            binding.replyRV.apply {
                adapter = discussionReplyAdapter
                discussionReplyAdapter.submitList(it as List<DiscussionReply>)
                layoutManager = LinearLayoutManager(requireContext()).apply {
                    orientation = RecyclerView.VERTICAL
                }
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

    companion object {

        @JvmStatic
        fun newInstance(replies:ArrayList<DiscussionReply>) =
            DiscussionReplyFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("replies", replies)
                }
            }
    }
}