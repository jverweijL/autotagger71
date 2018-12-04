package com.liferay.demo.autotagging.api.api;


import java.util.HashMap;
import java.util.Map;

/**
 * @author jverweij
 */
public interface AutoTaggingApi {

    public String Ping();
    public HashMap<String,String> List();
    public String Update(String id, String tag, String businessrule);
    public String Delete(String id);
    public void Init();
    public String[] Match(String doc);
    public Map<String, Object> Get(String id);
}