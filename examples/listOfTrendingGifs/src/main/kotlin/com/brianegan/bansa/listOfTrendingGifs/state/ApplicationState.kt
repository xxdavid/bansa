package com.brianegan.bansa.listOfTrendingGifs.state

import com.brianegan.bansa.listOfTrendingGifs.api.NextPage
import com.brianegan.bansa.listOfTrendingGifs.models.Gif
import rx.Subscription
import rx.subscriptions.Subscriptions

data class ApplicationState(val isRefreshing: Boolean = true,
                            val isFetching: Boolean = false,
                            val gifs: List<Gif> = listOf(),
                            val pagination: NextPage = NextPage(),
                            val currentRequest: Subscription = Subscriptions.empty())
