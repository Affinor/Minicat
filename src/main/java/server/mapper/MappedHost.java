package server.mapper;

import lombok.Data;

import java.util.List;

/**
 * @author wangjin
 */
@Data
public class MappedHost {

    public static final String DEFAULTHOST = "DEFAULTHOST";

    private volatile List<MappedWrapper> contextList;

}
