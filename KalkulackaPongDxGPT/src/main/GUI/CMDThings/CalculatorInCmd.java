package CMDThings;

class CalculatorInCmd {

    private int display;

    public CalculatorInCmd() {
        display = 0;
    }

    public void add(int number) {
        display += number;
    }

    public void subtract(int number) {
        display -= number;
    }

    public void multiply(int number) {
        display *= number;
    }

    public void divide(int number) {
        display /= number;
    }

    public int getDisplay() {
        return display;
    }

    public void clear() {
        display = 0;
    }

    public static void main(String[] args) {
        CalculatorInCmd calculatorInCmd = new CalculatorInCmd();

        calculatorInCmd.add(1);
        System.out.println(calculatorInCmd.getDisplay()); // 10

        calculatorInCmd.subtract(5);
        System.out.println(calculatorInCmd.getDisplay()); // 5

        calculatorInCmd.multiply(2);
        System.out.println(calculatorInCmd.getDisplay()); // 10

        calculatorInCmd.divide(2);
        System.out.println(calculatorInCmd.getDisplay()); // 5

        calculatorInCmd.clear();
        System.out.println(calculatorInCmd.getDisplay()); // 0
    }
}
