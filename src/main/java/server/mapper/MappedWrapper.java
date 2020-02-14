package server.mapper;

import lombok.Data;
import server.Servlet;

/**
 * @author wangjin
 */
@Data
public class MappedWrapper {
    private String name;
    private Servlet object;
}
