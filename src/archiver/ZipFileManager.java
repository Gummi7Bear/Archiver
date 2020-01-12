package archiver;


import archiver.exception.PathIsNotFoundException;
import archiver.exception.WrongZipFileException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**Совершает операции над файлом архива
 */

public class ZipFileManager {

    // Полный путь zip файла
    private final Path zipFile;

    public ZipFileManager(Path zipFile) {
        this.zipFile = zipFile;
    }

    /**Создает zip архив
     * @param source - путь к файлу, который будем архивировать
     * @throws Exception
     */
    public void createZip(Path source) throws Exception {

        // Проверяем, существует ли директория, где будет создаваться архив
        // Если такой нет - создаем ее
        Path zipDirectory = zipFile.getParent();
        if (Files.notExists(zipDirectory))
            Files.createDirectories(zipDirectory);

        // Создаем zip поток
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(zipFile))) {
            //Если архивируется файл, то узнаем его родительскую аудиторию и имя файла и архивируем
            if (Files.isRegularFile(source)) {
                addNewZipEntry(zipOutputStream, source.getParent(), source.getFileName());
            }

            //Если архивируется директория, получаем список всех ее файлов и архивируем каждый файл
            else if(Files.isDirectory(source)) {
                FileManager fileManager = new FileManager(source);
                List<Path> fileNamesList = fileManager.getFileList();

                for (Path fileName : fileNamesList)
                    addNewZipEntry(zipOutputStream, source, fileName);
            }

            else {
                // Если source не файл или директория, бросаем исключение
                throw new PathIsNotFoundException();
            }
        }
    }

    /**Создает список из одного элемента и вызывает addFiles()
     */
    public void addFile(Path absolutePath) throws Exception {
        addFiles(Collections.singletonList(absolutePath));
    }

    /**Добавляет файл в архив
     * @param absolutePathList - список абсолютных путей добавляемых файлов
     * @throws Exception
     */
    public void addFiles(List<Path> absolutePathList) throws Exception {
        //Бросает исключение, если файл архива не существует
        if (!Files.isRegularFile(zipFile)) {
            throw new WrongZipFileException();
        }
        // Создаем временный файл архива
        Path tempZipFile = Files.createTempFile(null, null);

        // Cписок переписанных файлов
        List<Path> copyPathList = new ArrayList<>();

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(tempZipFile))) {
            try (ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(zipFile))) {

                ZipEntry zipEntry;

                //Пишем во временный файл все файлы из архива.
                //Имена скопированных файлов сохраняем в локальный список.
                while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                    zipOutputStream.putNextEntry(zipEntry);
                    copyData(zipInputStream, zipOutputStream);
                    copyPathList.add(Paths.get(zipEntry.getName()));

                    zipInputStream.closeEntry();
                    zipOutputStream.closeEntry();
                }
            }

            //Берет файл из переданного листа
            for(Path pathOfNewFile : absolutePathList) {

                //Проверяет есть ли такой файл в архиве
                if (copyPathList.contains(pathOfNewFile)) {
                    ConsoleHelper.writeMessage(String.format("'%s' уже есть в архиве.", pathOfNewFile.toString()));
                    continue;
                }

                // Если архивируем директорию, то нужно получить список файлов в ней
                if (Files.isDirectory(pathOfNewFile)) {
                    FileManager fileManager = new FileManager(pathOfNewFile);
                    List<Path> fileNames = fileManager.getFileList();

                    // Добавляем каждый файл в архив
                    for (Path fileName : fileNames) {

                        //Находим путь очередного файла с родителем
                        Path fileNameWithParent = pathOfNewFile.getFileName().resolve(fileName);

                        if (copyPathList.contains(fileNameWithParent)) {
                            ConsoleHelper.writeMessage(String.format("'%s' уже есть в архиве.", fileName.toString()));
                            continue;
                        }

                        //Добавляет новый файл во временный zip файл
                        addNewZipEntry(zipOutputStream, pathOfNewFile.getParent(), fileNameWithParent);
                        ConsoleHelper.writeMessage(String.format("Файл '%s' добавлен в архив.", fileNameWithParent.toString()));
                    }

                }
                else if (Files.isRegularFile(pathOfNewFile)) {

                    // Если архивируем простой файл, то добавляем, получив его директорию и имя
                    addNewZipEntry(zipOutputStream, pathOfNewFile.getParent(), pathOfNewFile.getFileName());
                }
                else {
                    // Если переданный source не директория и не файл, бросаем исключение
                    throw new PathIsNotFoundException();
                }
            }

        }
        // Перемещаем временный файл на место оригинального
        Files.move(tempZipFile, zipFile, StandardCopyOption.REPLACE_EXISTING);


    }

    /**Распаковывает архив
     * @param outputFolder - путь, куда будем распаковывать архив
     * @throws Exception
     */
    public void extractAll(Path outputFolder) throws Exception {
        //Существует ли файл?
        if(!Files.isRegularFile(zipFile)) {
            throw new WrongZipFileException();
        }

        try (ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(zipFile))) {

            //Если переданная аудитория для распаковки не существует - создаем ее
            if (Files.notExists(outputFolder)) {
                Files.createDirectories(outputFolder);
            }

            ZipEntry zipEntry;
            String zipName;

            //Пока не закончатся zipEntry
            while((zipEntry=zipInputStream.getNextEntry())!=null){
                //Получаем абсолютный путь для очередного zipEntry в новой директории
                Path fullFaleName = outputFolder.resolve(zipEntry.getName());

                //Распаковка
                // Если элемент это директория, создаем директорию
                if (zipEntry.isDirectory()) {
                    Files.createDirectory(outputFolder.resolve(fullFaleName));
                }

                //Если элемент это файл, вычитываем данные
                else {
                    try (OutputStream outputStream = Files.newOutputStream(outputFolder.resolve(fullFaleName))) {
                        copyData(zipInputStream, outputStream);
                    }
                }
                zipInputStream.closeEntry();
            }
        }
    }

    /**Метод для удаления файлов из архива
     * @param pathList - список относительных путей на файлы внутри архива.
     * @throws Exception
     */
    public void removeFiles(List<Path> pathList) throws Exception {

        //Бросает исключение, если файл архива не существует
        if (!Files.isRegularFile(zipFile)) {
            throw new WrongZipFileException();
        }
        //Создает временный файл архива в директории по умолчанию
        Path tempFile = Files.createTempFile(".zip", null);

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(tempFile))) {
            try (ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(zipFile))) {
                ZipEntry zipEntry;

                //Проходит по всем файлам оригинального архива, и проверять,
                // есть ли текущий файл в переданном списке на удаление
                while ((zipEntry = zipInputStream.getNextEntry()) != null) {

                    //Если файл в списке, выводим сообщение об удалении.
                    if(pathList.contains(Paths.get(zipEntry.getName()))) {
                        ConsoleHelper.writeMessage("Файл " + zipEntry.getName() + " удалён.");
                    }

                    //Если файла в списке на удаления нет, вычитываем его во временный архив
                    else {
                        zipOutputStream.putNextEntry(zipEntry);
                        copyData(zipInputStream, zipOutputStream);
                        zipInputStream.closeEntry();
                        zipOutputStream.closeEntry();
                    }
                }
            }
        }
        //Заменяем оригинальный файл архива временным, в котором записаны нужные файлы.
        Files.move(tempFile, zipFile, REPLACE_EXISTING);
    }

    /**Создает список из одного элемента и вызывает removeFiles()
     */
    public void removeFile(Path path) throws Exception {
        removeFiles(Collections.singletonList(path));
    }

        /**
         *Метод создает новый зип-файл и копирует его
         * @param zipOutputStream - переданный поток для вывода
         * @param filePath - путь директории
         * @param fileName - имя файла
         */
        private void addNewZipEntry (ZipOutputStream zipOutputStream, Path filePath, Path fileName) throws Exception {
            //Получаем абсолютный путь
            Path fullPath = filePath.resolve(fileName);

            try (InputStream inputStream = Files.newInputStream(fullPath)) {
                ZipEntry entry = new ZipEntry(fileName.toString());
                zipOutputStream.putNextEntry(entry);

                //копируем файл
                copyData(inputStream, zipOutputStream);
                zipOutputStream.closeEntry();
            }
        }

        /**Читает данные из in и записывает в out, пока не вычитает все.
         */
        private void copyData (InputStream in, OutputStream out) throws Exception {
            byte[] buffer = new byte[8 * 1024];
            int len;
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
        }

    /**Возвращает список файлов и их свойств
     */
    public List<FileProperties> getFilesList() throws Exception { //возвращает список свойств файлов (класс свойств FileProperties)
        //Является ли путь файлом?
        if (Files.isRegularFile(zipFile)) {
            List<FileProperties> listFilesProperties = new ArrayList<>();

            try (ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(zipFile))) {
                ZipEntry zipEntry;

                //Проходимся по каждому zip-элементу и вычитываем его содержимое, чтобы узнать его размер.
                while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    copyData(zipInputStream, baos);

                    //Создаем объект с данными, добавляя имя элемента, размер, сжатый размер и метод сжатия элемента
                    FileProperties fileProperties = new FileProperties(zipEntry.getName(), baos.size(), zipEntry.getCompressedSize(), zipEntry.getMethod());
                    listFilesProperties.add(fileProperties);
                    baos.close();
                }
            }
            return listFilesProperties;
        }

        else  {
            throw new WrongZipFileException();
        }

    }
}


