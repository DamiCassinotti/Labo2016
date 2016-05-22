package com.fiuba.dami.labo2016;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.zerokol.views.JoystickView;

import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener {

	private Button btOn;
	private JoystickView joystick;
	private TextView angle;
	private TextView power;
	boolean deviceConnected=false;
	private String DEVICE_ADDRESS="D4:93:98:AD:F9:3F";
	private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");//Serial Port Service ID
	private BluetoothDevice device;
	private BluetoothSocket socket;
	private OutputStream outputStream;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		btOn = (Button) findViewById(R.id.btOn);
		angle = (TextView) findViewById(R.id.angle);
		power = (TextView) findViewById(R.id.power);
		joystick = (JoystickView) findViewById(R.id.joystickView);
		createJoystickListener();

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.setDrawerListener(toggle);
		toggle.syncState();

		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);
	}

	@Override
	public void onBackPressed() {
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}

	public void btOn(View view) {
		if(BTinit()) {
			if (BTconnect()) {
				Toast.makeText(getApplicationContext(), "Conectado",Toast.LENGTH_SHORT).show();
				setUiConnected(true);
				deviceConnected = true;
			}
		}
	}

	private boolean BTinit()
	{
		boolean found = false;
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (bluetoothAdapter == null) {
			Toast.makeText(getApplicationContext(),"El dispositivo no soporta Bluetooth",Toast.LENGTH_SHORT).show();
		}
		if(!bluetoothAdapter.isEnabled())
		{
			Toast.makeText(getApplicationContext(),"Por favor, encienda el Bluetooth y vuelva a intentar",Toast.LENGTH_SHORT).show();
			return false;
		}
		Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
		if(bondedDevices.isEmpty())
		{
			Toast.makeText(getApplicationContext(),"Por favor, vincule primero los dispositivos.",Toast.LENGTH_SHORT).show();
		}
		else
		{
			for (BluetoothDevice iterator : bondedDevices)
			{
				if(iterator.getAddress().equals(DEVICE_ADDRESS))
				{
					device = iterator;
					found = true;
					break;
				}
			}
			if(!found) {
				Toast.makeText(getApplicationContext(),"Dispositivo no encontrado",Toast.LENGTH_SHORT).show();
			}
		}
		return found;
	}

	public boolean BTconnect() {
		boolean connected = true;
		try {
			socket = device.createRfcommSocketToServiceRecord(PORT_UUID);
			socket.connect();
		} catch (IOException e) {
			e.printStackTrace();
			connected = false;
		}
		if (connected) {
			try {
				outputStream = socket.getOutputStream();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return connected;
	}

	private void setUiConnected(boolean bool) {
		btOn.setEnabled(!bool);
	}

	private void createJoystickListener() {
		joystick.setOnJoystickMoveListener(new JoystickView.OnJoystickMoveListener() {
			@Override
			public void onValueChanged(int angulo, int poder, int direction) {
				angle.setText("Angle: " + String.valueOf(angulo) + "°");
				power.setText("Power: " + String.valueOf(poder) + "%");
			}
		}, JoystickView.DEFAULT_LOOP_INTERVAL);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}
