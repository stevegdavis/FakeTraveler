package cl.coders.faketraveler;


import android.content.Context;
import android.os.AsyncTask;

import java.io.*;
import java.util.*;

public class GPSCoordinatesGPXFileManager implements GPSCoordinatesParseAsyncResponse {

    private static final int REQUEST_PARSE_POINTS = 1;
    private Context mContext;
    private ArrayList<GPSCoordinates> mList;
    private InputStream mInputStream;
    @Override
    public void processFinish(int requestCode, Object o) {

    }
    // Methods
    public void parse(ArrayList<GPSCoordinates> list, InputStream ips, Context context)
    {
        mList = list;
        mInputStream = ips;
        this.mContext = context;
        //this.mDbxClient = dbxClient;
        Object mReadDialog;
        new ParsePointsTask().setListener(this, REQUEST_PARSE_POINTS).execute();
    }
    public class ParsePointsTask extends AsyncTask<Void, Integer, Integer>
    {
        GPSCoordinatesParseAsyncResponse delegate = null;
        int requestCode = 0;

        public ParsePointsTask setListener(GPSCoordinatesParseAsyncResponse parseAsyncResponse, int requestCode){
            this.delegate = parseAsyncResponse;
            this.requestCode = requestCode;
            //this.dialog = dialog;
            return this;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Displays the progress bar for the first time. Not used
//            CGPSService.mNotificationBuilder.setProgress(100, 0, false);
//            CGPSService.mNotificationBuilder.setSmallIcon(R.drawable.upload);
//            CGPSService.mNotificationManager.notify(MainActivity.mNotifiyId, CGPSService.mNotificationBuilder.build());
//            CGPSService.mNotificationBuilder.setContentText("Uploading to DropBox");
        }

        protected Integer doInBackground(Void... voids)
        {
            InputStream in;
            String line = null;
            mList.clear();
            if (mInputStream != null) {
                int Idx = 0;
                BufferedReader buffReader = new BufferedReader(new InputStreamReader(mInputStream));
                for (; ; ) {
                    try {
                        line = buffReader.readLine();
                        if (line == null)
                            break;
                        if (line.startsWith("<wpt") || line.startsWith("<rtept")) {
                            GPSCoordinates data = parseWayPoint(line);
                            if (data != null)
                                mList.add(data);
                        } else if (line.contains("<wpt") || line.contains("<rtept")) {
                            parseWayPoints(line);
                        }

                        if (line.startsWith("<ele>") && MainActivity.list.size() > 0 && Idx < MainActivity.list.size()) {
                            GPSCoordinates tmp = MainActivity.list.get(Idx);
                            tmp.Altitude = parseEle(line);
                            mList.set(Idx++, tmp);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        protected void onProgressUpdate(Integer... values) {
            // Update progress
            //CGPSService.mNotificationBuilder.setProgress(100, values[0], false);
            //CGPSService.mNotificationManager.notify(MainActivity.mNotifiyId, CGPSService.mNotificationBuilder.build());
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
//			CGPSService.mNotificationBuilder.setContentText("Upload complete");
//			// Removes the progress bar
//			CGPSService.mNotificationBuilder.setProgress(0, 0, false);
//			CGPSService.mNotificationManager.notify(CGPSService.mNotifiyId, CGPSService.mNotificationBuilder.build());
//            CGPSService.mNotificationManager.cancel(MainActivity.mNotifiyId);
        }
    }
    private String parseEle(String line) {
        //eg. <ele>125.45325353527</ele>
        String Altitude = "0";
        int IdxEle = line.indexOf("<ele>");
        if (IdxEle > -1) {
            int IdxEleEnd = line.indexOf("</ele>");
            if (IdxEleEnd > -1) {
                Altitude = line.substring(IdxEle + "<ele>".length(), IdxEleEnd);
            }
        }
        return Altitude;
    }

    private GPSCoordinates parseWayPoint(String line) {
        GPSCoordinates loc = new GPSCoordinates();
        // <wpt lat="51.74746121" lon="-2.25416582"><ele>185.6999969482422</ele><desc>Point #1 Logged at:27/03/2014 11:04:26 Journey time to here: 0 Days, 0 Hours, 0 Mins</desc></wpt>

        int IdxLat = line.indexOf("lat=");    // lat="
        if (IdxLat > -1) {
            int IdxLon = line.indexOf("lon=");
            if (IdxLon > -1) {
                loc.Latitude = line.substring((IdxLat + ("lat=").length() + 1), IdxLon - 2);
                int IdxLon2 = line.substring(IdxLon).indexOf('>');
                if (IdxLon2 > -1) {
                    loc.Longitude = line.substring((IdxLon + ("lon=").length() + 1), IdxLon + (IdxLon2 - 1));
                }
            }

        }
        loc.Accuracy = "0";//TODO ?
        return loc;
    }

    private void parseWayPoints(String line) {
        // On the same line
        String[] points = line.split("</wpt>");
        for (String s : points) {
            if (s.toString().contains("<wpt")) {
                GPSCoordinates loc = new GPSCoordinates();
                int IdxLat = line.indexOf("lat=");    // lat="
                if (IdxLat > -1) {
                    int IdxLon = line.indexOf("lon=");
                    if (IdxLon > -1) {
                        loc.Latitude = line.substring((IdxLat + ("lat=").length() + 1), IdxLon - 2);
                        int IdxLon2 = line.substring(IdxLon).indexOf('>');
                        if (IdxLon2 > -1) {
                            loc.Longitude = line.substring((IdxLon + ("lon=").length() + 1), IdxLon + (IdxLon2 - 1));
                        }
                    }

                }
                loc.Accuracy = "0";//TODO ?
                mList.add(loc);
            }
        }
    }
}

