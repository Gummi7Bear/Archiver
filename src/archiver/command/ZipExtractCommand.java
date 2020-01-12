package archiver.command;

import archiver.ConsoleHelper;
import archiver.ZipFileManager;
import archiver.exception.PathIsNotFoundException;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Команда распаковки архива
 */
public class ZipExtractCommand extends ZipCommand {
    @Override
    public void execute() throws Exception {

        try {
            ConsoleHelper.writeMessage("Извлечение архива");

            //Запрашивает ввести путь архива и создает объект ZipFileManager
            ZipFileManager zipFileManager = getZipFileManager();

            ConsoleHelper.writeMessage("Введите полное имя директории для распаковки:");
            Path extractPath = Paths.get(ConsoleHelper.readString());

            //Извлекаем архив
            zipFileManager.extractAll(extractPath);
            ConsoleHelper.writeMessage("Архив извлечен.");
        }

        catch (PathIsNotFoundException e) {
            ConsoleHelper.writeMessage("Вы неверно указали имя директории.");
        }

    }
}
