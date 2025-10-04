package net.pwing.itemattributes.util;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.function.Function;

public final class ExpressionUtils {
    private static final Function MIN = new Function("min", 2) {

        @Override
        public double apply(double... doubles) {
            return Math.min(doubles[0], doubles[1]);
        }
    };

    public static Expression createExpression(String expression) {
        return new ExpressionBuilder(expression)
                .functions(MIN)
                .variables("rand")
                .build()
                .setVariable("rand", Math.random());
    }
}
