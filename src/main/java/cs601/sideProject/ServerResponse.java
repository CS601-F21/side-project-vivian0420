package cs601.sideProject;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class ServerResponse {
    private int code;
    private PrintWriter output;
    private Map<String, String> headers;
    //private Map<String, String> headers;

    public ServerResponse(PrintWriter output) {
        this.code = 200;
        this.output = output;
        this.headers = new HashMap<>();

    }

    public void setCode(int code) {
        this.code = code;
    }

    public void addHeader(String name, String content) {

        headers.put(name, content);
    }
    public void response(String content) {

        this.output.println("HTTP/1.1 " + this.code);
        this.output.println("content-length: " + content.length());
        for(Map.Entry<String,String> entry: headers.entrySet()){
            this.output.println(entry.getKey() + ": " + entry.getValue());
        }
        this.output.println("");
        this.output.println(content);
    }


}