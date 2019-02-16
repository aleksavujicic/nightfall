package deimophobe.nightfall.map.region;

import org.codehaus.jparsec.*;
import org.codehaus.jparsec.error.ParserException;

import java.util.List;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by Deimophobe on 7/02/19.
 */
public class MapParser {
	private static final String SPHERE = "sphere";
	private static final String CYLINDER = "cylinder";
	private static final String BOX = "box";
	private static final String OVAL = "oval";
	
	private static final Terminals OPERATORS = Terminals.operators(
			"+","-","*","/", "^",               // Number operators
			"x", "y", "z",                      // Variables
			"<", "<=", ">", ">=",               // Conditionals
			"(", ")", "[", "]", ",",            // Syntax
			"&&", "||", "!",                    // Boolean
			SPHERE, CYLINDER, BOX, OVAL         // Region names
	);
	private static final Parser<?> TOKENIZER = OPERATORS.tokenizer().or(Terminals.DecimalLiteral.TOKENIZER.cast());
	
	private static final Parser<Void> IGNORED = Parsers.or(
			Scanners.JAVA_LINE_COMMENT,
			Scanners.JAVA_BLOCK_COMMENT,
			Scanners.WHITESPACES
	).skipMany();
	
	private static Parser<?> term(String... names) {
		return OPERATORS.token(names);
	}
	private static <T> Parser<T> operation(String name, T value) {
		return term(name).retn(value);
	}
	private static <T> Parser<T> asFullParser(Parser<T> parser) {
		return parser.from(TOKENIZER, IGNORED);
	}
	
	private static final Parser<?> OPEN_BRACKET = term("(");
	private static final Parser<?> CLOSE_BRACKET = term(")");
	private static <T> Parser<T> betweenBrackets(Parser<T> parser) {
		return parser.between(OPEN_BRACKET, CLOSE_BRACKET);
	}
	
	
	private static final Parser<Double> CALCULATOR;
	private static final Parser<Position> POSITION_PARSER;
	private static final Parser<DirectionalPosition> DIRECTIONAL_POSITION_PARSER;
	private static final Parser<Expression> EXPRESSION_PARSER;
	private static final Parser<Region> REGION_PARSER;
	
	static {
		// Number
		final Parser<Double> decimalNumber = Terminals.DecimalLiteral.PARSER.map(Double::valueOf);
		
		// 'Caluclator' (expressions without variables)
		Parser.Reference<Double> ref = Parser.newReference();
		Parser<Double> unit = betweenBrackets(ref.lazy()).or(decimalNumber);
		CALCULATOR = new OperatorTable<Double>()
				.infixl(operation("+", (l, r) -> l + r), 10)
				.infixl(operation("-", (l, r) -> l - r), 10)
				.infixl(operation("*", (l, r) -> l * r), 20)
				.infixl(operation("/", (l, r) -> l / r), 20)
				.prefix(operation("-", v -> -v), 30)
				.build(unit);
		ref.set(CALCULATOR);
		
		// Position parsers
		Parser<Position> positionParser =
				doubleList(3)
				.map(Position::fromDoubleList)
				.between(term("["), term("]"));
		
		Parser<DirectionalPosition> directionalPositionParser =
				doubleList(3, 5)
				.map(DirectionalPosition::fromDoubleList)
				.between(term("["), term("]"));
		
		POSITION_PARSER = asFullParser(positionParser);
		DIRECTIONAL_POSITION_PARSER = asFullParser(directionalPositionParser);
		
		
		// Expressions (with vars)
		Parser.Reference<Expression> unitExpressionRef = Parser.newReference();
		Parser<Expression> unitExpression = Parsers.or(
				betweenBrackets(unitExpressionRef.lazy()),
				operation("x", (x,y,z) -> x),
				operation("y", (x,y,z) -> y),
				operation("z", (x,y,z) -> z),
				decimalNumber.map(value -> ((x, y, z) -> value))
		);
		Parser<Expression> expression = new OperatorTable<Expression>()
				.infixl(operation("+", Expression::add), 10)
				.infixl(operation("-", Expression::subtract), 10)
				.infixl(operation("*", Expression::multiply), 20)
				.infixl(operation("/", Expression::divide), 20)
				.infixl(operation("^", Expression::power), 40)
				.prefix(operation("-", Expression::negate), 30)
				.build(unitExpression);
		unitExpressionRef.set(expression);
		EXPRESSION_PARSER = asFullParser(expression);
		
		// Conditional
		Parser<Conditional> conditional = Parsers.or(
				operation("<" , (l, r) -> l <  r),
				operation("<=", (l, r) -> l <= r),
				operation(">" , (l, r) -> l >  r),
				operation(">=", (l, r) -> l >= r)
		);
		Parser<Region> expressionRegion = Parsers.tuple(expression, conditional, expression).map(tuple ->
			new ExpressionRegion(tuple.a, tuple.c, tuple.b)
		);
		
		
		// Regions
		Parser.Reference<Region> regionReference = Parser.newReference();
		Parser<Region> unitRegion = Parsers.or(
				betweenBrackets(regionReference.lazy()),
				expressionRegion,
				regionParser(SPHERE,   4, SphericalRegion::fromParameterList),
				regionParser(CYLINDER, 3, CylindricalRegion::fromParameterList),
				regionParser(BOX,      6, BoxRegion::fromParameterList),
				regionParser(OVAL,     6, OvalRegion::fromParameterList)
		);
		Parser<Region> region = new OperatorTable<Region>()
				.prefix(operation("!", Region::not), 10)
				.infixl(operation("&&", (l, r) -> Region.and(l, r)), 20)
				.infixl(operation("||", (l, r) -> Region.or(l, r)), 30)
				.build(unitRegion);
		regionReference.set(region);
		REGION_PARSER = asFullParser(region);
	}
	
	
	
	private static Parser<List<Double>> doubleList(int length) {
		return doubleList(length, length);
	}
	
	private static Parser<List<Double>> doubleList(int min, int max) {
		checkArgument(min >= 1, "List parser must have positive min length (got %s)", min);
		checkArgument(max >= 1, "List parser must have positive max length (got %s)", max);
		checkArgument(max >= min, "List parser must have max length greater than min length (got min %s and max %s)", min, max);
		
		Parser<Double> preCommaValue = term(",").next(CALCULATOR);
		Parser<List<Double>> multiPreCommaValue = preCommaValue.times(min - 1, max - 1);
		
		return CALCULATOR.next(test ->
			multiPreCommaValue.map(list -> {
				list.add(0, test);
				return list;
			})
		);
	}
	
	private static Parser<Region> regionParser(String name, int parameters, Function<List<Double>, Region> regionCreator) {
		return term(name).next(
				betweenBrackets(
					doubleList(parameters)
					.map(regionCreator::apply)
				)
		);
	}
	
	private static <T> T parse(Parser<T> parser, String text) throws BadlyFormattedStringException {
		try {
			return parser.parse(text);
		} catch (ParserException e) {
			throw new BadlyFormattedStringException(e);
		}
	}
	
	
	
	public static Position parseLocation(String text) throws BadlyFormattedStringException {
		return parse(POSITION_PARSER, text);
	}
	
	public static DirectionalPosition parseFacingLocation(String text) throws BadlyFormattedStringException {
		return parse(DIRECTIONAL_POSITION_PARSER, text);
	}
	
	static Expression parseExpression(String text) throws BadlyFormattedStringException {
		return parse(EXPRESSION_PARSER, text);
	}
	
	public static Region parseRegion(String text) throws BadlyFormattedStringException {
		return parse(REGION_PARSER, text);
	}
}
