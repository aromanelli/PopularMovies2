package info.romanelli.udacity.android.popularmovies.network;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

@SuppressWarnings("unused")
public class MovieReviewsInfo implements Parcelable {

    final static private String TAG = MovieReviewsInfo.class.getSimpleName();

    @SerializedName("author")
    private String author;

    @SerializedName("content")
    private String content;

    @SerializedName("id")
    private String id;

    @SerializedName("url")
    private String url;

    @SuppressWarnings("WeakerAccess")
    protected MovieReviewsInfo(Parcel in) {
        author = in.readString();
        content = in.readString();
        id = in.readString();
        url = in.readString();
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovieReviewsInfo that = (MovieReviewsInfo) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "MovieReviewsInfo{" +
                "author='" + author + '\'' +
                ", content='" + content + '\'' +
                ", id='" + id + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    public static final Creator<MovieReviewsInfo> CREATOR = new Creator<MovieReviewsInfo>() {
        @Override
        public MovieReviewsInfo createFromParcel(Parcel in) {
            return new MovieReviewsInfo(in);
        }

        @Override
        public MovieReviewsInfo[] newArray(int size) {
            return new MovieReviewsInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(author);
        dest.writeString(content);
        dest.writeString(id);
        dest.writeString(url);
    }
}
