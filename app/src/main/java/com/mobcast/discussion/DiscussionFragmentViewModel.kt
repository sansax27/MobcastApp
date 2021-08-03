package com.mobcast.discussion

import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.mobcast.data.utils.ResultState
import com.mobcast.data.utils.UIState
import com.mobcast.discussion.models.Employees
import com.mobcast.discussion.paging.DiscussionPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiscussionFragmentViewModel @Inject constructor(private val discussionPagingSource: DiscussionPagingSource, private val discussionFragmentRepository: DiscussionFragmentRepository): ViewModel() {

    val discussionItemLiveData = Pager(PagingConfig(3)) {
        discussionPagingSource
    }.flow.cachedIn(viewModelScope).asLiveData()

    private val getEmployeesStatusPrivate = MutableLiveData<UIState<Employees>>(UIState.Empty)
    val getEmployeesStatus : LiveData<UIState<Employees>> get() = getEmployeesStatusPrivate

    init {
        getEmployees()
    }

    private fun getEmployees() = viewModelScope.launch {
        getEmployeesStatusPrivate.postValue(UIState.Loading)
        when (val response = discussionFragmentRepository.getEmployees()) {
            is ResultState.GenericError -> getEmployeesStatusPrivate.postValue(UIState.Failure(response.message))
            is ResultState.NetworkError -> getEmployeesStatusPrivate.postValue(UIState.Failure("Network Error"))
            is ResultState.HttpError -> getEmployeesStatusPrivate.postValue(UIState.Failure("${response.code} ${response.message}"))
            is ResultState.Success -> getEmployeesStatusPrivate.postValue(UIState.Success(response.data))
        }
    }

}