package chunjae.api.service.home;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.web.multipart.MultipartFile;

import chunjae.api.domain.dto.home.LcmsNoticeDto;
import chunjae.api.domain.entity.home.LcmsNotice;
import chunjae.api.domain.repository.home.LcmsNoticeRepository;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import chunjae.api.common.queryFactory.ContantList;
import chunjae.api.common.queryFactory.QueryFactory;
import chunjae.api.common.queryFactory.SaveResult;

import java.time.LocalDateTime;
import java.io.IOException;
import java.time.LocalDate;

import chunjae.api.common.fileIo.FileIoHelper;
import chunjae.api.common.fileIo.FileIoHelper.FileIoResult;

@Service
public class HomeServiceImpl implements HomeService{

    @Autowired
    LcmsNoticeRepository noticeRepo;

    @Autowired
    FileIoHelper fileIoHelper;

    @Autowired
    DataSource dataSource;

    @Autowired
    QueryFactory queryFactory;

    @Override
    public List<LcmsNotice> getList(int pageNo, int listSize) throws Exception{
        return noticeRepo.findAll(PageRequest.of(pageNo, listSize, Sort.by("Idx").descending())).toList();
    }

    @Override
    public ContantList<String, Object> getList(Map<String, Object> map) throws Exception{        
        return queryFactory
                    .createMyBatisStatement()
                    .set("/home/list")
                    .addParam("@in_PageNo", map.get("pageno"))
                    .addParam("@in_ListSize", map.get("listsize"))
                    .addParam("@in_Key", map.get("key"))
                    .addParam("@in_Val", map.get("val"))
                    .queryMap();
    }

    @Override
    public Map<String, Object> getView(Map<String, Object> map) throws Exception {

        return queryFactory
                    .createTextStatement()
                    .set("SELECT * FROM TBL_LCMS_Notice Where Idx = @in_Idx")
                    .addParam("@in_Idx", map.get("idx"))
                    .queryScalar();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_UNCOMMITTED)
    public List<String> saveFiles(List<MultipartFile> files) throws IllegalStateException, IOException{
        return fileIoHelper.saveFiles(files);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_UNCOMMITTED)
    public LcmsNotice create(LcmsNoticeDto dto, Optional<List<String>> ff) throws Exception
    {
        noticeRepo.save(LcmsNotice
                                .builder()
                                .userID(dto.getUserId())
                                .title(dto.getTitle())
                                .content(dto.getContent())
                                .attachFile1(ff.filter(e -> e.get(0) != FileIoResult.NO_SIZE.name())
                                                .orElse(emtyFf)
                                                .get(0))
                                .attachFile2(ff.filter(e -> e.get(1) != FileIoResult.NO_SIZE.name())
                                                .orElse(emtyFf)
                                                .get(1))
                                .regDate(LocalDateTime.now())
                                .inputDate(LocalDate.now().toString())
                                .subjectCode("")
                                .build());

        return new LcmsNotice();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_UNCOMMITTED)
    public LcmsNotice update(LcmsNoticeDto dto, Optional<List<String>> ff) throws NoSuchElementException{
        LcmsNotice before = noticeRepo.findLcmsNoticeByIdx(dto.getIdx())
                                .orElseThrow(() -> new NoSuchElementException("찾는 글이 없습니다."));
        return noticeRepo.save(before
                                    .setUserID(dto.getUserId())
                                    .setTitle(dto.getTitle())
                                    .setContent(dto.getContent())
                                    .setAttachFile1(ff.filter(e -> e.get(0) != FileIoResult.NO_SIZE.name())
                                                        .orElse(Arrays.asList("" + before.getAttachFile1()))
                                                        .get(0))
                                    .setAttachFile2(ff.filter(e -> e.get(1) != FileIoResult.NO_SIZE.name())
                                                        .orElse(Arrays.asList("", "" + before.getAttachFile2()) )
                                                        .get(1)));
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_UNCOMMITTED)
    public SaveResult setDelete(int idx) throws Exception{
        SaveResult result = SaveResult.FAIL;
    
        // 1. save
        noticeRepo.findLcmsNoticeByIdx(idx)
            .orElseThrow(() -> new NoSuchElementException("찾는 글이 없습니다."))
            .setDeleteYN('Y');
    
        // 2. result
        result = SaveResult.SUCCESS;
        return result;
    }
}

