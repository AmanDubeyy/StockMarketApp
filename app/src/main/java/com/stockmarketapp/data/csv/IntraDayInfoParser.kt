package com.stockmarketapp.data.csv

import com.opencsv.CSVReader
import com.stockmarketapp.data.mapper.toIntraDayInfo
import com.stockmarketapp.data.remote.dto.IntraDayInfoDto
import com.stockmarketapp.domain.model.CompanyListing
import com.stockmarketapp.domain.model.IntraDayInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.InputStreamReader
import java.time.LocalDateTime
import javax.inject.Inject

class IntraDayInfoParser @Inject constructor() : CSVParser<IntraDayInfo> {

    override suspend fun parse(stream: InputStream): List<IntraDayInfo> {
        val csvReader = CSVReader(InputStreamReader(stream))
        return withContext(Dispatchers.IO){
            csvReader.readAll()
                .drop(1)
                .mapNotNull { line->
                    val time = line.getOrNull(0)
                    val close = line.getOrNull(4)
                    val dto = IntraDayInfoDto(
                        timestamp = time?: return@mapNotNull null,
                        close = close?.toDouble() ?: return@mapNotNull null
                    )
                    dto.toIntraDayInfo()
                }
                .filter {
                    it.date.dayOfMonth == LocalDateTime.now().minusDays(1).dayOfMonth
                }
                .sortedBy {
                    it.date.hour
                }
                .also {
                    csvReader.close()
                }
        }
    }
}