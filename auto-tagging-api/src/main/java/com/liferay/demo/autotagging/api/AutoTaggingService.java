package com.liferay.demo.autotagging.api;


import java.util.HashMap;

/**
 * @author jverweij
 */
public interface AutoTaggingService {

    public String Ping();
    public HashMap<String,String> List();
    public String Update(String id, String tag, String businessrule);
    public String Delete(String id);
    public void Init();
    public String Match(String doc);
    public String Get(String id);
}