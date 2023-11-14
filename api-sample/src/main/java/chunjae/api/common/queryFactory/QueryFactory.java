package chunjae.api.common.queryFactory;

public interface QueryFactory {
    QueryStatement createMyBatisStatement();
    QueryStatement createTextStatement();
    QueryStatement createProcedureStatement();
}
