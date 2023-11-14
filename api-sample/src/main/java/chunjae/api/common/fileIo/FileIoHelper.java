package chunjae.api.common.fileIo;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;


public interface FileIoHelper {

    enum FileIoResult {
        SUCCESS, FAIL, NO_SIZE;
    }

    default boolean isValidFile(Object fileData){
        Optional<Object> maybeFileData = Optional.ofNullable(fileData);
                
        // && 조건
        return !maybeFileData.isEmpty() 
            && !maybeFileData.get().equals("");
    }

    List<String> saveFiles(List<MultipartFile> files) throws IllegalStateException, IOException;
}
