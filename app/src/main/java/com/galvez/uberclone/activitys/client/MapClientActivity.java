package com.galvez.uberclone.activitys.client;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryEventListener;
import com.galvez.uberclone.R;
import com.galvez.uberclone.activitys.LoginActivity;
import com.galvez.uberclone.activitys.MainActivity;
import com.galvez.uberclone.activitys.driver.MapDriverActivity;
import com.galvez.uberclone.includes.MyToolbar;
import com.galvez.uberclone.models.Driver;
import com.galvez.uberclone.providers.AuthProvider;
import com.galvez.uberclone.providers.DriverProvider;
import com.galvez.uberclone.providers.GeofireProvider;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MapClientActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final static int LOCATION_REQUEST_CODE = 1;
    private final static int SETTINGS_REQUEST_CODE = 2;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    AuthProvider authProvider;
    private LocationRequest mlocationRequest;
    private FusedLocationProviderClient fusedLocation;

    private Marker mMarker;

    private AutocompleteSupportFragment mAutoComplete;
    private AutocompleteSupportFragment mAutoCompleteDest;
    private PlacesClient mPLaces;
    private String mOrigin;
    private LatLng mOriginLatlng;
    private String mDest;
    private LatLng mDestLatlng;
    private GoogleMap.OnCameraIdleListener mCameraListener;

    private LatLng mCurrentLatlng;
    private GeofireProvider mgeofireProvider;

    private List<Marker> mDriversMarker=new ArrayList<>();

    private boolean misFirst=true;


    private Spinner spinner;
    DriverProvider driverProvider;
    private DatabaseReference mdatabase;

    String empresa_uno;

    LocationCallback mlocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                if (getApplicationContext() != null) {
                    mCurrentLatlng= new LatLng(location.getLatitude(),location.getLongitude());
                    /*if (mMarker!=null){
                        mMarker.remove();
                    }
                    mMarker=mMap.addMarker(new MarkerOptions().position(
                            new LatLng(location.getLatitude(), location.getLongitude())
                            ).title("Tu posicion")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icons8_marcador_40))

                    );*/

                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                                    .zoom(16f)
                                    .build()
                    ));

                    if (misFirst){
                        misFirst=false;
                        getActiveDrivers();
                        limitSearch();
                    }

                }
            }
            super.onLocationResult(locationResult);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_client);
        MyToolbar.show(this, "Cliente", false);
        fusedLocation = LocationServices.getFusedLocationProviderClient(this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapClient);
        mgeofireProvider=new GeofireProvider();
        mapFragment.getMapAsync(this);
        authProvider = new AuthProvider();
        mdatabase= FirebaseDatabase.getInstance().getReference();
        driverProvider=new DriverProvider();

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_key));
        }
        mPLaces = Places.createClient(this);
        instanciarAutocompleteDest();
        instanciarAutocompleteOrigin();
        onCameraMove();

        spinner=findViewById(R.id.mSpinnerRuta);
        List<String> rutas = new ArrayList<>();
        rutas.add(0,"Selecciona una ruta");
        rutas.add("empresa_uno");
        rutas.add("Ruta 2");
        rutas.add("Ruta 3");
        mdatabase.child("Drivers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    empresa_uno= Objects.requireNonNull(snapshot.getValue(Driver.class)).toString();
                }
                /*for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    Driver driver = snapshot1.getValue(Driver.class);
                    assert driver != null;
                    String empresa=driver.getEmpresa();

                }*/
                    Toast.makeText(MapClientActivity.this, " "+empresa_uno, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


//        Toast.makeText(this, " "+empresa_uno, Toast.LENGTH_SHORT).show();

        ArrayAdapter<String> dataAdapter;
        dataAdapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,rutas);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(adapterView.getItemAtPosition(i).equals("Selecciona una ruta")){



                }else{
                    String item=adapterView.getItemAtPosition(i).toString();
                    Toast.makeText(MapClientActivity.this, "Selecciono: "+item, Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });





    }
    private void limitSearch(){
        LatLng northSide= SphericalUtil.computeOffset(mCurrentLatlng,5000,0);
        LatLng southSide= SphericalUtil.computeOffset(mCurrentLatlng,5000,180);
        mAutoComplete.setCountry("PER");
        mAutoComplete.setLocationBias(RectangularBounds.newInstance(southSide,northSide));
        mAutoCompleteDest.setCountry("PER");
        mAutoCompleteDest.setLocationBias(RectangularBounds.newInstance(southSide,northSide));

    }
    private void onCameraMove(){
        mCameraListener=new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                try {
                    Geocoder geocoder=new Geocoder(MapClientActivity.this);
                    mOriginLatlng=mMap.getCameraPosition().target;
                    List<Address> addressList =geocoder.getFromLocation(mOriginLatlng.latitude,mOriginLatlng.longitude,1);
                    String city=addressList.get(0).getLocality();
                    String country=addressList.get(0).getCountryName();
                    String address=addressList.get(0).getAddressLine(0);
                    mOrigin=address+" "+city;
                    mAutoComplete.setText(address+" "+city);
                }catch (Exception e){
                    Log.d("Error","error: "+ e.getMessage());
                }
            }
        };


    }
    private void instanciarAutocompleteOrigin(){
        mAutoComplete = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.places_autocompleteOrigin);
        assert mAutoComplete != null;
        mAutoComplete.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME));
        mAutoComplete.setHint("Origen");
        mAutoComplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                mOrigin = place.getName();
                mOriginLatlng = place.getLatLng();
                Log.d("PLACE", "Name: " + mOrigin);
                Log.d("PLACE", "Lat: " + mOriginLatlng.latitude);
                Log.d("PLACE", "Lng: " + mOriginLatlng.longitude);
            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });
    }
    private void instanciarAutocompleteDest(){
        mAutoCompleteDest = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.places_autocompleteDest);
        assert mAutoCompleteDest != null;
        mAutoCompleteDest.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME));
        mAutoCompleteDest.setHint("Origen");
        mAutoCompleteDest.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                mDest = place.getName();
                mDestLatlng = place.getLatLng();
                Log.d("PLACE", "Name: " + mDest);
                Log.d("PLACE", "Lat: " + mDestLatlng.latitude);
                Log.d("PLACE", "Lng: " + mDestLatlng.longitude);
            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });
    }

    void logOut() {
        authProvider.logout();
        Intent intent = new Intent(MapClientActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    private void getEmpresa(){

    }


    private void getActiveDrivers(){

        mgeofireProvider.getActiveDrivers(mCurrentLatlng).addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                for (Marker marker: mDriversMarker){
                    if (marker.getTag()!=null){

                        if (marker.getTag().equals(key))
                        return;
                    }
                }

                LatLng driverLatlng = new LatLng(location.latitude,location.longitude);
                Marker marker = mMap.addMarker(new MarkerOptions().position(driverLatlng)
                        .title("Conductor Disponible")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icons8_personas_en_coche__vista_lateral_60)));
                marker.setTag(key);



                //mDriversMarker.add(marker);


            }

            @Override
            public void onKeyExited(String key) {
                for (Marker marker: mDriversMarker){
                    if (marker.getTag()!=null){
                        if (marker.getTag().equals(key))
                            marker.remove();
                        mDriversMarker.remove(marker);
                            return;
                    }
                }

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

                for (Marker marker: mDriversMarker){
                    if (marker.getTag()!=null){
                        if (marker.getTag().equals(key)){
                            marker.setPosition(new LatLng(location.latitude,location.longitude));
                        }

                        return;
                    }
                }

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        mMap.setOnCameraIdleListener(mCameraListener);

        mlocationRequest = new LocationRequest();
        mlocationRequest.setInterval(1000);
        mlocationRequest.setFastestInterval(1000);
        mlocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mlocationRequest.setSmallestDisplacement(5);

        startLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if (gpsActived()){
                        fusedLocation.requestLocationUpdates(mlocationRequest, mlocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);

                    }else{
                        showDialogNoGps();
                    }
                } else {
                    checkLocationPermissions();
                }
            } else {
                checkLocationPermissions();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTINGS_REQUEST_CODE && gpsActived()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            fusedLocation.requestLocationUpdates(mlocationRequest, mlocationCallback, Looper.myLooper());
            mMap.setMyLocationEnabled(true);

        }else if (requestCode == SETTINGS_REQUEST_CODE && !gpsActived()){
            showDialogNoGps();
        }
    }

    private void showDialogNoGps(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("Activa el gps")
                .setPositiveButton("Configuracion", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),SETTINGS_REQUEST_CODE);
                    }
                }).create().show();
    }

    private boolean gpsActived(){
        boolean isActive=false;
        LocationManager locationManager= (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            isActive=true;
        }
        return isActive;
    }

    private void startLocation(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                if (gpsActived()){
                    fusedLocation.requestLocationUpdates(mlocationRequest,mlocationCallback, Looper.myLooper());
                    mMap.setMyLocationEnabled(true);
                }else {
                    showDialogNoGps();
                }
            }else{
                checkLocationPermissions();
            }
        }else{
            if (gpsActived()){
                fusedLocation.requestLocationUpdates(mlocationRequest,mlocationCallback, Looper.myLooper());
                mMap.setMyLocationEnabled(true);
            }else {
                showDialogNoGps();
            }
        }
    }

    private void checkLocationPermissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                new AlertDialog.Builder(this)
                        .setTitle("Proporciona los permisos para continuar")
                        .setMessage("Requiere permisos")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MapClientActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);

                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(MapClientActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.driver_menu,menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.action_logout){
            logOut();
        }
        return super.onOptionsItemSelected(item);
    }
}