package com.mobcast.discussion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.mobcast.discussion.paging.DiscussionPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DiscussionFragmentViewModel @Inject constructor(private val discussionPagingSource: DiscussionPagingSource): ViewModel() {

    val discussionItemLiveData = Pager(PagingConfig(3)) {
        discussionPagingSource
    }.flow.cachedIn(viewModelScope).asLiveData()
}