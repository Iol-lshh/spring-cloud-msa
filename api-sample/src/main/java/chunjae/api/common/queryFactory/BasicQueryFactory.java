package chunjae.api.common.queryFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class BasicQueryFactory implements QueryFactory{

    @Autowired
    DataSource dataSource;

    @Override
    public QueryStatement createMyBatisStatement() {
        return new MyBatisQueryStatement(dataSource);
    }

    @Override
    public QueryStatement createTextStatement() {
        return new TextQueryStatement(dataSource);
    }

    @Override
    public QueryStatement createProcedureStatement() {
        return new ProcedureQueryStatement(dataSource);
    }
    
}
