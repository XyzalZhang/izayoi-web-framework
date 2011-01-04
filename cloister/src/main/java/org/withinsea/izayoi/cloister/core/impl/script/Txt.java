package org.withinsea.izayoi.cloister.core.impl.script;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 11-1-3
 * Time: 上午8:39
 */
public class Txt extends StaticTxtData {

    public Txt(String encoding) {
        super(encoding);
    }

    @Override
    protected Object load(String txt) {
        return txt;
    }
}
