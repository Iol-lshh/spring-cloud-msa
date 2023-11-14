package chunjae.api.common.queryFactory;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class ContantList<T, Y> extends ArrayList<List<Map<T, Y>>> 
{
    public List<Object> output;

    public ContantList(){
        output = new ArrayList<>();
    }
}
