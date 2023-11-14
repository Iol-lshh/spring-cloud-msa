package chunjae.api.common.queryFactory;

import java.util.ArrayList;
import java.util.List;

public class SetResult {
    public int count;
    public List<Object> output;

    public SetResult(){
        output = new ArrayList<>();
    }
}
