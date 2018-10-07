package deimophobe.nightfall.common.items.modifiers;

import com.google.common.base.Preconditions;

import java.math.BigInteger;
import java.text.DecimalFormat;

import static com.google.common.base.Preconditions.checkArgument;


/**
 * Created by Deimophobe on 7/10/18.
 */
class FractionalFormatter implements ValueFormatter {
	private final int denominator;
	private final DecimalFormat format;
	
	FractionalFormatter(int decimalShifts) {
		checkArgument(decimalShifts > 0);
		
		int tempDenom = 1;
		for (int i=0; i<decimalShifts; i++) {
			tempDenom *= 10;
		}
		denominator = tempDenom;
		
		format = new DecimalFormat();
		format.setMaximumFractionDigits(decimalShifts);
		format.setMinimumFractionDigits(1);
	}
	
	@Override
	public String formatValue(int value, boolean reasonedValue) {
		StringBuilder builder = new StringBuilder();
		
		if (value >= 0 && reasonedValue) builder.append('+');
		String number = format.format((double) value / denominator);
		builder.append(number);
		
		return builder.toString();
	}
}
