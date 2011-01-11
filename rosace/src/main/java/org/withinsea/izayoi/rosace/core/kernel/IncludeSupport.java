package org.withinsea.izayoi.rosace.core.kernel;

import org.withinsea.izayoi.rosace.core.exception.RosaceException;

import java.io.Writer;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-11-28
 * Time: 18:11:36
 */
public abstract class IncludeSupport {

    abstract protected void doInclude(Writer writer, String path, Map<String, Object> context) throws RosaceException;

    public final void include(Writer writer, String path, Map<String, Object> context) throws RosaceException {

        String selfPath = Tracer.getPath();
        if (selfPath == null) {
            throw new RosaceException("template path should be set into IncludeSupport.Tracer before rendering.");
        }

        Tracer.getIncludingStack().push(new Tracer.Including(path, context));
        try {
            doInclude(writer, path, context);
        } finally {
            Tracer.getIncludingStack().pop();
        }
    }

    public static final class Tracer {

        private static final ThreadLocal<String> SELF_PATH = new ThreadLocal<String>();
        private static final ThreadLocal<Deque<Including>> INCLUDING_STACK = new ThreadLocal<Deque<Including>>() {
            @Override
            protected Deque<Including> initialValue() {
                return new LinkedList<Including>() {
                    @Override
                    public Including pop() {
                        Including top = super.pop();
                        if (isEmpty()) {
                            SELF_PATH.remove();
                        } else {
                            SELF_PATH.set(peek().getIncluderPath());
                        }
                        return top;
                    }
                };
            }
        };

        public static void setPath(String path) {
            SELF_PATH.set(path);
        }

        public static String getPath() {
            return SELF_PATH.get();
        }

        public static Deque<Including> getIncludingStack() {
            return INCLUDING_STACK.get();
        }

        public static class Including {

            protected String includerPath;
            protected String targetPath;
            protected Map<String, Object> context;

            public Including(String targetPath, Map<String, Object> context) {
                this.includerPath = getPath();
                this.targetPath = targetPath;
                this.context = context;
            }

            public String getIncluderPath() {
                return includerPath;
            }

            public String getTargetPath() {
                return targetPath;
            }

            public Map<String, Object> getContext() {
                return context;
            }
        }
    }
}
