package wxaccessi.dun.com.wxaccessi.bean;

/**
 * Created by 01
 *
 * @time 2017/2/17. 16:13.
 * @des ${TODO}
 */
public class MapBean  {
    private String lat;
    private String log;
    private String id;


    public MapBean(String lat, String log, String id) {
        this.lat = lat;
        this.log = log;
        this.id = id;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
