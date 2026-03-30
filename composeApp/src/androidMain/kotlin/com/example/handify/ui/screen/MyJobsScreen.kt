package com.example.handify.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.handify.domain.model.Job
import com.example.handify.domain.model.JobStatus
import com.example.handify.presentation.job.MyJobsState
import com.example.handify.presentation.job.MyJobsViewModel
import com.example.handify.ui.component.AvatarInitials
import com.example.handify.ui.theme.*
import org.koin.compose.viewmodel.koinViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun MyJobsScreen(viewModel: MyJobsViewModel = koinViewModel()) {
    val state = viewModel.state

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Sand)
    ) {
        Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 8.dp)) {
            Text(
                text = "My Jobs",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = SlateDark
            )
            Spacer(modifier = Modifier.height(12.dp))
            StatusTabRow(
                selected = state.selectedStatus,
                onSelect = viewModel::selectStatus
            )
        }

        when {
            state.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Forest)
                }
            }
            state.error != null -> {
                val msg = state.error ?: ""
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = msg, color = TextMuted, fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 32.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = viewModel::loadMyJobs,
                            colors = ButtonDefaults.buttonColors(containerColor = Forest)) {
                            Text("Retry")
                        }
                    }
                }
            }
            state.filteredJobs.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "No jobs here yet.", color = TextMuted, fontSize = 14.sp)
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 4.dp, bottom = 100.dp)
                ) {
                    items(state.filteredJobs, key = { it.id }) { job ->
                        MyJobCard(job = job)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusTabRow(selected: JobStatus, onSelect: (JobStatus) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf(
            JobStatus.ACTIVE to "Active",
            JobStatus.DRAFT to "Drafts",
            JobStatus.COMPLETED to "Completed"
        ).forEach { (status, label) ->
            val isSelected = selected == status
            val shape = RoundedCornerShape(8.dp)
            Box(
                modifier = Modifier
                    .then(if (!isSelected) Modifier.border(1.dp, Grey200, shape) else Modifier)
                    .clip(shape)
                    .background(if (isSelected) SlateDark else Cream)
                    .clickable { onSelect(status) }
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
}

@Composable
private fun MyJobCard(job: Job) {
    val catColor = CategoryColors[job.category.name] ?: Forest
    val catLabel = job.category.name.lowercase().replaceFirstChar { it.uppercase() }
    val budgetText = formatBudget(job.budgetMin, job.budgetMax)
    val statusLabel = job.status.name.lowercase().replaceFirstChar { it.uppercase() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Cream)
            .height(IntrinsicSize.Min)
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
                .background(catColor)
        )
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(catColor)
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = catLabel.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            when (job.status) {
                                JobStatus.ACTIVE -> Forest.copy(alpha = 0.1f)
                                JobStatus.DRAFT -> Grey200
                                JobStatus.COMPLETED -> Slate.copy(alpha = 0.08f)
                            }
                        )
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = statusLabel.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (job.status) {
                            JobStatus.ACTIVE -> Forest
                            JobStatus.DRAFT -> Grey500
                            JobStatus.COMPLETED -> Slate
                        },
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = job.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = SlateDark,
                lineHeight = 21.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = job.description,
                fontSize = 13.sp,
                color = TextMuted,
                lineHeight = 18.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = job.location, fontSize = 12.sp, color = Grey500)
                Text(text = job.duration, fontSize = 12.sp, color = Grey500)
                Text(text = budgetText, fontSize = 12.sp, color = Forest)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                AvatarInitials(initials = "AR", size = 28, bg = catColor)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "You", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextMid)
                Spacer(modifier = Modifier.weight(1f))
                if (job.applicantsCount > 0) {
                    Text(
                        text = "👥 ${job.applicantsCount}",
                        fontSize = 12.sp,
                        color = Grey500,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            if (job.status == JobStatus.ACTIVE) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ActionButton(
                        label = "View applicants",
                        modifier = Modifier.weight(1f)
                    )
                    ActionButton(
                        label = "Edit",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionButton(label: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, Grey200, RoundedCornerShape(8.dp))
            .background(Cream)
            .clickable { }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextMid)
    }
}

private fun formatBudget(min: Double, max: Double): String {
    val fmt = NumberFormat.getCurrencyInstance(Locale.US).apply { maximumFractionDigits = 0 }
    return if (min == max) fmt.format(min) else "${fmt.format(min)} – ${fmt.format(max)}"
}
