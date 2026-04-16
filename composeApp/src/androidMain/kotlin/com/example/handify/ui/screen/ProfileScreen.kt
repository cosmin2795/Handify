package com.example.handify.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.handify.R
import com.example.handify.domain.model.Job
import com.example.handify.domain.model.JobStatus
import com.example.handify.domain.model.User
import com.example.handify.presentation.job.MyJobsViewModel
import com.example.handify.presentation.profile.ProfileViewModel
import com.example.handify.ui.component.AvatarInitials
import com.example.handify.ui.theme.*
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileScreen(
    onLogOut: () -> Unit,
    onSavedAddressesClick: () -> Unit = {},
    onJobClick: (Job) -> Unit = {},
    viewModel: ProfileViewModel = koinViewModel(),
    myJobsViewModel: MyJobsViewModel = koinViewModel()
) {
    val state = viewModel.state
    val myJobsState = myJobsViewModel.state

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Sand),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        item { ProfileHeader(user = state.user) }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item {
            MyJobsSection(
                selectedStatus = myJobsState.selectedStatus,
                jobs = myJobsState.filteredJobs,
                isLoading = myJobsState.isLoading,
                onStatusSelect = myJobsViewModel::selectStatus,
                onJobClick = onJobClick
            )
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item {
            SettingsSection(
                onSavedAddressesClick = onSavedAddressesClick,
                onLogOut = {
                    viewModel.logOut()
                    onLogOut()
                }
            )
        }
    }
}

@Composable
private fun ProfileHeader(user: User?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Forest)
            .padding(top = 28.dp, bottom = 28.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val initials = user?.name
                ?.split(" ")
                ?.mapNotNull { it.firstOrNull()?.uppercaseChar() }
                ?.take(2)
                ?.joinToString("")
                ?: "?"

            Box {
                AvatarInitials(initials = initials, size = 72, bg = ForestLight)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.5.dp, Forest, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_check),
                        contentDescription = "Verified",
                        tint = Forest,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = user?.name ?: "",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = user?.email ?: "",
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.75f)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(value = "5", label = "Jobs Posted")
                StatDivider()
                StatItem(value = "3", label = "Applications")
                StatDivider()
                StatItem(value = "4.8", label = "Rating")
            }
        }
    }
}

@Composable
private fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White.copy(alpha = 0.65f),
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
private fun StatDivider() {
    Box(
        modifier = Modifier
            .height(36.dp)
            .width(1.dp)
            .background(Color.White.copy(alpha = 0.25f))
    )
}

@Composable
private fun MyJobsSection(
    selectedStatus: JobStatus,
    jobs: List<Job>,
    isLoading: Boolean,
    onStatusSelect: (JobStatus) -> Unit,
    onJobClick: (Job) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "My Jobs",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = SlateDark,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(
                JobStatus.ACTIVE to "Active",
                JobStatus.DRAFT to "Drafts",
                JobStatus.COMPLETED to "Completed"
            ).forEach { (status, label) ->
                val isSelected = selectedStatus == status
                val shape = RoundedCornerShape(8.dp)
                Box(
                    modifier = Modifier
                        .then(if (!isSelected) Modifier.border(1.dp, Grey200, shape) else Modifier)
                        .clip(shape)
                        .background(if (isSelected) SlateDark else Cream)
                        .clickable { onStatusSelect(status) }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
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
        Spacer(modifier = Modifier.height(10.dp))
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxWidth().height(80.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Forest, modifier = Modifier.size(24.dp))
                }
            }
            jobs.isEmpty() -> {
                Box(modifier = Modifier.fillMaxWidth().height(80.dp), contentAlignment = Alignment.Center) {
                    Text(text = "No jobs here yet.", color = TextMuted, fontSize = 14.sp)
                }
            }
            else -> {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    jobs.forEach { job ->
                        ProfileJobCard(job = job, onClick = { onJobClick(job) })
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileJobCard(job: Job, onClick: () -> Unit) {
    val catColor = CategoryColors[job.category.name] ?: Forest

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Cream)
            .clickable(onClick = onClick)
            .height(IntrinsicSize.Min)
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
                .background(catColor)
        )
        Column(modifier = Modifier.padding(12.dp).weight(1f)) {
            Text(
                text = job.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = SlateDark,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = job.location, fontSize = 12.sp, color = Grey500)
        }
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.End
        ) {
            if (job.applicantsCount > 0) {
                Text(
                    text = "👥 ${job.applicantsCount}",
                    fontSize = 12.sp,
                    color = Grey500,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Icon(
                painter = painterResource(R.drawable.ic_chevron_right),
                contentDescription = null,
                tint = Grey400,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
private fun SettingsSection(onSavedAddressesClick: () -> Unit, onLogOut: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Cream)
    ) {
        SettingsItem(icon = R.drawable.ic_user, label = "My Applications")
        SettingsDivider()
        SettingsItem(icon = R.drawable.ic_bookmark, label = "Saved Jobs")
        SettingsDivider()
        SettingsItem(icon = R.drawable.ic_star, label = "My Reviews")
        SettingsDivider()
        SettingsItem(icon = R.drawable.ic_location, label = "Saved Addresses", onClick = onSavedAddressesClick)
        SettingsDivider()
        SettingsItem(icon = R.drawable.ic_bell, label = "Notifications")
        SettingsDivider()
        SettingsItem(icon = R.drawable.ic_settings, label = "Account Settings")
        SettingsDivider()
        SettingsItem(icon = R.drawable.ic_help, label = "Help Center")
    }

    Spacer(modifier = Modifier.height(12.dp))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Cream)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onLogOut() }
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_logout),
                contentDescription = "Log Out",
                tint = Color(0xFFD64C3B),
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "Log Out",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFD64C3B)
            )
        }
    }
}

@Composable
private fun SettingsItem(icon: Int, label: String, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = label,
            tint = SlateDark,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = label,
            fontSize = 15.sp,
            color = SlateDark,
            modifier = Modifier.weight(1f)
        )
        Icon(
            painter = painterResource(R.drawable.ic_chevron_right),
            contentDescription = null,
            tint = Grey400,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        color = Grey100,
        thickness = 1.dp
    )
}
