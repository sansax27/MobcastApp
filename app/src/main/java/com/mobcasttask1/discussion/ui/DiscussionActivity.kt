package com.mobcasttask1.discussion.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mobcasttask1.R
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DiscussionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_discussion)
    }
}