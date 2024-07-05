import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.conversor.ExchangeRateClient;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.http.HttpResponse;

public class CurrencyConverterGUI {
    private final JTextField amountField;
    private final JComboBox<String> baseCurrencyComboBox;
    private final JComboBox<String> targetCurrencyComboBox;
    private final JTextArea resultArea;
    private final JTextArea historyArea;

    private static final String[] CURRENCIES = {"USD", "ARS", "BOB", "BRL", "CLP", "COP"};

    public CurrencyConverterGUI() {
        JFrame frame = new JFrame("Convertidor de monedas");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 600);
        frame.setLayout(new GridLayout(7, 1));

        // Create UI elements
        JLabel baseCurrencyLabel = new JLabel("Moneda Base:");
        baseCurrencyComboBox = new JComboBox<>(CURRENCIES);
        JLabel amountLabel = new JLabel("Cantidad:");
        amountField = new JTextField();
        JLabel targetCurrencyLabel = new JLabel("Moneda objetivo:");
        targetCurrencyComboBox = new JComboBox<>(CURRENCIES);
        JButton convertButton = new JButton("Convertir");
        resultArea = new JTextArea(5, 20);
        resultArea.setEditable(false);
        historyArea = new JTextArea(10, 20);
        historyArea.setEditable(false);

        // Add UI elements to frame
        frame.add(baseCurrencyLabel);
        frame.add(baseCurrencyComboBox);
        frame.add(amountLabel);
        frame.add(amountField);
        frame.add(targetCurrencyLabel);
        frame.add(targetCurrencyComboBox);
        frame.add(convertButton);
        frame.add(new JScrollPane(resultArea));
        frame.add(new JLabel("Historial de Conversiones:"));
        frame.add(new JScrollPane(historyArea));

        // Add action listener to the button
        convertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performConversion();
            }
        });

        frame.setVisible(true);
    }

    private void performConversion() {
        String baseCurrency = (String) baseCurrencyComboBox.getSelectedItem();
        double amount = Double.parseDouble(amountField.getText());
        String targetCurrency = (String) targetCurrencyComboBox.getSelectedItem();

        // Obtener las tasas de cambio
        ExchangeRateClient client = new ExchangeRateClient();
        HttpResponse<String> response = client.getExchangeRates(baseCurrency);

        if (response.statusCode() == 200) {
            String responseBody = response.body();
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(responseBody, JsonObject.class);

            // Acceder a las tasas de cambio
            if (jsonObject.has("conversion_rates")) {
                JsonObject rates = jsonObject.getAsJsonObject("conversion_rates");

                // Realizar y mostrar las conversiones
                if (rates.has(targetCurrency)) {
                    double rate = rates.get(targetCurrency).getAsDouble();
                    double convertedAmount = convertCurrency(amount, rate);
                    String result = String.format("%.2f %s es igual a %.2f %s%n", amount, baseCurrency, convertedAmount, targetCurrency);
                    resultArea.setText(result);

                    // Agregar al historial
                    historyArea.append(result);
                } else {
                    resultArea.setText("No se encontró la tasa de cambio para " + targetCurrency);
                }
            } else {
                resultArea.setText("La respuesta JSON no contiene la clave 'conversion_rates'");
            }
        } else {
            resultArea.setText("Error: " + response.statusCode());
        }
    }

    private double convertCurrency(double amount, double rate) {
        return amount * rate;
    }

    public static void main(String[] args) {
        new CurrencyConverterGUI();
    }
}