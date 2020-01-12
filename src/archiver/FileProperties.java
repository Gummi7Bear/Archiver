package archiver;

/**Содержит свойства каждого файла в архиве
 */
public class FileProperties {
    private String name;
    private long size; //Размер в байтах
    private long compressedSize; //Размер после сжатия в байтах
    private int compressionMethod; //Метод сжатия

    public FileProperties(String name, long size, long compressedSize, int compressionMethod) {
        this.name = name;
        this.size = size;
        this.compressedSize = compressedSize;
        this.compressionMethod = compressionMethod;
    }

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    public long getCompressedSize() {
        return compressedSize;
    }

    public int getCompressionMethod() {
        return compressionMethod;
    }

    /**Считает степень сжатия файла
     */
    public long getCompressionRatio() {

        long compressionRatio = 100 - ((compressedSize * 100) / size); // считает степень сжатия
        return compressionRatio;
    }

    @Override
    public String toString() {
        if (size > 0) {
            return String.format("%s %d Kb (%d Kb) сжатие: %d%%", name, size/1024, compressedSize/1024, getCompressionRatio());
        }
        else {
            return name;
        }
    }
}

