package com.example.handify.ui.screen

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.LaunchedEffect
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
import com.example.handify.domain.model.SavedAddress
import com.example.handify.presentation.job.JobListState
import com.example.handify.presentation.location.LocationState
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: JobListState,
    locationState: LocationState,
    onJobClick: (Job) -> Unit,
    onCategorySelect: (String) -> Unit,
    onSortSelect: (String) -> Unit,
    onRetry: () -> Unit,
    onLoadUserLocation: () -> Unit = {},
    onLocationPillClick: () -> Unit = {},
    onLocationSelect: (String) -> Unit = {},
    onLocationDismiss: () -> Unit = {},
    onLocationRemove: (String) -> Unit = {},
    onLocationSearch: (String) -> Unit = {},
    onOpenAddForm: () -> Unit = {},
    onCloseAddForm: () -> Unit = {},
    onNewAddressTextChange: (String) -> Unit = {},
    onNewAddressLabelChange: (String) -> Unit = {},
    onSaveAddress: () -> Unit = {}
) {
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        if (grants[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            grants[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            onLoadUserLocation()
        }
    }
    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Sand)
    ) {
        TopBar(displayCity = locationState.displayCity, onLocationPillClick = onLocationPillClick)
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
                        JobCard(
                            job = job,
                            onClick = { onJobClick(job) },
                            userLat = state.userLat,
                            userLng = state.userLng
                        )
                    }
                }
            }
        }
    }

    if (locationState.showModal) {
        ModalBottomSheet(
            onDismissRequest = onLocationDismiss,
            containerColor = Sand,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        ) {
            if (locationState.showAddForm) {
                AddressForm(
                    state = locationState,
                    onTextChange = onNewAddressTextChange,
                    onLabelChange = onNewAddressLabelChange,
                    onSave = onSaveAddress,
                    onCancel = onCloseAddForm
                )
            } else {
                LocationSheetContent(
                    locationState = locationState,
                    onSearch = onLocationSearch,
                    onSelect = onLocationSelect,
                    onRemove = onLocationRemove,
                    onAddNew = onOpenAddForm
                )
            }
        }
    }
}

@Composable
private fun TopBar(displayCity: String, onLocationPillClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Handify",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = SlateDark
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Grey100)
                    .clickable { onLocationPillClick() }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_location),
                    contentDescription = null,
                    tint = Forest,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = displayCity,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SlateDark
                )
                Spacer(modifier = Modifier.width(2.dp))
                Icon(
                    painter = painterResource(R.drawable.ic_chevron_right),
                    contentDescription = null,
                    tint = Grey400,
                    modifier = Modifier.size(12.dp)
                )
            }
            Icon(
                painter = painterResource(R.drawable.ic_bell),
                contentDescription = "Notifications",
                tint = Slate,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun LocationSheetContent(
    locationState: LocationState,
    onSearch: (String) -> Unit,
    onSelect: (String) -> Unit,
    onRemove: (String) -> Unit,
    onAddNew: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Your Location",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = SlateDark,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = locationState.searchQuery,
            onValueChange = onSearch,
            placeholder = { Text("Search city or address...", color = Grey400) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Forest,
                unfocusedBorderColor = Grey200,
                focusedContainerColor = Cream,
                unfocusedContainerColor = Cream
            ),
            singleLine = true,
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = null,
                    tint = Grey400,
                    modifier = Modifier.size(18.dp)
                )
            }
        )
        Spacer(modifier = Modifier.height(12.dp))
        locationState.filteredAddresses.forEach { address ->
            SheetAddressRow(
                address = address,
                isActive = address.id == locationState.activeAddressId,
                onSelect = { onSelect(address.id) },
                onRemove = { onRemove(address.id) }
            )
        }
        TextButton(
            onClick = onAddNew,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                text = "+ Add new address",
                color = Forest,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SheetAddressRow(
    address: SavedAddress,
    isActive: Boolean,
    onSelect: () -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_location),
            contentDescription = null,
            tint = Forest,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = address.label,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = SlateDark
            )
            Text(
                text = address.fullAddress,
                fontSize = 12.sp,
                color = Grey500
            )
        }
        if (isActive) {
            Icon(
                painter = painterResource(R.drawable.ic_check),
                contentDescription = "Active",
                tint = Forest,
                modifier = Modifier.size(18.dp)
            )
        } else {
            IconButton(onClick = onRemove, modifier = Modifier.size(36.dp)) {
                Icon(
                    painter = painterResource(R.drawable.ic_trash),
                    contentDescription = "Remove",
                    tint = Grey400,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
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
