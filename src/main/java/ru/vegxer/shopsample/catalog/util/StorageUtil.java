package ru.vegxer.shopsample.catalog.util;

import lombok.val;

public class StorageUtil {
    public static String buildThumbnailPath(final String filename) {
        val lastDotIndex = filename.lastIndexOf('.');
        return filename.substring(0, lastDotIndex) + "_thumbnail" + filename.substring(lastDotIndex);
    }
}
