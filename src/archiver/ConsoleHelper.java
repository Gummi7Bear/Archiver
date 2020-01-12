package archiver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
Класс для работы с консолью. Читает строки с консоли. Выводит сообщения в консоль.
 */
public class ConsoleHelper {

    private static BufferedReader bis = new BufferedReader(new InputStreamReader(System.in));

    public static void writeMessage(String message) {
        System.out.println(message);
    }

    public static String readString() throws IOException {
        String text = bis.readLine();
        return text;
    }

    public static int readInt() throws IOException {
        String text = readString();
        return Integer.parseInt(text.trim());
    }
}
