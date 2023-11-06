package net.jmb19905.test;

import net.jmb19905.ParamParser;
import org.junit.jupiter.api.Test;

public class TestParamParser {

    @Test
    void testParamParsing() {
        var p = ParamParser.parse("dhparam.jparam");
        assert p != null;
        System.out.println(p.prime());
        System.out.println(p.base().intValue());
    }

}
