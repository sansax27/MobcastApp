package com.mobcasttask1

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.children
import androidx.core.view.isVisible
import com.mobcasttask1.databinding.ActivityTopicBinding
import com.mobcasttask1.databinding.TopicItemBinding
import kotlin.random.Random

class TopicActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityTopicBinding
    private val binding:ActivityTopicBinding get() = _binding
    private val addedTopics = mutableListOf<Topic>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityTopicBinding.inflate(layoutInflater)
        val list = mutableListOf<Topic>()
        val icon = Icon(1,"PPPPP", "DDDD")
        for (i in 0..15) {
            val times = Random.nextInt(10)
            var s = "f"
            for (l in 1..times) {
                s += "f"
            }
            list.add(Topic(1,s, icon,"444444",1,"FFFF"))
        }
        val translationXAmount = 35*resources.displayMetrics.density
        binding.topicsToolbar.topicHeading.text = getString(R.string.selectTopics)
        binding.topicsGroup.apply {
            for (l in list) {
                val topicItem = TopicItemBinding.inflate(layoutInflater)
                topicItem.topicName.text = l.tagName
                topicItem.topicName.isSelected = false
                topicItem.topicName.setOnClickListener {
                    it as TextView
                    it.background = if (it.isSelected) {
                        addedTopics.remove(l)
                        if(addedTopics.isEmpty()) {
                            binding.resetButton.isVisible = false
                            binding.resetButton.translationX = 0f
                            binding.showResultButton.translationX = 0f
                        }
                        AppCompatResources.getDrawable( this@TopicActivity, R.drawable.circular_grey_background)
                    } else {
                        addedTopics.add(l)
                        if (addedTopics.size==1 && !binding.resetButton.isVisible) {
                            binding.resetButton.isVisible = true
                            binding.resetButton.translationX = -translationXAmount
                            binding.showResultButton.translationX = translationXAmount
                        }
                        AppCompatResources.getDrawable(this@TopicActivity, R.drawable.circular_accent_background)
                    }
                    it.isSelected = !it.isSelected
                }
                addView(topicItem.root)
            }
        }
        binding.resetButton.setOnClickListener {
            for (topicItem in binding.topicsGroup.children) {
                topicItem.findViewById<TextView>(R.id.topicName).apply {
                    if (isSelected) {
                        callOnClick()
                    }
                }
            }
        }
        setContentView(binding.root)
    }
}