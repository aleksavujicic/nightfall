package deimophobe.nightfall.map.region;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by Deimophobe on 16/02/19.
 */
class ExpressionTest {
	private static final int NUM_DATA_TO_GENERATE = 50;
	private final Random random = new Random(3141592);
	
	private static Stream<Arguments> expressionData() {
		return Stream.of(
				Arguments.of("x", (Expression) (x, y, z) -> x),
				Arguments.of("y", (Expression) (x, y, z) -> y),
				Arguments.of("z", (Expression) (x, y, z) -> z),
				Arguments.of("3", (Expression) (x, y, z) -> 3),
				Arguments.of("3 + 2", (Expression) (x, y, z) -> 5),
				Arguments.of("x + 3", (Expression) (x, y, z) -> x + 3),
				Arguments.of("3 + x", (Expression) (x, y, z) -> x + 3),
				Arguments.of("x + y + z", (Expression) (x, y, z) -> x + y + z),
				Arguments.of("x+y", (Expression) (x, y, z) -> x+y),
				Arguments.of("x-y", (Expression) (x, y, z) -> x-y),
				Arguments.of("x*y", (Expression) (x, y, z) -> x*y),
				Arguments.of("x/y", (Expression) (x, y, z) -> x/y),
				Arguments.of("x^y", (Expression) (x, y, z) -> Math.pow(x,y)),
				Arguments.of("-z", (Expression) (x, y, z) -> -z),
				Arguments.of("(x+y)/(z*z + 3*x + -y*y)", (Expression) (x, y, z) -> (x+y)/(z*z + 3*x + -y*y))
		);
	}
	
	@DisplayName("Should parse valid expressions correctly")
	@ParameterizedTest(name = "(${index}) expr={0}")
	@MethodSource("expressionData")
	void testExpressions(String text, Expression expected) throws BadlyFormattedStringException {
		Expression parsed = MapParser.parseExpression(text);
		
		for (int i=0; i<NUM_DATA_TO_GENERATE; i++) {
			double x = random.nextDouble();
			double y = random.nextDouble();
			double z = random.nextDouble();
			
			assertEquals(
					expected.evaluate(x,y,z),
					parsed.evaluate(x,y,z)
			);
		}
	}
	
	private static Stream<Arguments> expressionRegionData() {
		return Stream.of(
				Arguments.of("x >= 0", (Region) (x, y, z) -> x >= 0),
				Arguments.of("y >= 0", (Region) (x, y, z) -> y >= 0),
				Arguments.of("z >= 0", (Region) (x, y, z) -> z >= 0),
				Arguments.of("x > 0", (Region) (x, y, z) -> x > 0),
				Arguments.of("x < 0", (Region) (x, y, z) -> x < 0),
				Arguments.of("x <= 0", (Region) (x, y, z) -> x <= 0),
				Arguments.of("x*x + y*y <= 0.5", (Region) (x, y, z) -> x*x + y*y <= 0.5)
		);
	}
	
	@DisplayName("Should parse valid expression regions")
	@ParameterizedTest(name = "(${index}) expr={0}")
	@MethodSource("expressionRegionData")
	void testExpressionRegions(String text, Region expected) throws BadlyFormattedStringException {
		Region parsed = MapParser.parseRegion(text);
		
		for (int i=0; i<NUM_DATA_TO_GENERATE; i++) {
			double x = random.nextDouble();
			double y = random.nextDouble();
			double z = random.nextDouble();
			
			assertEquals(
					expected.containsPosition(x,y,z),
					parsed.containsPosition(x,y,z)
			);
		}
	}
}
