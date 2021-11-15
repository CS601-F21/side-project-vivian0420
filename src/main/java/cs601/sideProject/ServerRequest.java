package cs601.sideProject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerRequest {
    private final String requestLine;
    private final Map<String, String> headers;
    private final String content;
    private String path;
    private String requestMethod;
    private String version;
    private final Map<String, String> queryParam;


    public ServerRequest(String requestLine, Map<String, String> headers, String content) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.content = content;
        this.queryParam = new HashMap<>();
        if (requestLine != null) {
            String[] requestLineParts = requestLine.split(" ");
            if (requestLineParts.length == 3) {
                this.requestMethod = requestLineParts[0];
                if(requestLineParts[1].contains("?")){
                    this.path = requestLineParts[1].split("\\?")[0];
                    String[] queryItem = requestLineParts[1].split("\\?")[1].split("&");
                    for(String query: queryItem){
                        if(query.split("=").length == 1) {
                            continue;
                        }else {
                            queryParam.put(query.split("=")[0], query.split("=")[1]);
                        }
                    }
                }
                else {
                    this.path = requestLineParts[1];
                }
                this.version = requestLineParts[2];
            } else {
                this.requestMethod = null;
                this.path = null;
                this.version = null;
            }
        }
    }
    public boolean is400() {
        return this.requestMethod == null || this.path == null || this.version == null;
    }

    public boolean is405() {
        return !this.requestMethod.equals("GET") && !this.requestMethod.equals("POST");
    }

    public String getContent() {
        return this.content;
    }

    public Map<String, String> getFormData() {
        Map<String, String> form = new HashMap<>();
        try {
            final String[] entries = URLDecoder.decode(content, StandardCharsets.UTF_8.name()).split("&");
            for (String e : entries) {
                final String[] split = e.split("=", 2);
                if (split.length == 2) {
                    form.put(split[0], split[1]);
                } else {
                    form.put(split[0], null);
                }
            }
            return form;
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public String getPath() {
        return this.path;
    }

    public String getRequestMethod() {
        return this.requestMethod;
    }

    public Map<String, String> getHeaders(){
        return this.headers;
    }

    public Map<String, String> getQueryParam() {
        return this.queryParam;
    }
}