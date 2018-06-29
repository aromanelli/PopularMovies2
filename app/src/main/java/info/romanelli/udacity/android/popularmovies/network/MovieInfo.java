package info.romanelli.udacity.android.popularmovies.network;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

@SuppressWarnings({"unused", "WeakerAccess"})
public class MovieInfo implements Parcelable {

    final static private String TAG = MovieInfo.class.getSimpleName();

    @SerializedName("vote_count")
    private int voteCount;

    @SerializedName("id")
    private int id;

    @SerializedName("vote_average")
    private float voteAverage;

    @SerializedName("title")
    private String title;

    @SerializedName("popularity")
    private float popularity;

    @SerializedName("poster_path")
    private String posterURL;

    @SerializedName("overview")
    private String overview;

    @SerializedName("release_date")
    private Date releaseDate;

    @SuppressWarnings("WeakerAccess")
    protected MovieInfo(Parcel in) {
        voteCount = in.readInt();
        id = in.readInt();
        voteAverage = in.readFloat();
        title = in.readString();
        popularity = in.readFloat();
        posterURL = in.readString();
        overview = in.readString();
        releaseDate = new Date(in.readLong());
    }

    // Used by DetailActivity after it makes calls to fetch the data ...
    private ArrayList<MovieVideosInfo> listMovieVideosInfo;
    private ArrayList<MovieReviewsInfo> listMovieReviewsInfo;

    public ArrayList<MovieVideosInfo> getMovieVideosInfo() {
        return listMovieVideosInfo;
    }

    public void setMovieVideosInfo(ArrayList<MovieVideosInfo> listMovieVideosInfo) {
        this.listMovieVideosInfo = listMovieVideosInfo;
    }

    public ArrayList<MovieReviewsInfo> getMovieReviewsInfo() {
        return listMovieReviewsInfo;
    }

    public void setMovieReviewsInfo(ArrayList<MovieReviewsInfo> listMovieReviewsInfo) {
        this.listMovieReviewsInfo = listMovieReviewsInfo;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(float voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public float getPopularity() {
        return popularity;
    }

    public void setPopularity(float popularity) {
        this.popularity = popularity;
    }

    public String getPosterURL() {
        return posterURL;
    }

    public void setPosterURL(String posterURL) {
        this.posterURL = posterURL;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    /////////////////////

    public String getReleaseDateYearText() {
        Log.d(TAG, "getReleaseDateYear() called: ["+ getReleaseDate() +"]");
        Calendar cal = Calendar.getInstance();
        cal.setTime(getReleaseDate());
        return String.valueOf(cal.get(Calendar.YEAR));
    }

    public String getVoteAverageText() {
        return String.format(Locale.getDefault(), "%s / 10", getVoteAverage());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovieInfo movieInfo = (MovieInfo) o;
        return id == movieInfo.id;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "MovieInfo{" +
                "voteCount=" + voteCount +
                ", id=" + id +
                ", voteAverage=" + voteAverage +
                ", title='" + title + '\'' +
                ", popularity=" + popularity +
                ", posterURL='" + posterURL + '\'' +
                ", overview='" + overview + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                '}';
    }

    public static final Creator<MovieInfo> CREATOR = new Creator<MovieInfo>() {
        @Override
        public MovieInfo createFromParcel(Parcel in) {
            return new MovieInfo(in);
        }

        @Override
        public MovieInfo[] newArray(int size) {
            return new MovieInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(voteCount);
        dest.writeInt(id);
        dest.writeFloat(voteAverage);
        dest.writeString(title);
        dest.writeFloat(popularity);
        dest.writeString(posterURL);
        dest.writeString(overview);
        dest.writeLong(releaseDate.getTime());
    }

}
