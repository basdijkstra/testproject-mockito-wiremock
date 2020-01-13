public class Order {

    public Order() {
    }

    public String checkout(PaymentProvider paymentProvider) {
        try {
            return paymentProvider.processPayment().equalsIgnoreCase("Approved") ? "Success" : "Failure";
        }
        catch(Exception e) {
            return "Exception occurred at payment provider when trying to checkout";
        }
    }
}
