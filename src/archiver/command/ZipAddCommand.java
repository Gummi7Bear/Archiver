package archiver.command;

import archiver.ConsoleHelper;
import archiver.ZipFileManager;
import archiver.exception.PathIsNotFoundException;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Команда добавления файла в архив
 */
public class ZipAddCommand extends ZipCommand {
    @Override
    public void execute() throws Exception {
        ConsoleHelper.writeMessage("Добавление файла в архив.");

        try {
            //Запрашивает ввести путь архива и создает объект ZipFileManager
            ZipFileManager zipFileManager = getZipFileManager();
            ConsoleHelper.writeMessage("Введите полный путь файла для добавления:");
            Path addPath = Paths.get(ConsoleHelper.readString());

            zipFileManager.addFile(addPath);
            ConsoleHelper.writeMessage("Файл добавлен.");
        }
        catch (PathIsNotFoundException e) {
            ConsoleHelper.writeMessage("Указанного файла не существует.");
        }
    }
}
