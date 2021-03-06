package iss.workshop.jsonparsingexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import iss.workshop.jsonparsingexample.Models.Item;
import iss.workshop.jsonparsingexample.Models.PO;

public class POList extends AppCompatActivity implements GetPurchaseOrderData.OnDataAvailable, GetRawData.OnDownloadComplete {

    public static final String TAG = "POList";


    RecyclerView rView;
    Button mbtnCreate;
    POListAdpater poAdapter;

    private String mLogoutURL;
    private String mURL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p_o_list);

        mURL = "https://logicuniversity.nusteamfour.online/PO/POListAPI";
        mLogoutURL = "https://logicuniversity.nusteamfour.online/logout/logoutapi";

        rView = findViewById(R.id.poListRecyclerView);
        Date d = new Date();

        /*PO po1 = new PO();
        po1.setId(1);
        po1.setOrderDate("10/10/2020 12:00:00 AM");
        po1.setSupplierName("Fair Price");
        po1.setStatus("Processing");*/

        mbtnCreate = findViewById(R.id.btnPOCreate);
        mbtnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(POList.this,PurchaseOrder.class);
                startActivity(intent);
            }
        });

            List<PO> poList = new ArrayList<>();

           // poList.add(po1);

             poAdapter = new POListAdpater(this,poList);
             poAdapter = new POListAdpater(this,new ArrayList<PO>());

            rView.setAdapter(poAdapter);
            rView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume starts");
        super.onResume();
        GetPurchaseOrderData getPurchaseOrderdata = new GetPurchaseOrderData(this);
        getPurchaseOrderdata.execute(mURL);
        Log.d(TAG, "onResume ends");
    }


    @Override
    public void onDataAvailable(List<PO> data, DownloadStatus status) {
        Log.d(TAG, "onDataAvailable: starts");
        if(status == DownloadStatus.OK) {
            poAdapter.loadNewData(data);
            Log.d(TAG, "onDataAvailable: in"+data.toString());
        } else {
            // download or processing failed
            Log.e(TAG, "onDataAvailable failed with status " + status);
        }

        Log.d(TAG, "onDataAvailable: ends");
    }

    @Override
    public void getRawDataOnDownloadComplete(String data, DownloadStatus status) {

    }

    //  Option Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.storeclerk_options_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent;

        switch (item.getItemId()) {
            case R.id.Bar_Chart_List_item:
                intent = new Intent(this, BarChartActivity.class);
                break;
            case R.id.Requisition_List_item:
                intent = new Intent(this, StoreClerkRequisitionListActivity.class);
                break;
            case R.id.Disbursement_List_item:
                intent = new Intent(this, StoreClerkDisbursementListActivity.class);
                break;
            case R.id.Stock_List_item:
                intent = new Intent(this, StockListActivity.class);
                break;
            case R.id.PO_List_item:
                intent = new Intent(this,POList.class);
                break;
            case R.id.Store_Clerk_Logout_item:
                GetRawData getRawData = new GetRawData(this);
                getRawData.execute(mLogoutURL);

                // clear shared preferences
                SharedPreferences pref = getSharedPreferences("user_credentials", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.clear();
                editor.commit();
                finish();

                intent = new Intent(this, LoginActivity.class);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        startActivity(intent);

        return true;
    }
}