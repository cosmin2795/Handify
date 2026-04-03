package com.example.handify.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
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
import com.example.handify.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@Composable
fun JobCard(job: Job, onClick: () -> Unit, userLat: Double? = null, userLng: Double? = null) {
    val catColor = CategoryColors[job.category.name] ?: Forest
    val catLabel = job.category.name.lowercase().replaceFirstChar { it.uppercase() }
    val budgetText = formatBudget(job.budgetMin, job.budgetMax)
    val isNew = System.currentTimeMillis() - job.createdAt < 86_400_000L
    val distanceText = run {
        val jLat = job.lat; val jLng = job.lng
        val uLat = userLat; val uLng = userLng
        if (jLat != null && jLng != null && uLat != null && uLng != null)
            formatDistance(haversineKm(uLat, uLng, jLat, jLng))
        else null
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
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
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                CategoryChip(label = catLabel, color = catColor)
                if (isNew) {
                    TagChip(label = "New", textColor = Ember, bgColor = Ember.copy(alpha = 0.1f))
                }
                if (job.isUrgent) {
                    TagChip(label = "Urgent", textColor = Color.White, bgColor = Ember)
                }
                if (distanceText != null) {
                    TagChip(label = distanceText, textColor = Forest, bgColor = Forest.copy(alpha = 0.1f))
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
                MetaLabel(text = job.location)
                MetaLabel(text = job.duration)
                MetaLabel(text = budgetText, color = Forest)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                AvatarInitials(
                    initials = job.clientName.toInitials(),
                    size = 28,
                    bg = catColor
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = job.clientName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextMid
                )
                if (job.clientRating > 0.0) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "★ ${job.clientRating}",
                        fontSize = 12.sp,
                        color = Color(0xFFD4A030),
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                if (job.applicantsCount > 0) {
                    Text(
                        text = "${job.applicantsCount} applied",
                        fontSize = 11.sp,
                        color = Forest,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryChip(label: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color)
            .padding(horizontal = 10.dp, vertical = 3.dp)
    ) {
        Text(
            text = label.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
private fun TagChip(label: String, textColor: Color, bgColor: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = label.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
private fun MetaLabel(text: String, color: Color = Grey500) {
    Text(text = text, fontSize = 12.sp, color = color)
}

@Composable
fun AvatarInitials(initials: String, size: Int, bg: Color) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(bg),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            fontSize = (size * 0.32f).sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

private fun formatBudget(min: Double, max: Double): String {
    val fmt = NumberFormat.getCurrencyInstance(Locale.US).apply { maximumFractionDigits = 0 }
    return if (min == max) fmt.format(min) else "${fmt.format(min)} – ${fmt.format(max)}"
}

private fun String.toInitials(): String =
    trim().split(" ").filter { it.isNotEmpty() }.take(2).joinToString("") { it.first().uppercase() }

private fun haversineKm(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
    val r = 6371.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLng = Math.toRadians(lng2 - lng1)
    val a = Math.sin(dLat / 2).let { it * it } +
            Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
            Math.sin(dLng / 2).let { it * it }
    return r * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
}

private fun formatDistance(km: Double): String =
    if (km < 1.0) "${(km * 1000).toInt()} m" else "${"%.1f".format(km)} km"
