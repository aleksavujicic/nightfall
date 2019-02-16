package deimophobe.nightfall.map.region;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by Deimophobe on 15/02/19.
 */
class ElementaryRegionTest {
	
	private static Stream<Arguments> sphereData() {
		return Stream.of(
				Arguments.of("sphere(1,2,3,4)", 1, 2, 3, 4)
		);
	}
	
	@DisplayName("Should parse valid sphere regions")
	@ParameterizedTest(name = "(${index}) x={1}, y={2}, z={3}, r={4}")
	@MethodSource("sphereData")
	void testPositionParse(String text, double x, double y, double z, double r) throws BadlyFormattedStringException {
		Region expectedRegion = new SphericalRegion(x, y, z, r);
		Region parsedRegion = MapParser.parseRegion(text);
		assertEquals(expectedRegion, parsedRegion);
	}
}
