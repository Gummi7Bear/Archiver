package archiver.command;

import archiver.ConsoleHelper;
import archiver.ZipFileManager;
import archiver.exception.PathIsNotFoundException;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Команда создания архива (упаковки файлов в архив)
 */
public class ZipCreateCommand extends ZipCommand {
    @Override
    public void execute() throws Exception {
        try {
            ConsoleHelper.writeMessage("Создание архива.");

            //Запрашивает ввести путь для будущего архива и создает объект ZipFileManager
            ZipFileManager zipFileManager = getZipFileManager();

            ConsoleHelper.writeMessage("Введите полное имя файла или директории для архивации");
            Path dirPath = Paths.get(ConsoleHelper.readString());

            //Создаем архив
            zipFileManager.createZip(dirPath);
            ConsoleHelper.writeMessage("Архив создан.");
        }

        catch (PathIsNotFoundException e) {
            ConsoleHelper.writeMessage("Вы неверно указали имя файла или директории.");
        }
    }
}