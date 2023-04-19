package com.stockmarketapp.data.remote

import com.stockmarketapp.data.remote.dto.CompanyInfoDto
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface StockApi {

    @GET("query?function=LISTING_STATUS")
    suspend fun getListings(
        @Query("apikey") apikey : String = API_KEY
    ): ResponseBody

    @GET("query?function=TIME_SERIES_INTRADAY&interval=60min&datatype=csv")
    suspend fun getIntraDayInfo(
        @Query("symbol") symbol : String,
        @Query("apiKey") apikey: String = API_KEY
    ) : ResponseBody

    @GET("query?function=OVERVIEW")
    suspend fun getCompanyInfo(
        @Query("symbol") symbol : String,
        @Query("apiKey") apikey: String = API_KEY
    ) : CompanyInfoDto

    companion object {
        const val BASE_URL = "https://alphavantage.co"
        const val API_KEY = "M5MZL4R513G1J9JF"
    }
}

