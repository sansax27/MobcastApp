package com.mobcast.discussion.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mobcast.data.utils.ResultState
import com.mobcast.discussion.DiscussionFragmentRepository
import com.mobcast.discussion.models.DiscussionItem
import timber.log.Timber
import javax.inject.Inject

class DiscussionPagingSource @Inject constructor(
    private val discussionFragmentRepository: DiscussionFragmentRepository
) : PagingSource<Int, DiscussionItem>() {
    override fun getRefreshKey(state: PagingState<Int, DiscussionItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DiscussionItem> {
        try {
            when(val response = discussionFragmentRepository.getDiscussionItems()) {
                is ResultState.Success -> {
                    return LoadResult.Page(
                        data = response.data.discussionItems ?: emptyList(),
                        prevKey = null,
                        nextKey = params.key?.plus(1) ?: 2
                    )
                }
                is ResultState.GenericError -> throw java.lang.Exception(response.message)
                is ResultState.HttpError -> throw java.lang.Exception("${response.code} ${response.message}")
                is ResultState.NetworkError -> throw java.lang.Exception("Network Error")
            }
        } catch (e: Exception) {
            Timber.e(e)
            return LoadResult.Error(e)
        }
    }

}