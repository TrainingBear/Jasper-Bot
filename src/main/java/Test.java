
public final class Test {
    public static void main(String[] args) {
        System.out.println("NOW: " + (subDigit(25.2, 2, false)));
    }

    public static String subDigit(final double d, int howMany, final boolean followZero) {
        String decimal = String.valueOf(d);
        int i = 0, dotIndex = decimal.length();
        for (; i < decimal.length(); i++) {
            final char c = decimal.charAt(i);
            if (c == '.') {
                dotIndex = i;
                continue;
            }
            if (dotIndex == decimal.length())
                continue;
            if (--howMany <= 0)
                break;
        }
        final boolean decimalBlank = dotIndex == decimal.length() - 2 && decimal.charAt(decimal.length() - 1) == '0';
        if (decimalBlank)
            howMany++;
        System.out.println((howMany <= -1) + "," + decimalBlank + " " + howMany + " " + i);
        decimal = howMany <= -1 ? decimal.substring(0, decimal.length() - 1)
                : decimal.substring(0, decimalBlank ? decimal.length() - 2
                        : ++i >= decimal.length() ? decimal.length() : i);
        if (followZero && dotIndex != decimal.length() && howMany >= 1)
            for (; howMany > 0; howMany--)
                decimal += '0';
        return decimal;
    }
}
