package com.example.handify.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.example.handify.domain.model.JobCategory
import com.example.handify.domain.model.JobStatus
import com.example.handify.ui.component.AvatarInitials
import com.example.handify.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

private data class Conversation(
    val id: Int,
    val name: String,
    val initials: String,
    val lastMsg: String,
    val time: String,
    val unread: Int,
    val jobTitle: String,
    val online: Boolean,
    val attachedJob: Job? = null
)

private data class Message(val id: Int, val fromMe: Boolean, val text: String, val time: String)

private val SEED_CONVERSATIONS = listOf(
    Conversation(1, "Joanna Park", "JP", "Perfect, see you Saturday at 10!", "10:23 AM", 2, "Photographer for christening", true),
    Conversation(2, "Dan Cooper", "DC", "Can you come tomorrow morning?", "Yesterday", 0, "IKEA furniture assembly", false),
    Conversation(3, "Alex Porter", "AP", "I'll bring the extra baseboards just in case.", "Monday", 1, "Hardwood flooring install", true),
)

private val SEED_MESSAGES = mapOf(
    1 to listOf(
        Message(1, false, "Hi! I saw your application for the christening.", "9:15 AM"),
        Message(2, false, "I really like your portfolio. Have you done christenings before?", "9:16 AM"),
        Message(3, true, "Hi Joanna! Yes, I've photographed over 20 christenings and baptisms.", "9:30 AM"),
        Message(4, true, "I can send you some samples if you'd like.", "9:31 AM"),
        Message(5, false, "That would be great! Also confirming the date — March 22, 10 AM.", "9:45 AM"),
        Message(6, true, "Confirmed! I'll be there at 9:45 for setup.", "10:00 AM"),
        Message(7, false, "Perfect, see you Saturday at 10!", "10:23 AM"),
    ),
    2 to listOf(
        Message(1, false, "Hey, I saw you applied for the furniture assembly.", "2:00 PM"),
        Message(2, true, "Yes! I have lots of experience with IKEA builds.", "2:15 PM"),
        Message(3, false, "Can you come tomorrow morning?", "2:30 PM"),
    ),
    3 to listOf(
        Message(1, false, "Hi, just wanted to confirm the flooring job details.", "11:00 AM"),
        Message(2, true, "Sure! I saw it's 220 sq ft of laminate on concrete.", "11:10 AM"),
        Message(3, false, "Exactly. I'll have all materials on site by Friday.", "11:15 AM"),
        Message(4, true, "Perfect. I'll plan for a Saturday start then.", "11:20 AM"),
        Message(5, false, "I'll bring the extra baseboards just in case.", "11:25 AM"),
    ),
)

@Composable
fun MessagesScreen(
    openChatWithJob: Pair<String, Job>? = null,
    onJobClick: (Job) -> Unit = {}
) {
    var conversations by remember {
        mutableStateOf(SEED_CONVERSATIONS)
    }
    var messageMap by remember { mutableStateOf(SEED_MESSAGES as Map<Int, List<Message>>) }
    var selectedConversationId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(openChatWithJob) {
        if (openChatWithJob != null) {
            val (workerName, job) = openChatWithJob
            val parts = workerName.trim().split(" ")
            val initials = parts.mapNotNull { it.firstOrNull()?.uppercaseChar() }.take(2).joinToString("")
            val now = java.text.SimpleDateFormat("h:mm a", Locale.US).format(java.util.Date())
            val newId = System.currentTimeMillis().toInt()
            val newConv = Conversation(
                id = newId,
                name = workerName,
                initials = initials,
                lastMsg = "Hi! I'd like to discuss a job with you.",
                time = "Now",
                unread = 0,
                jobTitle = job.title,
                online = true,
                attachedJob = job
            )
            val firstMsg = Message(1, true, "Hi ${parts.firstOrNull() ?: workerName}! I'd like to discuss a job with you.", now)
            conversations = listOf(newConv) + conversations
            messageMap = messageMap + (newId to listOf(firstMsg))
            selectedConversationId = newId
        }
    }

    val selectedConv = conversations.find { it.id == selectedConversationId }

    if (selectedConv != null) {
        ChatScreen(
            conversation = selectedConv,
            initialMessages = messageMap[selectedConv.id] ?: emptyList(),
            onBack = { selectedConversationId = null },
            onJobClick = onJobClick
        )
    } else {
        ConversationListScreen(
            conversations = conversations,
            onConversationClick = { selectedConversationId = it }
        )
    }
}

@Composable
private fun ConversationListScreen(
    conversations: List<Conversation>,
    onConversationClick: (Int) -> Unit
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
                .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 8.dp)
        ) {
            Text(
                text = "Messages",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = SlateDark
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(conversations) { conv ->
                ConversationItem(
                    conversation = conv,
                    onClick = { onConversationClick(conv.id) }
                )
                HorizontalDivider(color = Grey100)
            }
        }
    }
}

@Composable
private fun ConversationItem(conversation: Conversation, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Cream)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box {
            AvatarInitials(initials = conversation.initials, size = 48, bg = Forest)
            if (conversation.online) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2ECC71))
                        .border(2.dp, Cream, CircleShape)
                        .align(Alignment.BottomEnd)
                )
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = conversation.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = SlateDark)
                Text(text = conversation.time, fontSize = 11.sp, color = Grey400)
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = conversation.jobTitle, fontSize = 11.sp, color = Forest, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = conversation.lastMsg,
                fontSize = 13.sp,
                color = TextMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (conversation.unread > 0) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Ember),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "${conversation.unread}", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
            }
        }
    }
}

@Composable
private fun ChatScreen(
    conversation: Conversation,
    initialMessages: List<Message>,
    onBack: () -> Unit,
    onJobClick: (Job) -> Unit
) {
    var messages by remember { mutableStateOf(initialMessages) }
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Sand)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Cream)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        painter = painterResource(R.drawable.ic_back),
                        contentDescription = "Back",
                        tint = Slate,
                        modifier = Modifier.size(24.dp)
                    )
                }
                AvatarInitials(initials = conversation.initials, size = 36, bg = Forest)
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = conversation.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SlateDark)
                        if (conversation.online) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF2ECC71)))
                        }
                    }
                    Text(
                        text = if (conversation.online) "Online" else "Offline",
                        fontSize = 11.sp,
                        color = Grey400
                    )
                }
            }
            HorizontalDivider(color = Grey200)
        }

        conversation.attachedJob?.let { job ->
            JobCardMessage(job = job, onViewDetails = { onJobClick(job) })
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Grey100)
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(text = "Today", fontSize = 11.sp, color = Grey400)
                    }
                }
            }
            items(messages) { msg -> ChatBubble(message = msg) }
        }

        Surface(color = Cream, modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Type a message...", color = Grey400, fontSize = 14.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Grey200,
                        unfocusedBorderColor = Grey200,
                        focusedContainerColor = Sand,
                        unfocusedContainerColor = Sand
                    ),
                    maxLines = 4
                )
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Forest)
                        .clickable {
                            val text = inputText.trim()
                            if (text.isNotEmpty()) {
                                messages = messages + Message(
                                    id = messages.size + 1,
                                    fromMe = true,
                                    text = text,
                                    time = java.text.SimpleDateFormat("h:mm a", Locale.US).format(java.util.Date())
                                )
                                inputText = ""
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(painter = painterResource(R.drawable.ic_send), contentDescription = "Send", tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
private fun JobCardMessage(job: Job, onViewDetails: () -> Unit) {
    val catColor = CategoryColors[job.category.name] ?: Forest
    val catLabel = job.category.name.lowercase().replaceFirstChar { it.uppercase() }
    val fmt = NumberFormat.getCurrencyInstance(Locale.US).apply { maximumFractionDigits = 0 }
    val budgetText = if (job.budgetMin == job.budgetMax) fmt.format(job.budgetMin)
                     else "${fmt.format(job.budgetMin)} – ${fmt.format(job.budgetMax)}"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Cream)
            .border(1.dp, Grey200, RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(start = 14.dp, end = 14.dp, top = 12.dp, bottom = 10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(catColor)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = catLabel.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            when (job.status) {
                                JobStatus.ACTIVE -> Forest.copy(alpha = 0.1f)
                                JobStatus.DRAFT -> Grey200
                                JobStatus.COMPLETED -> Slate.copy(alpha = 0.08f)
                            }
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = job.status.name.lowercase().replaceFirstChar { it.uppercase() }.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (job.status) {
                            JobStatus.ACTIVE -> Forest
                            JobStatus.DRAFT -> Grey500
                            JobStatus.COMPLETED -> Slate
                        },
                        letterSpacing = 0.4.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = job.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = SlateDark, lineHeight = 20.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    Icon(painter = painterResource(R.drawable.ic_location), contentDescription = null, tint = Grey500, modifier = Modifier.size(12.dp))
                    Text(text = job.location, fontSize = 12.sp, color = Grey500)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    Icon(painter = painterResource(R.drawable.ic_dollar), contentDescription = null, tint = Forest, modifier = Modifier.size(12.dp))
                    Text(text = budgetText, fontSize = 12.sp, color = Forest, fontWeight = FontWeight.SemiBold)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    Icon(painter = painterResource(R.drawable.ic_clock), contentDescription = null, tint = Grey500, modifier = Modifier.size(12.dp))
                    Text(text = job.duration, fontSize = 12.sp, color = Grey500)
                }
            }
        }
        HorizontalDivider(color = Grey200)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onViewDetails)
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "View Details",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Forest
            )
        }
    }
}

@Composable
private fun ChatBubble(message: Message) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.fromMe) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 12.dp, topEnd = 12.dp,
                        bottomStart = if (message.fromMe) 12.dp else 4.dp,
                        bottomEnd = if (message.fromMe) 4.dp else 12.dp
                    )
                )
                .background(if (message.fromMe) Forest else Cream)
                .border(
                    width = if (message.fromMe) 0.dp else 1.dp,
                    color = if (message.fromMe) Color.Transparent else Grey200,
                    shape = RoundedCornerShape(
                        topStart = 12.dp, topEnd = 12.dp,
                        bottomStart = if (message.fromMe) 12.dp else 4.dp,
                        bottomEnd = if (message.fromMe) 4.dp else 12.dp
                    )
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Column {
                Text(
                    text = message.text,
                    fontSize = 14.sp,
                    color = if (message.fromMe) Color.White else HandifyText,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message.time,
                    fontSize = 10.sp,
                    color = if (message.fromMe) Color.White.copy(alpha = 0.6f) else Grey400,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}
