package deimophobe.nightfall.map.region;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Created by Deimophobe on 7/02/19.
 */
@DisplayName("Checks that the position parser works as intended")
class PositionParserTest {
	
	private static Stream<Arguments> positionData() {
		return Stream.of(
				Arguments.of("[ 1, 2, 3]",   1, 2, 3),
				Arguments.of("  [ 1, 2, 3]  ",   1, 2, 3),
				Arguments.of("[ 1, 2.5, 4]", 1, 2.5, 4),
				Arguments.of("[ 1, 1, 1]",   1, 1, 1),
				Arguments.of("[ 1, 0, -1]",  1, 0, -1),
				Arguments.of("[ -1.5, -2.6, -15.82]",  -1.5, -2.6, -15.82),
				Arguments.of("[ 0.0, 3.0, -2.0]",  0.0, 3.0, -2.0),
				Arguments.of("[ 1.23456, -1.23456, 1.23456]",  1.23456, -1.23456, 1.23456),
				Arguments.of("  [ - 1.0 , 0,2 ]",  -1, 0, 2)
		);
	}
	
	@DisplayName("Should parse valid location objects accurately")
	@ParameterizedTest(name = "(${index}) x={1}, y={2}, z={3}")
	@MethodSource("positionData")
	void testPositionParse(String text, double x, double y, double z) throws BadlyFormattedStringException {
		Position position = MapParser.parseLocation(text);
		Position expectedLocation = new Position(x, y, z);
		assertEquals(expectedLocation, position);
	}
	
	
	
	@DisplayName("Should fail to parse bad positions.")
	@ParameterizedTest()
	@ValueSource(strings = {
			"[1, 2, 3",
			"1, 2, 3]",
			"1, 2, 3",
			"[1, 2, 3",
			"[1, 2]",
			"[1, 2, 3, 4]",
			"[1 2 3]",
			"[1 2,3]",
			"[1,2 3]",
			"[1 2, 3, 4]",
			"[a, 2, 3]",
			"[2a, 1, 2]",
			"(2, 1, 2)",
			"2[1, 1, 2]",
			"[1, 1, 2]4",
			"[1, 2, NaN]",
	})
	void testBadPositionParse(String text) {
		assertThrows(BadlyFormattedStringException.class,
				() -> MapParser.parseLocation(text)
		);
	}
	
	private static Stream<Arguments> directionalPositionData() {
		return Stream.of(
				Arguments.of("[ 1, 2, 3]",   1, 2, 3, 0f, 0f),
				Arguments.of("[ 1, 2, 3, 4]",   1, 2, 3, 4f, 0f),
				Arguments.of("[ 1, 2, 3, 4, 5]",   1, 2, 3, 4f, 5f),
				Arguments.of("[ 1.1, -2.2, 3.3, -4.4, 5.5]",   1.1, -2.2, 3.3, -4.4f, 5.5f)
		);
	}
	
	@DisplayName("Should parse valid facing location objects accurately")
	@ParameterizedTest(name = "(${index}) x={1}, y={2}, z={3}, yaw={4}, pitch={5}")
	@MethodSource("directionalPositionData")
	void testDirectionalPositionParse(String text, double x, double y, double z, float yaw, float pitch) throws BadlyFormattedStringException {
		DirectionalPosition evaluatedFacingLocation = MapParser.parseFacingLocation(text);
		DirectionalPosition expectedFacingLocation = new DirectionalPosition(x, y, z, yaw, pitch);
		assertEquals(expectedFacingLocation, evaluatedFacingLocation);
	}
	
	
	@DisplayName("Should fail to parse bad directional positions.")
	@ParameterizedTest()
	@ValueSource(strings = {
			"[1, 2, 3",
			"1, 2, 3]",
			"1, 2, 3, 4, 5",
			"[1, 2, 3",
			"[1, 2]",
			"[1, 2, 3, 4, 5, 6]",
			"[1 2 3]",
			"[1 2,3]",
			"[1,2 3]",
			"[1 2, 3, 4]",
			"[a, 2, 3]",
			"[2a, 1, 2]",
			"(2, 1, 2)",
			"2[1, 1, 2]",
			"[1, 1, 2]4",
	})
	void testBadDirectionalPositionParse(String text) {
		assertThrows(BadlyFormattedStringException.class,
				() -> MapParser.parseFacingLocation(text)
		);
	}
}
