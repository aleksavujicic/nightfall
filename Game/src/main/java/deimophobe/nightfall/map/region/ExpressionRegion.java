package deimophobe.nightfall.map.region;

/**
 * Created by Deimophobe on 15/02/19.
 */
class ExpressionRegion implements Region {
	private final Expression leftExpression;
	private final Expression rightExpression;
	private final Conditional conditional;
	
	ExpressionRegion(Expression leftExpression, Expression rightExpression, Conditional conditional) {
		this.leftExpression = leftExpression;
		this.rightExpression = rightExpression;
		this.conditional = conditional;
	}
	
	@Override
	public boolean containsPosition(double x, double y, double z) {
		double left = leftExpression.evaluate(x,y,z);
		double right = rightExpression.evaluate(x,y,z);
		return conditional.evaluate(left, right);
	}
	
}
