package cs601.sideProject;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * ServerResponse class. set up the content that we want to respond to user based on user request
 */
public class ServerResponse {
    private int code;
    private PrintWriter output;
    private Map<String, String> headers;
    public ServerResponse(PrintWriter output) {
        this.code = 200;
        this.output = output;
        this.headers = new HashMap<>();

    }

    /**
     * Set response code
     *
     * @param code The response code that we want to respond to user
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * add header method
     * @param name header's name
     * @param content header's content
     */
    public void addHeader(String name, String content) {

        headers.put(name, content);
    }

    /**
     * Response for user request
     *
     * @param content the content that we want to respond to user based on user request
     */
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
