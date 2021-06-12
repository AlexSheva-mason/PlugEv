package com.shevaalex.android.plugev.presentation.mapscreen

data class FilterRowState(
    val optionsList: Set<FilterOption> = setOf(
        FilterOption.Level1,
        FilterOption.Level2,
        FilterOption.Level3,
        FilterOption.Public,
        FilterOption.Private
    )
)

enum class FilterOption(
    val filterType: FilterType,
    val text: String,
    var chipState: ChipState
) {
    Level1(FilterType.PowerLevel, "Level 1", ChipState.Enabled),
    Level2(FilterType.PowerLevel, "Level 2", ChipState.Enabled),
    Level3(FilterType.PowerLevel, "Level 3", ChipState.Enabled),
    Public(FilterType.Accessibility, "Public", ChipState.Enabled),
    Private(FilterType.Accessibility, "Private", ChipState.Enabled)
}

enum class FilterType {
    PowerLevel,
    Accessibility
}

enum class ChipState {
    Enabled,
    Disabled
}
