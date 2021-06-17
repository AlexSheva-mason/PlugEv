package com.shevaalex.android.plugev.presentation.mapscreen

data class FilterRowState(
    val optionsList: Set<FilterOption> = setOf(
        FilterOption.Level1(),
        FilterOption.Level2(),
        FilterOption.Level3(),
        FilterOption.Public(),
        FilterOption.Private()
    )
)

sealed class FilterOption(
    val filterType: FilterType,
    val text: String,
    val chipState: ChipState,
    val optionIds: List<String>
) {

    data class Level1(
        val isEnabled: Boolean = true,
        val filterValues: List<String> = listOf("1"),
    ) : FilterOption(
        filterType = FilterType.PowerLevel,
        text = "Level 1",
        chipState = if (isEnabled) ChipState.Enabled else ChipState.Disabled,
        optionIds = filterValues
    )

    data class Level2(
        val isEnabled: Boolean = true,
        val filterValues: List<String> = listOf("2"),
    ) : FilterOption(
        filterType = FilterType.PowerLevel,
        text = "Level 2",
        chipState = if (isEnabled) ChipState.Enabled else ChipState.Disabled,
        optionIds = filterValues
    )

    data class Level3(
        val isEnabled: Boolean = true,
        val filterValues: List<String> = listOf("3"),
    ) : FilterOption(
        filterType = FilterType.PowerLevel,
        text = "Level 3",
        chipState = if (isEnabled) ChipState.Enabled else ChipState.Disabled,
        optionIds = filterValues
    )

    data class Public(
        val isEnabled: Boolean = true,
        val filterValues: List<String> = listOf("1", "4", "5", "7"),
    ) : FilterOption(
        filterType = FilterType.Accessibility,
        text = "Public",
        chipState = if (isEnabled) ChipState.Enabled else ChipState.Disabled,
        optionIds = filterValues
    )

    data class Private(
        val isEnabled: Boolean = true,
        val filterValues: List<String> = listOf("2", "3", "6"),
    ) : FilterOption(
        filterType = FilterType.Accessibility,
        text = "Private",
        chipState = if (isEnabled) ChipState.Enabled else ChipState.Disabled,
        optionIds = filterValues
    )
}

enum class FilterType {
    PowerLevel,
    Accessibility
}

enum class ChipState {
    Enabled,
    Disabled
}
