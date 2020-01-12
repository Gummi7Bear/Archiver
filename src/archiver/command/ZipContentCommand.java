package archiver.command;

import archiver.ConsoleHelper;
import archiver.FileProperties;
import archiver.ZipFileManager;

import java.util.List;

/**
 * Команда получения содержимого архива
 */
public class ZipContentCommand extends ZipCommand{
    @Override
    public void execute() throws Exception {
        ConsoleHelper.writeMessage("Просмотр содержимого архива.");

        //Запрашивает ввести путь архива и создает объект ZipFileManager
        ZipFileManager zipFileManager = getZipFileManager();

        ConsoleHelper.writeMessage("Содержимое архива:");

        //Получаем лист с именами файлов и их свойствами
        List<FileProperties> listOfArchiveFiles = zipFileManager.getFilesList();

        for (int i = 0; i < listOfArchiveFiles.size(); i++) {
            ConsoleHelper.writeMessage(listOfArchiveFiles.get(i).toString());
        }
    }
}

