package com.mobcast.discussion

import com.mobcast.data.api.APIService
import com.mobcast.data.utils.ResultState
import com.mobcast.data.utils.safeAPICall
import com.mobcast.discussion.models.DiscussionItems
import com.mobcast.discussion.models.Employees
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class DiscussionFragmentRepository @Inject constructor(private val retrofitAPIService: APIService, private val dispatcher: CoroutineDispatcher) {

    suspend fun getDiscussionItems():ResultState<DiscussionItems> {
        return safeAPICall(dispatcher) {
            retrofitAPIService.getDiscussionItems()
        }
    }

    suspend fun getEmployees():ResultState<Employees> {
        return safeAPICall(dispatcher) {
            retrofitAPIService.getEmployees()
        }
    }
}