package com.mobcast.topic

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobcast.data.utils.ResultState
import com.mobcast.data.utils.UIState
import com.mobcast.topic.models.Topics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TopicViewModel @Inject constructor(private val topicRepository: TopicRepository) :
    ViewModel() {

    private val topicLoadStatusPrivate = MutableLiveData<UIState<Topics>>(UIState.Empty)
    val topicLoadStatus: LiveData<UIState<Topics>> get() = topicLoadStatusPrivate

    init {
        getTopics()
    }

    private fun getTopics() = viewModelScope.launch {
        when (val response = topicRepository.getTopics()) {
            is ResultState.GenericError -> topicLoadStatusPrivate.postValue(UIState.Failure(response.message))
            is ResultState.NetworkError -> topicLoadStatusPrivate.postValue(UIState.Failure("Network Error"))
            is ResultState.HttpError -> topicLoadStatusPrivate.postValue(UIState.Failure("${response.code} ${response.message}"))
            is ResultState.Success -> topicLoadStatusPrivate.postValue(UIState.Success(response.data))
        }
    }
}