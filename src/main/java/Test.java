import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.function.BiFunction;

public class Test {
    private static class InputWrapper {
        public StringBuilder input;
        public int index = 0;

        public InputWrapper(String input) {
            this.input = new StringBuilder(input);
        }

        public InputWrapper addIndex() {
            this.index++;
            return this;
        }

        public void createError(final String message, final int whichIndex) {
            this.index = whichIndex;
            throw new RuntimeException(message);
        }
    }

    final static Map<Character, BiFunction<BigDecimal, BigDecimal, BigDecimal>> operatorsMap = Map.of(
            '+', (a, b) -> a.add(b),
            '-', (a, b) -> a.subtract(b),
            '*', (a, b) -> a.multiply(b),
            '/', (a, b) -> a.divide(b, 10, RoundingMode.HALF_UP),
            ';', (a, b) -> a.remainder(b));

    public static void main(String[] args) {
        InputWrapper input = new InputWrapper("10/(1200-(2*(1300/2)))+8");
        System.out.println("INPUT: " + input.input);
        BigDecimal output;
        try {
            output = handleScope(input, false);
        } catch (Exception e) {
            input.input.insert(input.index, " ").insert(input.index + 2, " ");
            System.out.println(input.input + "\nERROR WHILE ON INDEX: " + input.index);
            e.printStackTrace();
            return;
        }
        System.out.println("OUTPUT: " + output);
    }

    /**
     * 
     * @param iW          NOT NULL
     * @param closingChar
     * @return
     */
    private static BigDecimal handleScope(InputWrapper iW, final boolean hasClosedParam) {
        final int startScope = iW.index;

        boolean isStartPlus = true, hasDecimalDotPassed = false, isDividing = false;
        BiFunction<BigDecimal, BigDecimal, BigDecimal> operator = null;
        int prevNumIndex = startScope;
        BigDecimal totalValue = new BigDecimal("0");

        for (; iW.index < iW.input.length(); iW.index++) {
            final char c = iW.input.charAt(iW.index);
            if (Character.isWhitespace(c)) {
                iW.input.deleteCharAt(iW.index--);
                continue;
            }
            final boolean isDot = c == '.';
            if (c == '(') {
                final BigDecimal totalCount;
                if (operator == null && prevNumIndex == startScope) {
                    totalCount = handleScope(iW.addIndex(), true);
                    totalValue = isStartPlus ? totalCount : totalCount.negate();
                    continue;
                } else if (operator == null)
                    operator = operatorsMap.get('*');
                final int parenCheckPoint = iW.index++;
                if ((totalCount = handleScope(iW, true)).compareTo(BigDecimal.ZERO) == 0 && isDividing)
                    iW.createError("Cannot divide by 0", parenCheckPoint);
                totalValue = operator.apply(totalValue, totalCount);
            } else if (c == ')') {
                if (!hasClosedParam)
                    iW.createError("Illegal close parenthesees ')'", iW.index);
                return totalValue;
            } else if (Character.isDigit(c) || isDot) {
                if (isDot && hasDecimalDotPassed)
                    iW.createError("Illegal twice '.'/decimal point", iW.index);
                else if (isDot)
                    hasDecimalDotPassed = true;

                if (operator != null && iW.index - 1 >= 0 && !isDigitOrDot(iW.input, iW.index - 1))
                    prevNumIndex = iW.index;

                final boolean isNextDigitOrDot = isDigitOrDot(iW.input, iW.index + 1);
                if (prevNumIndex == startScope && !isNextDigitOrDot) {
                    System.out.println("TEST: " + prevNumIndex + " AND " + (iW.index + 1));
                    totalValue = new BigDecimal(iW.input.substring(prevNumIndex, iW.index + 1));
                } else if (operator != null && !isNextDigitOrDot) {
                    final BigDecimal bigDecimal;
                    if ((bigDecimal = new BigDecimal(iW.input.substring(prevNumIndex, iW.index + 1)))
                            .compareTo(BigDecimal.ZERO) == 0 && isDividing)
                        iW.createError("Cannot divide by 0", prevNumIndex);
                    totalValue = operator.apply(totalValue, bigDecimal);
                    operator = null;
                }
            } else if ((operator = operatorsMap.get(c)) != null) {
                if (++iW.index < iW.input.length() && operatorsMap.containsKey(iW.input.charAt(iW.index)))
                    iW.createError("Illegal twice operator " + c, iW.index);

                if (--iW.index == startScope) {
                    switch (c) {
                        case '-':
                            isStartPlus = false;
                        case '+':
                            operator = null;
                            break;
                        case '*', '/', ';':
                            iW.createError("Illegal operator in front", iW.index);
                    }
                    continue;
                }
                isDividing = c == '/' ? true : false;
                hasDecimalDotPassed = false;
            }
        }
        return totalValue;
    }

    private static boolean isDigitOrDot(final StringBuilder input, int nextIndex) {
        final char nextChar;
        return nextIndex >= input.length() || nextIndex < 0 ? false
                : (nextChar = input.charAt(nextIndex)) == '.' || Character.isDigit(nextChar);
    }
}
