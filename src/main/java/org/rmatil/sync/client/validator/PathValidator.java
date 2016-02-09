package org.rmatil.sync.client.validator;

import org.rmatil.sync.client.exception.ValidationException;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PathValidator implements IValidator {

    /**
     * The path to check for existence
     */
    protected String path;

    /**
     * Validates the given path for existence
     *
     * @param path The path to check for existence
     */
    public PathValidator(String path) {
        this.path = path;
    }

    @Override
    public boolean validate()
            throws ValidationException {

        String resolvedPath = this.path.replaceFirst("^~", System.getProperty("user.home"));
        Path pathToValidate = Paths.get(resolvedPath).toAbsolutePath();

        return pathToValidate.toFile().exists();
    }
}
