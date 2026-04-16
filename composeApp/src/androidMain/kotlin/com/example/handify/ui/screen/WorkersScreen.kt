package com.example.handify.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.handify.R
import com.example.handify.ui.component.AvatarInitials
import com.example.handify.ui.theme.*

private data class Worker(
    val id: Int,
    val name: String,
    val initials: String,
    val title: String,
    val categories: List<String>,
    val rating: Double,
    val reviewCount: Int,
    val location: String,
    val rate: String,
    val rateType: String,
    val jobsDone: Int,
    val availability: String,
    val isSolo: Boolean,
    val isVerified: Boolean,
    val about: String,
    val experience: String,
    val responseTime: String
)

private val WORKERS = listOf(
    Worker(1, "Alex Porter", "AP", "Flooring Specialist", listOf("Flooring", "Construction"), 4.8, 34, "Brooklyn, NY", "\$85", "hr", 23, "Available now", true, true, "5 years of experience installing hardwood, laminate, and engineered flooring. I work clean, fast, and always leave the site spotless.", "5 years", "< 1 hour"),
    Worker(2, "Chris Mendez", "CM", "Licensed Contractor", listOf("Plumbing", "Construction"), 4.6, 21, "Queens, NY", "\$95", "hr", 15, "Available this week", true, true, "Licensed contractor specializing in plumbing and general construction. 3 years delivering quality work on time.", "3 years", "< 2 hours"),
    Worker(3, "Victor Reeves", "VR", "Electrical & Flooring", listOf("Electrical", "Flooring"), 4.9, 58, "Manhattan, NY", "\$110", "hr", 41, "Available tomorrow", true, true, "Flooring and electrical specialist. I guarantee my work with a 1-year warranty on all installations.", "8 years", "< 30 min"),
    Worker(4, "Ian Bradley", "IB", "Home Services", listOf("Cleaning", "Moving"), 4.4, 12, "Bronx, NY", "\$60", "hr", 9, "Available weekends", true, false, "Reliable and affordable for home cleaning and moving jobs. Fast turnaround with quality results.", "2 years", "< 3 hours"),
    Worker(5, "Frank Murray", "FM", "Landscaping & Garden", listOf("Garden", "Cleaning"), 4.7, 44, "Staten Island, NY", "\$75", "hr", 28, "Available now", false, true, "Two-person crew specializing in landscaping, garden maintenance, and outdoor cleanup.", "6 years", "< 1 hour"),
    Worker(6, "George Tomlin", "GT", "IT & Tech Support", listOf("IT", "Events"), 4.5, 27, "Jersey City, NJ", "\$90", "hr", 19, "Available this week", true, false, "7 years in tech support and event AV setup. Fair price, solid work. Happy to show references.", "7 years", "< 2 hours"),
)

private val WORKER_COLORS = mapOf(
    "AP" to Color(0xFF2D5A3D),
    "CM" to Color(0xFFD4572A),
    "VR" to Color(0xFF1A6B8A),
    "IB" to Color(0xFF7B5EA7),
    "FM" to Color(0xFF4A7C59),
    "GT" to Color(0xFF8B6914)
)

private val CATEGORIES = listOf("All", "Electrical", "Plumbing", "Construction", "Cleaning", "Moving", "Garden", "Auto", "IT", "Events")

@Composable
fun WorkersScreen(onContact: (String) -> Unit = {}) {
    var search by remember { mutableStateOf("") }
    var selectedCat by remember { mutableStateOf("All") }
    var showFilters by remember { mutableStateOf(false) }
    var selectedWorker by remember { mutableStateOf<Worker?>(null) }

    val filtered = remember(search, selectedCat) {
        WORKERS.filter { w ->
            (selectedCat == "All" || w.categories.any { it.equals(selectedCat, ignoreCase = true) }) &&
                (search.isBlank() || w.name.contains(search, ignoreCase = true) || w.title.contains(search, ignoreCase = true))
        }
    }

    selectedWorker?.let { worker ->
        WorkerDetailScreen(
            worker = worker,
            onDismiss = { selectedWorker = null },
            onContact = { onContact(worker.name) }
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Sand)
    ) {
        Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 12.dp)) {
            Text(
                text = "Find Workers",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = SlateDark
            )
            Spacer(modifier = Modifier.height(12.dp))
            WorkerSearchBar(
                value = search,
                onValueChange = { search = it },
                onFilterClick = { showFilters = !showFilters }
            )
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            items(CATEGORIES) { cat ->
                CategoryChip(label = cat, selected = cat == selectedCat, onClick = { selectedCat = cat })
            }
        }

        if (showFilters) {
            SubFiltersPanel()
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 4.dp, bottom = 100.dp)
        ) {
            items(filtered, key = { it.id }) { worker ->
                WorkerCard(worker = worker, onClick = { selectedWorker = worker })
            }
        }
    }
}

@Composable
private fun WorkerSearchBar(value: String, onValueChange: (String) -> Unit, onFilterClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Cream)
            .border(1.dp, Grey200, RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_search),
            contentDescription = null,
            tint = Grey400,
            modifier = Modifier.size(18.dp)
        )
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            textStyle = TextStyle(fontSize = 14.sp, color = SlateDark),
            cursorBrush = SolidColor(Forest),
            singleLine = true,
            decorationBox = { inner ->
                if (value.isEmpty()) {
                    Text("Search workers, skills...", fontSize = 14.sp, color = Grey400)
                }
                inner()
            }
        )
        Icon(
            painter = painterResource(R.drawable.ic_filter),
            contentDescription = "Filters",
            tint = if (false) Forest else Grey400,
            modifier = Modifier
                .size(18.dp)
                .clickable { onFilterClick() }
        )
    }
}

@Composable
private fun SubFiltersPanel() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SubFilterRow(label = "Distance", options = listOf("Any", "5 mi", "10 mi", "25 mi"))
        SubFilterRow(label = "Rating", options = listOf("Any", "4.5+", "4.0+", "3.5+"))
        SubFilterRow(label = "Availability", options = listOf("Any", "Now", "Today", "This week"))
        SubFilterRow(label = "Rate type", options = listOf("Any", "Hourly", "Fixed"))
    }
}

@Composable
private fun SubFilterRow(label: String, options: List<String>) {
    var selected by remember { mutableStateOf(options.first()) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Grey500,
            modifier = Modifier.width(72.dp)
        )
        options.forEach { opt ->
            val isSelected = opt == selected
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isSelected) SlateDark else Cream)
                    .border(1.dp, if (isSelected) SlateDark else Grey200, RoundedCornerShape(6.dp))
                    .clickable { selected = opt }
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = opt,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSelected) Color.White else Grey500
                )
            }
        }
    }
}

@Composable
private fun CategoryChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(8.dp)
    Box(
        modifier = Modifier
            .clip(shape)
            .background(if (selected) SlateDark else Cream)
            .border(1.dp, if (selected) SlateDark else Grey200, shape)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = label.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (selected) Color.White else Grey500,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
private fun WorkerCard(worker: Worker, onClick: () -> Unit) {
    val bg = WORKER_COLORS[worker.initials] ?: Forest

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Cream)
            .clickable(onClick = onClick)
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box {
            AvatarInitials(initials = worker.initials, size = 48, bg = bg)
            if (worker.isVerified) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(Forest),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_check),
                        contentDescription = "Verified",
                        tint = Color.White,
                        modifier = Modifier.size(9.dp)
                    )
                }
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = worker.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateDark,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "★ ${worker.rating}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFD4A030)
                )
            }
            Text(text = worker.title, fontSize = 13.sp, color = TextMuted)
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                worker.categories.take(2).forEach { cat ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(bg.copy(alpha = 0.1f))
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(text = cat, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = bg)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = worker.location, fontSize = 11.sp, color = Grey500, modifier = Modifier.weight(1f))
                Text(
                    text = "${worker.rate}/${worker.rateType}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Forest
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${worker.jobsDone} jobs",
                    fontSize = 11.sp,
                    color = Grey500
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Forest.copy(alpha = 0.1f))
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = worker.availability,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Forest
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = if (worker.isSolo) "Solo" else "Team",
                    fontSize = 11.sp,
                    color = Grey400
                )
            }
        }
    }
}

@Composable
private fun WorkerDetailScreen(worker: Worker, onDismiss: () -> Unit, onContact: () -> Unit = {}) {
    val bg = WORKER_COLORS[worker.initials] ?: Forest

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Sand)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(bg)
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
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box {
                                AvatarInitials(initials = worker.initials, size = 72, bg = bg.copy(alpha = 0.7f))
                                if (worker.isVerified) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .size(22.dp)
                                            .clip(CircleShape)
                                            .background(Color.White),
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
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = worker.name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text(text = worker.title, fontSize = 13.sp, color = Color.White.copy(alpha = 0.8f))
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Cream)
                            .padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        WorkerStat("★ ${worker.rating}", "${worker.reviewCount} reviews")
                        Box(modifier = Modifier.width(1.dp).height(36.dp).background(Grey100))
                        WorkerStat(worker.rate + "/" + worker.rateType, "Rate")
                        Box(modifier = Modifier.width(1.dp).height(36.dp).background(Grey100))
                        WorkerStat("${worker.jobsDone}", "Jobs done")
                        Box(modifier = Modifier.width(1.dp).height(36.dp).background(Grey100))
                        WorkerStat(worker.experience, "Experience")
                    }
                    HorizontalDivider(color = Grey200)
                }

                item {
                    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                        Text("Services", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SlateDark)
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            worker.categories.forEach { cat ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(bg.copy(alpha = 0.1f))
                                        .border(1.dp, bg.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 14.dp, vertical = 8.dp)
                                ) {
                                    Text(text = cat, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = bg)
                                }
                            }
                        }
                    }
                }

                item {
                    Surface(
                        color = Cream,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .padding(bottom = 16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("About", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = SlateDark)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(worker.about, fontSize = 14.sp, color = TextMuted, lineHeight = 21.sp)
                        }
                    }
                }

                item {
                    Surface(
                        color = Cream,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .padding(bottom = 16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Details", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = SlateDark)
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(modifier = Modifier.fillMaxWidth()) {
                                WorkerDetailItem(
                                    icon = R.drawable.ic_location,
                                    label = "Location",
                                    value = worker.location,
                                    modifier = Modifier.weight(1f)
                                )
                                WorkerDetailItem(
                                    icon = R.drawable.ic_clock,
                                    label = "Response time",
                                    value = worker.responseTime,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(modifier = Modifier.fillMaxWidth()) {
                                WorkerDetailItem(
                                    icon = R.drawable.ic_dollar,
                                    label = "Rate",
                                    value = "${worker.rate}/${worker.rateType}",
                                    modifier = Modifier.weight(1f)
                                )
                                WorkerDetailItem(
                                    icon = R.drawable.ic_calendar,
                                    label = "Availability",
                                    value = worker.availability,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                item {
                    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                        Text("Reviews", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SlateDark)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "★ ${worker.rating}  ·  ${worker.reviewCount} reviews",
                            fontSize = 13.sp,
                            color = Grey500
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        listOf(
                            Triple("Sarah K.", 5.0, "Great work, very professional and clean."),
                            Triple("Marcus T.", 4.5, "Showed up on time and finished ahead of schedule."),
                            Triple("Jennifer L.", 5.0, "Highly recommend! Will hire again.")
                        ).forEach { (name, rating, comment) ->
                            ReviewCard(name = name, rating = rating, comment = comment)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }

            Button(
                onClick = onContact,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Ember),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Contact ${worker.name.split(" ").first()}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

internal data class WorkerContact(val name: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactWorkerSheet(
    workerName: String,
    jobs: List<com.example.handify.domain.model.Job>,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onStartConversation: (com.example.handify.domain.model.Job) -> Unit,
    onCreateJob: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedJobId by remember { mutableStateOf<String?>(null) }
    val firstName = workerName.trim().split(" ").firstOrNull() ?: workerName
    val activeJobs = jobs.filter {
        it.status == com.example.handify.domain.model.JobStatus.ACTIVE ||
        it.status == com.example.handify.domain.model.JobStatus.DRAFT
    }
    val hasJobs = activeJobs.isNotEmpty()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Cream,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        dragHandle = {
            Box(
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(modifier = Modifier.width(36.dp).height(4.dp).clip(RoundedCornerShape(2.dp)).background(Grey300))
            }
        }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Which job is this about?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateDark
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "The worker will see your job details.",
                    fontSize = 13.sp,
                    color = TextMuted
                )
            }
            HorizontalDivider(color = Grey200)

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Forest, modifier = Modifier.size(24.dp))
                }
            } else if (!hasJobs) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Grey100),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_briefcase),
                            contentDescription = null,
                            tint = Grey400,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "You haven't posted any jobs yet.",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SlateDark
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Create a job first so $firstName knows what you need.",
                        fontSize = 13.sp,
                        color = TextMuted,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 360.dp)
                ) {
                    items(activeJobs) { job ->
                        val isSelected = selectedJobId == job.id
                        val catColor = CategoryColors[job.category.name] ?: Forest
                        val catLabel = job.category.name.lowercase().replaceFirstChar { it.uppercase() }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (isSelected) Forest.copy(alpha = 0.04f) else Color.Transparent)
                                .clickable { selectedJobId = if (isSelected) null else job.id }
                                .padding(horizontal = 20.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(catColor)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(catColor)
                                            .padding(horizontal = 7.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = catLabel.uppercase(),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.White,
                                            letterSpacing = 0.4.sp
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(
                                                if (job.status == com.example.handify.domain.model.JobStatus.ACTIVE)
                                                    Forest.copy(alpha = 0.1f) else Grey200
                                            )
                                            .padding(horizontal = 7.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = job.status.name.lowercase().replaceFirstChar { it.uppercase() }.uppercase(),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (job.status == com.example.handify.domain.model.JobStatus.ACTIVE) Forest else Grey500,
                                            letterSpacing = 0.3.sp
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = job.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = SlateDark,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text(text = job.location, fontSize = 12.sp, color = Grey500)
                                    val fmt = java.text.NumberFormat.getCurrencyInstance(java.util.Locale.US).apply { maximumFractionDigits = 0 }
                                    val budgetText = if (job.budgetMin == job.budgetMax) fmt.format(job.budgetMin)
                                                     else "${fmt.format(job.budgetMin)} – ${fmt.format(job.budgetMax)}"
                                    Text(text = budgetText, fontSize = 12.sp, color = Forest, fontWeight = FontWeight.SemiBold)
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) Forest else Color.Transparent)
                                    .border(2.dp, if (isSelected) Forest else Grey300, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_check),
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(11.dp)
                                    )
                                }
                            }
                        }
                        HorizontalDivider(color = Grey100)
                    }
                }
            }

            HorizontalDivider(color = Grey200)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (hasJobs) {
                    Button(
                        onClick = {
                            val job = activeJobs.find { it.id == selectedJobId }
                            if (job != null) onStartConversation(job)
                        },
                        enabled = selectedJobId != null,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Ember)
                    ) {
                        Text("Start Conversation", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    }
                    TextButton(
                        onClick = onCreateJob,
                        modifier = Modifier.fillMaxWidth().height(40.dp)
                    ) {
                        Text("Create a new job instead", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Forest)
                    }
                } else {
                    Button(
                        onClick = onCreateJob,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Ember)
                    ) {
                        Text("Create a Job First", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun WorkerStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = SlateDark)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = label, fontSize = 11.sp, color = Grey500)
    }
}

@Composable
private fun WorkerDetailItem(icon: Int, label: String, value: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = Grey400,
            modifier = Modifier.size(16.dp).padding(top = 2.dp)
        )
        Column {
            Text(text = label, fontSize = 11.sp, color = Grey400)
            Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = SlateDark)
        }
    }
}

@Composable
private fun ReviewCard(name: String, rating: Double, comment: String) {
    Surface(
        color = Cream,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AvatarInitials(
                    initials = name.split(" ").mapNotNull { it.firstOrNull()?.uppercaseChar() }.take(2).joinToString(""),
                    size = 32,
                    bg = Grey400
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = SlateDark, modifier = Modifier.weight(1f))
                Text(text = "★ $rating", fontSize = 12.sp, color = Color(0xFFD4A030), fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = comment, fontSize = 13.sp, color = TextMuted, lineHeight = 19.sp)
        }
    }
}
