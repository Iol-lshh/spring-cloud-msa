package chunjae.api.common.queryFactory;

import java.util.List;
import java.util.Map;

import chunjae.api.common.queryFactory.QueryStatement.Parameter.ParameterType;
import lombok.Getter;
import lombok.Setter;

public interface QueryStatement {
    
    // # set 메서드에 이용할 프로퍼티 타입
    enum PropertyName {
        PATH_NAME, QUERY_TEXT, SP_NAME
    }
    
    // # param 클래스
    @Getter
    @Setter
    public class Parameter {
        enum ParameterType {INPUT, OUTPUT}
        
        public String key;
        public Object val;
        public ParameterType type;
    }

    // # property 제어
    QueryStatement set(String val);
    QueryStatement set(PropertyName property, String val) throws Exception;

    // # param 제어
    // ## param 모두 제거
    QueryStatement clearParams();
    
    // ## param 추가
    QueryStatement addParam(String key, Object val);
    QueryStatement addParam(String key, Object val, ParameterType type);
    
    // select
    ContantList<String, Object> queryMap() throws Exception;
    <T> List<T> queryByClass(Class<T> theClass) throws Exception;
    Map<String, Object> queryScalar() throws Exception;
    <T> T queryScalarByClass(Class<T> theClass) throws Exception;
    
    // insert, update
    SetResult command() throws Exception;
}
