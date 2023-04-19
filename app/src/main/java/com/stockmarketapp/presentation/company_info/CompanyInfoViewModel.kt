package com.stockmarketapp.presentation.company_info

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stockmarketapp.domain.model.CompanyInfoState
import com.stockmarketapp.domain.repository.StockRepository
import com.stockmarketapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompanyInfoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: StockRepository
): ViewModel() {

    private var state by mutableStateOf(CompanyInfoState())

    init{
        viewModelScope.launch {
            val symbol = savedStateHandle.get<String>("symbol") ?: return@launch
            state = state.copy(isLoading = true)
            val companyInfoResult = async {  repository.getCompanyInfo(symbol)}
            val intraDayInfoResult = async { repository.getIntraDayInfo(symbol)}

            //COMPANY INFO
            when(val result = companyInfoResult.await()){
                is Resource.Success -> {
                    state = state.copy(
                        company = result.data,
                        isLoading = false,
                        error = null
                    )
                }
                is Resource.Error -> {
                    state = state.copy(
                        isLoading = false,
                        error = result.message,
                        company = null
                    )
                }
                else -> Unit
            }

            //INTRA DAY INFO
            when(val result = intraDayInfoResult.await()){
                is Resource.Success -> {
                    state = state.copy(
                        stockInfo = result.data?: emptyList(),
                        isLoading = false,
                        error = null
                    )
                }
                is Resource.Error -> {
                    state = state.copy(
                        isLoading = false,
                        error = result.message,
                        company = null
                    )
                }
                else -> Unit
            }
        }
    }

}