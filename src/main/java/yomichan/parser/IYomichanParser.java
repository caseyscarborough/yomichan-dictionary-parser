package yomichan.parser;

import yomichan.utils.FileUtils;

import java.io.File;

public interface IYomichanParser<T> {

    default T parse(String path) {
        return parse(FileUtils.getFile(path));
    }

    T parse(File file);
}
