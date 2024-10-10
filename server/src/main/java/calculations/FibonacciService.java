package calculations;

public class FibonacciService {
    public String calculateFibonacci(int n) {
        if (n <= 0) return "";
        StringBuilder result = new StringBuilder();
        int a = 0, b = 1;
        result.append(a).append(" ");
        for (int i = 1; i < n; i++) {
            result.append(b).append(" ");
            int next = a + b;
            a = b;
            b = next;
        }
        return result.toString().trim();
    }
}
