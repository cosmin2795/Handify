package com.example.handify.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.handify.R
import com.example.handify.domain.model.Job
import com.example.handify.ui.component.AvatarInitials
import com.example.handify.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@Composable
fun JobDetailScreen(job: Job, onDismiss: () -> Unit) {
    var showApply by remember { mutableStateOf(false) }
    var applyDone by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Sand)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                val catColor = CategoryColors[job.category.name] ?: Forest
                val catLabel = job.category.name.lowercase().replaceFirstChar { it.uppercase() }
                val budgetText = formatJobBudget(job.budgetMin, job.budgetMax)

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

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 100.dp)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(catColor)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = catLabel.uppercase(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    letterSpacing = 0.5.sp
                                )
                            }
                            if (job.isUrgent) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Ember)
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "URGENT",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Surface(
                            color = Cream,
                            shape = RoundedCornerShape(10.dp),
                            tonalElevation = 0.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    AvatarInitials(
                                        initials = job.clientName.toInitialsDetail(),
                                        size = 48,
                                        bg = catColor
                                    )
                                    Column {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = job.clientName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp
                                            )
                                            Text(
                                                text = "✓ Verified",
                                                fontSize = 11.sp,
                                                color = Forest,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        if (job.clientRating > 0.0) {
                                            Text(
                                                text = "★ ${job.clientRating}",
                                                fontSize = 13.sp,
                                                color = Color(0xFFD4A030),
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        DetailGrid(job = job, budgetText = budgetText)

                        Spacer(modifier = Modifier.height(12.dp))

                        Surface(
                            color = Cream,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "DESCRIPTION",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Slate,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = job.description,
                                    fontSize = 14.sp,
                                    color = TextMuted,
                                    lineHeight = 22.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            repeat(3) {
                                Box(
                                    modifier = Modifier
                                        .size(width = 140.dp, height = 100.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Grey100),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_camera),
                                        contentDescription = null,
                                        tint = Grey400,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (job.applicantsCount > 0) {
                            Text(
                                text = "${job.applicantsCount} people applied",
                                fontSize = 12.sp,
                                color = Grey400,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                color = Cream,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "BUDGET",
                            fontSize = 10.sp,
                            color = Grey400,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = formatJobBudget(job.budgetMin, job.budgetMax),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateDark
                        )
                    }
                    Button(
                        onClick = { showApply = true; applyDone = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Ember),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text(
                            text = "Apply Now",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }

            if (showApply) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f))
                        .clickable { if (!applyDone) showApply = false }
                )
                ApplySheet(
                    budget = formatJobBudget(job.budgetMin, job.budgetMax),
                    done = applyDone,
                    onSubmit = { applyDone = true },
                    onClose = {
                        showApply = false
                        if (applyDone) onDismiss()
                    },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}

@Composable
private fun ApplySheet(
    budget: String,
    done: Boolean,
    onSubmit: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var pitch by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var selectedAvailability by remember { mutableStateOf<String?>(null) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = Cream,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        shadowElevation = 16.dp
    ) {
        if (done) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Forest),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_check),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Application sent",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateDark
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "You'll be notified when you get a response.",
                    fontSize = 14.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onClose,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Forest),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Back to Feed", fontWeight = FontWeight.Bold)
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 40.dp, height = 4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Grey300)
                        .align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Apply to this job",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateDark
                )
                Spacer(modifier = Modifier.height(16.dp))

                FormLabel("YOUR PITCH")
                OutlinedTextField(
                    value = pitch,
                    onValueChange = { if (it.length <= 300) pitch = it },
                    placeholder = { Text("Why are you the right person for this job?", color = Grey400) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4,
                    maxLines = 6,
                    colors = outlinedFieldColors(),
                    shape = RoundedCornerShape(8.dp)
                )
                Text(
                    text = "${pitch.length} / 300",
                    fontSize = 11.sp,
                    color = Grey400,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                )

                Spacer(modifier = Modifier.height(14.dp))

                FormLabel("YOUR PROPOSED PRICE")
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    placeholder = { Text("e.g. \$500", color = Grey400) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = outlinedFieldColors(),
                    shape = RoundedCornerShape(8.dp)
                )
                Text(
                    text = "Client budget: $budget",
                    fontSize = 11.sp,
                    color = Forest,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                FormLabel("AVAILABILITY")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Anytime", "Mon-Fri", "Weekends", "Next week").forEach { opt ->
                        val selected = selectedAvailability == opt
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .border(
                                    width = 1.5.dp,
                                    color = if (selected) Forest else Grey200,
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .background(if (selected) Forest.copy(alpha = 0.08f) else Cream)
                                .clickable { selectedAvailability = opt }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = opt,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (selected) Forest else Grey500
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onSubmit,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Ember),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Submit Application", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun DetailGrid(job: Job, budgetText: String) {
    val items = listOf(
        Triple(R.drawable.ic_map_pin, "LOCATION", job.location),
        Triple(R.drawable.ic_dollar, "BUDGET", budgetText),
        Triple(R.drawable.ic_clock, "DURATION", job.duration),
        Triple(R.drawable.ic_calendar, "WHEN", "Flexible"),
    )
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { (icon, label, value) ->
                    Surface(
                        color = Cream,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    painter = painterResource(icon),
                                    contentDescription = null,
                                    tint = Grey400,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    color = Grey400,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = value,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = SlateDark
                            )
                        }
                    }
                }
                if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun FormLabel(text: String) {
    Text(
        text = text,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = Grey500,
        letterSpacing = 0.5.sp,
        modifier = Modifier.padding(bottom = 6.dp)
    )
}

@Composable
private fun outlinedFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Forest,
    unfocusedBorderColor = Grey200,
    focusedContainerColor = Cream,
    unfocusedContainerColor = Cream
)

private fun formatJobBudget(min: Double, max: Double): String {
    val fmt = NumberFormat.getCurrencyInstance(Locale.US).apply { maximumFractionDigits = 0 }
    return if (min == max) fmt.format(min) else "${fmt.format(min)} – ${fmt.format(max)}"
}

private fun String.toInitialsDetail(): String =
    trim().split(" ").filter { it.isNotEmpty() }.take(2).joinToString("") { it.first().uppercase() }
