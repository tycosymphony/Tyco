package com.poc.employeeapp;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapWaypoints extends FragmentActivity implements
        OnMapReadyCallback, LocationListener, GoogleMap.OnMarkerClickListener, LocationSource {

    private static final long MINIMUM_DISTANCECHANGE_FOR_UPDATE = 10; // in
    // Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATE = 1000; // in
    // Milliseconds

    GoogleMap map;
    ArrayList<LatLng> markerPoints;
    Button btnDraw;
    ArrayList<LatLng> mBoundPoints = null;
    private boolean isOnPath;

    // Temporary latlng values for routing the map.
    private static final LatLng WHITEFIELD_VAIDEHI_HOSPITAL = new LatLng(
            12.976349, 77.726944);
    private static final LatLng SILK_BOARD = new LatLng(12.917746, 77.623788);

    private static final LatLng WAYPOINT_KADUBEESANAHALLI = new LatLng(12.939414, 77.695203);
    private OnLocationChangedListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_map);

        // Initializing
        markerPoints = new ArrayList<LatLng>();

        // Getting reference to SupportMapFragment of the activity_main
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        // Getting Map for the SupportMapFragment

        fm.getMapAsync(this);

    }


    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + ","
                + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=true";

        // Defining Units
        String units = "units=metric";

        // Definig mode
        String mode = "mode=driving";

        // Defining Alternatives
        String alternative = "alternatives=true";

        // Waypoints
        String waypoints = "";
        for (int i = 2; i < markerPoints.size(); i++) {
            LatLng point = (LatLng) markerPoints.get(i);
            if (i == 2)
                waypoints = "waypoints=";
            waypoints += point.latitude + "," + point.longitude + "|";
        }

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&"
                + waypoints + "&" + units + "&" + mode + "&" + alternative;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + parameters;

        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception while downloading url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
    }

    @Override
    public void deactivate() {
        mListener = null;
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends
            AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                WayPointsDirectionsJSONParser parser = new WayPointsDirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                mBoundPoints = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    mBoundPoints.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(mBoundPoints);
                lineOptions.width(5);
                lineOptions.color(Color.BLUE);
            }

            // Drawing polyline in the Google Map for the i-th route
            map.addPolyline(lineOptions);
        }
    }

    @Override
    public void onMapReady(GoogleMap gmap) {
        map = gmap;
        // Enable MyLocation Button in the Map
        map.setMyLocationEnabled(true);
        map.setOnMarkerClickListener(this);
        map.setLocationSource(this);
        // Setting onclick event listener for the map

        map.moveCamera(CameraUpdateFactory.newLatLng(WAYPOINT_KADUBEESANAHALLI));
        map.animateCamera(CameraUpdateFactory.zoomTo(12));
        addMarkerForLatLng(WHITEFIELD_VAIDEHI_HOSPITAL);
        addMarkerForLatLng(SILK_BOARD);
        addMarkerForLatLng(WAYPOINT_KADUBEESANAHALLI);
        drawRoute();
        /*map.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public void onMapClick(LatLng point) {

				// Already 10 locations with 8 waypoints and 1 start location
				// and 1 end location.
				// Upto 8 waypoints are allowed in a query for non-business
				// users
				if (markerPoints.size() >= 10) {
					return;
				}

				// Adding new item to the ArrayList
				markerPoints.add(point);

				// Creating MarkerOptions
				MarkerOptions options = new MarkerOptions();

				// Setting the position of the marker
				options.position(point);

				*//**
         * For the start location, the color of marker is GREEN and for
         * the end location, the color of marker is RED and for the rest
         * of markers, the color is AZURE
         *//*
                if (markerPoints.size() == 1) {
					options.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
				} else if (markerPoints.size() == 2) {
					options.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_RED));
				} else {
					options.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
				}

				// Add new marker to the Google Map Android API V2
				map.addMarker(options);

			}
		});*/

        // The map will be cleared on long click
        /*map.setOnMapLongClickListener(new OnMapLongClickListener() {

            @Override
            public void onMapLongClick(LatLng point) {
                // Removes all the points from Google Map
                map.clear();

                // Removes all the points in the ArrayList
                markerPoints.clear();

            }
        });*/

        // Click event handler for Button btn_draw
        /*btnDraw.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Checks, whether start and end locations are captured
				if (markerPoints.size() >= 2) {
					LatLng origin = markerPoints.get(0);
					LatLng dest = markerPoints.get(1);

					// Getting URL to the Google Directions API
					String url = getDirectionsUrl(origin, dest);

					DownloadTask downloadTask = new DownloadTask();

					// Start downloading json data from Google Directions API
					downloadTask.execute(url);
					LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
					locationManager.requestLocationUpdates(
							LocationManager.GPS_PROVIDER,
							MINIMUM_TIME_BETWEEN_UPDATE,
							MINIMUM_DISTANCECHANGE_FOR_UPDATE,
							MapWaypoints.this);
				}

			}
		});*/
    }

    private void drawRoute() {
        // Checks, whether start and end locations are captured
        if (markerPoints.size() >= 2) {
            LatLng origin = markerPoints.get(0);
            LatLng dest = markerPoints.get(1);

            // Getting URL to the Google Directions API
            String url = getDirectionsUrl(origin, dest);

            DownloadTask downloadTask = new DownloadTask();

            // Start downloading json data from Google Directions API
            downloadTask.execute(url);
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    MINIMUM_TIME_BETWEEN_UPDATE,
                    MINIMUM_DISTANCECHANGE_FOR_UPDATE,
                    MapWaypoints.this);
        }
    }

    private void addMarkerForLatLng(LatLng point) {
        // Already 10 locations with 8 waypoints and 1 start location
        // and 1 end location.
        // Upto 8 waypoints are allowed in a query for non-business
        // users
        if (markerPoints.size() >= 10) {
            return;
        }
        // Adding new item to the ArrayList
        markerPoints.add(point);

        // Creating MarkerOptions
        MarkerOptions options = new MarkerOptions();
        // Setting the position of the marker
        options.position(point);

        /**
         * For the start location, the color of marker is GREEN and for
         * the end location, the color of marker is RED and for the rest
         * of markers, the color is AZURE
         */
        if (markerPoints.size() == 1) {
            options.icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            options.position(point);
        } else if (markerPoints.size() == 2) {
            options.icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_RED));
            options.position(point);
        } else {
            options.icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            options.position(point).title("Pick Up Time: 7:00 AM");
        }

        // Add new marker to the Google Map Android API V2
        Marker marker = map.addMarker(options);
        marker.showInfoWindow();
//        marker.setVisible(false);
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng instanceLatLng = new LatLng(location.getLatitude(),
                location.getLongitude());
        ArrayList<LatLng> routeLatLngBoundValues = WayPointsDirectionsJSONParser
                .getLatlngBoundValues();
        //To focus to the source Location.
        if( mListener != null )
        {
            mListener.onLocationChanged( location );

            LatLngBounds bounds = this.map.getProjection().getVisibleRegion().latLngBounds;

            if(!bounds.contains(new LatLng(location.getLatitude(), location.getLongitude())))
            {
                //Move the camera to the user's location if they are off-screen!
                map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
            }
        }
    }

    @Override
    public void onProviderDisabled(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        // TODO Auto-generated method stub

    }
}