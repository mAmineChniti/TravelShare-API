package tn.esprit.services;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

public class PaymentService {

    static {
        Stripe.apiKey = "sk_test_51QxCbhKN3zE00jsYFP6WfPm0ulgXA3fmEQTNvVxaaMmKp8MbTmAVPsfrMSbVz14hnxWj4aWlDgMnrvyN1sawLo7D00YlAKa7Yk"; // Remplacez par votre clé secrète Stripe
    }

    public PaymentIntent createPaymentIntent(long amount) throws Exception {
        PaymentIntentCreateParams params =
                PaymentIntentCreateParams.builder()
                        .setAmount(amount) // Montant en cents
                        .setCurrency("usd") // La devise
                        .setDescription("Paiement pour l'excursion") // Optionnel, ajouter une description
                        .build();

        return PaymentIntent.create(params);
    }
}
