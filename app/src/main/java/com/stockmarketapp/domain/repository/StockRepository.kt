package com.stockmarketapp.domain.repository

import android.app.DownloadManager.Query
import com.stockmarketapp.domain.model.CompanyListing
import com.stockmarketapp.util.Resource
import kotlinx.coroutines.flow.Flow

interface StockRepository {
    suspend fun getCompanyListings(
        fetchFromRemote : Boolean,
        query: String
    ) : Flow<Resource<List<CompanyListing>>>
}