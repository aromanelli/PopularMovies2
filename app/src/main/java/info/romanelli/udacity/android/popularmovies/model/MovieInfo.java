package info.romanelli.udacity.android.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class MovieInfo implements Parcelable {

    final static private String TAG = MovieInfo.class.getSimpleName();

    private String title;
    private String posterURL;
    private String overview;
    private String voteAverage;
    private String releaseDate;

    public MovieInfo(
            final String title,
            final String posterURL,
            final String overview,
            final String voteAverage,
            final String releaseDate) {

        this.title = title;
        this.posterURL = posterURL;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
    }

    @SuppressWarnings("WeakerAccess")
    public MovieInfo(Parcel in) {
        title = in.readString();
        posterURL = in.readString();
        overview = in.readString();
        voteAverage = in.readString();
        releaseDate = in.readString();
    }

//    // https://developers.themoviedb.org/3/movies/get-popular-movies does not specify
//    // what time zone the release date is based on, so we just convert it to Locale.US ...
//    final private SimpleDateFormat dateConverter =
//            new SimpleDateFormat("yyyy-MM-dd", Locale.US);
//    public Date getDateOfRelease() {
//        try {
//            return dateConverter.parse(releaseDate);
//        } catch (ParseException pe) {
//            Log.e(TAG, "getDateOfRelease: Unable to parse text into a Date!", pe);
//            return null;
//        }
//    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    /**
     * <p>Equality is based on the title and release date of the movie.</p>
     * @param o
     * @return {@code true} is equals, {@code false} if not.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovieInfo movieInfo = (MovieInfo) o;
        return Objects.equals(title, movieInfo.title) &&
                Objects.equals(releaseDate, movieInfo.releaseDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, releaseDate);
    }

    @Override
    public String toString() {
        return "MovieInfo{" +
                "title='" + title + '\'' +
                ", posterURL='" + posterURL + '\'' +
                ", overview='" + overview + '\'' +
                ", voteAverage='" + voteAverage + '\'' +
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
        dest.writeString(title);
        dest.writeString(posterURL);
        dest.writeString(overview);
        dest.writeString(voteAverage);
        dest.writeString(releaseDate);
    }
}
