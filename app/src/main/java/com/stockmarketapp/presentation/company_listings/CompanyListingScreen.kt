package com.stockmarketapp.presentation.company_listings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.stockmarketapp.presentation.company_info.CompanyInfoScreen
import com.stockmarketapp.presentation.destinations.CompanyInfoScreenDestination

@Composable
@Destination(start = true)
fun CompanyListingScreen(
    navigator : DestinationsNavigator,
    vm : CompanyListingsViewModel = hiltViewModel()
) {
    val state = vm.state
    val swipeRefreshState = rememberSwipeRefreshState(
        isRefreshing = state.isRefreshing
    )

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        OutlinedTextField(
            value = state.query,
            onValueChange = {
                vm.onEvent(CompanyListingsEvent.OnSearchQueryChange(it))
            },
            placeholder = {
                Text(text = "SEARCH...")
            },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            maxLines = 1,
            singleLine = true
        )
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = {
                vm.onEvent(CompanyListingsEvent.Refresh)
            }
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ){
                items(state.companies.size){ i ->
                    val company = state.companies[i]
                    CompanyItem(
                        company = company,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navigator.navigate(
                                    CompanyInfoScreenDestination(symbol = company.symbol)
                                )
                            }
                            .padding(16.dp)
                    )
                    if(i < state.companies.size){
                        Divider(
                            modifier = Modifier.padding(
                                    horizontal = 16.dp
                                )
                        )
                    }
                }
            }
        }
    }
}