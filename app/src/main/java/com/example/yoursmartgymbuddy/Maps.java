package com.example.yoursmartgymbuddy;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;

public class Maps extends AppCompatActivity {

    private MapView mapView;
    private MyLocationNewOverlay myLocationOverlay;
    private ArrayList<GeoPoint> pathPoints = new ArrayList<>();
    private boolean isTracking = false;
    private float totalDistance = 0;

    private Chronometer chronometer;
    private TextView distanceTextView;
    private Button startStopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().load(getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        setContentView(R.layout.activity_maps);

        // Initialize UI
        chronometer = findViewById(R.id.chronometer);
        distanceTextView = findViewById(R.id.distanceTextView);
        startStopButton = findViewById(R.id.startStopButton);
        startStopButton.setOnClickListener(v -> toggleTracking());

        // Setup MapView
        mapView = findViewById(R.id.map);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), mapView);
        myLocationOverlay.enableMyLocation();

        myLocationOverlay.runOnFirstFix(() -> {
            runOnUiThread(() -> {
                IMapController mapController = mapView.getController();
                mapController.setZoom(18.0);
                mapController.setCenter(myLocationOverlay.getMyLocation());
            });
        });

        mapView.getOverlays().add(myLocationOverlay);
    }

    private void toggleTracking() {
        if (isTracking) stopTracking();
        else startTracking();
    }

    private void startTracking() {
        isTracking = true;
        startStopButton.setText("Stop");
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
        totalDistance = 0;
        pathPoints.clear();
    }

    private void stopTracking() {
        isTracking = false;
        startStopButton.setText("Start");
        chronometer.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        myLocationOverlay.enableMyLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        myLocationOverlay.disableMyLocation();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        mapView.getOverlays().add(myLocationOverlay);
        updateTracking();
    }

    private void updateTracking() {
        mapView.postDelayed(() -> {
            if (isTracking && myLocationOverlay.getMyLocation() != null) {
                GeoPoint currentLocation = myLocationOverlay.getMyLocation();
                if (!pathPoints.isEmpty()) {
                    GeoPoint lastPoint = pathPoints.get(pathPoints.size() - 1);
                    totalDistance += lastPoint.distanceToAsDouble(currentLocation) / 1000.0; // Distance in KM
                }
                pathPoints.add(currentLocation);
                drawPath();
                distanceTextView.setText(String.format("%.2f km", totalDistance));
            }
            updateTracking(); // Recursively call for real-time tracking
        }, 1000); // Update every 1 second
    }

    private void drawPath() {
        Polyline polyline = new Polyline();
        polyline.setPoints(pathPoints);
        mapView.getOverlays().clear();
        mapView.getOverlays().add(myLocationOverlay);
        mapView.getOverlays().add(polyline);
        mapView.invalidate();
    }
}
