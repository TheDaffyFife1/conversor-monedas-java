import com.conversor.ExchangeRateClient;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.net.http.HttpResponse;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Solicitar al usuario la moneda base y la cantidad
        System.out.print("Ingrese la moneda base (por ejemplo, USD): ");
        String baseCurrency = scanner.nextLine().toUpperCase();

        System.out.print("Ingrese la cantidad en " + baseCurrency + ": ");
        double amount = scanner.nextDouble();
        scanner.nextLine(); // Limpiar el buffer

        // Solicitar al usuario las monedas a convertir
        System.out.print("Ingrese las monedas de destino (separadas por comas, por ejemplo, ARS,BOB,BRL): ");
        String[] targetCurrencies = scanner.nextLine().toUpperCase().split(",");

        // Obtener las tasas de cambio
        ExchangeRateClient client = new ExchangeRateClient();
        HttpResponse<String> response = client.getExchangeRates(baseCurrency);

        if (response.statusCode() == 200) {
            String responseBody = response.body();
            System.out.println("JSON Response: " + responseBody); // Imprimir el JSON completo

            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(responseBody, JsonObject.class);

            // Acceder a las tasas de cambio
            if (jsonObject.has("conversion_rates")) {
                JsonObject rates = jsonObject.getAsJsonObject("conversion_rates");

                // Realizar y mostrar las conversiones
                for (String targetCurrency : targetCurrencies) {
                    if (rates.has(targetCurrency)) {
                        double rate = rates.get(targetCurrency).getAsDouble();
                        double convertedAmount = convertCurrency(amount, rate);
                        System.out.printf("%.2f %s es igual a %.2f %s%n", amount, baseCurrency, convertedAmount, targetCurrency);
                    } else {
                        System.out.println("No se encontró la tasa de cambio para " + targetCurrency);
                    }
                }
            } else {
                System.out.println("La respuesta JSON no contiene la clave 'conversion_rates'");
            }
        } else {
            System.out.println("Error: " + response.statusCode());
        }
    }

    private static double convertCurrency(double amount, double rate) {
        return amount * rate;
    }
}