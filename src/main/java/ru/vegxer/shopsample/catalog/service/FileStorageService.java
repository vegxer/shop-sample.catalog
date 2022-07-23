package ru.vegxer.shopsample.catalog.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;

import lombok.val;
import org.apache.commons.compress.utils.FileNameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.vegxer.shopsample.catalog.exception.FileNotFoundException;
import ru.vegxer.shopsample.catalog.exception.StorageException;
import ru.vegxer.shopsample.catalog.util.StorageUtil;

@Service
public class FileStorageService {
    
    private final Path attachmentsLocation;
    private final int thumbnailSize;

    @Autowired
    public FileStorageService(@Value("${app.attachment.location}") final String attachmentsLocation,
                              @Value("${app.attachment.thumbnail.size}") final int thumbnailSize) {
        this.attachmentsLocation = Paths.get(attachmentsLocation);
        this.thumbnailSize = thumbnailSize;
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(attachmentsLocation);
        } catch (IOException e) {
            throw new StorageException("Не удалось создать директорию для хранения файлов", e);
        }
    }

    public String store(final MultipartFile file) {
        val filename = String.format("%s_%s", UUID.randomUUID(), StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename())));
        try {
            if (file.isEmpty()) {
                throw new StorageException(String.format("Не удалось сохранить пустой файл %s", filename));
            }
            if (filename.contains("..")) {
                throw new StorageException(String.format("Невозможно сохранить файл вне данной директории %s", filename));
            }
            val filePath = attachmentsLocation.resolve(filename);
            try (val inputStream = file.getInputStream()) {
                Files.copy(inputStream, filePath,
                    StandardCopyOption.REPLACE_EXISTING);
            }
            createThumbnail(filePath.toFile(), attachmentsLocation.resolve(StorageUtil.buildThumbnailPath(filename)).toFile());
        }
        catch (IOException e) {
            throw new StorageException(String.format("Не удалось создать файл %s", filename), e);
        }

        return filename;
    }

    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.attachmentsLocation, 1)
                .filter(path -> !path.equals(this.attachmentsLocation))
                .map(this.attachmentsLocation::relativize);
        }
        catch (IOException e) {
            throw new StorageException("Не удалось прочитать хранимые файлы", e);
        }

    }

    public Path load(final String filename) {
        return attachmentsLocation.resolve(filename);
    }

    public Resource loadAsResource(final String filename) {
        try {
            val resource = new UrlResource(load(filename).toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new FileNotFoundException(String.format("Не удалось прочитать файл %s", filename));
            }
        }
        catch (MalformedURLException e) {
            throw new FileNotFoundException(String.format("Не удалось прочитать файл %s", filename), e);
        }
    }

    public void deleteAll() {
        FileSystemUtils.deleteRecursively(attachmentsLocation.toFile());
    }

    public void deleteResource(final String filename) {
        val filePath = load(filename);
        if (!filePath.startsWith(attachmentsLocation)) {
            throw new IllegalArgumentException("Путь не должен содержать возможности выхода за пределы директории хранилища");
        }
        if (!filePath.toFile().exists()) {
            throw new FileNotFoundException(String.format("Файл %s не существует", filename));
        }
        try {
            Files.delete(filePath);
        }
        catch (IOException e) {
            throw new StorageException(String.format("Не удалось удалить файл %s", filename), e);
        }
    }

    private void createThumbnail(final File sourceFile, final File targetFile) throws IOException {
        val bufferedImage = ImageIO.read(sourceFile);
        val compressionCoefficient = (double) Math.min(bufferedImage.getHeight(), bufferedImage.getWidth()) / thumbnailSize;
        val targetWidth = (int) (bufferedImage.getWidth() / compressionCoefficient);
        val targetHeight = (int) (bufferedImage.getHeight() / compressionCoefficient);
        val compressedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        val graphics2D = compressedImage.createGraphics();
        graphics2D.drawImage(bufferedImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();
        ImageIO.write(compressedImage, FileNameUtils.getExtension(sourceFile.getName()), targetFile);
    }
}
