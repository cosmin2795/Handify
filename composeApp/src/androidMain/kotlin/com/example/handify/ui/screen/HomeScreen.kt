package com.example.handify.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.handify.R
import com.example.handify.domain.model.Job
import com.example.handify.presentation.job.JobListState
import com.example.handify.ui.component.JobCard
import com.example.handify.ui.theme.*

private val CATEGORIES = listOf(
    "all" to "All",
    "trades" to "Trades",
    "cleaning" to "Cleaning",
    "moving" to "Moving",
    "garden" to "Garden",
    "events" to "Events",
    "repairs" to "Repairs",
    "transport" to "Transport"
)

private val SORTS = listOf(
    "recent" to "Recent",
    "budgetUp" to "Budget +",
    "budgetDown" to "Budget -",
    "near" to "Nearby"
)

@Composable
fun HomeScreen(
    state: JobListState,
    onJobClick: (Job) -> Unit,
    onCategorySelect: (String) -> Unit,
    onSortSelect: (String) -> Unit,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Sand)
    ) {
        TopBar()
        SearchBar()
        CategoryFilterRow(selected = state.selectedCategory, onSelect = onCategorySelect)
        SortRow(selected = state.selectedSort, onSelect = onSortSelect)

        when {
            state.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Forest)
                }
            }
            state.error != null -> {
                val errorMsg = state.error ?: ""
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = errorMsg,
                            color = TextMuted,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onRetry,
                            colors = ButtonDefaults.buttonColors(containerColor = Forest)
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }
            state.filteredJobs.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "No jobs found.", color = TextMuted, fontSize = 14.sp)
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 100.dp)
                ) {
                    items(state.filteredJobs, key = { it.id }) { job ->
                        JobCard(job = job, onClick = { onJobClick(job) })
                    }
                }
            }
        }
    }
}

@Composable
private fun TopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(R.drawable.ic_location),
                contentDescription = null,
                tint = Forest,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "New York",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = SlateDark
            )
        }
        Text(
            text = "Handify",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = SlateDark
        )
        Icon(
            painter = painterResource(R.drawable.ic_bell),
            contentDescription = "Notifications",
            tint = Slate,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun SearchBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .border(1.dp, Grey200, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .background(Cream)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_search),
            contentDescription = null,
            tint = Grey400,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "Search for a service...",
            fontSize = 14.sp,
            color = Grey400,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun CategoryFilterRow(selected: String, onSelect: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CATEGORIES.forEach { (id, label) ->
            val isSelected = selected == id
            val shape = RoundedCornerShape(8.dp)
            Box(
                modifier = Modifier
                    .then(if (!isSelected) Modifier.border(1.dp, Grey200, shape) else Modifier)
                    .clip(shape)
                    .background(if (isSelected) Forest else Cream)
                    .clickable { onSelect(id) }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = label,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSelected) Color.White else TextMid
                )
            }
        }
    }
}

@Composable
private fun SortRow(selected: String, onSelect: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        SORTS.forEach { (id, label) ->
            val isSelected = selected == id
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isSelected) SlateDark else Color.Transparent)
            ) {
                TextButton(
                    onClick = { onSelect(id) },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text(
                        text = label.uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else Grey500,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}
