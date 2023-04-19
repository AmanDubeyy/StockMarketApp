package com.stockmarketapp.di

import com.stockmarketapp.data.csv.CSVParser
import com.stockmarketapp.data.csv.CompanyListingParser
import com.stockmarketapp.data.csv.IntraDayInfoParser
import com.stockmarketapp.data.repository.StockRepositoryImpl
import com.stockmarketapp.domain.model.CompanyListing
import com.stockmarketapp.domain.model.IntraDayInfo
import com.stockmarketapp.domain.repository.StockRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCompanyListingParser(
        companyListingParser: CompanyListingParser
    ) : CSVParser<CompanyListing>

    @Binds
    @Singleton
    abstract fun bindIntraDayInfoParser(
        intraDayInfoParser: IntraDayInfoParser
    ) : CSVParser<IntraDayInfo>


    @Binds
    @Singleton
    abstract fun bindStockRepository(
        stockRepositoryImpl: StockRepositoryImpl
    ) : StockRepository

}