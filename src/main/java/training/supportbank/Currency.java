package training.supportbank;

public class Currency {
    private int value;

    public int getValue() {
        return value;
    }

    public Currency(int value) {
        this.value = value;
    }

    public Currency(String string) throws NumberFormatException{
        value = (int) Math.round(100 * Double.parseDouble(string));
    }

    public void add(int value) {
        this.value += value;
    }

    @Override
    public String toString() {
        return String.format("%.2f", ((double) value) / 100);
    }
}
