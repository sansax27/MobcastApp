package com.mobcast.discussion.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.mobcast.R
import com.mobcast.databinding.ActivityDiscussionBinding
import com.mobcast.discussion.DiscussionItemAdapter
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DiscussionActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityDiscussionBinding
    private val binding:ActivityDiscussionBinding get() = _binding

    inner class DiscussionViewPagerAdapter: FragmentStateAdapter(this) {
        override fun getItemCount(): Int {
            return 3
        }

        override fun createFragment(position: Int): Fragment {
            return DiscussionFragment()
        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDiscussionBinding.inflate(layoutInflater)
        binding.discussionViewPager.adapter = DiscussionViewPagerAdapter()
        binding.topicsDiscussionToolbar.topicHeading.text = getString(R.string.discussionForum)
        val stringArray = resources.getStringArray(R.array.discussionSections)
        TabLayoutMediator(binding.discussionTabLayout, binding.discussionViewPager) { tab, position ->
            tab.text = stringArray[position]
        }.attach()
        setContentView(binding.root)
    }
}