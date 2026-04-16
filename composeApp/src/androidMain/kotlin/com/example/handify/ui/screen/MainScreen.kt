package com.example.handify.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.example.handify.domain.model.JobCategory
import com.example.handify.domain.model.JobStatus
import com.example.handify.presentation.job.JobListViewModel
import com.example.handify.presentation.job.MyJobsViewModel
import com.example.handify.presentation.job.PostJobState
import com.example.handify.presentation.location.LocationViewModel
import com.example.handify.ui.theme.*
import org.koin.compose.viewmodel.koinViewModel

private enum class Tab { HOME, WORKERS, MESSAGES, PROFILE }

@Composable
fun MainScreen(
    onLogOut: () -> Unit,
    jobViewModel: JobListViewModel = koinViewModel(),
    locationViewModel: LocationViewModel = koinViewModel(),
    myJobsViewModel: MyJobsViewModel = koinViewModel()
) {
    var selectedTab by rememberSaveable { mutableStateOf(Tab.HOME) }
    var showPostJob by rememberSaveable { mutableStateOf(false) }
    var showAddresses by rememberSaveable { mutableStateOf(false) }
    var selectedJob by remember { mutableStateOf<Job?>(null) }
    var selectedMyJob by remember { mutableStateOf<Job?>(null) }
    val state = jobViewModel.state
    val locationState = locationViewModel.state
    val myJobsState = myJobsViewModel.state

    var contactWorkerName by remember { mutableStateOf<String?>(null) }
    var openChatWithJob by remember { mutableStateOf<Pair<String, Job>?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Sand,
            bottomBar = {
                BottomBar(
                    selectedTab = selectedTab,
                    onTabSelect = { selectedTab = it },
                    onPostJob = { showPostJob = true }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                when (selectedTab) {
                    Tab.HOME -> HomeScreen(
                        state = state,
                        locationState = locationState,
                        onJobClick = { selectedJob = it },
                        onCategorySelect = jobViewModel::selectCategory,
                        onSortSelect = jobViewModel::selectSort,
                        onRetry = jobViewModel::loadJobs,
                        onLoadUserLocation = jobViewModel::loadUserLocation,
                        onLocationPillClick = locationViewModel::openModal,
                        onLocationSelect = locationViewModel::selectAddress,
                        onLocationDismiss = locationViewModel::closeModal,
                        onLocationRemove = locationViewModel::removeAddress,
                        onLocationSearch = locationViewModel::updateSearch,
                        onOpenAddForm = { locationViewModel.openAddForm() },
                        onCloseAddForm = locationViewModel::closeAddForm,
                        onNewAddressTextChange = locationViewModel::updateNewAddressText,
                        onNewAddressLabelChange = locationViewModel::updateNewAddressLabel,
                        onSaveAddress = locationViewModel::saveAddress
                    )
                    Tab.WORKERS -> WorkersScreen(
                        onContact = { workerName -> contactWorkerName = workerName }
                    )
                    Tab.MESSAGES -> MessagesScreen(
                        openChatWithJob = openChatWithJob,
                        onJobClick = { selectedMyJob = it }
                    )
                    Tab.PROFILE -> ProfileScreen(
                        onLogOut = onLogOut,
                        onSavedAddressesClick = { showAddresses = true },
                        onJobClick = { selectedMyJob = it }
                    )
                }
            }
        }

        if (showPostJob) {
            PostJobScreen(
                onDismiss = {
                    showPostJob = false
                    contactWorkerName = null
                },
                onViewMyJobs = {
                    showPostJob = false
                    selectedTab = Tab.PROFILE
                },
                workerName = contactWorkerName,
                onStartChat = if (contactWorkerName != null) { postState: PostJobState ->
                    val workerName = contactWorkerName!!
                    val job = postState.toJob()
                    openChatWithJob = workerName to job
                    showPostJob = false
                    contactWorkerName = null
                    selectedTab = Tab.MESSAGES
                } else null
            )
        }

        if (showAddresses) {
            AddressesScreen(
                onDismiss = { showAddresses = false },
                viewModel = locationViewModel
            )
        }

        selectedJob?.let { job ->
            JobDetailScreen(
                job = job,
                onDismiss = { selectedJob = null }
            )
        }

        selectedMyJob?.let { job ->
            MyJobDetailScreen(
                job = job,
                onDismiss = { selectedMyJob = null }
            )
        }

        contactWorkerName?.let { workerName ->
            if (!showPostJob) {
                ContactWorkerSheet(
                    workerName = workerName,
                    jobs = myJobsState.jobs,
                    isLoading = myJobsState.isLoading,
                    onDismiss = { contactWorkerName = null },
                    onStartConversation = { job ->
                        openChatWithJob = workerName to job
                        contactWorkerName = null
                        selectedTab = Tab.MESSAGES
                    },
                    onCreateJob = {
                        showPostJob = true
                    }
                )
            }
        }
    }
}

private fun PostJobState.toJob(): Job {
    val budgetVal = budget.replace(Regex("[^\\d.]"), "").toDoubleOrNull() ?: 0.0
    return Job(
        id = System.currentTimeMillis().toString(),
        title = title.ifBlank { "New Job" },
        description = "",
        category = category ?: JobCategory.TRADES,
        location = location.ifBlank { "TBD" },
        budgetMin = budgetVal,
        budgetMax = budgetVal,
        duration = duration.ifBlank { "Flexible" },
        status = if (isDraft) JobStatus.DRAFT else JobStatus.ACTIVE,
        isUrgent = isUrgent,
        clientId = "",
        clientName = "",
        clientRating = 0.0,
        applicantsCount = 0,
        lat = lat,
        lng = lng,
        createdAt = System.currentTimeMillis()
    )
}

@Composable
private fun BottomBar(selectedTab: Tab, onTabSelect: (Tab) -> Unit, onPostJob: () -> Unit) {
    Surface(
        color = Cream,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .height(64.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(
                icon = R.drawable.ic_home,
                label = "Home",
                selected = selectedTab == Tab.HOME,
                onClick = { onTabSelect(Tab.HOME) },
                modifier = Modifier.weight(1f)
            )
            NavItem(
                icon = R.drawable.ic_workers,
                label = "Workers",
                selected = selectedTab == Tab.WORKERS,
                onClick = { onTabSelect(Tab.WORKERS) },
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Ember)
                        .clickable { onPostJob() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_plus),
                        contentDescription = "Post a job",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            NavItem(
                icon = R.drawable.ic_chat,
                label = "Messages",
                selected = selectedTab == Tab.MESSAGES,
                onClick = { onTabSelect(Tab.MESSAGES) },
                modifier = Modifier.weight(1f)
            )
            NavItem(
                icon = R.drawable.ic_user,
                label = "Profile",
                selected = selectedTab == Tab.PROFILE,
                onClick = { onTabSelect(Tab.PROFILE) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun NavItem(
    icon: Int,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = label,
            tint = if (selected) Forest else Grey400,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = if (selected) Forest else Grey400,
            letterSpacing = 0.5.sp
        )
    }
}
