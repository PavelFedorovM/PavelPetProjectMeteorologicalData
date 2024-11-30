import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Получение уникального ключа
        String key = "410d33a9-8d30-4f56-a9f0-79cfaad271f2"; // Тут указываем X-Yandex-Weather-Key

        System.out.println("Добрый день! Это программа по получению метеорологических данных!");

        Scanner scanner = new Scanner(System.in);

        System.out.println("Надо будет ввести lat и lon.");
        System.out.println("Для Москвы например lat:55.75 и lon:37.62");
        System.out.print("Введите lat — широту:");
        String lat = scanner.nextLine();
        System.out.print("Введите lon — долготу:");
        String lon = scanner.nextLine();

        // Создание URL для запроса
        String url = "https://api.weather.yandex.ru/v2/forecast?lat=" + lat + "&lon=" + lon;

        // Создание HTTP-запроса
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestProperty("X-Yandex-Weather-Key", key);
            connection.setRequestMethod("GET");
            connection.connect();

            // Чтение ответа
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String response = reader.readLine();
            System.out.println("Снизу будет представлен json ответа на ваши координаты");
            System.out.println(response);

            // Обработка ответа
            String temperature = getTemperature(response);
            System.out.println("Температура: " + temperature + "℃");

            // Вычисление средней температуры за определенный период
            System.out.print("Пожалуйста, укажите количество дней, за которые вы хотите рассчитать среднюю температуру:");
            String limit = scanner.next();
            String averageTemperature = calculateAverageTemperature(response, limit);
            System.out.println("Средняя температура за " + limit + " дней: " + averageTemperature + "℃");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static String getTemperature(String response) {
        // Извлечение температуры из JSON-ответа
        int startIndex = response.indexOf("\"temp\":") + 7;
        int endIndex = response.indexOf(",", startIndex);
        String temperatureString = response.substring(startIndex, endIndex);
        return temperatureString;
    }

    private static String calculateAverageTemperature(String response, String limit) {
        String newResponce = response;
        int sum = 0;
        int count = 0;
        for (int i = 0; i<Integer.parseInt(limit);i++){
            int zeroIndex = newResponce.indexOf("\"day\": {"); // Данный "\" нужен, чтобы показать, что " используются в тексте, а не в коде
            int startIndex = newResponce.indexOf("\"temp_avg\":", zeroIndex) + 11; // https://javarush.com/groups/posts/2890-metod-indexof-klassa-string-zachem-on-nuzhen-i-kak-rabotaet
            int endIndex = newResponce.indexOf(",", startIndex);
            String temperatureString = newResponce.substring(startIndex, endIndex);
            sum += Double.parseDouble(temperatureString);
            count++;
            newResponce = newResponce.substring(newResponce.indexOf("\"temp_avg\":") + 11,newResponce.length()); // обрезаю строку, хранящую json

        }
        double averageTemperature = sum / count;
        return String.format("%.2f", averageTemperature);
    }
}