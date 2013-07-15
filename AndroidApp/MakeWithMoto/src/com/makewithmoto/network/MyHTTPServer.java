package com.makewithmoto.network;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringEscapeUtils;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.makewithmoto.app.network.NanoHTTPD;
import com.makewithmoto.app.utils.NetworkUtils;
import com.makewithmoto.utils.ALog;

/**
 * An example of subclassing NanoHTTPD to make a custom HTTP server.
 */
public class MyHTTPServer extends NanoHTTPD {
	public static final String TAG = "myHTTPServer";
	private WeakReference<Context> ctx;
	
    private static final Map<String, String> MIME_TYPES = new HashMap<String, String>() {{
        put("css", "text/css");
        put("htm", "text/html");
        put("html", "text/html");
        put("xml", "text/xml");
        put("txt", "text/plain");
        put("asc", "text/plain");
        put("gif", "image/gif");
        put("jpg", "image/jpeg");
        put("jpeg", "image/jpeg");
        put("png", "image/png");
        put("mp3", "audio/mpeg");
        put("m3u", "audio/mpeg-url");
        put("mp4", "video/mp4");
        put("ogv", "video/ogg");
        put("flv", "video/x-flv");
        put("mov", "video/quicktime");
        put("swf", "application/x-shockwave-flash");
        put("js", "application/javascript");
        put("pdf", "application/pdf");
        put("doc", "application/msword");
        put("ogg", "application/x-ogg");
        put("zip", "application/octet-stream");
        put("exe", "application/octet-stream");
        put("class", "application/octet-stream");
    }};
    
    private static MyHTTPServer instance;
    
    public static MyHTTPServer getInstance(int port, Context aCtx) {
    	if (instance == null)
			try {
				instance = new MyHTTPServer(port, aCtx);
			} catch (IOException e) {
				e.printStackTrace();
			}
    	
    	return instance;
    }
	
	public MyHTTPServer(int port, Context aCtx) throws IOException {
		super(port);
		ctx = new WeakReference<Context>(aCtx);
		InetAddress ip = NetworkUtils.getLocalIpAddress();
		if (ip == null) {
			ALog.d(TAG, "No IP found. Please connect to a newwork and try again");

			throw(new IOException());
		} else {
			ALog.d(TAG, "Launched server at http://" + ip.toString() + ":" + port);
		}
	}

	public Response serve(String uri, String method, Properties header, Properties parms,
			Properties files) {
		Response res = null;
		
		Log.d(TAG, "" + method + " '" + uri + " " + /* header + */ " " + parms);

		String escapedCode = parms.getProperty("code");
		String unescapedCode = StringEscapeUtils.unescapeEcmaScript(escapedCode);
		Log.d("HTTP Code", "" + escapedCode); 
		Log.d("HTTP Code", "" + unescapedCode); 
		
		// Clean up uri
		uri = uri.trim().replace(File.separatorChar, '/');
		if (uri.indexOf('?') >= 0)
			uri = uri.substring(0, uri.indexOf('?'));
		
		// We never want to request just the '/'
		if (uri.length() == 1)
			uri = "index.html";
		
		// We're using assets, so we can't have a leading '/'
		if (uri.charAt(0) == '/')
			uri = uri.substring(1, uri.length());
		
		// have the object build the directory structure, if needed.
		AssetManager am = ctx.get().getAssets();
		try {
			InputStream fi = (InputStream) am.open(uri);
			
            // Get MIME type from file name extension, if possible
            String mime = null;
            int dot = uri.lastIndexOf('.');
            if (dot >= 0)
                mime = MIME_TYPES.get(uri.substring(dot + 1).toLowerCase());
            if (mime == null)
                mime = NanoHTTPD.MIME_DEFAULT_BINARY;
            
			res = new Response(HTTP_OK, mime, fi);
		} catch (IOException e) {
			e.printStackTrace();
			ALog.d(TAG, e.getStackTrace().toString());
			res = new Response(HTTP_INTERNALERROR, "text/html", "ERROR: " + e.getMessage());
		}
//		return serveFile(uri, header, servingFolder, true);
		return res;
	}
	
	
}