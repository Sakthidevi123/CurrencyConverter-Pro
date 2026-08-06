package currencyconverter;

/** Centralised, reusable validation for the amount field and currency pair. */
public final class InputValidator {

    public static final double MAX_AMOUNT = 1_000_000_000d;

    /** Outcome of a validation pass. */
    public static final class Result {
        private final boolean valid;
        private final String message;
        private final double amount;

        private Result(boolean valid, String message, double amount) {
            this.valid = valid;
            this.message = message;
            this.amount = amount;
        }

        public boolean isValid()   { return valid; }
        public String getMessage() { return message; }
        public double getAmount()  { return amount; }
    }

    private InputValidator() { }

    public static Result validate(String rawAmount, String from, String to) {
        if (rawAmount == null || rawAmount.trim().isEmpty()) {
            return new Result(false, "Please enter an amount before converting.", 0);
        }

        String cleaned = rawAmount.trim().replace(",", "");

        if (!cleaned.matches("\\d+(\\.\\d+)?")) {
            return new Result(false,
                    "The amount may only contain digits and a single decimal point.", 0);
        }

        double value;
        try {
            value = Double.parseDouble(cleaned);
        } catch (NumberFormatException ex) {
            return new Result(false, "That amount could not be read as a number.", 0);
        }

        if (value <= 0) {
            return new Result(false, "The amount must be greater than zero.", 0);
        }
        if (value > MAX_AMOUNT) {
            return new Result(false, "The amount is too large. Please stay below 1,000,000,000.", 0);
        }
        if (from == null || to == null) {
            return new Result(false, "Please choose both currencies.", 0);
        }
        if (from.equals(to)) {
            return new Result(false, "Source and target currency cannot be the same.", 0);
        }
        return new Result(true, "", value);
    }
}
