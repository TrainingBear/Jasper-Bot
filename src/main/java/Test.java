import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BinaryOperator;

import org.w3c.dom.Notation;

public final class Test {
    final private static BigDecimal PI = new BigDecimal(Math.PI), E = new BigDecimal(Math.E);
    final static Map<String, Notation> notationsMap = Map.of(
            "pi", (a, b, c) -> PI, "e", (a, b, c) -> E);

    public static void main(String[] args) {
        InputWrapper input = new InputWrapper("sqrt(2)/2");
        System.out.println("INPUT: " + input.input);
        final long startTime;
        BigDecimal output;
        try {
            char c;
            for (int i = 0; i < input.input.length(); i++)
                if (Character.isWhitespace(c = input.input.charAt(i)) || c == '\'')
                    input.input.deleteCharAt(i--);
            startTime = System.currentTimeMillis();
            output = handleScope(input, false)
                    .setScale(10, RoundingMode.FLOOR)
                    .stripTrailingZeros();
        } catch (Exception e) {
            System.out.println("ERROR WHILE ON INDEX: " + input.index);
            if (input.index < input.input.length())
                input.input.insert(input.index, " ").insert(input.index + 2, " ");
            System.out.println(input.input);
            e.printStackTrace();
            return;
        }
        System.out.println("OUTPUT: " + output.toPlainString() + "\nTime: " +
                (System.currentTimeMillis() - startTime) + "ms");
    }

    private static class ScopeContainer {
        boolean isStartPlus = true, isDividing = false, isFront = true;
        OperatorEnum operator = null, ASOperator = null;
        BigDecimal totalValue = BigDecimal.ZERO, termsScope = BigDecimal.ZERO;
        final int startScope;

        private ScopeContainer(int startScope) {
            this.startScope = startScope;
        }
    }

    private static BigDecimal handleScope(InputWrapper iw, final boolean hasClosedParam) {
        if (hasClosedParam && iw.index >= iw.input.length())
            iw.createError("There is nothing to be calculate", --iw.index);
        ScopeContainer sc = new ScopeContainer(iw.index);

        for (; iw.index < iw.input.length(); iw.index++) {
            final char c = iw.input.charAt(iw.index);
            if (c == '(')
                handleParentheses(sc, iw);
            else if (c == ')') {
                if (!hasClosedParam)
                    iw.createError("Illegal close parenthesees ')'", iw.index);
                if (sc.startScope == iw.index)
                    iw.createError("There is nothing to be calculate", sc.startScope - 1);
                return sc.totalValue = sc.ASOperator != null
                        ? sc.ASOperator.apply(sc.totalValue, sc.termsScope)
                        : sc.termsScope;

            } else if (Character.isDigit(c) || c == '.')
                handleNumber(sc, iw, true);

            else if ((sc.operator = OperatorEnum.map.get(c)) != null) {
                if (++iw.index < iw.input.length() && OperatorEnum.map.containsKey(iw.input.charAt(iw.index)))
                    iw.createError("Illegal twice operator: " + iw.input.charAt(iw.index), iw.index);
                if (--iw.index != sc.startScope) {
                    sc.isDividing = sc.operator == OperatorEnum.DIVIDE;
                    if (sc.operator == OperatorEnum.ADD || sc.operator == OperatorEnum.SUBTRACT) {
                        sc.totalValue = sc.ASOperator == null ? sc.termsScope
                                : sc.ASOperator.apply(sc.totalValue, sc.termsScope);
                        sc.ASOperator = sc.operator;
                        sc.termsScope = BigDecimal.ZERO; // = 0
                        sc.operator = null;
                        sc.isFront = true;
                    }
                    continue;
                }
                switch (sc.operator) {
                    case SUBTRACT:
                        sc.isStartPlus = false;
                    case ADD:
                        sc.operator = null;
                        break;
                    default:
                        iw.createError("Illegal operator in the front", iw.index);
                }
            } else if (Character.isLetter(c)) {
                handleLetter(sc, iw, true);
            } else {
                final String errorMessage = "Unknown character/operator/notation/terms, could be unsupported symbol?";
                iw.createError(errorMessage, iw.index);
            }
        }
        return sc.totalValue = sc.ASOperator != null
                ? sc.ASOperator.apply(sc.totalValue, sc.termsScope)
                : sc.termsScope;

    }

    private static void handleParentheses(ScopeContainer sc, InputWrapper iW) {
        BigDecimal totalCount;
        if (sc.operator == null) {
            sc.operator = OperatorEnum.MULTIPLY;
            sc.isDividing = false;
        }
        final int parenErrorCheckPoint = iW.index++;
        if ((totalCount = handleScope(iW, true)).signum() == 0 && sc.isDividing)
            iW.createError("Cannot divide by 0", parenErrorCheckPoint);
        sc.termsScope = sc.operator.apply(sc.termsScope, totalCount);
        Notation suffix;
        while (++iW.index < iW.input.length()
                && (suffix = SuffixEnum.map.get(iW.input.charAt(iW.index))) != null)
            totalCount = suffix.apply(iW, sc, totalCount);
        iW.index--;
        if (sc.isFront) {
            sc.isFront = false;
            sc.termsScope = sc.isStartPlus ? totalCount : totalCount.negate();
        }
        sc.operator = OperatorEnum.MULTIPLY;
        sc.isDividing = false;
    }

    private static BigDecimal handleLetter(ScopeContainer sc, InputWrapper iw, final boolean execOperator) {
        final int fIndexChar = iw.index;
        while (++iw.index < iw.input.length() && Character.isLetter(iw.input.charAt(iw.index)))
            ;
        final String letterNotation = iw.input.substring(fIndexChar, iw.index--).toLowerCase();
        BigDecimal countTotal;
        Notation notation;

        if (letterNotation.length() == 1 && SuffixEnum.map.containsKey(letterNotation.charAt(0))) {
            iw.createError("Illegal " + iw.input.charAt(fIndexChar) +
                    " suffixes which not leading/after a number/parentheses", fIndexChar);
            return null;
        } else if ((notation = notationsMap.get(letterNotation)) != null) {
            countTotal = notation.apply(null, null, null);
        } else if ((notation = FunctionEnum.map.get(letterNotation)) != null) {
            if (++iw.index >= iw.input.length() || iw.input.charAt(iw.index) != '(')
                iw.createError("Expected '(' after a function name", --iw.index);
            countTotal = notation.apply(null, sc, handleScope(iw.addIndex(), true));
        } else {
            iw.createError("Unknown notation or function name, could be a typo?", fIndexChar);
            return null;
        }

        Notation suffix;
        while (++iw.index < iw.input.length()
                && (suffix = SuffixEnum.map.get(iw.input.charAt(iw.index))) != null)
            countTotal = suffix.apply(iw, sc, countTotal);
        iw.index--;

        if (!execOperator) // FLAG STOP HERE
            return countTotal;
        if (sc.operator == null) {
            sc.operator = OperatorEnum.MULTIPLY;
            sc.isDividing = false;
        }
        if (countTotal.signum() == 0 && sc.isDividing)
            iw.createError("Can not divide by 0", iw.index);
        sc.termsScope = sc.isFront ? (sc.isStartPlus ? countTotal : countTotal.negate())
                : sc.operator.apply(sc.termsScope, countTotal);
        sc.operator = OperatorEnum.MULTIPLY;
        sc.isDividing = false;
        sc.isFront = false;
        return null;
    }

    private static BigDecimal handleNumber(ScopeContainer sc, InputWrapper iw, final boolean execOperator) {
        boolean hasDecimalDotPassed = false;
        final int startNumber = iw.index;
        char c = '\0';
        for (; iw.index < iw.input.length();) {
            if ((c = iw.input.charAt(iw.index++)) == '.') {
                if (hasDecimalDotPassed)
                    iw.createError("Illegal twice '.'/decimal point", iw.index);
                hasDecimalDotPassed = true;
            }
            if (c != '.' && !Character.isDigit(c)) {
                iw.index--;
                break;
            }
        }
        BigDecimal countTotal = new BigDecimal(iw.input.substring(startNumber, iw.index--));
        Notation suffix;
        while (++iw.index < iw.input.length()
                && (suffix = SuffixEnum.map.get((c = iw.input.charAt(iw.index)))) != null)
            countTotal = suffix.apply(iw, sc, countTotal);
        iw.index--;
        if (execOperator) {
            if (sc.isDividing && countTotal.signum() == 0)
                iw.createError("Cannot divide by 0", startNumber);
            if (sc.operator != null)
                sc.termsScope = sc.operator.apply(sc.termsScope, countTotal);
            else if (sc.isFront)
                sc.termsScope = sc.isStartPlus ? countTotal : countTotal.negate();
            sc.operator = null;
            sc.isFront = false;
            return null;
        }
        return countTotal;
    }

    private static boolean isDigitOrDot(final StringBuilder input, int nextIndex) {
        final char c;
        return nextIndex >= 0 && nextIndex < input.length()
                && ((c = input.charAt(nextIndex)) == '.' || Character.isDigit(c));
    }

    private static final class InputWrapper {
        public StringBuilder input;
        public int index = 0;

        public InputWrapper(String input) {
            this.input = new StringBuilder(input);
        }

        public InputWrapper addIndex() {
            this.index++;
            return this;
        }

        /**
         * 
         * @param message
         * @param whichIndex nullable
         */
        public void createError(final String message, final Integer whichIndex) {
            if (whichIndex != null)
                this.index = whichIndex;
            throw new RuntimeException(message);
        }
    }

    private static interface Notation {
        BigDecimal apply(InputWrapper iW, ScopeContainer sc, final BigDecimal curValue);
    }

    private static enum FunctionEnum {
        SQRT((iw, sc, cv) -> cv.sqrt(new MathContext(11)), "sqrt"),
        CBRT((iw, sc, cv) -> new BigDecimal(Math.cbrt(cv.doubleValue())), "cbrt"),

        LOGT((iw, sc, cv) -> new BigDecimal(Math.log10(cv.doubleValue())), "logt"),
        LOGE((iw, sc, cv) -> new BigDecimal(Math.log(cv.doubleValue())), "loge"),

        CEIL((iw, sc, cv) -> cv.setScale(0, RoundingMode.CEILING), "ceil"),
        FLR((iw, sc, cv) -> cv.setScale(0, RoundingMode.FLOOR), "flr"),
        RND((iw, sc, cv) -> cv.setScale(0, RoundingMode.HALF_UP), "rnd"),

        SIN((iw, sc, cv) -> new BigDecimal(Math.sin(cv.doubleValue())), "sin"),
        COS((iw, sc, cv) -> new BigDecimal(Math.cos(cv.doubleValue())), "cos"),
        TAN((iw, sc, cv) -> new BigDecimal(Math.tan(cv.doubleValue())), "tan"),

        DEG((iw, sc, cv) -> new BigDecimal(Math.toDegrees(cv.doubleValue())), "deg"),
        RAD((iw, sc, cv) -> new BigDecimal(Math.toRadians(cv.doubleValue())), "rad"),
        ABS((iw, sc, cv) -> cv.abs(), "abs");

        private final Notation function;
        private final String stringOperator;

        final static Map<String, Notation> map = new HashMap<>();

        FunctionEnum(final Notation suffix, String stringOperator) {
            this.function = suffix;
            this.stringOperator = stringOperator;
        }

        static {
            for (FunctionEnum e : values())
                map.put(e.stringOperator, e.function);
        }
    }

    private static enum SuffixEnum {
        THOUSAND((iw, sc, cv) -> cv.movePointRight(3), 'k'),
        MILLION((iw, sc, cv) -> cv.movePointRight(6), 'm'),
        BILLION((iw, sc, cv) -> cv.movePointRight(9), 'b'),
        TRILLION((iw, sc, cv) -> cv.movePointRight(12), 't'),
        FACTORIAL((iw, sc, cv) -> {
            if (cv.stripTrailingZeros().scale() > 0)
                iw.createError("Factorial can only be done with integer numbers!", null);
            if (cv.compareTo(new BigDecimal("20")) > 0)
                iw.createError("Factorial is too large!", null);
            BigDecimal hasil = BigDecimal.ONE;
            for (byte i = 1; i <= cv.abs().byteValue(); i++)
                hasil = hasil.multiply(BigDecimal.valueOf(i));
            return cv.signum() < 0/* is negative */ ? hasil.negate() : hasil;
        }, '!'),
        PERCENTAGE((iw, sc, cv) -> {
            final BigDecimal pct = cv.movePointLeft(2);// cv / 100
            return sc.ASOperator == OperatorEnum.ADD || sc.ASOperator == OperatorEnum.SUBTRACT
                    ? sc.totalValue.multiply(pct)
                    : pct;
        }, '%'),
        POWER((iw, sc, cv) -> {
            if (++iw.index >= iw.input.length())
                iw.createError("No another number/math notation to be calculate for power ^", --iw.index);
            final double result;
            final char ch;
            if (isDigitOrDot(iw.input, iw.index))
                result = handleNumber(sc, iw, false).doubleValue();
            else if ((ch = iw.input.charAt(iw.index)) == '(')
                result = handleScope(iw.addIndex(), true).doubleValue();
            else if (Character.isLetter(ch))
                result = handleLetter(sc, iw, false).doubleValue();
            else {
                iw.createError("Illegal symbol/notation to be calculate for power ^", iw.index);
                return null;
            }
            return new BigDecimal(Math.pow(cv.doubleValue(), result));
        }, '^');

        private final Notation suffix;
        private final char charOperator;

        final static Map<Character, Notation> map = new HashMap<>();

        SuffixEnum(final Notation suffix, final char charOperator) {
            this.suffix = suffix;
            this.charOperator = charOperator;
        }

        static {
            for (SuffixEnum e : values())
                map.put(e.charOperator, e.suffix);
        }
    }

    private static enum OperatorEnum {
        MULTIPLY(BigDecimal::multiply, '*'),
        DIVIDE((a, b) -> a.divide(b, 10, RoundingMode.FLOOR), '/'),
        MODULUS(BigDecimal::remainder, ';'),
        ADD(BigDecimal::add, '+'),
        SUBTRACT(BigDecimal::subtract, '-');

        private final BinaryOperator<BigDecimal> operator;
        private final char charOperator;

        final static Map<Character, OperatorEnum> map = new HashMap<>();

        OperatorEnum(final BinaryOperator<BigDecimal> operator, final char charOperator) {
            this.operator = operator;
            this.charOperator = charOperator;
        }

        public BigDecimal apply(BigDecimal a, BigDecimal b) {
            return operator.apply(a, b);
        }

        static {
            for (OperatorEnum e : values())
                map.put(e.charOperator, e);
        }
    }
}
