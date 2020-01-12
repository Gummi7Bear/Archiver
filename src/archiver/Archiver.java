package archiver;

import archiver.command.ExitCommand;
import archiver.exception.WrongZipFileException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;

public class Archiver {
    public static void main(String[] args) throws IOException {

        Operation operation = null;

        //Пока пользователь не введет выход
        while (operation != Operation.EXIT){
            try {
                //Получаем порядковый номер команды, введенной пользователем
                operation = askOperation();
                //Передаем команду для запуска execute метода у нужного класса-исполнителя
                CommandExecutor.execute(operation);
            }
            catch (WrongZipFileException e) {
                ConsoleHelper.writeMessage("Вы не выбрали файл архива или выбрали неверный файл.");
            } catch (Exception e) {
                ConsoleHelper.writeMessage("Произошла ошибка. Проверьте введенные данные.");
            }
        }
    }

    /**
     *Метод должен вывести в консоль список доступных команд и попросить выбрать одну из них
     */
    public static Operation askOperation() throws IOException {
        ConsoleHelper.writeMessage("");
        ConsoleHelper.writeMessage("Выберите операцию:");
        ConsoleHelper.writeMessage(String.format("%d - упаковать файлы в архив", Operation.CREATE.ordinal()));
        ConsoleHelper.writeMessage(String.format("%d - добавить файл в архив", Operation.ADD.ordinal()));
        ConsoleHelper.writeMessage(String.format("%d - удалить файл из архива", Operation.REMOVE.ordinal()));
        ConsoleHelper.writeMessage(String.format("%d - распаковать архив", Operation.EXTRACT.ordinal()));
        ConsoleHelper.writeMessage(String.format("%d - просмотреть содержимое архива", Operation.CONTENT.ordinal()));
        ConsoleHelper.writeMessage(String.format("%d - выход", Operation.EXIT.ordinal()));

        //Считывает введенный номер коменды и возвращает ее из перечисления Operation
        return Operation.values()[ConsoleHelper.readInt()];
    }
}
