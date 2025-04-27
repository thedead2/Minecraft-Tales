package de.thedead2.minecraft_tales.util.helper;

import com.mojang.serialization.Codec;
import de.thedead2.minecraft_tales.util.exceptions.FileCopyException;
import net.minecraft.nbt.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static de.thedead2.minecraft_tales.MTGlobalConstants.LOGGER;
import static de.thedead2.minecraft_tales.MTGlobalConstants.MOD_ID;


public class IOHelper {

    private IOHelper() {}

    public static boolean createDirectory(File directoryIn) {
        if(!directoryIn.exists()) {
            if(directoryIn.mkdir()) {
                LOGGER.debug("Created directory: {}", directoryIn.toPath());
                return true;
            }
            else {
                return false;
            }
        }
        else {
            LOGGER.debug("Found directory {} at {}", directoryIn.getName(), directoryIn.toPath());
            return false;
        }
    }


    public static void readDirectory(File directory, Consumer<File> fileReader) {
        if(directory.exists()) {
            File[] folders = directory.listFiles();

            assert folders != null;
            if(Arrays.stream(folders).anyMatch(File::isFile)) {
                fileReader.accept(directory);
            }

            for(File subfolder : folders) {
                if(subfolder.isDirectory()) {
                    fileReader.accept(subfolder);

                    readSubDirectories(subfolder, fileReader);
                }
            }
        }
    }


    private static void readSubDirectories(File folderIn, Consumer<File> fileReader) {
        for(File folder : Objects.requireNonNull(folderIn.listFiles())) {
            if(folder.isDirectory()) {
                fileReader.accept(folder);
                readSubDirectories(folder, fileReader);
            }
        }
    }


    public static void copyModFiles(String pathIn, Path pathOut, String filter) throws FileCopyException {
        URL filepath = ReflectionHelper.findResource(pathIn);

        try(Stream<Path> paths = Files.list(Path.of(filepath.getPath()))) {
            paths.filter(path -> path.toString().endsWith(filter)).forEach(path -> {
                try {
                    writeFile(Files.newInputStream(path), pathOut.resolve(path.getFileName().toString()));
                }
                catch(IOException e) {
                    FileCopyException copyException = new FileCopyException("Failed to copy mod files!");
                    copyException.addSuppressed(e);
                    throw copyException;
                }
            });
            LOGGER.debug("Copied files from directory " + MOD_ID + ":{} to directory {}", pathIn, pathOut);
        }
        catch(IOException e) {
            FileCopyException copyException = new FileCopyException("Unable to locate directory: " + MOD_ID + ":" + pathIn);
            copyException.addSuppressed(e);
            throw copyException;
        }
    }


    public static void writeFile(InputStream inputStream, Path outputPath) throws IOException {
        OutputStream fileOut = Files.newOutputStream(outputPath);

        writeToFile(inputStream, fileOut);

        fileOut.close();
    }


    public static void writeToFile(InputStream inputStream, OutputStream fileOut) throws IOException {
        int input;
        while((input = inputStream.read()) != -1) {
            fileOut.write(input);
        }

        inputStream.close();
    }

    public static <T> void saveToFile(T object, Codec<T> codec, Path outputPath, boolean compressed) throws IOException {
        Optional<Tag> tag = codec.encodeStart(NbtOps.INSTANCE, object).resultOrPartial(LOGGER::error);

        if(tag.isEmpty()) return;

        if(compressed) NbtIo.writeCompressed((CompoundTag) tag.get(), outputPath);
        else NbtIo.write((CompoundTag) tag.get(), outputPath);
    }


    public static <T> Optional<T> loadFromFile(Codec<T> codec, Path path, boolean compressed) throws IOException {
        CompoundTag tag = null;

        if(path.toFile().exists()) {
            tag = compressed ? NbtIo.readCompressed(path, NbtAccounter.unlimitedHeap()) : NbtIo.read(path);
        }


        return codec.parse(NbtOps.INSTANCE, tag).resultOrPartial(LOGGER::error);
    }
}
