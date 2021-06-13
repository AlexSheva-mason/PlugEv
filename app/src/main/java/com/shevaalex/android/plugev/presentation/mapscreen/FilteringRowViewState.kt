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
    var chipState: ChipState
) {
    data class Level1(
        val id: String = "1"
    ) : FilterOption(FilterType.PowerLevel, "Level 2", ChipState.Enabled)

    data class Level2(
        val id: String = "2"
    ) : FilterOption(FilterType.PowerLevel, "Level 2", ChipState.Enabled)

    data class Level3(
        val id: String = "3"
    ) : FilterOption(FilterType.PowerLevel, "Level 3", ChipState.Enabled)

    data class Public(
        val ids: List<String> = listOf("1", "4", "5", "7")
    ) : FilterOption(FilterType.Accessibility, "Public", ChipState.Enabled)

    data class Private(
        val ids: List<String> = listOf("2", "3", "6")
    ) : FilterOption(FilterType.Accessibility, "Private", ChipState.Enabled)
}

enum class FilterType {
    PowerLevel,
    Accessibility
}

enum class ChipState {
    Enabled,
    Disabled
}
