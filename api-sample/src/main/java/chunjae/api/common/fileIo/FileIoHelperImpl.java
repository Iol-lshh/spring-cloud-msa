package chunjae.api.common.fileIo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileIoHelperImpl implements FileIoHelper{

    private String FILE_PATH;

    public FileIoHelperImpl(@Value("${customize-function.fileio.path}") String filePath){
        this.FILE_PATH = filePath;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_UNCOMMITTED)
    public List<String> saveFiles(List<MultipartFile> files) throws IllegalStateException, IOException{
        List<String> fileNameList = new ArrayList<String>();
        
        for(MultipartFile f : files)
        {
            if(f.getSize() > 0)
            {
                String origName = f.getOriginalFilename();
                String fileName = origName.substring(0, origName.lastIndexOf("."));
                String extension = origName.substring(origName.lastIndexOf("."));
                
                String savedName = fileName + extension;
                String savedPath = ResourceUtils.getFile(FILE_PATH).getAbsolutePath();
                int cc = 1;

                File file = new File(savedPath + "\\" + savedName);

                while(true)
                {                    
                    if(file.exists()){
                        savedName = fileName + "(" + cc + ")" + extension;
                        file = new File(savedPath + "\\" + savedName);
                        cc++;
                    }
                    else
                        break;
                }

                f.transferTo(file);
                fileNameList.add(savedName);
            } else {
                fileNameList.add(FileIoResult.NO_SIZE.toString());
            }
        }
        return fileNameList;
    }
}
