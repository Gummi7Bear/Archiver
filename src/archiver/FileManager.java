package archiver;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс для получения списка всех файлов в какой-то папке.
 */
public class FileManager {
    //Корневой путь директории, файлы которой нас интересуют
    private Path rootPath;
    //Список относительных путей файлов внутри rootPath
    private List<Path> fileList;

    public FileManager(Path rootPath) throws IOException {
        this.rootPath = rootPath;
        this.fileList = new ArrayList<>();
        collectFileList(rootPath);
    }

    public List<Path> getFileList() {
        return fileList;
    }

    /**
     *Проходится по директори с файлами и добавляет их относительные пути в список
     */
    private void collectFileList(Path path) throws IOException {
        //если файл является файлом
        if (Files.isRegularFile(path)) {
            //Получаем относительный путь файла относительно rootPath и добавляем в список
            Path relativePath = rootPath.relativize(path);
            fileList.add(relativePath);
        }
        //если файл является директорией
        if (Files.isDirectory(path)) {
            //С помощью DirectoryStream проходимся по всему содержимому директории и выхываем collectFileList
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
                for (Path file : directoryStream) {
                    collectFileList(file);
                }
            }

        }


    }
}
