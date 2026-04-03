package com.example.handify.ui.screen

import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.handify.R
import com.example.handify.domain.model.SavedAddress
import com.example.handify.presentation.location.LocationState
import com.example.handify.presentation.location.LocationViewModel
import com.example.handify.ui.theme.*
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AddressesScreen(
    onDismiss: () -> Unit,
    viewModel: LocationViewModel = koinViewModel()
) {
    val state = viewModel.state

    BackHandler {
        if (state.showAddForm) viewModel.closeAddForm() else onDismiss()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Sand)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        AddressesTopBar(onBack = onDismiss)
        if (state.showAddForm) {
            AddressForm(
                state = state,
                onTextChange = viewModel::updateNewAddressText,
                onLabelChange = viewModel::updateNewAddressLabel,
                onSave = viewModel::saveAddress,
                onCancel = viewModel::closeAddForm
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(top = 12.dp, bottom = 100.dp)
            ) {
                items(state.addresses, key = { it.id }) { address ->
                    AddressRow(
                        address = address,
                        isActive = address.id == state.activeAddressId,
                        onSelect = { viewModel.selectAddress(address.id) },
                        onEdit = { viewModel.openAddForm(editingId = address.id) },
                        onRemove = { viewModel.removeAddress(address.id) }
                    )
                }
                item {
                    TextButton(
                        onClick = { viewModel.openAddForm() },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = "+ Add new address",
                            color = Forest,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AddressesTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                painter = painterResource(R.drawable.ic_chevron_right),
                contentDescription = "Back",
                tint = SlateDark,
                modifier = Modifier.size(22.dp)
            )
        }
        Text(
            text = "Saved Addresses",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = SlateDark,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
internal fun AddressRow(
    address: SavedAddress,
    isActive: Boolean,
    onSelect: () -> Unit,
    onEdit: () -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Cream)
            .clickable { onSelect() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_location),
            contentDescription = null,
            tint = Forest,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f).clickable { onEdit() }) {
            Text(
                text = address.label,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = SlateDark
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = address.fullAddress,
                fontSize = 12.sp,
                color = Grey500
            )
        }
        if (isActive) {
            Icon(
                painter = painterResource(R.drawable.ic_check),
                contentDescription = "Active",
                tint = Forest,
                modifier = Modifier.size(18.dp)
            )
        } else {
            IconButton(onClick = onRemove, modifier = Modifier.size(36.dp)) {
                Icon(
                    painter = painterResource(R.drawable.ic_trash),
                    contentDescription = "Remove",
                    tint = Grey400,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
internal fun AddressForm(
    state: LocationState,
    onTextChange: (String) -> Unit,
    onLabelChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = if (state.editingId != null) "Edit Address" else "New Address",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = SlateDark
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "FULL ADDRESS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Grey500,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = state.newAddressText,
            onValueChange = onTextChange,
            placeholder = { Text("e.g. 123 Main St, Austin, TX", color = Grey400) },
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

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "LABEL",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Grey500,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        LabelSegmentedControl(
            selected = state.newAddressLabel,
            onSelect = onLabelChange
        )

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Forest)
        ) {
            Text("Save Address", fontWeight = FontWeight.SemiBold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(
            onClick = onCancel,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancel", color = Grey500, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
internal fun LabelSegmentedControl(selected: String, onSelect: (String) -> Unit) {
    val labels = listOf("Home", "Work", "Other")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Grey100)
            .padding(3.dp)
    ) {
        labels.forEach { label ->
            val isSelected = selected == label
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isSelected) Cream else Color.Transparent)
                    .clickable { onSelect(label) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSelected) SlateDark else Grey400
                )
            }
        }
    }
}
