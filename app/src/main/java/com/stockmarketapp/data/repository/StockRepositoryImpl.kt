package com.stockmarketapp.data.repository

import com.stockmarketapp.data.csv.CSVParser
import com.stockmarketapp.data.csv.IntraDayInfoParser
import com.stockmarketapp.data.local.StockDatabase
import com.stockmarketapp.data.mapper.toCompanyInfo
import com.stockmarketapp.data.mapper.toCompanyListing
import com.stockmarketapp.data.mapper.toCompanyListingEntity
import com.stockmarketapp.data.remote.StockApi
import com.stockmarketapp.domain.model.CompanyInfo
import com.stockmarketapp.domain.model.CompanyListing
import com.stockmarketapp.domain.model.IntraDayInfo
import com.stockmarketapp.domain.repository.StockRepository
import com.stockmarketapp.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepositoryImpl @Inject constructor(
    private val api : StockApi,
    private val db : StockDatabase,
    private val companyListingParser: CSVParser<CompanyListing>,
    private val intraDayParser: CSVParser<IntraDayInfo>
) : StockRepository {

    private val dao = db.dao

    override suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>> {
        return flow{
            emit(Resource.Loading(true))
            val localListings = dao.searchCompanyListing(query)
            emit(Resource.Success(
                data = localListings.map { it.toCompanyListing() }
            ))

            val isDbEmpty = localListings.isEmpty() && query.isBlank()
            val shouldLoadFromCache = !isDbEmpty && !fetchFromRemote
            if(shouldLoadFromCache){
                emit(Resource.Loading(false))
                return@flow
            }

            val remoteListings = try{
                val response = api.getListings()
                companyListingParser.parse(response.byteStream())
            }catch (e : IOException){
                e.printStackTrace()
                emit(Resource.Error("Error loading list"))
                null
            }catch (e : HttpException){
                e.printStackTrace()
                emit(Resource.Error("Error loading list"))
                null
            }

            remoteListings?.let { listings ->
                dao.clearCompanyListing()
                dao.insertCompanyListings(
                    listings.map { it.toCompanyListingEntity() }
                )

                emit(Resource.Success(
                    data = dao
                        .searchCompanyListing("")
                        .map { it.toCompanyListing() }
                ))
                emit(Resource.Loading(false))
            }
        }
    }

    override suspend fun getCompanyInfo(symbol: String): Resource<CompanyInfo> {
        return try{
            val result = api.getCompanyInfo(symbol)
            Resource.Success(result.toCompanyInfo())
        }catch (e : IOException){
            e.printStackTrace()
            Resource.Error(
                message = "Could not load Company info"
            )
        }catch (e : HttpException){
            e.printStackTrace()
            Resource.Error(
                message = "Could not load Company info"
            )
        }
    }

    override suspend fun getIntraDayInfo(symbol: String): Resource<List<IntraDayInfo>> {
        return try{
            val response = api.getIntraDayInfo(symbol)
            val result = intraDayParser.parse(response.byteStream())
            Resource.Success(data = result)
        }catch (e : IOException){
            e.printStackTrace()
            Resource.Error(
                message = "Could not load IntraDay info"
            )
        }catch (e : HttpException){
            e.printStackTrace()
            Resource.Error(
                message = "Could not load IntraDay info"
            )
        }
    }
}