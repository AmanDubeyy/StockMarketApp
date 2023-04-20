package com.stockmarketapp.presentation.company_info

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.plcoding.stockmarketapp.ui.theme.DarkBlue
import com.ramcosta.composedestinations.annotation.Destination

@Composable
@Destination
fun CompanyInfoScreen(
    symbol : String,
    vm : CompanyInfoViewModel = hiltViewModel()
) {
    val state = vm.state

    if(state.error == null){
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(DarkBlue)
        ){
            state.company?.let{company->
                Text(
                    text = company.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = company.symbol,
                    fontStyle = FontStyle.Italic,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Divider(
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = company.industry,
                    fontStyle = FontStyle.Italic,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth(),
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = company.country,
                    fontStyle = FontStyle.Italic,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth(),
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Divider(
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = company.description,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth(),
                )
                if(state.stockInfo.isNotEmpty()){
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Market Summary")
                    Spacer(modifier = Modifier.height(16.dp))
                    StockChart(
                        infos = state.stockInfo,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .align(CenterHorizontally)
                    )
                }
            }
        }
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        if(state.isLoading){
            CircularProgressIndicator()
        }else if(state.error != null){
            Text(
                text = state.error,
                color = MaterialTheme.colors.onBackground
            )
        }
    }
}