package com.shevaalex.android.plugev.presentation.mapscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shevaalex.android.plugev.domain.model.ChargingStation
import com.shevaalex.android.plugev.domain.model.Connection
import com.shevaalex.android.plugev.presentation.common.compose.*

@Composable
fun BottomSheet(
    chargingStation: ChargingStation,
    modifier: Modifier
) {
    val isPublic = chargingStation.usageTypeTitle.contains("public", true)
    val addressLine = getFullAddress(chargingStation)
    BottomSheetContent(
        isOperational = chargingStation.isOperationalStatus,
        isPublic = isPublic,
        title = chargingStation.addressTitle,
        address = addressLine,
        accessType = chargingStation.usageTypeTitle,
        usageCost = chargingStation.usageCost,
        connections = chargingStation.connections,
        modifier = modifier
    )
}

@Composable
private fun BottomSheetContent(
    isOperational: Boolean?,
    isPublic: Boolean,
    title: String,
    address: String,
    accessType: String?,
    usageCost: String,
    connections: List<Connection>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            when (isOperational) {
                true -> ChipGreen(text = "Operational")
                false -> ChipRed(text = "Not Operational")
                null -> ChipGrey()
            }
            if (isPublic) {
                ChipGreen(text = "Public")
            } else ChipRed(text = "Private")
        }
        Spacer(Modifier.height(10.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.subtitle1,
        )
        Spacer(Modifier.height(1.dp))
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                text = address,
                style = MaterialTheme.typography.body2
            )
        }
        accessType?.let { access ->
            Spacer(Modifier.height(3.dp))
            Text(
                text = access,
                style = MaterialTheme.typography.caption,
                color = if (isPublic) Teal800 else Red900
            )
        }
        Spacer(Modifier.height(5.dp))
        Text(
            text = if (usageCost.isNotBlank()) usageCost else "£ Usage cost unknown",
            style = MaterialTheme.typography.body2
        )
        Spacer(Modifier.height(5.dp))
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(horizontal = 2.dp, vertical = 2.dp)
        ) {
            items(connections.size) { index ->
                val connection = connections[index]
                ConnectionListItem(
                    quantity = connection.quantity,
                    connectionTitle = connection.connectionTitle,
                    powerLevelTitle = connection.powerLevelTitle,
                    isOperational = connection.isOperationalStatus,
                    powerKw = connection.power
                )
            }
        }
    }
}

@Composable
private fun ChipGreen(text: String) {
    ChipStatus(
        backgroundColor = Teal100trans70,
        contentColor = Teal800,
        text = text
    )
}

@Composable
private fun ChipRed(text: String) {
    ChipStatus(
        backgroundColor = Red200trans50,
        contentColor = Red900,
        text = text
    )
}

@Composable
private fun ChipGrey() {
    ChipStatus(
        backgroundColor = Grey200,
        contentColor = Color.Unspecified,
        text = "Unknown"
    )
}

@Composable
private fun ChipStatus(backgroundColor: Color, contentColor: Color, text: String) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = backgroundColor,
        contentColor = contentColor,
        modifier = Modifier.height(32.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 12.dp, end = 12.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.body2
            )
        }
    }
}

@Composable
private fun ConnectionListItem(
    quantity: Int,
    connectionTitle: String,
    powerLevelTitle: String?,
    isOperational: Boolean?,
    powerKw: String
) {
    val backgroundColour = isOperational?.let { isOperationalBoolean ->
        if (isOperationalBoolean) Teal050
        else Red050
    } ?: Grey200
    val operationalText = isOperational?.let { isOperationalBoolean ->
        if (isOperationalBoolean) "Operational"
        else "Not Operational"
    } ?: "Unknown"
    val quantityText = if (quantity == 0) "N/A" else quantity.toString().plus("x")
    val operationalColour = isOperational?.let { isOperationalBoolean ->
        if (isOperationalBoolean) Teal800
        else Red900
    }
    Card(
        backgroundColor = backgroundColour
    ) {
        Row(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(0.10f)
            ) {
                Text(
                    text = quantityText,
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier
                        .padding(3.dp)
                )
            }
            Spacer(
                Modifier
                    .fillMaxHeight()
                    .width(1.dp)
                    .background(MaterialTheme.colors.onPrimary)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.90f)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp, start = 6.dp, bottom = 3.dp, end = 7.dp)
                ) {
                    Text(
                        text = connectionTitle,
                        style = MaterialTheme.typography.body2,
                    )
                    Text(
                        text = operationalText,
                        style = MaterialTheme.typography.caption,
                        color = operationalColour ?: Color.Unspecified,
                        modifier = Modifier.padding(end = 3.dp)
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 3.dp, start = 6.dp, bottom = 6.dp, end = 7.dp)
                ) {
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                        Text(
                            text = powerLevelTitle ?: "Power level unknown",
                            style = MaterialTheme.typography.body2,
                        )
                    }
                    val power = if (powerKw.isBlank()) "? KW" else powerKw.plus(" KW")
                    Text(
                        text = power,
                        style = MaterialTheme.typography.body2,
                        modifier = Modifier.padding(end = 3.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun BottomSheetPreview() {
    PlugEvTheme {
        BottomSheetContent(
            isOperational = false,
            isPublic = true,
            title = "Park Plaza Westminster Bridge Hotel",
            address = "200 Westminster Bridge Rd, London SE1 7UT",
            accessType = "Public - Membership Required",
            usageCost = "",
            connections = connectionListForPreview(),
        )
    }
}

@Preview
@Composable
private fun ConnectionListItemPreview() {
    PlugEvTheme {
        ConnectionListItem(
            quantity = 1,
            connectionTitle = "Type 2 (Tethered Connector)",
            powerLevelTitle = "Level 3:  High (Over 40kW)",
            isOperational = true,
            powerKw = "50.0 KW"
        )
    }
}

private fun connectionListForPreview(): List<Connection> {
    return listOf(
        Connection(
            connectionFormalName = "IEC 62196-3 Configuration AA",
            connectionTitle = "CHAdeMO",
            statusTitle = "Operational",
            isOperationalStatus = true,
            powerLevel = 3,
            powerLevelTitle = "Level 3:  High (Over 40kW)",
            power = "50.0",
            quantity = 2
        ),
        Connection(
            connectionFormalName = "SAE J1772-2009",
            connectionTitle = "Type 1 (J1772)",
            statusTitle = "Not Operational",
            isOperationalStatus = false,
            powerLevel = 2,
            powerLevelTitle = "Level 2 : Medium (Over 2kW)",
            power = "4.0",
            quantity = 0
        ),
        Connection(
            connectionFormalName = "IEC 62196-2 Type 2",
            connectionTitle = "Type 2 (Socket Only)",
            statusTitle = "Operational",
            isOperationalStatus = true,
            powerLevel = 2,
            powerLevelTitle = "Level 2 : Medium (Over 2kW)",
            power = "5.0",
            quantity = 4
        ),
        Connection(
            connectionFormalName = "SAE J1772-2009",
            connectionTitle = "Type 1 (J1772)",
            statusTitle = "Unknown",
            isOperationalStatus = null,
            powerLevel = 2,
            powerLevelTitle = "Level 2 : Medium (Over 2kW)",
            power = "7.0",
            quantity = 3
        )
    )
}

private fun getFullAddress(chargingStation: ChargingStation): String {
    val addressLine1 = if (chargingStation.addressLine1.isNotBlank()) {
        chargingStation.addressLine1.plus(", ")
    } else null
    val addressLine2 = if (chargingStation.addressLine2.isNotBlank()) {
        chargingStation.addressLine2.plus(", ")
    } else null
    val town = if (chargingStation.town.isNotBlank()) {
        chargingStation.town.plus(", ")
    } else null
    val province = if (chargingStation.province.isNotBlank()) {
        chargingStation.province.plus(", ")
    } else null
    return addressLine1.orEmpty() +
            addressLine2.orEmpty() +
            town.orEmpty() +
            province.orEmpty() +
            chargingStation.postCode
}
