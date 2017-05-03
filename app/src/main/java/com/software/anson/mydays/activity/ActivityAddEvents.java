package com.software.anson.mydays.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.backendless.Backendless;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.software.anson.mydays.utils.PermissionUtils;
import com.software.anson.mydays.R;
import com.software.anson.mydays.db.Db;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Anson on 2017/2/18.
 */

/**
 * Picker Code from https://github.com/googlesamples/android-play-places/blob/master/PlacePicker/Application/src/main/java/com/example/google/playservices/placepicker/MainActivity.java
 */
public class ActivityAddEvents extends AppCompatActivity implements
        GoogleMap.OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private Db db;
    private SQLiteDatabase dbwriter;
    private ContentValues cv;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private GoogleApiClient mGoogleApiClient;

    @BindView(R.id.date)
    TextView tv_date;
    @BindView(R.id.time)
    TextView tv_time;
    @BindView(R.id.et_note)
    EditText et_note;
    @BindView(R.id.et_address)
    EditText et_address;
    @BindView(R.id.tv_lat)
    TextView tv_lat;
    @BindView(R.id.tv_lng)
    TextView tv_lng;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionDenied = false;
    private GoogleMap mMap;
    private static final int PLACE_PICKER_REQUEST = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        ButterKnife.bind(this);
        Backendless.initApp(this, "816605BB-C858-510E-FF58-19AAD0ADFE00", "77B405BF-FCE5-31B9-FF2D-1B0F4737DC00", "v1");

        //Access to database
        db = new Db(this);
        dbwriter = db.getWritableDatabase();

        //Init map
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);

        if (mGoogleApiClient == null) {// if the client is null, give a new client
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        //Set the picker button on map
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadPlacePicker();
            }
        });

    }

    //Place picker
    private void loadPlacePicker() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(ActivityAddEvents.this), PLACE_PICKER_REQUEST);
        } catch(GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    //Callback the selected place information
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                String addressText = place.getName().toString();
                addressText += "\n" + place.getAddress().toString();

                //Display the address
                placeMarkerOnMap(place.getLatLng());
                et_address.setText(addressText);

                //Display the latlng on UI
                tv_lat.setText(String.valueOf(place.getLatLng().latitude));
                tv_lng.setText(String.valueOf(place.getLatLng().longitude));
            }
        }
    }

    protected  void placeMarkerOnMap(LatLng location) {
        MarkerOptions markerOptions = new MarkerOptions().position(location);

        String titleStr = getAddress(location);  // add these two lines
        markerOptions.title(titleStr);

        mMap.addMarker(markerOptions);
    }

    //Get the picked Address
    private String getAddress( LatLng latLng ) {

        Geocoder geocoder = new Geocoder( this );
        String addressText = "";
        List<Address> addresses = null;
        Address address = null;
        try {
            addresses = geocoder.getFromLocation( latLng.latitude, latLng.longitude, 1 );
            if (null != addresses && !addresses.isEmpty()) {
                address = addresses.get(0);
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    addressText += (i == 0)?address.getAddressLine(i):("\n" + address.getAddressLine(i));
                }
            }
        } catch (IOException e ) {
        }
        return addressText;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if( mGoogleApiClient != null && mGoogleApiClient.isConnected() ) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation();

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (myLocation == null) {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            //String provider = locationManager.getBestProvider(criteria, true);
            String provider = locationManager.NETWORK_PROVIDER;
            myLocation = locationManager.getLastKnownLocation(provider);
        }

        LatLng myLatLng = new LatLng(myLocation.getLatitude(),myLocation.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 18));

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnMarkerClickListener(this);
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }


    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    @OnClick(R.id.btn_clear)
    void clearOnClick () {
        et_address.setText("");
    }

    @OnClick(R.id.back)
    void backOnClick (){
        Intent intent = new Intent();
        intent.setClass(ActivityAddEvents.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.push_right_out, R.anim.push_right_in);
        finish();
    }

    @OnClick(R.id.layout_time)
    void timeOnClick (View view) {
            final View viewDialog = (View) getLayoutInflater().inflate(R.layout.time_picker_dialog, null);

            // set up date
            new AlertDialog.Builder(ActivityAddEvents.this)
                    .setTitle("Time")
                    .setView(viewDialog)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            timePicker = (TimePicker) viewDialog.findViewById(R.id.timePicker);
                            int hour = timePicker.getCurrentHour();
                            int minute = timePicker.getCurrentMinute();
                            if (minute == 0 || minute <10){
                                String minute_final = "0" + minute;
                                tv_time.setText(hour+" : "+minute_final);
                            }else {
                                tv_time.setText(hour+" : "+minute);
                            }

                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();//turn off dialog
                }
            }).show();
    }

    @OnClick(R.id.layout_date)
    void dateOnClick(View view) {
            final View viewDialog = (View) getLayoutInflater().inflate(R.layout.date_picker_dialog, null);

            // set up date
            new AlertDialog.Builder(ActivityAddEvents.this)
                    .setTitle("Date")
                    .setView(viewDialog)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            datePicker = (DatePicker) viewDialog.findViewById(R.id.datePicker);
                            int day = datePicker.getDayOfMonth();
                            int month = datePicker.getMonth()+1;
                            int year = datePicker.getYear();

                            tv_date.setText(day+"/"+month+"/"+year);
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();//turn off dialog
                }
            }).show();
    }

    @OnClick(R.id.btn_add_note)
    void tv1OnClick(View view) {
        Log.d("AddEvent", "Button clicked");
        if (TextUtils.isEmpty(et_note.getText().toString()) || TextUtils.isEmpty(et_address.getText().toString())) {
            Toast.makeText(ActivityAddEvents.this, "Please fill in your content.", Toast.LENGTH_SHORT).show();
        } else {
            String lat = tv_lat.getText().toString();
            String lng = tv_lng.getText().toString();
            String date = tv_date.getText().toString();
            String time = tv_time.getText().toString();
            String note = et_note.getText().toString();
            String address = et_address.getText().toString();

            cv = new ContentValues();
            cv.put("date", date);
            cv.put("time", time);
            cv.put("note", note);
            cv.put("address", address);
            cv.put("lat", lat);
            cv.put("lng", lng);

            dbwriter.insert("event", null, cv);
            dbwriter.close();

//                Event newEvent = new Event();
//                newEvent.setAddress(address);
//                newEvent.setDay(day);
//                newEvent.setHour(hour);
//                newEvent.setMinute(minutes);
//                newEvent.setMonth(month);
//                newEvent.setNote(note);
//                newEvent.setYear(year);
//
//                Backendless.Persistence.save(newEvent, new AsyncCallback<Event>() {
//
//                    @Override
//                    public void handleResponse(Event event) {
//                        Log.d("AddEvent", "Saved event");
//                        Toast.makeText(ActivityAddEvents.this, "save event", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void handleFault(BackendlessFault e) {
//                        Log.d("AddEvent", "Error: " + e.getMessage());
//                        Toast.makeText(ActivityAddEvents.this,e.getMessage(), Toast.LENGTH_LONG).show();
//                    }
//
//                });
            Intent intent = new Intent();
            intent.setClass(ActivityAddEvents.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.push_right_out, R.anim.push_right_in);
            finish();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


}
