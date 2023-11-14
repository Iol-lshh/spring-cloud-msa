package chunjae.api.common.queryFactory;

import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStreamReader;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import javax.sql.DataSource;

import bsh.EvalError;
import bsh.Interpreter;
import chunjae.api.common.queryFactory.QueryStatement.Parameter.ParameterType;

public class MyBatisQueryStatement implements QueryStatement {

    private DataSource dataSource;

    private String pathName;
    private List<Parameter> params;

    public MyBatisQueryStatement(DataSource dataSource){
        this.dataSource = dataSource;
        this.params = new ArrayList<>();
    }

    /*---------------------------------------------*/
    // # 1. property 제어
    @Override
    public QueryStatement set(String pathName){
        setPathName(pathName);
        return this;
    }

    @Override
    public QueryStatement set(PropertyName property, String val) throws Exception {
        switch(property){
            case PATH_NAME:
                setPathName(val);
                break;
            default:
                throw new Exception("no such property");
        }
        return this;
    }

    private void setPathName(String pathName){
        this.pathName = pathName;
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
        return getMyBatis();
    }

    @Override
    public <T> List<T> queryByClass(Class<T> theClass) throws Exception {
        return getMyBatisGen(theClass);
    }

    @Override
    public Map<String, Object> queryScalar() throws Exception {        
        return getMyBatis().get(0).get(0);
    }

    @Override
    public <T> T queryScalarByClass(Class<T> theClass) throws Exception {        
        return getMyBatisGen(theClass).get(0);
    }

    // # 4. command
    @Override
    public SetResult command() throws Exception {
        return setMyBatis();
    }
    
    /*---------------------------------------------*/
    // # 5. MyBatis: select
    private ContantList<String, Object> getMyBatis() throws Exception
    {
        ContantList<String, Object> result = new ContantList<>();

        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        // ## json
        JSONObject json = toJSONObject();
        // ## parameters
        MapSqlParameterSource parameters = toMapSqlParameterSource();
        
        // ## mainQuery
        JSONArray sqlList = (JSONArray)json.get("mainQuery");
        for(Object sql : sqlList)
        {
            String sqlText = sql.toString();
            sqlText = compileSqlText(json.get("brickQuery"), sqlText);

            result.add(jdbcTemplate.queryForList(sqlText, parameters));
        }

        return result;
    }

    // # 5-1. MyBatis: select by some class
    private <T> List<T> getMyBatisGen(Class<T> theClass) throws Exception
    {
        List<T> result = new ArrayList<T>();

        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        // ## json
        JSONObject json = toJSONObject();
        // ## parameters
        MapSqlParameterSource parameters = toMapSqlParameterSource();
        // ## mainQuery
        String sqlText = ((JSONArray)json.get("mainQuery")).get(0).toString();
        sqlText = compileSqlText(json.get("brickQuery"), sqlText);

        result = jdbcTemplate.queryForList(sqlText, parameters, theClass);

        return result;
    }

    // # 6. MyBatis: insert / update
    private SetResult setMyBatis() throws Exception
    {
        SetResult result = new SetResult();

        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        // ## json
        JSONObject json = toJSONObject();
        // ## parameters
        MapSqlParameterSource parameters = toMapSqlParameterSource();
        // ## mainQuery
        JSONArray sqlList = (JSONArray)json.get("mainQuery");
        
        for(Object sql : sqlList)
        {
            String sqlText = sql.toString();
            sqlText = compileSqlText(json.get("brickQuery"), sqlText);
            // ## count
            result.count = jdbcTemplate.update(sqlText, parameters);
        }

        return result;
    }    


    /*---------------------------------------------*/
    // # 7. MapSqlParameterSource 반환
    public MapSqlParameterSource toMapSqlParameterSource(){
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        for(Parameter p : this.params)
        {
            parameters.addValue(p.key.replace("@", ""), p.val);
        }
        return parameters;
    }

    // # 8. JSONObject 반환
    public JSONObject toJSONObject() throws IOException, ParseException{
        ClassPathResource resource = new ClassPathResource("json/" + this.pathName + ".json");
        return (JSONObject) new JSONParser().parse(new InputStreamReader(resource.getInputStream()));
    }
    

    // # 9. SqlText 컴파일
    private String compileSqlText(Object brickQuery, String sqlText) throws EvalError
    {
        if(brickQuery != null) 
        {
            for(Object o : (JSONArray)brickQuery)
            {
                JSONObject parseJson = (JSONObject)o;
                String whereKey = parseJson.get("key").toString();
                String whereVal = compileWhere(parseJson);

                sqlText = sqlText.replace("{"+whereKey+"}", " " + whereVal + " ");
                sqlText = sqlText.replace("{"+whereKey+" }", " " + whereVal + " ");
                sqlText = sqlText.replace("{ "+whereKey+"}", " " + whereVal + " ");
                sqlText = sqlText.replace("{ "+whereKey+" }", " " + whereVal + " ");
            }
        }
        
        sqlText = sqlText.replace("@", ":");
        return sqlText;
    }

    // # 10. where 컴파일
    private String compileWhere(JSONObject json) throws EvalError
    {
        Interpreter inPt = new Interpreter();
        String condition = json.get("condition").toString();
        String javaCode = condition.replace("@", "").replace("'", "\"");

        for(Parameter p : this.params) {
            inPt.set(p.key.replace("@", ""), p.val);
        }

        inPt.eval("v=" + javaCode);
        Boolean result = (Boolean)inPt.get("v");

        if(result)
            return json.get("query").toString();
        else
            return "";
    }
}
