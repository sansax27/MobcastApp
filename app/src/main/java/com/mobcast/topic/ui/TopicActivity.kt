package com.mobcast.topic.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.children
import androidx.core.view.isVisible
import coil.load
import com.mobcast.R
import com.mobcast.data.utils.UIState
import com.mobcast.databinding.ActivityTopicBinding
import com.mobcast.databinding.TopicItemBinding
import com.mobcast.discussion.ui.DiscussionActivity
import com.mobcast.topic.TopicViewModel
import com.mobcast.topic.models.Topic
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class TopicActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityTopicBinding
    private val binding:ActivityTopicBinding get() = _binding
    private val addedTopics = mutableListOf<Topic>()
    private val viewModel:TopicViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityTopicBinding.inflate(layoutInflater)
        val translationXAmount = 35*resources.displayMetrics.density

        viewModel.topicLoadStatus.observe(this) {
            when(it) {
                is UIState.Success -> {
                    binding.topicsGroup.apply {
                        it.data.topics?.let { list ->
                            for (topic in list) {
                                val topicItem = TopicItemBinding.inflate(layoutInflater)
                                topicItem.topicName.text = topic.tagName
                                topicItem.topicName.isSelected = false
                                topic.backgroundColor?.let { backgroundColor ->
                                    topicItem.topicImageCardView.setCardBackgroundColor(Color.parseColor("#%s".format(backgroundColor)))
                                }
                                topic.icon?.let { icon ->
                                    icon.iconURL.let { imageURL ->
                                        topicItem.topicImage.load(imageURL)
                                    }
                                }
                                topic.tagName.let { tagName ->
                                    topicItem.topicName.text = tagName
                                }
                                topicItem.topicName.setOnClickListener { textView ->
                                    textView as TextView
                                    textView.background = if (textView.isSelected) {
                                        addedTopics.remove(topic)
                                        if (addedTopics.isEmpty()) {
                                            binding.resetButton.isVisible = false
                                            binding.resetButton.translationX = 0f
                                            binding.showResultButton.translationX = 0f
                                        }
                                        AppCompatResources.getDrawable(
                                            this@TopicActivity,
                                            R.drawable.circular_grey_background
                                        )
                                    } else {
                                        addedTopics.add(topic)
                                        if (addedTopics.size == 1 && !binding.resetButton.isVisible) {
                                            binding.resetButton.isVisible = true
                                            binding.resetButton.translationX = -translationXAmount
                                            binding.showResultButton.translationX =
                                                translationXAmount
                                        }
                                        AppCompatResources.getDrawable(
                                            this@TopicActivity,
                                            R.drawable.circular_accent_background
                                        )
                                    }
                                    textView.isSelected = !textView.isSelected
                                }
                                addView(topicItem.root)
                            }
                        }
                        binding.topicPB.visibility = View.GONE
                        binding.topicsGroup.visibility = View.VISIBLE
                    }
                }
                is UIState.Failure -> {
                    if (it.message=="Network Error") {
                        Toast.makeText(this, getString(R.string.networkError), Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    }
                }
                else -> {}
            }
        }
        binding.topicsToolbar.topicHeading.text = getString(R.string.selectTopics)

        binding.resetButton.setOnClickListener {
            for (topicItem in binding.topicsGroup.children) {
                topicItem.findViewById<TextView>(R.id.topicName).apply {
                    if (isSelected) {
                        callOnClick()
                    }
                }
            }
        }
        binding.showResultButton.setOnClickListener {
            startActivity(Intent(this, DiscussionActivity::class.java))
        }
        setContentView(binding.root)
    }

}