package chunjae.api.common.queryFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import chunjae.api.common.queryFactory.QueryStatement.Parameter.ParameterType;

public class TextQueryStatement implements QueryStatement{
    
    private DataSource dataSource;

    private String queryText;
    private List<Parameter> params = new ArrayList<>();

    public TextQueryStatement(DataSource dataSource){
        this.dataSource = dataSource;
        this.params = new ArrayList<>();
    }
    
    /*---------------------------------------------*/
    // # 1. property 제어
    @Override
    public QueryStatement set(String queryText) {
        setQueryText(queryText);
        return this;
    }

    @Override
    public QueryStatement set(PropertyName property, String val) throws Exception {
        switch(property){
            case QUERY_TEXT:
                setQueryText(val);
                break;
            default:
                throw new Exception("no such property");
        }
        return this;
    }

    private void setQueryText(String queryText){
        this.queryText = queryText;
    }

    /*---------------------------------------------*/
    // # 2. param 제어
    @Override
    public QueryStatement clearParams() {
        if(this.params != null) 
            this.params.clear();

        return this;
    }

    @Override
    public QueryStatement addParam(String key, Object val) {
        Parameter param = new Parameter();
        param.key = key;
        param.val = val;
        param.type = ParameterType.INPUT;
        
        this.params.add(param);
        return this;
    }

    @Override
    public QueryStatement addParam(String key, Object val, ParameterType type) {
        Parameter param = new Parameter();
        param.key = key;
        param.val = val;
        param.type = type;

        this.params.add(param);

        return this;
    }

    /*---------------------------------------------*/
    // # 3. query
    @Override
    public ContantList<String, Object> queryMap() throws Exception {
        return getText();
    }

    @Override
    public <T> List<T> queryByClass(Class<T> theClass) throws Exception {
        return getTextGen(theClass);
    }

    @Override
    public Map<String, Object> queryScalar() throws Exception {
        return getText().get(0).get(0);
    }

    @Override
    public <T> T queryScalarByClass(Class<T> theClass) throws Exception {
        return getTextGen(theClass).get(0);
    }    

    // # 4. command
    @Override
    public SetResult command() throws Exception {
        return setText();
    }    
    

    /*---------------------------------------------*/
    // # 5. Text: select
    private ContantList<String, Object> getText() throws Exception
    {
        ContantList<String, Object> result = new ContantList<>();
        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        MapSqlParameterSource parameters = toMapSqlParameterSource();
        
        String[] querrys = queryText.split(";");
        for(String q : querrys){
            result.add(jdbcTemplate.queryForList(q, parameters));
        } 

        return result;
    }

    // # 5-1. Text: select by some class
    private <T> List<T> getTextGen(Class<T> theClass) throws Exception
    {
        List<T> result = new ArrayList<>();

        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        MapSqlParameterSource parameters = toMapSqlParameterSource();
        
        String querry = queryText.split(";")[0];
        result = jdbcTemplate.queryForList(querry, parameters, theClass);

        return result;
    }    

    // # 6. Text: insert / update
    private SetResult setText() throws Exception
    {
        SetResult result = new SetResult();

        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        MapSqlParameterSource parameters = toMapSqlParameterSource();
        
        String[] querrys = queryText.split(";");
        for(String q : querrys){
            result.count += jdbcTemplate.update(q, parameters);
        }

        return result;
    }

    // # 7. MapSqlParameterSource 반환
    public MapSqlParameterSource toMapSqlParameterSource(){
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        for(Parameter p : this.params)
        {
            parameters.addValue(p.key.replace("@", ""), p.val);
            queryText = queryText.replace(p.key, p.key.replace("@", ":"));
        }
        return parameters;
    }
}
