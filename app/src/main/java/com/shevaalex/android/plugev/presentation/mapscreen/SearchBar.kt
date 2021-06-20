package com.shevaalex.android.plugev.presentation.mapscreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shevaalex.android.plugev.R
import com.shevaalex.android.plugev.presentation.common.compose.PlugEvTheme

@Composable
fun SearchBar(
    state: TextFieldValue,
    modifier: Modifier,
    onTextValueChange: (TextFieldValue) -> Unit,
    onSearchRequested: (String) -> Unit
) {

    SearchField(
        modifier = modifier,
        state = state,
        onTextValueChange = onTextValueChange,
        onSearchRequested = onSearchRequested
    )

}

@Composable
private fun SearchField(
    modifier: Modifier,
    state: TextFieldValue,
    onTextValueChange: (TextFieldValue) -> Unit,
    onSearchRequested: (String) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = SEARCH_BAR_PADDING_HORIZONTAL.dp,
                end = SEARCH_BAR_PADDING_HORIZONTAL.dp
            )
    ) {
        TextField(
            value = state,
            onValueChange = onTextValueChange,
            label = { Text(text = "Search postcode") },
            placeholder = { Text(text = "enter postcode e.g. EC2M 7PD") },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search_24),
                    contentDescription = null,
                )
            },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Characters,
                autoCorrect = false,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearchRequested(state.text)
                }
            ),
            singleLine = true,
            shape = MaterialTheme.shapes.small,
            colors = TextFieldDefaults
                .textFieldColors(
                    backgroundColor = MaterialTheme.colors.surface,
                    focusedIndicatorColor = MaterialTheme.colors.surface,
                    unfocusedIndicatorColor = MaterialTheme.colors.surface,
                    disabledIndicatorColor = MaterialTheme.colors.surface,
                    errorIndicatorColor = MaterialTheme.colors.surface
                ),
            modifier = Modifier
                .shadow(4.dp, MaterialTheme.shapes.small)
                .fillMaxWidth()
        )
    }
}

@Preview
@Composable
private fun SearchRowPreview() {
    PlugEvTheme {
        SearchField(
            modifier = Modifier,
            state = TextFieldValue(),
            onTextValueChange = {},
            onSearchRequested = {}
        )
    }
}
