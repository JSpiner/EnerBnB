package net.jspiner.enerbnb.Activity;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import net.jspiner.enerbnb.R;

/**
 * Copyright 2016 JSpiner. All rights reserved.
 *
 * @author JSpiner (jspiner@naver.com)
 * @project EnerBnB
 * @since 2016. 3. 26.
 */
public class MapActivity extends Activity {
    static final LatLng SEOUL = new LatLng( 37.56, 126.97);
    private GoogleMap map;

    LatLng[] lats = {new LatLng(37.540693,	127.07023),
            new LatLng(37.537077,	127.085916),
            new LatLng(37.535095,	127.094681),
            new LatLng(37.520733,	127.10379),
            new LatLng(37.51395,	127.102234),
            new LatLng(37.511687,	127.086162),
            new LatLng(37.510997,	127.073642),
            new LatLng(37.508844,	127.06316),
            new LatLng(37.504503,	127.049008),
            new LatLng(37.500622,	127.036456),
            new LatLng(37.497175,	127.027926),
            new LatLng(37.491897,	127.007917),
            new LatLng(37.481426,	126.997596),
            new LatLng(37.555134,	126.936893),
            new LatLng(37.556733,	126.946013),
            new LatLng(37.557345,	126.956141),
            new LatLng(37.559973,	126.963672),
            new LatLng(37.561904,	127.050899),
            new LatLng(37.57004,	127.046481),
            new LatLng(37.514287,	126.882768),
            new LatLng(37.512398,	126.865819),
            new LatLng(37.520074,	126.852912),
            new LatLng(37.47653,	126.981685),
            new LatLng(37.47693,	126.963693),
            new LatLng(37.481247,	126.952739),
            new LatLng(37.482362,	126.941892),
            new LatLng(37.484201,	126.929715),
            new LatLng(37.487462,	126.913149),
            new LatLng(37.485266,	126.901401),
            new LatLng(37.49297,	126.895801),
            new LatLng(37.508725,	126.891295),
            new LatLng(37.517933,	126.89476),
            new LatLng(37.52497,	126.895951),
            new LatLng(37.53438,	126.902281),
            new LatLng(37.549463,	126.913739),
            new LatLng(37.557192,	126.925381),
            new LatLng(37.566014,	126.982618),
            new LatLng(37.566941,	126.998079),
            new LatLng(37.565138,	127.007896),
            new LatLng(37.565972,	127.01782),
            new LatLng(37.564354,	127.029354),
            new LatLng(37.561533,	127.037732),
            new LatLng(37.555273,	127.043655),
            new LatLng(37.547184,	127.047367),
            new LatLng(37.544581,	127.055961),
            new LatLng(37.574028,	127.038091),
            new LatLng(37.566295,	126.99191),};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();

        for(LatLng lng : lats){

            Marker seoul = map.addMarker(new MarkerOptions().position(lng)
                    .title("Seoul"));
        }

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(SEOUL, 15));

        map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
    }


}
