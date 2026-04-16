package com.example.handify.ui.screen

import androidx.activity.compose.BackHandler
import android.location.Geocoder
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.handify.R
import com.example.handify.domain.model.JobCategory
import com.example.handify.presentation.job.PostJobState
import com.example.handify.presentation.job.PostJobViewModel
import com.example.handify.ui.theme.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.compose.viewmodel.koinViewModel
import java.util.Calendar
import java.util.Locale

@Composable
fun PostJobScreen(
    onDismiss: () -> Unit,
    onViewMyJobs: () -> Unit,
    workerName: String? = null,
    onStartChat: ((PostJobState) -> Unit)? = null,
    viewModel: PostJobViewModel = koinViewModel()
) {
    val state = viewModel.state

    LaunchedEffect(Unit) { viewModel.reset() }

    BackHandler {
        if (state.isSuccess) onDismiss()
        else if (state.step > 1) viewModel.prevStep()
        else onDismiss()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Sand)
    ) {
        if (state.isSuccess) {
            SuccessContent(
                isDraft = state.isDraft,
                workerName = workerName,
                onViewMyJobs = onViewMyJobs,
                onStartChat = if (onStartChat != null) { { onStartChat(state) } } else null
            )
        } else {
            Column(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.statusBars)) {
                WizardTopBar(
                    step = state.step,
                    onClose = onDismiss
                )
                LinearProgressIndicator(
                    progress = { state.step / 3f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = Forest,
                    trackColor = Grey200
                )
                AnimatedContent(
                    targetState = state.step,
                    transitionSpec = {
                        if (targetState > initialState) {
                            (slideInVertically { it / 4 } + fadeIn()) togetherWith (slideOutVertically { -it / 4 } + fadeOut())
                        } else {
                            (slideInVertically { -it / 4 } + fadeIn()) togetherWith (slideOutVertically { it / 4 } + fadeOut())
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) { step ->
                    Box(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                        Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 120.dp)) {
                            when (step) {
                                1 -> Step1Content(
                                    title = state.title,
                                    selectedCategory = state.category,
                                    isUrgent = state.isUrgent,
                                    onTitleChange = viewModel::updateTitle,
                                    onCategorySelect = viewModel::updateCategory,
                                    onUrgencyChange = viewModel::updateUrgency
                                )
                                2 -> Step2Content(
                                    description = state.description,
                                    pickedAddress = state.location,
                                    pickedLat = state.lat,
                                    pickedLng = state.lng,
                                    duration = state.duration,
                                    budget = state.budget,
                                    onDescriptionChange = viewModel::updateDescription,
                                    onLocationPicked = viewModel::updateLocation,
                                    onDurationSelect = viewModel::updateDuration,
                                    onBudgetChange = viewModel::updateBudget
                                )
                                3 -> Step3Content(state = state)
                            }
                        }
                    }
                }
                WizardBottomBar(
                    step = state.step,
                    isLoading = state.isLoading,
                    onBack = viewModel::prevStep,
                    onContinue = viewModel::nextStep,
                    onPublish = viewModel::publish,
                    onSaveDraft = viewModel::saveDraft
                )
            }
        }
    }
}

@Composable
private fun WizardTopBar(step: Int, onClose: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onClose) {
            Icon(
                painter = painterResource(R.drawable.ic_close),
                contentDescription = "Close",
                tint = SlateDark,
                modifier = Modifier.size(22.dp)
            )
        }
        Text(
            text = "Post a Job",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = SlateDark,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "STEP $step/3",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Grey400,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.width(12.dp))
    }
}

@Composable
private fun Step1Content(
    title: String,
    selectedCategory: JobCategory?,
    isUrgent: Boolean,
    onTitleChange: (String) -> Unit,
    onCategorySelect: (JobCategory) -> Unit,
    onUrgencyChange: (Boolean) -> Unit
) {
    Text("What do you need?", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = SlateDark)
    Spacer(modifier = Modifier.height(4.dp))
    Text("Pick a category and describe briefly.", fontSize = 13.sp, color = TextMuted)
    Spacer(modifier = Modifier.height(20.dp))

    FieldLabel("Title")
    OutlinedTextField(
        value = title,
        onValueChange = onTitleChange,
        placeholder = { Text("e.g. Install bedroom shelves", color = Grey400) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Forest,
            unfocusedBorderColor = Grey200,
            focusedContainerColor = Cream,
            unfocusedContainerColor = Cream
        ),
        singleLine = true
    )
    Spacer(modifier = Modifier.height(20.dp))

    FieldLabel("Category")
    Spacer(modifier = Modifier.height(10.dp))
    val categories = JobCategory.entries
    val chunked = categories.chunked(2)
    chunked.forEach { row ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            row.forEach { cat ->
                val selected = selectedCategory == cat
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .border(
                            width = if (selected) 2.dp else 1.5.dp,
                            color = if (selected) Forest else Grey200,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .background(if (selected) Forest.copy(alpha = 0.06f) else Cream)
                        .clickable { onCategorySelect(cat) }
                        .padding(horizontal = 12.dp, vertical = 14.dp)
                ) {
                    Text(
                        text = cat.name.lowercase().replaceFirstChar { it.uppercase() },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (selected) Forest else Grey500
                    )
                }
            }
            if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(10.dp))
    }

    Spacer(modifier = Modifier.height(10.dp))
    FieldLabel("Urgency")
    Spacer(modifier = Modifier.height(10.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Grey100)
            .padding(3.dp)
    ) {
        UrgencyButton(label = "Normal", selected = !isUrgent, isUrgent = false) { onUrgencyChange(false) }
        UrgencyButton(label = "Urgent", selected = isUrgent, isUrgent = true) { onUrgencyChange(true) }
    }
}

@Composable
private fun RowScope.UrgencyButton(label: String, selected: Boolean, isUrgent: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(6.dp))
            .background(
                when {
                    selected && isUrgent -> Ember
                    selected -> Cream
                    else -> Color.Transparent
                }
            )
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = when {
                selected && isUrgent -> Color.White
                selected -> SlateDark
                else -> Grey400
            }
        )
    }
}

@Composable
private fun Step2Content(
    description: String,
    pickedAddress: String,
    pickedLat: Double?,
    pickedLng: Double?,
    duration: String,
    budget: String,
    onDescriptionChange: (String) -> Unit,
    onLocationPicked: (String, Double, Double) -> Unit,
    onDurationSelect: (String) -> Unit,
    onBudgetChange: (String) -> Unit
) {
    Text("Details", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = SlateDark)
    Spacer(modifier = Modifier.height(4.dp))
    Text("Add more information about the job.", fontSize = 13.sp, color = TextMuted)
    Spacer(modifier = Modifier.height(20.dp))

    FieldLabel("Description")
    OutlinedTextField(
        value = description,
        onValueChange = { if (it.length <= 500) onDescriptionChange(it) },
        placeholder = { Text("Describe in detail what you need done...", color = Grey400) },
        modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp),
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Forest,
            unfocusedBorderColor = Grey200,
            focusedContainerColor = Cream,
            unfocusedContainerColor = Cream
        ),
        maxLines = 6
    )
    Text(
        text = "${description.length} / 500",
        fontSize = 11.sp,
        color = Grey400,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.End
    )
    Spacer(modifier = Modifier.height(16.dp))

    FieldLabel("Location")
    Spacer(modifier = Modifier.height(6.dp))
    MapLocationPicker(
        initialLat = pickedLat,
        initialLng = pickedLng,
        onLocationPicked = onLocationPicked
    )
    if (pickedAddress.isNotBlank()) {
        Spacer(modifier = Modifier.height(6.dp))
        Text(pickedAddress, fontSize = 13.sp, color = Grey500)
    }
    Spacer(modifier = Modifier.height(16.dp))

    FieldLabel("Date")
    Spacer(modifier = Modifier.height(8.dp))
    DatePicker()
    Spacer(modifier = Modifier.height(16.dp))

    FieldLabel("Duration")
    Spacer(modifier = Modifier.height(8.dp))
    val durations = listOf("Half day", "1 day", "2-3 days", "~1 week", "Flexible")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        durations.take(3).forEach { d ->
            DurationChip(label = d, selected = duration == d) { onDurationSelect(d) }
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        durations.drop(3).forEach { d ->
            DurationChip(label = d, selected = duration == d) { onDurationSelect(d) }
        }
    }
    Spacer(modifier = Modifier.height(16.dp))

    FieldLabel("Budget ($)")
    OutlinedTextField(
        value = budget,
        onValueChange = onBudgetChange,
        placeholder = { Text("e.g. 500", color = Grey400) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Forest,
            unfocusedBorderColor = Grey200,
            focusedContainerColor = Cream,
            unfocusedContainerColor = Cream
        ),
        singleLine = true
    )
}

@Composable
private fun DatePicker() {
    val cal = Calendar.getInstance()
    val days = (0 until 14).map { offset ->
        val c = Calendar.getInstance()
        c.add(Calendar.DAY_OF_MONTH, offset)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val wd = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")[c.get(Calendar.DAY_OF_WEEK) - 1]
        day to wd
    }
    var selectedDay by remember { mutableStateOf<Int?>(null) }
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        days.forEach { (day, wd) ->
            val selected = selectedDay == day
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .border(
                        1.5.dp,
                        if (selected) Forest else Grey200,
                        RoundedCornerShape(8.dp)
                    )
                    .background(if (selected) Forest else Cream)
                    .clickable { selectedDay = day }
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(wd, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = if (selected) Color.White.copy(alpha = 0.7f) else Grey400)
                Text("$day", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = if (selected) Color.White else SlateDark)
            }
        }
    }
}

@Composable
private fun DurationChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .border(1.5.dp, if (selected) Forest else Grey200, RoundedCornerShape(6.dp))
            .background(if (selected) Forest.copy(alpha = 0.06f) else Cream)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (selected) Forest else Grey500
        )
    }
}

@Composable
private fun Step3Content(state: com.example.handify.presentation.job.PostJobState) {
    Text("Review and Publish", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = SlateDark)
    Spacer(modifier = Modifier.height(4.dp))
    Text("Review the details and publish your job.", fontSize = 13.sp, color = TextMuted)
    Spacer(modifier = Modifier.height(20.dp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Cream)
            .height(IntrinsicSize.Min)
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
                .background(Forest)
        )
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = state.title.ifBlank { "Job title" },
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = SlateDark
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val cat = state.category
                if (cat != null)
                    Text(cat.name.lowercase().replaceFirstChar { it.uppercase() }, fontSize = 12.sp, color = Grey500)
                if (state.location.isNotBlank())
                    Text(state.location, fontSize = 12.sp, color = Grey500)
                if (state.duration.isNotBlank())
                    Text(state.duration, fontSize = 12.sp, color = Grey500)
                if (state.budget.isNotBlank())
                    Text("$${state.budget}", fontSize = 12.sp, color = Forest)
                if (state.isUrgent)
                    Text("Urgent", fontSize = 12.sp, color = Ember, fontWeight = FontWeight.Bold)
            }
            if (state.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(state.description, fontSize = 13.sp, color = TextMuted, lineHeight = 18.sp)
            }
        }
    }

    Spacer(modifier = Modifier.height(20.dp))
    FieldLabel("Photos (optional)")
    Spacer(modifier = Modifier.height(10.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        repeat(3) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(2.dp, Grey300, RoundedCornerShape(8.dp))
                    .background(Grey100),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_camera),
                    contentDescription = "Add photo",
                    tint = Grey400,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }

    val errorMsg = state.error
    if (errorMsg != null) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(errorMsg, fontSize = 13.sp, color = Color(0xFFD64C3B))
    }
}

@Composable
private fun WizardBottomBar(
    step: Int,
    isLoading: Boolean,
    onBack: () -> Unit,
    onContinue: () -> Unit,
    onPublish: () -> Unit,
    onSaveDraft: () -> Unit
) {
    Surface(
        color = Cream,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (step > 1) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(width = 1.5.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SlateDark)
                ) {
                    Text("Back", fontWeight = FontWeight.SemiBold)
                }
            }
            if (step < 3) {
                Button(
                    onClick = onContinue,
                    modifier = Modifier.weight(2f).height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Forest)
                ) {
                    Text("Continue", fontWeight = FontWeight.SemiBold)
                }
            } else {
                OutlinedButton(
                    onClick = onSaveDraft,
                    enabled = !isLoading,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(width = 1.5.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SlateDark)
                ) {
                    Text("Save Draft", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
                Button(
                    onClick = onPublish,
                    enabled = !isLoading,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Ember)
                ) {
                    if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    else Text("Publish", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun SuccessContent(
    isDraft: Boolean,
    workerName: String? = null,
    onViewMyJobs: () -> Unit,
    onStartChat: (() -> Unit)? = null
) {
    val firstName = workerName?.trim()?.split(" ")?.firstOrNull()
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Forest),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_check),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = if (isDraft) "Draft saved!" else "Your job is live!",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = SlateDark
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = when {
                onStartChat != null && firstName != null -> "Starting a conversation with $firstName…"
                isDraft -> "You can publish it anytime from My Jobs."
                else -> "You'll get notifications when someone applies."
            },
            fontSize = 14.sp,
            color = TextMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        if (onStartChat != null) {
            Button(
                onClick = onStartChat,
                modifier = Modifier.fillMaxWidth(0.7f).height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Ember)
            ) {
                Text("Start Conversation", fontWeight = FontWeight.SemiBold)
            }
        } else {
            Button(
                onClick = onViewMyJobs,
                modifier = Modifier.fillMaxWidth(0.7f).height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Forest)
            ) {
                Text("View My Jobs", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun MapLocationPicker(
    initialLat: Double?,
    initialLng: Double?,
    onLocationPicked: (address: String, lat: Double, lng: Double) -> Unit
) {
    val context = LocalContext.current
    val defaultLat = initialLat ?: 40.7128
    val defaultLng = initialLng ?: -74.0060
    val markerState = remember { MarkerState(position = LatLng(defaultLat, defaultLng)) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(defaultLat, defaultLng), 13f)
    }

    LaunchedEffect(initialLat, initialLng) {
        if (initialLat != null && initialLng != null) {
            val pos = LatLng(initialLat, initialLng)
            markerState.position = pos
            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(pos, 13f))
        }
    }

    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            val pos = cameraPositionState.position.target
            markerState.position = pos
            val address = withContext(Dispatchers.IO) {
                try {
                    @Suppress("DEPRECATION")
                    Geocoder(context, Locale.getDefault())
                        .getFromLocation(pos.latitude, pos.longitude, 1)
                        ?.firstOrNull()
                        ?.let { a ->
                            listOfNotNull(a.thoroughfare, a.locality, a.adminArea, a.countryName)
                                .joinToString(", ")
                        } ?: "${pos.latitude}, ${pos.longitude}"
                } catch (e: Exception) {
                    "${pos.latitude}, ${pos.longitude}"
                }
            }
            onLocationPicked(address, pos.latitude, pos.longitude)
        }
    }

    GoogleMap(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(10.dp)),
        cameraPositionState = cameraPositionState
    ) {
        Marker(state = markerState, draggable = true)
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text.uppercase(),
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = Grey500,
        letterSpacing = 0.5.sp
    )
    Spacer(modifier = Modifier.height(6.dp))
}
