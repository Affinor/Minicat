package server;

import server.mapper.MappedHost;
import server.mapper.MappedWrapper;

import java.io.InputStream;
import java.net.Socket;
import java.util.Map;
import java.util.Optional;

public class RequestProcessor extends Thread {

    private Socket socket;
    private Map<String,HttpServlet> servletMap;
    private Map<String, MappedHost> mapperHostContext;
    public RequestProcessor(Socket socket, Map<String, HttpServlet> servletMap,Map<String, MappedHost> mapperHostContext) {
        this.socket = socket;
        this.servletMap = servletMap;
        this.mapperHostContext = mapperHostContext;
    }

    @Override
    public void run() {
        try{
            InputStream inputStream = socket.getInputStream();

            // 封装Request对象和Response对象
            Request request = new Request(inputStream);
            Response response = new Response(socket.getOutputStream());

            // 静态资源处理   V3.0处理方案
            /*if(servletMap.get(request.getUrl()) == null) {
                response.outputHtml(request.getUrl());
            }else{
                // 动态资源servlet请求
                HttpServlet httpServlet = servletMap.get(request.getUrl());
                httpServlet.service(request,response);
            }*/
            // V4.0静态资源处理
            String url = request.getUrl();
            String[] split = url.split("/",-1);
            //请求路径中根路径“/”后第一个文件描述符为项目名hostName
            MappedHost mappedHost = mapperHostContext.get(split[1]);
            //如果找不到，就从默认的host中找
            if (null == mappedHost){
                mappedHost = mapperHostContext.get(MappedHost.DEFAULTHOST);
            }
            //如果还找不到，就返回404
            if (null == mappedHost){
                response.outputHtml(url);
            }else {
                //找到对应的servlet
                Optional<MappedWrapper> first = mappedHost.getContextList()
                        .stream()
                        .filter(mappedWrapper -> mappedWrapper.getName().equalsIgnoreCase(url))
                        .findFirst();
                //如果还没找不到，就返回404
                if (!first.isPresent()){
                    response.outputHtml(url);
                }else {
                    //返回响应
                    first.get().getObject().service(request,response);
                }
            }

            socket.close();

        }catch (Exception e) {
            e.printStackTrace();
        }

    }
}
