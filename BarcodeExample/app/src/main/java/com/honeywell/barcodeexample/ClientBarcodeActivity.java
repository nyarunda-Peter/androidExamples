package com.example.login;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.honeywell.aidc.BarcodeFailureEvent;
import com.honeywell.aidc.BarcodeReadEvent;
import com.honeywell.aidc.BarcodeReader;
import com.honeywell.aidc.ScannerNotClaimedException;
import com.honeywell.aidc.ScannerUnavailableException;
import com.honeywell.aidc.TriggerStateChangeEvent;
import com.honeywell.aidc.UnsupportedPropertyException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientBarcodeActivity extends Activity implements BarcodeReader.BarcodeListener,
        BarcodeReader.TriggerListener {

    private com.honeywell.aidc.BarcodeReader barcodeReader;
    private ListView barcodeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);

        if(Build.MODEL.startsWith("VM1A")) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        else{
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        // get bar code instance from MainActivity
        barcodeReader = MainActivity.getBarcodeObject();

        if (barcodeReader != null) {

            // register bar code event listener
            barcodeReader.addBarcodeListener(this);

            // set the trigger mode to client control
            try {
               barcodeReader.setProperty(BarcodeReader.PROPERTY_TRIGGER_CONTROL_MODE,
                       BarcodeReader.TRIGGER_CONTROL_MODE_CLIENT_CONTROL);

            } catch (UnsupportedPropertyException e) {
                Toast.makeText(this, "Failed to apply properties", Toast.LENGTH_SHORT).show();
            }
            // register trigger state change listener
            barcodeReader.addTriggerListener(this);

            Map<String, Object> properties = new HashMap<String, Object>();
            // Set Symbologies On/Off
            properties.put(BarcodeReader.PROPERTY_CODE_128_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_GS1_128_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_QR_CODE_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_CODE_39_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_DATAMATRIX_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_UPC_A_ENABLE, true);
            properties.put(BarcodeReader.PROPERTY_EAN_13_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_AZTEC_ENABLED, false);
            properties.put(BarcodeReader.PROPERTY_CODABAR_ENABLED, false);
            properties.put(BarcodeReader.PROPERTY_INTERLEAVED_25_ENABLED, false);
            properties.put(BarcodeReader.PROPERTY_PDF_417_ENABLED, false);
            // Set Max Code 39 barcode length
            properties.put(BarcodeReader.PROPERTY_CODE_39_MAXIMUM_LENGTH, 10);
            // Turn on center decoding
            properties.put(BarcodeReader.PROPERTY_CENTER_DECODE, true);
            // Disable bad read response, handle in onFailureEvent
            properties.put(BarcodeReader.PROPERTY_NOTIFICATION_BAD_READ_ENABLED, false);
            // Sets time period for decoder timeout in any mode
            properties.put(BarcodeReader.PROPERTY_DECODER_TIMEOUT,  400);
            // Apply the settings
            barcodeReader.setProperties(properties);
        }

        // get initial list
        barcodeList = (ListView) findViewById(R.id.listViewBarcodeData);
    }

    public String addChar(String str, char ch, int position) {
        return str.substring(0, position) + ch + str.substring(position);
    }


    @Override
    public void onBarcodeEvent(final BarcodeReadEvent event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // update UI to reflect the data
                List<String> list = new ArrayList();
                String data=event.getBarcodeData();
                list.add("Barcode data: " +data);

                if (data.length()==12) {
                    String Manufacture_ID=data.substring(1,7);
                    String Item_No=data.substring(7,11);
                    String Last_Digit=data.substring(12);

                    list.add("Manufacture_ID: " +Manufacture_ID);
                    list.add("Item_No: " +Item_No);
                    list.add("Last_Digit: " +Last_Digit);
                         }
//                }
//
//                else if(data.length()==13){
//                    String Country_Code = data.substring(-1,0);
//                    String Company_Prefix = data.substring(1,6);
//                    String Product_Code = data.substring(7, 11);
//                    String Control_key = data.substring(12);
//
//                    list.add("Country_Code" + Country_Code);
//                    list.add("Company_Prefix: " + Company_Prefix);
//                    list.add("Product_Code: " + Product_Code);
//                    list.add("Control_key: " + Control_key);
//                }

                else if (data.length()==56){
                    String gtin=data.substring(2,15);
                    String ref=data.substring(18,25);
                    String lot=data.substring(29,39);

                    list.add("GTIN: " + gtin);
                    list.add("REF: " + ref);
                    list.add("LOT: " + lot);


                }
                else if (data.length()==57) {
                    String gtin=data.substring(2,16);
                    String ref=data.substring(46,57);
                    String lot=data.substring(26,34);

                    String date1=data.substring(18,24);

                    String date2=data.substring(37,43);

                    list.add("GTIN: " + gtin);
                    list.add("REF: " + ref);
                        list.add("LOT: " + lot);

                    date1=addChar(date1,'-',2);
                    date2=addChar(date2,'-',2);

                    date1=addChar(date1,'-',5);
                    date2=addChar(date2,'-',5);

                    date1=addChar(date1,'0',0);
                    date2=addChar(date2,'0',0);


                    date1=addChar(date1,'2',0);
                    date2=addChar(date2,'2',0);


                    list.add("Manufacturing Date: "+ date1);
                    list.add("Expiry Date : " +date2);

                }
                else{
                    if (data.length()==12) {
                        String Manufacture_ID=data.substring(1,7);
                        String Item_No=data.substring(7,11);
                        String Last_Digit=data.substring(12);

                        list.add("Manufacture_ID: " +Manufacture_ID);
                        list.add("Item_No: " +Item_No);
                        list.add("Last_Digit: " +Last_Digit);
                    }
                }

                final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                        ClientBarcodeActivity.this, android.R.layout.simple_list_item_1, list);


                barcodeList.setAdapter(dataAdapter);


            }

        });
    }

    // When using Automatic Trigger control do not need to implement the
    // onTriggerEvent function
    @Override
  public void onTriggerEvent(TriggerStateChangeEvent event) {
        try {
            // only handle trigger presses
            // turn on/off aimer, illumination and decoding
            barcodeReader.aim(event.getState());
//            barcodeReader.light(event.getState());
            barcodeReader.decode(event.getState());

        } catch (ScannerNotClaimedException e) {
            e.printStackTrace();
            Toast.makeText(this, "Scanner is not claimed", Toast.LENGTH_SHORT).show();
        } catch (ScannerUnavailableException e) {
            e.printStackTrace();
            Toast.makeText(this, "Scanner unavailable", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFailureEvent(BarcodeFailureEvent arg0) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(ClientBarcodeActivity.this, "No data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (barcodeReader != null) {
            try {
                barcodeReader.claim();
            } catch (ScannerUnavailableException e) {
                e.printStackTrace();
                Toast.makeText(this, "Scanner unavailable", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (barcodeReader != null) {
            // release the scanner claim so we don't get any scanner
            // notifications while paused.
            barcodeReader.release();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (barcodeReader != null) {
            // unregister barcode event listener
            barcodeReader.removeBarcodeListener(this);

            // unregister trigger state change listener
            barcodeReader.removeTriggerListener(this);
        }
    }
}
