package deimophobe.nightfall.common.items.modifiers;

/**
 * Created by Deimophobe on 24/02/18.
 */
@FunctionalInterface
interface ValueFormatter {
	String formatValue(int value, boolean reasonedValue);
	
	
	
	ValueFormatter NULL_FORMATTER = (value, reasonedValue) -> null;
	ValueFormatter SIMPLE_FORMATTER = (value, reasonedValue) -> {
		StringBuilder builder = new StringBuilder();
		
		if (value >= 0 && reasonedValue) builder.append('+');
		builder.append(value);
		
		return builder.toString();
	};
	ValueFormatter PERCENT_FORMATTER = new PercentFormatter();
	ValueFormatter HEALTH_FORMATTER = (value, reasonedValue) ->  {
		if (reasonedValue) {
			return SIMPLE_FORMATTER.formatValue(value, reasonedValue);
		} else {
			return SIMPLE_FORMATTER.formatValue(value + 10, reasonedValue);
		}
	};
}
