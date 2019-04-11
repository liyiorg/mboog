package mboog.generator.internal.rules;

import org.mybatis.generator.internal.rules.Rules;
import org.mybatis.generator.internal.rules.RulesDelegate;

/**
 * @author liyi
 */
public class MboogRulesDelegate extends RulesDelegate {

    public MboogRulesDelegate(Rules rules) {
        super(rules);
    }

    @Override
    public boolean generateBaseRecordClass() {
        return true;
    }

    @Override
    public boolean generateRecordWithBLOBsClass() {
        return false;
    }

    @Override
    public boolean generateResultMapWithBLOBs() {
        return false;
    }

    @Override
    public boolean generateSelectByExampleWithBLOBs() {
        return false;
    }


    @Override
    public boolean generateUpdateByExampleWithBLOBs() {
        return false;
    }


    @Override
    public boolean generateUpdateByPrimaryKeyWithBLOBs() {
        return false;
    }


}
