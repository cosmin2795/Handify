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
import com.example.handify.presentation.job.JobListViewModel
import com.example.handify.ui.theme.*
import org.koin.compose.viewmodel.koinViewModel

private enum class Tab { HOME, MY_JOBS, MESSAGES, PROFILE }

@Composable
fun MainScreen(viewModel: JobListViewModel = koinViewModel()) {
    var selectedTab by rememberSaveable { mutableStateOf(Tab.HOME) }
    val state = viewModel.state

    Scaffold(
        containerColor = Sand,
        bottomBar = {
            BottomBar(selectedTab = selectedTab, onTabSelect = { selectedTab = it })
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
                    onJobClick = {},
                    onCategorySelect = viewModel::selectCategory,
                    onSortSelect = viewModel::selectSort,
                    onRetry = viewModel::loadJobs
                )
                Tab.MY_JOBS -> PlaceholderTab(label = "My Jobs")
                Tab.MESSAGES -> PlaceholderTab(label = "Messages")
                Tab.PROFILE -> PlaceholderTab(label = "Profile")
            }
        }
    }
}

@Composable
private fun BottomBar(selectedTab: Tab, onTabSelect: (Tab) -> Unit) {
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
                icon = R.drawable.ic_briefcase,
                label = "Jobs",
                selected = selectedTab == Tab.MY_JOBS,
                onClick = { onTabSelect(Tab.MY_JOBS) },
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
                        .clickable { },
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

@Composable
private fun PlaceholderTab(label: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = label, color = TextMuted)
    }
}
