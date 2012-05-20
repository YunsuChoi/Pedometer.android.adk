package com.tigris.adk.connection;

import com.tigris.adk.connection.R;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.future.usb.UsbAccessory;
import com.android.future.usb.UsbManager;

public class AdkExampleActivity extends Activity {
	
	private static final String ACTION_USB_PERMISSION = 
			"com.tigris.adk.connection.USB_PERMISSION";
	
	// USB가 감지되었을 때의 이벤트를 받음.
	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ACTION_USB_PERMISSION.equals(action)) {
				    // 사용자에게 Android Accessory Protocol을 구현한 장비가 연결되면
				    // 수락할 것인지 문의한 다이얼로그에 대한 사용자의 선택 결과를 받는다.
					synchronized (this) {
					UsbAccessory accessory = UsbManager.getAccessory(intent);
					if (intent.getBooleanExtra(
							UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
						// 수락했을 경우
						showMessage("receiver : USB Host 연결됨.");
					} else {
						Log.d(AdkExampleActivity.class.getName(), 
								"permission denied for accessory "
								+ accessory);
					}
					
					openAccessory(accessory);
					// 연결 수락 결과를 받았음을 표시
					mPermissionRequestPending = false;
				}
			} else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
				// Android Accessory Protocol을 구현한 장비의 연결이 해제되었을 때
				UsbAccessory accessory = UsbManager.getAccessory(intent);
				// 앱이 사용하고 있는 장비와 같은 것인지 확인
				if (accessory != null && accessory.equals(mAccessory)) {
					showMessage("USB Host 연결 해제됨.");
					closeAccessory();
				}
			}
		}
	};
	
	private TextView txtMsg;
	private UsbManager mUsbManager;
	private UsbAccessory mAccessory;
	private PendingIntent mPermissionIntent;
	private boolean mPermissionRequestPending;
	
	/** 액티비티가 생성될 때 호출 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        txtMsg = (TextView)this.findViewById(R.id.txtMsg);
        
        //Android Accessory Protocol을 구현한 장비의 연결에 대한 브로드캐스트 리시버 등록
      	IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
      	filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
      	registerReceiver(mUsbReceiver, filter);
      	
      	mUsbManager = UsbManager.getInstance(this);
		mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(
				ACTION_USB_PERMISSION), 0);
    }
    
    /** 액티비티가 화면에 보일 때 호출 */
    @Override
	public void onResume() {
		super.onResume();
		// 앱이 화면에 보일 때 안드로이드 장비에 Android Accessory Protocol을 
		// 구현한 USB Host가 연결되어 있는지 확인
		UsbAccessory[] accessories = mUsbManager.getAccessoryList();
		UsbAccessory accessory = (accessories == null ? null : accessories[0]);
		if (accessory != null) { // Android Accessory Protocol를 구현한 장비를 찾았을 경우
			if (mUsbManager.hasPermission(accessory)) {
				showMessage("onresume : USB Host 연결됨.");
				openAccessory(accessory);
			} else {
				synchronized (mUsbReceiver) {
					if (!mPermissionRequestPending) {
						mUsbManager.requestPermission(accessory,
								mPermissionIntent); // USB 연결을 통해 장비에 연결해도 되는지 사용자에게 문의
						mPermissionRequestPending = true; // 연결권한을 물어보드 코드를 실행했음을 표시
					}
				}
			}
		} else {
			Log.d(AdkExampleActivity.class.getName(), "mAccessory is null");
		}
	}
    
    // 액티비티가 소멸될 때 호출
 	@Override
 	protected void onDestroy() {
 		// 브로드캐스트 리시버를 제거
 		unregisterReceiver(mUsbReceiver);
 		super.onDestroy();
 	}
    
    private void openAccessory(UsbAccessory accessory){
		mAccessory = accessory;
	}
	
	private void closeAccessory(){
		mAccessory = null;
	}
	
	private void showMessage(String msg){
		txtMsg.setText(msg);
	}
}