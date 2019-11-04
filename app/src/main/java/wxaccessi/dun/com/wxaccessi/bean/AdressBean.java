package wxaccessi.dun.com.wxaccessi.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 01
 *
 * @time 2017/2/15. 16:16.
 * @des ${TODO}
 */
public class AdressBean implements Parcelable{
    private String adress;
    private String lat;
    private String log;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public AdressBean( String lat, String log) {
        this.adress = "";
        this.lat = lat;
        this.log = log;
        this.id = "";
    }

    public AdressBean(String adress, String lat, String log, String id) {
        this.adress = adress;
        this.lat = lat;
        this.log = log;
        this.id = id;
    }

    public static final Creator<AdressBean> CREATOR = new Creator<AdressBean>() {
        @Override
        public AdressBean createFromParcel(Parcel source) {
            AdressBean mMapBean = new AdressBean(source.readString(),source.readString(), source.readString(),source.readString());
            return mMapBean;
        }
        @Override
        public AdressBean[] newArray(int size) {
            return new AdressBean[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(adress);
        parcel.writeString(lat);
        parcel.writeString(log);
        parcel.writeString(id);
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
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
}
