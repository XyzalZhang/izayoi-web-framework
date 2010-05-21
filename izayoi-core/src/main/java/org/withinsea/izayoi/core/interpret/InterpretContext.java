package org.withinsea.izayoi.core.interpret;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-5-16
 * Time: 9:24:54
 */
public interface InterpretContext {

    public <T> T getBean(String name);

    public boolean isReturned();

    public void doReturn(Object result);

    public Object getResult();
    
    public void doThrow(Exception ex) throws Exception;

    public Exception getException();
}
