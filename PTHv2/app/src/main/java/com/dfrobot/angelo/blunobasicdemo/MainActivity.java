package com.dfrobot.angelo.blunobasicdemo;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Bundle;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.text.SpannableStringBuilder;
import android.text. style.ForegroundColorSpan;
import android.text.Spannable;
import android.view.Gravity;
import android.net.Uri;


public class MainActivity extends BlunoLibrary {

	private Button buttonScan;
	private Button buttonLog;
	private TextView text_p1, text_p2, text_p3, text_p4, text_p5, text_p6, text_p7, text_p8, ps1, ps2, ps3, ps4, ps5, ps6, time,info;
	public static double p1_value, p2_value, p3_value, p4_value, p5_value, p6_value, p7_value, p8_value;

	public static int aa =0;
	public static String dataST, tempST=null;
	public static SQLiteOpenHelper DatabaseHelper = null;
	public static SQLiteDatabase db = null;
	public static ContentValues values=null;
	public static SimpleDateFormat sdFormat = null;
	public static Date date = null;
	public static String strDate = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        onCreateProcess();															//onCreate Process by BlunoLibrary
		setupUI();

		DatabaseHelper = new DatabaseHelper(this);
		db = DatabaseHelper.getWritableDatabase();
		values=new ContentValues();  //建立 ContentValues 物件並呼叫 put(key,value) 儲存欲新增的資料，key 為欄位名稱  value 為對應值。
		sdFormat = new SimpleDateFormat("MM/dd hh:mm:ss");
	}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id==R.id.item0){	//未使用之ＤＯＰＳ
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			intent.setData(Uri.parse("https://asking.wenjoy.com/s/PyRKg7W"));
			startActivity(intent);
		}
		else if (id==R.id.item1){	//使用的ＤＯＰＳ
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			intent.setData(Uri.parse("https://asking.wenjoy.com/s/EAfjb2M"));
			startActivity(intent);
		}
        else if(id==R.id.item2){	//問卷
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			intent.setData(Uri.parse("https://asking.wenjoy.com/s/8ojVL2E"));
			startActivity(intent);
		}
		else if (id == R.id.item3){		//thingspeak
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			intent.setData(Uri.parse("https://thingspeak.com/channels/726930"));
			startActivity(intent);
		}
        else if(id==R.id.item4){	//Copyright
			Toast toast = Toast.makeText(this,"本設備為論文研究\n指導教授: 段裘慶,研究生: 鍾華\n© 2019, 國立臺北科大-電子所-網路應用實驗室", Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER,0,0);
			toast.show();

		}
        return super.onOptionsItemSelected(item);
    }

	//1. 初始化UI介面===============================================================================
	public void setupUI(){
		serialBegin(115200);										//set the Baud rate on BLE chip to 9600 or 115200
		buttonLog = (Button) findViewById(R.id.buttonLog);				//initial the button for sending the data
		buttonLog.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				buttonLog();
			}
		});
		buttonScan = (Button) findViewById(R.id.buttonScan);			//initial the button for scanning the BLE device
		buttonScan.setOnClickListener(new OnClickListener() {
			//Alert Dialog for selecting the BLE device
			public void onClick(View v) {
				buttonScanOnClickProcess();
			}
		});
		text_p1 = (TextView)findViewById(R.id.text_view_p1_value);
		text_p2 = (TextView)findViewById(R.id.text_view_p2_value);
		text_p3 = (TextView)findViewById(R.id.text_view_p3_value);
		text_p4 = (TextView)findViewById(R.id.text_view_p4_value);
		text_p5 = (TextView)findViewById(R.id.text_view_p5_value);
		text_p6 = (TextView)findViewById(R.id.text_view_p6_value);
		text_p7 = (TextView)findViewById(R.id.text_view_temperature_value);
		text_p8 = (TextView)findViewById(R.id.text_view_humidity_value);
		info = (TextView)findViewById(R.id.info);
		ps1=(TextView)findViewById(R.id.t1);
		ps2=(TextView)findViewById(R.id.t2);
		ps3=(TextView)findViewById(R.id.t3);
		ps4=(TextView)findViewById(R.id.t4);
		ps5=(TextView)findViewById(R.id.t5);
		ps6=(TextView)findViewById(R.id.t6);
		time=(TextView)findViewById(R.id.date);
	}
	public void timeshow(){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd\t\t\tHH:mm");
		Date date = new Date(System.currentTimeMillis());
		time.setText(simpleDateFormat.format(date));
	}
	//2. 連線狀態 : Once connection state changes, this function will be called=====================
	public void onConectionStateChange(connectionStateEnum theConnectionState) {
		switch (theConnectionState) {
		case isConnected:
			buttonScan.setText("已 連 線");
			break;
		case isConnecting:
			buttonScan.setText("配 對 中");
			break;
		case isToScan:
			buttonScan.setText("藍 牙 配 對");
			break;
		case isScanning:
			buttonScan.setText("掃 瞄 中");
			break;
		case isDisconnecting:
			buttonScan.setText("離 線");
			break;
		default:
			break;
		}
	}


	//3. 過濾BLE封包內容
	public void onSerialReceived(String theString) {
		//append the text into the EditText
		System.out.println("=======================> " +  theString);
		if(theString.indexOf("=")==-1){
			if(theString.indexOf("(")!=-1){
				dataST = theString;
			}
			else if(theString.indexOf(")")!=-1){
				dataST+=theString;
				getData(dataST);
				dataST="";
			}
			else{
				dataST+=theString;
			}
		}
	}


	//4. 解析填入UI
	public void getData(String data){
		tempST = data.substring(1, data.indexOf(")"));
		//System.out.println("=======================> " +  tempST);
		String[] tokens = tempST.split(",");
		aa=1;
		for (String token:tokens) {
			if(aa==1) {
				text_p1.setText(token+" mmHg");
				p1_value = Double.parseDouble(token);
				if(p1_value>=0 && p1_value<8){
					text_p1.setTextColor(Color.parseColor("#0000FF"));	//blue
					ps1.setBackgroundColor(Color.BLUE);
				}
				else if(p1_value>=8 && p1_value<25){
					text_p1.setTextColor(Color.parseColor("#000000"));	//black
					ps1.setBackgroundColor(Color.BLACK);
				}
				else{
					text_p1.setTextColor(Color.parseColor("#FF0000"));	//red
					ps1.setBackgroundColor(Color.RED);
				}
			}
			else if(aa==2) {
				text_p2.setText(token+" mmHg");
				p2_value = Double.parseDouble(token);
				if(p2_value>=0 && p2_value<8){
					text_p2.setTextColor(Color.parseColor("#0000FF"));
					ps2.setBackgroundColor(Color.BLUE);
				}
				else if(p2_value>=8 && p2_value<25){
					text_p2.setTextColor(Color.parseColor("#000000"));
					ps2.setBackgroundColor(Color.BLACK);
				}
				else{
					text_p2.setTextColor(Color.parseColor("#FF0000"));
					ps2.setBackgroundColor(Color.RED);
				}
			}
			else if(aa==3) {
				text_p3.setText(token+" mmHg");
				p3_value = Double.parseDouble(token);
				if(p3_value>=0 && p3_value<8){
					text_p3.setTextColor(Color.parseColor("#0000FF"));
					ps3.setBackgroundColor(Color.BLUE);
				}
				else if(p3_value>=8 && p3_value<25){
					text_p3.setTextColor(Color.parseColor("#000000"));
					ps3.setBackgroundColor(Color.BLACK);
				}
				else{
					text_p3.setTextColor(Color.parseColor("#FF0000"));
					ps3.setBackgroundColor(Color.RED);
				}
			}
			else if(aa==4) {
				text_p4.setText(token+" mmHg");
				p4_value = Double.parseDouble(token);
				if(p4_value>=0 && p4_value<8){
					text_p4.setTextColor(Color.parseColor("#0000FF"));
					ps4.setBackgroundColor(Color.BLUE);
				}
				else if(p4_value>=8 && p4_value<25){
					text_p4.setTextColor(Color.parseColor("#000000"));
					ps4.setBackgroundColor(Color.BLACK);
				}
				else{
					text_p4.setTextColor(Color.parseColor("#FF0000"));
					ps4.setBackgroundColor(Color.RED);
				}
			}
			else if(aa==5) {
				text_p5.setText(token+" mmHg");
				p5_value = Double.parseDouble(token);
				if(p5_value>=0 && p5_value<8){
					text_p5.setTextColor(Color.parseColor("#0000FF"));
					ps5.setBackgroundColor(Color.BLUE);
				}
				else if(p5_value>=8 && p5_value<25){
					text_p5.setTextColor(Color.parseColor("#000000"));
					ps5.setBackgroundColor(Color.BLACK);
				}
				else{
					text_p5.setTextColor(Color.parseColor("#FF0000"));
					ps5.setBackgroundColor(Color.RED);
				}
			}
			else if(aa==6) {
				text_p6.setText(token+" mmHg");
				p6_value = Double.parseDouble(token);
				if(p6_value>=0 && p6_value<8){
					text_p6.setTextColor(Color.parseColor("#0000FF"));
					ps6.setBackgroundColor(Color.BLUE);
				}
				else if(p6_value>=8 && p6_value<25){
					text_p6.setTextColor(Color.parseColor("#000000"));
					ps6.setBackgroundColor(Color.BLACK);
				}
				else{
					text_p6.setTextColor(Color.parseColor("#FF0000"));
					ps6.setBackgroundColor(Color.RED);
				}
			}
			else if(aa==8) {	//H&T
				text_p8.setText(token+" %  ");
				p8_value = Double.parseDouble(token);
				if(p8_value>=0 && p8_value<40){
					text_p8.setTextColor(Color.parseColor("#0000FF"));
				}
				else if(p8_value>=40 && p8_value<80){
					text_p8.setTextColor(Color.parseColor("#000000"));
				}
				else{
					text_p8.setTextColor(Color.parseColor("#FF0000"));
				}
			}
			else if(aa==7) {	//temp
				text_p7.setText(token+" ℃  ");
				p7_value = Double.parseDouble(token);
				if(p7_value>=0 && p7_value<20){
					text_p7.setTextColor(Color.parseColor("#0000FF"));
				}
				else if(p7_value>=20 && p7_value<35){
					text_p7.setTextColor(Color.parseColor("#000000"));
				}
				else{
					text_p7.setTextColor(Color.parseColor("#FF0000"));
				}
			}
			aa++;
		}
		info.setText("藍色");

		SpannableStringBuilder style = new SpannableStringBuilder("藍色=過鬆\t黑色=正常\t紅色=過緊");
		style.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		style.setSpan(new ForegroundColorSpan(Color.BLACK), 6, 12, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		style.setSpan(new ForegroundColorSpan(Color.RED), 12, 17, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		info.setText(style);



		try{
			date = new Date();
			strDate = sdFormat.format(date);
			timeshow();
			values.put("P0", strDate);
			values.put("P1", p1_value);
			values.put("P2", p2_value);
			values.put("P3", p3_value);
			values.put("P4", p4_value);
			values.put("P5", p5_value);
			values.put("P6", p6_value);
			values.put("P7", p7_value);
			values.put("P8", p8_value);
			db.insert("table_ble_601CH",null,values);
		}
		catch (SQLiteException e){
			Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	public void buttonLog(){
		Intent intent = new Intent(this, LogActivity.class);
		startActivity(intent);
	}

	protected void onResume(){
		super.onResume();
		System.out.println("BlUNOActivity onResume");
		onResumeProcess();														//onResume Process by BlunoLibrary
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		onActivityResultProcess(requestCode, resultCode, data);					//onActivityResult Process by BlunoLibrary
		super.onActivityResult(requestCode, resultCode, data);
	}
	@Override
	protected void onPause() {
		super.onPause();
		onPauseProcess();														//onPause Process by BlunoLibrary
	}
	protected void onStop() {
		super.onStop();
		onStopProcess();														//onStop Process by BlunoLibrary
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		onDestroyProcess();														//onDestroy Process by BlunoLibrary
	}

}
