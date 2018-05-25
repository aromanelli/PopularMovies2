The app requires an API key from the 'themoviedb.org' web site, to run properly.

Once a key has been obtained, add it to the .gradle directory's gradle.properties file that exists off of the USER/home directory (NOT the gradle.properties files that exists inside of the AndroidStudio project!) ...

    Windows: C:\Users\<Your Username>\.gradle
    Mac: /Users/<Your Username>/.gradle
    Linux: /home/<Your Username>/.gradle

If the file does not exist, create it.

The entry in the gradle.properties file should look like this ...

    ApiKey_TheMovieDB="YOUR-API-KEY-VALUE-GOES-HERE"

Without specifying the api key value, the app will not work!

See ...

    https://medium.com/code-better/hiding-api-keys-from-your-android-repository-b23f5598b906
    https://stackoverflow.com/a/34021467/435519   (option #2)

...  for the technical details of why this needs to be done.