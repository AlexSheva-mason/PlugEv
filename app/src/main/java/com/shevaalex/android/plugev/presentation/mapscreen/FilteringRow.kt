package com.shevaalex.android.plugev.presentation.mapscreen

import androidx.compose.animation.animateColor
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shevaalex.android.plugev.R
import com.shevaalex.android.plugev.presentation.common.compose.PlugEvTheme
import com.shevaalex.android.plugev.presentation.common.compose.Teal100
import com.shevaalex.android.plugev.presentation.common.compose.Teal800

@Composable
fun FilteringRow(
    state: FilterRowState,
    modifier: Modifier,
    onFilterOptionStateChange: (FilterOption, Boolean) -> Unit
) {
    Row(
        modifier = modifier
            .padding(
                top = FILTER_ROW_PADDING_VERTICAL.dp,
                bottom = FILTER_ROW_PADDING_VERTICAL.dp
            )
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.width(FILTER_ROW_PADDING_HORIZONTAL.dp))
        state.optionsList.forEach {
            ChipFilter(it, onFilterOptionStateChange)
        }
        Spacer(modifier = Modifier.width(FILTER_ROW_PADDING_HORIZONTAL.dp))
    }
}

@Composable
private fun ChipFilter(
    option: FilterOption,
    onFilterOptionStateChange: (FilterOption, Boolean) -> Unit
) {
    val transition = updateTransition(
        targetState = option.chipState,
        label = "filter_chip_transition"
    )
    val colourBackground by transition.animateColor(
        label = "filter_chip_transition"
    ) { state ->
        when (state) {
            ChipState.Enabled -> Teal100
            ChipState.Disabled -> MaterialTheme.colors.background
        }
    }
    val contentColour by transition.animateColor(
        label = "filter_chip_transition"
    ) { state ->
        when (state) {
            ChipState.Enabled -> Teal800
            ChipState.Disabled -> MaterialTheme.colors.onBackground
        }
    }

    Surface(
        shape = MaterialTheme.shapes.small,
        color = colourBackground,
        contentColor = contentColour,
        modifier = Modifier
            .animateContentSize()
            .padding(FILTER_CHIP_PADDING.dp)
            .shadow(2.dp, MaterialTheme.shapes.small)
            .height(FILTER_CHIP_HEIGHT.dp)
            .clickable(true) {
                val newChipState = option.chipState != ChipState.Enabled
                onFilterOptionStateChange(option, newChipState)
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 4.dp, end = 12.dp)
        ) {
            if (option.chipState == ChipState.Enabled) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_check_24),
                    contentDescription = null,
                )
            }
            Text(
                text = option.text,
                style = MaterialTheme.typography.body2,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Preview
@Composable
private fun FilteringRowPreview() {
    PlugEvTheme {
        FilteringRow(
            state = FilterRowState(),
            modifier = Modifier,
            onFilterOptionStateChange = { _, _ -> }
        )
    }
}

@Preview
@Composable
private fun ChipFilterActivePreview() {
    PlugEvTheme {
        ChipFilter(FilterOption.Private()) { _, _ -> }
    }
}
