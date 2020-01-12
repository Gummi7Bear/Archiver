package archiver.command;

import archiver.ConsoleHelper;
import archiver.ZipFileManager;

import java.nio.file.Path;
import java.nio.file.Paths;

/**Класс с общим функционалом для классов-команд, которые работают непосредственно с архивом
 */
public abstract class ZipCommand implements Command{

    /**Запрашивает путь и создает и возвращает объект ZipFileManager
     */
    public ZipFileManager getZipFileManager() throws Exception {
        ConsoleHelper.writeMessage("Введите полный путь файла архива");
        String path = ConsoleHelper.readString();
        Path objectPath = Paths.get(path);

        ZipFileManager zipFileManager = new ZipFileManager(objectPath);
        return zipFileManager;
    }
}

