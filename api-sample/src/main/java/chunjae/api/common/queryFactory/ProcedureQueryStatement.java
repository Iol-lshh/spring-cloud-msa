package chunjae.api.common.queryFactory;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import chunjae.api.common.queryFactory.QueryStatement.Parameter.ParameterType;

public class ProcedureQueryStatement implements QueryStatement{
    
    private DataSource dataSource;

    private String spName;
    private List<Parameter> params;

    public ProcedureQueryStatement(DataSource dataSource){
        this.dataSource = dataSource;        
        this.params = new ArrayList<>();
    }    

    /*---------------------------------------------*/
    // # 1. property 제어
    @Override
    public QueryStatement set(String spName) {
        setSpName(spName);
        return this;
    }

    @Override
    public QueryStatement set(PropertyName property, String val) throws Exception {
        switch(property){
            case SP_NAME:
                setSpName(val);
                break;
            default:
                throw new Exception("no such property");
        }
        return this;
    }

    private void setSpName(String spName){
        this.spName = spName;
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
        return getProc();
    }

    @Override
    public <T> List<T> queryByClass(Class<T> theClass) throws Exception {
        return getProcGen(theClass);
    }

    @Override
    public Map<String, Object> queryScalar() throws Exception {     
        return getProc().get(0).get(0);
    }        

    @Override
    public <T> T queryScalarByClass(Class<T> theClass) throws Exception {
        return getProcGen(theClass).get(0);
    }    

    // # command
    @Override
    public SetResult command() throws Exception {
        return setProc();
    }    


    /*---------------------------------------------*/
    // # 5. Procedure: select
    private ContantList<String, Object> getProc() throws Exception 
    {
        ContantList<String, Object> result = new ContantList<>();

        String sql = "{call " + spName + "(";
        String paramText = "";
        List<Integer> output = new ArrayList<>();

        for(Parameter p : params)
        {            
            if(!paramText.isEmpty())
                paramText += ",";

            paramText += p.key + "=?";
        }

        sql = sql + paramText + ")}";
        CallableStatement cs = dataSource.getConnection().prepareCall(sql);
        
        for(Integer i = 0; i < params.size(); i++)
        {
            if(params.get(i).type == ParameterType.INPUT)
                cs.setObject(i+1, params.get(i).val);
            else {
                cs.registerOutParameter(i+1, java.sql.Types.JAVA_OBJECT);
                output.add(i+1);
            }
        }

        boolean csResult = cs.execute();

        while(csResult)
        {
            ResultSet rs = cs.getResultSet();
            ResultSetMetaData metaData = rs.getMetaData();
            List<Map<String, Object>> mapList = new ArrayList<>();

            while (rs.next()) 
            {
                Map<String, Object> map = new HashMap<String, Object>();
                
                for(Integer i = 1; i <= metaData.getColumnCount(); i++)
                {
                    String columnName = metaData.getColumnName(i);
                    map.put(columnName, rs.getObject(columnName));
                }

                mapList.add(map);
            }
            
            result.add(mapList);

            rs.close();
            csResult = cs.getMoreResults();
        }

        if(output.size() > 0)
        {
            for(Integer n : output)
            {
                result.output.add(cs.getString(n));
            }
        }

        cs.close();
        
        return result;
    }

    // # 5-1. Procedure: select by some class
    private <T> List<T> getProcGen(Class<T> theClass) throws Exception 
    {
        List<T> result = new ArrayList<>();
        String query = "EXEC " + spName;
        String param = "";

        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        for(Parameter p : params)
        {
            parameters.addValue(p.key.replace("@", ""), p.val);

            if(!param.isEmpty())
                param += ",";

            param += p.key + "=" + p.key.replace("@", ":");
        }

        query += query + " " + param;

        result = jdbcTemplate.queryForList(query, parameters, theClass);
        
        return result;
    }    

    // # 6. Procedure: insert / update
    private SetResult setProc() throws Exception
    {
        SetResult result = new SetResult();

        String sql = "{call " + spName + "(";
        String paramText = "";
        List<Integer> output = new ArrayList<>();

        for(Parameter p : params)
        {            
            if(!paramText.isEmpty())
                paramText += ",";

            paramText += p.key + "=?";
        }

        sql = sql + paramText + ")}";
        CallableStatement cs = dataSource.getConnection().prepareCall(sql);
        
        for(Integer i = 0; i < params.size(); i++)
        {
            if(params.get(i).type == ParameterType.INPUT)
                cs.setObject(i+1, params.get(i).val);
            else {
                cs.registerOutParameter(i+1, java.sql.Types.JAVA_OBJECT);
                output.add(i+1);
            }
        }

        result.count = cs.executeUpdate();

        if(output.size() > 0)
        {
            for(Integer n : output)
            {
                result.output.add(cs.getString(n));
            }
        }            

        return result;
    }

    /*---------------------------------------------*/
}
