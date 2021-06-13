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
    var chipState: ChipState,
    open val optionIds: List<String>
) {
    data class Level1(
        override val optionIds: List<String> = listOf("1"),
    ) : FilterOption(FilterType.PowerLevel, "Level 2", ChipState.Enabled, optionIds = optionIds)

    data class Level2(
        override val optionIds: List<String> = listOf("2"),
    ) : FilterOption(FilterType.PowerLevel, "Level 2", ChipState.Enabled, optionIds = optionIds)

    data class Level3(
        override val optionIds: List<String> = listOf("3"),
    ) : FilterOption(FilterType.PowerLevel, "Level 3", ChipState.Enabled, optionIds = optionIds)

    data class Public(
        override val optionIds: List<String> = listOf("1", "4", "5", "7"),
    ) : FilterOption(FilterType.Accessibility, "Public", ChipState.Enabled, optionIds = optionIds)

    data class Private(
        override val optionIds: List<String> = listOf("2", "3", "6"),
    ) : FilterOption(FilterType.Accessibility, "Private", ChipState.Enabled, optionIds = optionIds)
}

enum class FilterType {
    PowerLevel,
    Accessibility
}

enum class ChipState {
    Enabled,
    Disabled
}
