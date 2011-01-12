package org.withinsea.izayoi.rosace.core.impl.template.dom;

import org.withinsea.izayoi.common.util.LazyLinkedHashMap;
import org.withinsea.izayoi.rosace.core.kernel.PrecompiletimeContext;
import sun.net.idn.StringPrep;

import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 11-1-12
 * Time: 上午8:29
 */
public class IdGenerator {

    protected static String ATTR_GENERATORS = IdGenerator.class.getCanonicalName() + ".ATTR_GENERATORS";

    public static IdGenerator get(String key) {
        PrecompiletimeContext ctx = PrecompiletimeContext.get();
        Map<String, IdGenerator> generators = ctx.getGlobalAttribute(ATTR_GENERATORS);
        if (generators == null) {
            generators = new LazyLinkedHashMap<String, IdGenerator>() {
                @Override
                protected IdGenerator createValue(String s) {
                    return new IdGenerator(s);
                }
            };
            ctx.setGlobalAttribute(ATTR_GENERATORS, generators);
        }
        return generators.get(key);
    }

    protected String prefix;
    protected int idCount = 0;

    protected IdGenerator(String prefix) {
        this.prefix = prefix;
    }

    public String currentId() {
        return idCount == 0 ? null : prefix + idCount;
    }

    public String nextId() {
        idCount ++;
        return prefix + idCount;
    }
}
