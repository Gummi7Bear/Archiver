package archiver.command;

/**
 * Родительский интерфейс для классов команд
 */
public interface Command {
    void execute() throws Exception;
}
