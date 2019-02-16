package deimophobe.nightfall.map.region;

/**
 * Created by Deimophobe on 15/02/19.
 */
@FunctionalInterface
interface Expression {
	double evaluate(double x, double y, double z);
	
	
	static Expression add(Expression expression1, Expression expression2) {
		return (x,y,z) -> expression1.evaluate(x,y,z) + expression2.evaluate(x,y,z);
	}
	static Expression subtract(Expression expression1, Expression expression2) {
		return (x,y,z) -> expression1.evaluate(x,y,z) - expression2.evaluate(x,y,z);
	}
	static Expression multiply(Expression expression1, Expression expression2) {
		return (x,y,z) -> expression1.evaluate(x,y,z) * expression2.evaluate(x,y,z);
	}
	static Expression divide(Expression expression1, Expression expression2) {
		return (x,y,z) -> expression1.evaluate(x,y,z) / expression2.evaluate(x,y,z);
	}
	static Expression power(Expression expression1, Expression expression2) {
		return (x,y,z) -> Math.pow(expression1.evaluate(x,y,z), expression2.evaluate(x,y,z));
	}
	static Expression negate(Expression expression) {
		return (x,y,z) -> -expression.evaluate(x,y,z);
	}
}
