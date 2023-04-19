package com.stockmarketapp.domain.repository

import com.stockmarketapp.domain.model.CompanyInfo
import com.stockmarketapp.domain.model.CompanyListing
import com.stockmarketapp.domain.model.IntraDayInfo
import com.stockmarketapp.util.Resource
import kotlinx.coroutines.flow.Flow

interface StockRepository {
    suspend fun getCompanyListings(
        fetchFromRemote : Boolean,
        query: String
    ) : Flow<Resource<List<CompanyListing>>>

    suspend fun getCompanyInfo(symbol : String) : Resource<CompanyInfo>

    suspend fun getIntraDayInfo(symbol : String) : Resource<List<IntraDayInfo>>
}