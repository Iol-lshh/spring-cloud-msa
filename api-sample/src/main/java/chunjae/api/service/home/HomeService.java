package chunjae.api.service.home;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import chunjae.api.common.queryFactory.ContantList;
import chunjae.api.common.queryFactory.SaveResult;
import chunjae.api.domain.dto.home.LcmsNoticeDto;
import chunjae.api.domain.entity.home.LcmsNotice;

public interface HomeService {
    List<String> emtyFf = Arrays.asList("", "");

    // # 1. 조회
    // ## 1.1 리스트
    List<LcmsNotice> getList(int pageNo, int listSize) throws Exception;
    ContantList<String, Object> getList(Map<String, Object> map) throws Exception;

    // ## 1.2 뷰
    Map<String, Object> getView(Map<String, Object> map) throws Exception;

    // # 2. 쓰기
    // ## 2.1 파일 포함 쓰기
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_UNCOMMITTED)
    default SaveResult setWrite(List<MultipartFile> files, LcmsNoticeDto dto) throws Exception{
        SaveResult result = SaveResult.FAIL;
        
        // 1. upload file
        List<String> ff = saveFiles(files);

        // 2. save
        if(dto.getIdx() == null || dto.getIdx() == 0)
            create(dto, Optional.of(ff));
        else
            update(dto, Optional.of(ff));

        // 3. result
        result = SaveResult.SUCCESS;
        return result;
    }

    // ## 2.1 리스트 쓰기
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_UNCOMMITTED)
    default SaveResult setWrite(List<LcmsNoticeDto> dtos) throws Exception{
        SaveResult result = SaveResult.FAIL;
        
        // 1. write
        for(LcmsNoticeDto dto: dtos){
            result = setWrite(dto);
        }

        // 2. result
        return result;
    }

    // ## 2.2 단일 쓰기
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_UNCOMMITTED)
    default SaveResult setWrite(LcmsNoticeDto dto) throws Exception{
        SaveResult result = SaveResult.FAIL;

        // 1. save
        if(dto.getIdx() == null || dto.getIdx() == 0)
            create(dto);
        else
            update(dto);

        // 2. result
        result = SaveResult.SUCCESS;
        return result;
    }

    // ## 2.3 삭제
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_UNCOMMITTED)
    SaveResult setDelete(int idx) throws Exception;

    // # 3. 파일 저장
    List<String> saveFiles(List<MultipartFile> files) throws IllegalStateException, IOException;


    // # 4. db row 생성
    // ## 4.1
    default LcmsNotice create(LcmsNoticeDto dto) throws Exception{
        return create(dto, Optional.empty());
    }
    // ## 4.2
    LcmsNotice create(LcmsNoticeDto dto, Optional<List<String>> ff) throws Exception;


    // # 5. db row 수정
    // ## 5.1
    default LcmsNotice update(LcmsNoticeDto dto) throws NoSuchElementException{
        return update(dto, Optional.empty());     
    }
    // ## 5.2
    LcmsNotice update(LcmsNoticeDto dto, Optional<List<String>> ff) throws NoSuchElementException;
}
