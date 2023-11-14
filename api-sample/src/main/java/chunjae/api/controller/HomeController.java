package chunjae.api.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import chunjae.api.service.home.HomeService;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import chunjae.api.common.queryFactory.ContantList;
import chunjae.api.common.queryFactory.SaveResult;
import chunjae.api.domain.dto.home.LcmsNoticeDto;
import chunjae.api.domain.entity.home.LcmsNotice;

@CrossOrigin("*")
@RequestMapping("/home")
@RestController
public class HomeController 
{
    @Autowired
    private HomeService homeService;

    
    //private HomeService homeServiceByJpa;
    //@Autowired
    //public void HomeController(HomeServiceImpl homeService1, HomeServiceImplByJpa homeService2){
        //    this.homeService = homeService1;
        //    this.homeServiceByJpa = homeService2;
        //}
    
    
    // private int a=1;
    // @GetMapping("/home/test")
    // public int test(){
    //     this.a++;
    //     System.out.println(a);
    //     return a;
    // }
    @GetMapping("/")
    public String hello(){
        return "hello";
    }

    @GetMapping("/jsonList/{pageNo}/{listSize}")
    public List<LcmsNotice> jsonList(@PathVariable int pageNo, @PathVariable int listSize) throws Exception 
    {
        return homeService.getList(pageNo, listSize);
    }

    @PostMapping("/jsonList")
    public ContantList<String, Object> jsonList(@RequestBody HashMap<String, Object> map) throws Exception 
    {
        return homeService.getList(map);
    }

    @PostMapping("/jsonView")
    public Map<String, Object> jsonView(@RequestBody HashMap<String, Object> map) throws Exception 
    {
        return homeService.getView(map);
    }    

    @PostMapping(value="/jsonWrite", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE })
    public SaveResult jsonWrite(@RequestPart("files") List<MultipartFile> files, @RequestPart("data") LcmsNoticeDto dto) throws Exception 
    {
        return homeService.setWrite(files, dto);
    }

    @PostMapping("/jsonWriteList")
    public SaveResult jsonWrite(@RequestBody List<LcmsNoticeDto> dtos) throws Exception 
    {
        System.out.println(dtos);
        return homeService.setWrite(dtos);
    }

    @DeleteMapping("/{idx}")
    public SaveResult delete(@PathVariable int idx) throws Exception{
        return homeService.setDelete(idx);
    }
}
