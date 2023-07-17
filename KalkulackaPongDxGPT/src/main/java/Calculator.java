class Calculator {

    private int display;

    public Calculator() {
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
        Calculator calculator = new Calculator();

        calculator.add(1);
        System.out.println(calculator.getDisplay()); // 10

        calculator.subtract(5);
        System.out.println(calculator.getDisplay()); // 5

        calculator.multiply(2);
        System.out.println(calculator.getDisplay()); // 10

        calculator.divide(2);
        System.out.println(calculator.getDisplay()); // 5

        calculator.clear();
        System.out.println(calculator.getDisplay()); // 0
    }
}
