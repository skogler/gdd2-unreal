package eu.gdd2.sensorcontroller;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SensorControllerActivity extends ActionBarActivity implements SensorFusion.ISensorFusionListener {

	private SensorFusion sensors;
	private Socket socket;
	private boolean connected;
	private boolean running;
	
	private HandlerThread networkThread;
	private Handler networkHandler;

	private TextView statusView;
	private EditText ipText;
	private Button button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sensor_controller);
		
		connected = false;
		running = false;
		networkHandler = null;
		
        statusView = (TextView) findViewById(R.id.statusView);
        ipText = (EditText) findViewById(R.id.ipText);
        button = (Button) findViewById(R.id.connectButton);
        button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(connected)
					disconnect();
				else
					connect();
			}
		});
		
		networkThread = new HandlerThread("network thread");
		
		networkThread.start();
		
		networkHandler = new Handler(networkThread.getLooper()) {
			@Override
			public void handleMessage(Message msg) {
				running = true;
			}
		};
		
		networkHandler.sendEmptyMessage(0);
		
		sensors = new SensorFusion(this, this);
		
		sensors.start();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		networkThread.quit();
		try {
			networkThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sensor_controller, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void newOrientation(final float[] orientation) {
		final String status = String.format("%f %f %f", orientation[0], orientation[1], orientation[2]);
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				statusView.setText(status);
			}
		});
		
		if(running) {
			networkHandler.post(new Runnable() {
				
				@Override
				public void run() {
					if(connected) {
						ByteBuffer buffer = ByteBuffer.allocate(orientation.length * 4);
						buffer.order(ByteOrder.LITTLE_ENDIAN);
						buffer.asFloatBuffer().put(orientation);
						
						try {
							socket.getOutputStream().write(buffer.array());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			});
		}
	}
	
	public void connect() {
		final String ip = ipText.getText().toString();
		
		networkHandler.post(new Runnable() {
			
			@Override
			public void run() {
				try {
					socket = new Socket(ip, 19834);
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				}
				
				connected = true;

				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						button.setText("Disconnect");
					}
				});
			}
		});
	}
	
	public void disconnect() {
		networkHandler.post(new Runnable() {
			
					@Override
					public void run() {
				connected = false;
				
				try {
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				socket = null;
				
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						button.setText("Connect");
					}
				});
			}
		});
	}
}
