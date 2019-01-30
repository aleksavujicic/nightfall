package deimophobe.nightfall.common.items.modifiers;

/**
 * Created by Deimophobe on 24/02/18.
 */
class PercentFormatter implements ValueFormatter {
	private final boolean showPlusOnFinal;
	
	PercentFormatter() {
		this.showPlusOnFinal = false;
	}
	
	PercentFormatter(boolean showPlusOnFinal) {
		this.showPlusOnFinal = showPlusOnFinal;
	}
	
	@Override
	public String formatValue(int value, boolean reasonedValue) {
		StringBuilder builder = new StringBuilder();
		
		if (value >= 0 && (reasonedValue || showPlusOnFinal)) {
			builder.append('+');
		}
		builder.append(value);
		builder.append('%');
		
		return builder.toString();
	}
}
