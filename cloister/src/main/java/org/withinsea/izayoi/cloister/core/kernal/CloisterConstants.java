package org.withinsea.izayoi.cloister.core.kernal;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-11
 * Time: 下午7:38
 */
public class CloisterConstants {

    protected static final String PREFIX = (CloisterConstants.class.getCanonicalName() + ".").replaceAll("\\.", "_");

    public static final String DEFAULT_POSTSCRIPT_ENTRANCE = "execute";

    public static final String ATTR_COMPILED_SCRIPT_CACHE = PREFIX + "ATTR_COMPILED_SCRIPT_CACHE";
    public static final String ATTR_COMPILED_DYNAPAGE_CACHE = PREFIX + "ATTR_COMPILED_DYNAPAGE_CACHE";
    public static final String ATTR_PROCESSED_RESPONDER_CACHE = PREFIX + "ATTR_PROCESSED_RESPONDER_CACHE";
    public static final String ATTR_REDISPATCHED_REQUEST = PREFIX + "ATTR_MAPPED_REQUEST";
    public static final String ATTR_DISPATCHED_GLOBALSCOPES = PREFIX + "ATTR_DISPATCHED_GLOBALSCOPES";
}
