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
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener {

	private Button btOn;
	private ListView listView;
	boolean deviceConnected=false;
	private final String DEVICE_ADDRESS="20:13:10:15:33:66";
	private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");//Serial Port Service ID
	private BluetoothDevice device;
	private BluetoothSocket socket;
	private OutputStream outputStream;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		btOn = (Button) findViewById(R.id.btOn);
		listView = (ListView) findViewById(R.id.dispositivos);

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

	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		// Handle navigation view item clicks here.
		int id = item.getItemId();

		if (id == R.id.nav_manejar) {

		} else if (id == R.id.nav_conectar) {

		}

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}

	public void btOn(View view) {
		if(BTinit()) {
			if (BTconnect()) {
				//setUiEnabled(true);
				deviceConnected = true;
				Toast.makeText(getApplicationContext(), "Turned on", Toast.LENGTH_LONG).show();
			}

		}
	}

	private boolean BTinit()
	{
		boolean found = false;
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (bluetoothAdapter == null) {
			Toast.makeText(getApplicationContext(),"Device doesnt Support Bluetooth",Toast.LENGTH_SHORT).show();
		}
		if(!bluetoothAdapter.isEnabled())
		{
			Intent enableAdapter = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableAdapter, 0);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
		if(bondedDevices.isEmpty())
		{
			Toast.makeText(getApplicationContext(),"Please Pair the Device first",Toast.LENGTH_SHORT).show();
		}
		else
		{
			ArrayList list = new ArrayList();
			for (BluetoothDevice iterator : bondedDevices)
			{
				if(iterator.getAddress().equals(DEVICE_ADDRESS))
				{
					device = iterator;
					found = true;
					break;
				}
			}
		}
		return found;
	}

	public boolean BTconnect()
	{
		boolean connected = true;
		try {
			socket = device.createRfcommSocketToServiceRecord(PORT_UUID);
			socket.connect();
		} catch (IOException e) {
			e.printStackTrace();
			connected = false;
		}
		if(connected) {
			try {
				outputStream = socket.getOutputStream();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return connected;
	}

	/*public void list(View v) {
		pairedDevices = btAdapter.getBondedDevices();
		ArrayList list = new ArrayList();

		for (BluetoothDevice bt : pairedDevices)
			list.add(bt.getName());
		Toast.makeText(getApplicationContext(), "Showing Paired Devices", Toast.LENGTH_SHORT).show();

		final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
		listView.setAdapter(adapter);
	}*/

	@Override
	public void onDestroy() {
		try {
			outputStream.close();
			socket.close();
		} catch(IOException e) {
            e.printStackTrace();
        }
		deviceConnected=false;
		super.onDestroy();
	}
}
