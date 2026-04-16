package com.example.handify.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.handify.R
import com.example.handify.domain.model.Job
import com.example.handify.domain.model.JobStatus
import com.example.handify.ui.component.AvatarInitials
import com.example.handify.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

private data class Applicant(
    val id: Int,
    val name: String,
    val initials: String,
    val rating: Double,
    val jobsDone: Int,
    val pitch: String,
    val price: String
)

private val SAMPLE_APPLICANTS = listOf(
    Applicant(1, "Alex Porter", "AP", 4.8, 23, "5 years experience installing hardwood. I work clean, fast, and always leave the site spotless. Can start this weekend.", "\$950"),
    Applicant(2, "Chris Mendez", "CM", 4.6, 15, "Licensed contractor, 3 years in flooring. I have all the tools needed and can bring an extra set of baseboards if needed.", "\$1,000"),
    Applicant(3, "Victor Reeves", "VR", 4.9, 41, "Flooring specialist — laminate, hardwood, and engineered. I guarantee my work with a 1-year warranty on installation.", "\$1,100"),
    Applicant(4, "Ian Bradley", "IB", 4.4, 9, "Reliable and affordable. I can complete the job in 2 days with quality materials and clean finish.", "\$850"),
    Applicant(5, "Frank Murray", "FM", 4.7, 28, "Two-person crew, we can knock this out in 2 days flat. Experienced with laminate on concrete subfloors.", "\$1,050"),
    Applicant(6, "George Tomlin", "GT", 4.5, 19, "7 years in the trade. Fair price, solid work. Happy to show references from similar jobs in the neighborhood.", "\$900"),
)

@Composable
fun MyJobDetailScreen(job: Job, onDismiss: () -> Unit) {
    val catColor = CategoryColors[job.category.name] ?: Forest
    val applicantCount = minOf(job.applicantsCount, SAMPLE_APPLICANTS.size)
    val applicants = SAMPLE_APPLICANTS.take(applicantCount)
    var acceptedId by remember { mutableStateOf<Int?>(null) }
    var declinedIds by remember { mutableStateOf<Set<Int>>(emptySet()) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Sand)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Sand)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = "Back",
                            tint = Slate,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Text(
                        text = job.title,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SlateDark,
                        modifier = Modifier.weight(1f),
                        maxLines = 1
                    )
                }
                HorizontalDivider(color = Grey200)
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                item {
                    Surface(
                        color = Cream,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                MetaItem(
                                    icon = R.drawable.ic_location,
                                    text = job.location
                                )
                                MetaItem(
                                    icon = R.drawable.ic_dollar,
                                    text = formatMyJobBudget(job.budgetMin, job.budgetMax),
                                    color = Forest
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            MetaItem(icon = R.drawable.ic_clock, text = job.duration)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = job.description,
                                fontSize = 14.sp,
                                color = TextMuted,
                                lineHeight = 21.sp
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "Applicants (${applicants.size})",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateDark,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                    )
                }

                if (applicants.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No applicants yet.",
                                color = TextMuted,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                itemsIndexed(applicants) { _, applicant ->
                    val isAccepted = acceptedId == applicant.id
                    val isDeclined = declinedIds.contains(applicant.id)

                    Surface(
                        color = Cream,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                AvatarInitials(
                                    initials = applicant.initials,
                                    size = 44,
                                    bg = catColor
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = applicant.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = SlateDark
                                        )
                                        Text(
                                            text = "★ ${applicant.rating}",
                                            fontSize = 12.sp,
                                            color = Color(0xFFD4A030),
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = applicant.price,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Forest
                                        )
                                        Text(
                                            text = "${applicant.jobsDone} jobs done",
                                            fontSize = 11.sp,
                                            color = Grey400
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = applicant.pitch,
                                fontSize = 13.sp,
                                color = TextMuted,
                                lineHeight = 19.sp
                            )

                            if (isAccepted) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Forest.copy(alpha = 0.1f))
                                        .padding(10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "✓ Accepted",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Forest
                                    )
                                }
                            } else if (isDeclined) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFC0392B).copy(alpha = 0.08f))
                                        .padding(10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Declined",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFFC0392B)
                                    )
                                }
                            } else {
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { acceptedId = applicant.id },
                                        modifier = Modifier.weight(1f).height(40.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Ember),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Accept", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                    OutlinedButton(
                                        onClick = { declinedIds = declinedIds + applicant.id },
                                        modifier = Modifier.weight(1f).height(40.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMid)
                                    ) {
                                        Text("Decline", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                    OutlinedButton(
                                        onClick = {},
                                        modifier = Modifier.height(40.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Forest)
                                    ) {
                                        Text("Message", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetaItem(icon: Int, text: String, color: Color = Grey500) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(13.dp)
        )
        Text(text = text, fontSize = 12.sp, color = color)
    }
}

private fun formatMyJobBudget(min: Double, max: Double): String {
    val fmt = NumberFormat.getCurrencyInstance(Locale.US).apply { maximumFractionDigits = 0 }
    return if (min == max) fmt.format(min) else "${fmt.format(min)} – ${fmt.format(max)}"
}
