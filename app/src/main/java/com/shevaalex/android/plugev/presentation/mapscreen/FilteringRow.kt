package com.shevaalex.android.plugev.presentation.mapscreen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shevaalex.android.plugev.R
import com.shevaalex.android.plugev.presentation.common.compose.PlugEvTheme
import com.shevaalex.android.plugev.presentation.common.compose.Teal100trans70
import com.shevaalex.android.plugev.presentation.common.compose.Teal800

@Composable
fun FilteringRow(
    state: FilterRowState,
    modifier: Modifier
) {
    Row(
        modifier = modifier
            .padding(top = 8.dp, bottom = 8.dp)
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
    ) {
        state.optionsList.forEach {
            Spacer(modifier = Modifier.width(8.dp))
            ChipFilter(it)
        }
        Spacer(modifier = Modifier.width(8.dp))
    }
}

@Composable
private fun ChipFilter(option: FilterOption) {
    Surface(
        shape = MaterialTheme.shapes.small,
        //TODO remove colour transparency
        color = Teal100trans70,
        contentColor = Teal800,
        modifier = Modifier
            .height(32.dp)
            .animateContentSize()
            .clickable(true) {
                //TODO
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 4.dp, end = 12.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_check_24),
                contentDescription = null,
            )
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
            modifier = Modifier
        )
    }
}

@Preview
@Composable
private fun ChipFilterActivePreview() {
    PlugEvTheme {
        ChipFilter(FilterOption.Private)
    }
}