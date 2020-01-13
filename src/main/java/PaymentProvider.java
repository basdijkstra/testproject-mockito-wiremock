import java.util.Random;

public class PaymentProvider {

    public PaymentProvider() {
    }

    public String processPayment() throws IllegalStateException {

        String[] results = {"Approved", "Denied", "Exception"};
        Random r = new Random();

        int randomIndex = r.nextInt(results.length);

        if(results[randomIndex].equalsIgnoreCase("Exception")) {
            throw new IllegalStateException();
        }

        return results[randomIndex];
    }
}
