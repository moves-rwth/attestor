package de.rwth.i2.attestor.phases.parser;

import de.rwth.i2.attestor.domain.RelativeInteger;
import de.rwth.i2.attestor.predicateAnalysis.RelativeIntegerBaseListener;
import de.rwth.i2.attestor.predicateAnalysis.RelativeIntegerParser;

import java.util.ArrayDeque;
import java.util.Deque;

public class RelativeIntegerExtractor extends RelativeIntegerBaseListener {
    Deque<AbstractionRuleExpression> stack = new ArrayDeque<>();

    public AbstractionRuleExpression getResult() {
        if (stack.size() != 1) {
            throw new IllegalStateException("Error on the operation stack");
        }

        return stack.peek();
    }

    @Override
    public void exitExpr(RelativeIntegerParser.ExprContext ctx) {
        super.exitExpr(ctx);

        switch (getCase(ctx)) {
            case INTEGER:
                final int integer = Integer.parseInt(ctx.INTEGER().getText());

                stack.push((index, assign) -> RelativeInteger.get(integer));
                break;

            case MINUS:
                final AbstractionRuleExpression toInvert = stack.pop();

                stack.push((index, assign) -> RelativeInteger.opSet.invert(toInvert.evaluate(index, assign)));
                break;

            case ADD:
                final AbstractionRuleExpression addRight = stack.pop();
                final AbstractionRuleExpression addLeft = stack.pop();

                stack.push((index, assign) -> RelativeInteger.opSet.add(
                        addLeft.evaluate(index, assign),
                        addRight.evaluate(index, assign)));
                break;

            case SUBTRACT:
                final AbstractionRuleExpression subRight = stack.pop();
                final AbstractionRuleExpression subLeft = stack.pop();

                stack.push((index, assign) -> RelativeInteger.opSet.subtract(
                        subLeft.evaluate(index, assign),
                        subRight.evaluate(index, assign)));
                break;

            case VAR:
                stack.push((index, assign) -> RelativeInteger.opSet.getVariable());
                break;

            case INDEX:
                stack.push((index, assign) -> index);
                break;

            case ASSIGN:
                final int idx = Integer.parseInt(ctx.INTEGER().getText());

                stack.push((index, assign) -> assign.get(idx));
                break;

            case PAREN:
                break;
            default:
                throw new IllegalStateException("Unknown parse tree node");
        }
    }

    private Case getCase(RelativeIntegerParser.ExprContext ctx) {
        if (ctx.INTEGER() != null && ctx.Idx == null) {
            return Case.INTEGER;
        }

        if (ctx.VAR() != null) {
            return Case.VAR;
        }

        if (ctx.INDEX() != null) {
            return Case.INDEX;
        }

        if (ctx.ASSIGN() != null) {
            return Case.ASSIGN;
        }

        if (ctx.MINUS() != null && ctx.Expr != null) {
            return Case.MINUS;
        }
        if (ctx.PLUS() != null && ctx.Left != null && ctx.Right != null) {
            return Case.ADD;
        }

        if (ctx.MINUS() != null && ctx.Left != null && ctx.Right != null) {
            return Case.SUBTRACT;
        }

        return Case.PAREN;
    }

    private enum Case {
        INTEGER, MINUS, ADD, SUBTRACT, VAR, INDEX, ASSIGN, PAREN
    }
}
