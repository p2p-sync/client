package org.rmatil.sync.client.validator;

import org.rmatil.sync.client.exception.ValidationException;

import java.nio.file.Path;
import java.nio.file.Paths;

public class DirectoryValidator implements IValidator {

    /**
     * The path to check whether it is a directory or not
     */
    protected String path;

    /**
     * Checks if the given path is a directory
     *
     * @param path The path to check
     */
    public DirectoryValidator(String path) {
        this.path = path;
    }

    @Override
    public boolean validate()
            throws ValidationException {

        String resolvedPath = this.path.replaceFirst("^~", System.getProperty("user.home"));
        Path pathToValidate = Paths.get(resolvedPath).toAbsolutePath();

        if (! pathToValidate.toFile().exists()) {
            throw new ValidationException("Path " + this.path + " does not exist");
        }

        return pathToValidate.toFile().isDirectory();
    }
}
