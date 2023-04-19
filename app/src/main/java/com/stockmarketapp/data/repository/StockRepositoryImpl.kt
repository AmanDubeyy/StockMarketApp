package com.stockmarketapp.data.repository

import com.stockmarketapp.data.csv.CSVParser
import com.stockmarketapp.data.csv.CompanyListingParser
import com.stockmarketapp.data.local.StockDatabase
import com.stockmarketapp.data.mapper.toCompanyListing
import com.stockmarketapp.data.mapper.toCompanyListingEntity
import com.stockmarketapp.data.remote.StockApi
import com.stockmarketapp.domain.model.CompanyListing
import com.stockmarketapp.domain.repository.StockRepository
import com.stockmarketapp.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import java.net.HttpCookie
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepositoryImpl @Inject constructor(
    val api : StockApi,
    val db : StockDatabase,
    val companyListingParser: CSVParser<CompanyListing>
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
                emit(Resource.Error("Error"))
                null
            }catch (e : HttpException){
                e.printStackTrace()
                emit(Resource.Error("Error"))
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

}