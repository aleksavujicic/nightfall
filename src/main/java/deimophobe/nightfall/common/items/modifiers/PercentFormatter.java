package deimophobe.nightfall.common.items.modifiers;

/**
 * Created by Deimophobe on 24/02/18.
 */
class PercentFormatter implements ValueFormatter {
	private final boolean showPlusOnFinal;
	
	public PercentFormatter() {
		this.showPlusOnFinal = true;
	}
	
	public PercentFormatter(boolean showPlusOnFinal) {
		this.showPlusOnFinal = showPlusOnFinal;
	}
	
	@Override
	public String formatValue(int value, boolean reasonedValue) {
		StringBuilder builder = new StringBuilder();
		
		if (showPlusOnFinal && value >= 0) builder.append('+');
		builder.append(value);
		builder.append('%');
		
		return builder.toString();
	}
}
