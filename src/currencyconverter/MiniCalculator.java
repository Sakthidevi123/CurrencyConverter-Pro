package currencyconverter;

/**
 * Evaluates a simple left-to-right arithmetic expression
 * (+, -, *, /) without any external dependency.
 */
public final class MiniCalculator {

    private MiniCalculator() { }

    public static String evaluate(String expression) {
        if (expression == null || expression.isBlank()) return "0";
        String cleaned = expression.replace(" ", "");
        if (!cleaned.matches("[0-9.+\\-*/]+")) return "0";

        try {
            java.util.List<String> tokens = tokenize(cleaned);
            if (tokens.isEmpty()) return "0";

            /* first pass: * and / */
            java.util.List<String> pass = new java.util.ArrayList<>();
            pass.add(tokens.get(0));
            for (int i = 1; i + 1 < tokens.size(); i += 2) {
                String op = tokens.get(i);
                String value = tokens.get(i + 1);
                if ("*".equals(op) || "/".equals(op)) {
                    double left = Double.parseDouble(pass.remove(pass.size() - 1));
                    double right = Double.parseDouble(value);
                    if ("/".equals(op) && right == 0) return "0";
                    pass.add(String.valueOf("*".equals(op) ? left * right : left / right));
                } else {
                    pass.add(op);
                    pass.add(value);
                }
            }

            /* second pass: + and - */
            double total = Double.parseDouble(pass.get(0));
            for (int i = 1; i + 1 < pass.size(); i += 2) {
                double value = Double.parseDouble(pass.get(i + 1));
                total = "+".equals(pass.get(i)) ? total + value : total - value;
            }

            java.text.DecimalFormat format = new java.text.DecimalFormat("0.####");
            return format.format(total);
        } catch (RuntimeException ex) {
            return "0";
        }
    }

    private static java.util.List<String> tokenize(String input) {
        java.util.List<String> tokens = new java.util.ArrayList<>();
        StringBuilder number = new StringBuilder();
        for (char ch : input.toCharArray()) {
            if (Character.isDigit(ch) || ch == '.') {
                number.append(ch);
            } else {
                if (number.length() > 0) tokens.add(number.toString());
                number.setLength(0);
                tokens.add(String.valueOf(ch));
            }
        }
        if (number.length() > 0) tokens.add(number.toString());
        return tokens;
    }
}
