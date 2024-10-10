package calculations;

public class PrimeFactorService {
    public String calculatePrimeFactors(int n) {
        StringBuilder result = new StringBuilder();
        while (n % 2 == 0) {
            result.append(2).append(" ");
            n /= 2;
        }
        for (int i = 3; i <= Math.sqrt(n); i += 2) {
            while (n % i == 0) {
                result.append(i).append(" ");
                n /= i;
            }
        }
        if (n > 2) {
            result.append(n).append(" ");
        }
        return result.toString().trim();
    }
}
