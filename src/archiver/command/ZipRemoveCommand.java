package archiver.command;

import archiver.ConsoleHelper;
import archiver.ZipFileManager;

import java.nio.file.Paths;

/**
 * Команда удаления файла из архива
 */
public class ZipRemoveCommand extends ZipCommand {
    @Override
    public void execute() throws Exception {
        ConsoleHelper.writeMessage("Удаление файла из архива.");

        //Запрашивает ввести путь архива и создает объект ZipFileManager
        ZipFileManager zipFileManager = getZipFileManager();

        ConsoleHelper.writeMessage("Введите имя удаляемого файла");
        zipFileManager.removeFile(Paths.get(ConsoleHelper.readString()));

        ConsoleHelper.writeMessage("Файл удален");

    }
}
