package com.software.anson.mydays.fragment;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.software.anson.mydays.R;
import com.software.anson.mydays.db.Db;

/**
 * Created by Anson on 2017/2/11.
 * Map code from https://github.com/googlemaps/android-samples/tree/master/ApiDemos/app/src/main/java/com/example/mapdemo
 */
public class FragmentMap extends Fragment implements
        OnMyLocationButtonClickListener,
        OnMapReadyCallback{

    private static final String TAG = "FragmentMap";

    private GoogleMap mMap;
    private Db db;
    private SQLiteDatabase dbreader;
    private LinearLayout linearLayout_menu;
    private String[] menu = {"Hybrid", "Satellite", "Terrain", "None", "Normal"};

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        //Initial map
        final SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Set menu on toolbar
        linearLayout_menu = (LinearLayout) v.findViewById(R.id.menu);
        linearLayout_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final View popupView = getActivity().getLayoutInflater().inflate(R.layout.menu_list, null);

                ListView list = (ListView) popupView.findViewById(R.id.menu_list);
                list.setAdapter(new ArrayAdapter<String>(getActivity(),
                                android.R.layout.simple_list_item_1, menu));

                //Configure the menu UI
                final PopupWindow window = new PopupWindow(popupView, 300, 670);
                window.setAnimationStyle(R.style.popup_window_anim);
                window.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F8F8F8")));
                window.setFocusable(true);
                window.setOutsideTouchable(true);
                window.update();
                window.showAsDropDown(linearLayout_menu, 0, 0);

                //Set the menu
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        switch (i) {
                            case 0:
                                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                                window.dismiss();
                                break;
                            case 1:
                                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                                window.dismiss();
                                break;
                            case 2:
                                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                                window.dismiss();
                                break;
                            case 3:
                                mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                                window.dismiss();
                                break;
                            case 4:
                                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                                window.dismiss();
                                break;
                        }
                    }
                });
            }
        });

        return v;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setMyLocationEnabled(true);

        //Animate to my location

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
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


        //Add address marks of the Events
        onAddMarks();
    }

    // Add markers according to the location from the event storage
    public void onAddMarks(){
        LatLng hive = new LatLng(52.193744, -2.225949);
       // LatLng city_campus = new LatLng(52.195655, -2.225926);

        db = new Db(getActivity());
        dbreader = db.getReadableDatabase();
        Cursor c =dbreader.query("event", new String[]{"address", "note", "lat", "lng"}, null, null, null, null, null);
        while (c.moveToNext()){
            String address = c.getString(0);
            String note = c.getString(1);
            String latstr = c.getString(2);
            String lngstr = c.getString(3);
            Double lat = Double.parseDouble(latstr);
            Double lng = Double.parseDouble(lngstr);

            LatLng latlng = new LatLng(lat,lng);
            mMap.addMarker(new MarkerOptions()
                    .title(address)
                    .snippet(note)
                    .position(latlng));
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }



}