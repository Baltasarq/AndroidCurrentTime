# AndroidCurrentTime
Fetches periodically the current time

This simple Android app just uses a REST service in order to get the time from geonames.org, and displays it. Its main features are that:
- It uses a timer in order to fetch the time for every twenty seconds.
- It uses an AsyncTask<> in order to do the connection work in the background.
- It needs the INTERNET and ACCESS_NETWORK_STATE permissions.
