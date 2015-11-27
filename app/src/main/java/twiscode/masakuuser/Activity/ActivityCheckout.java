package twiscode.masakuuser.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.angmarch.views.NiceSpinner;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import info.hoang8f.android.segmented.SegmentedGroup;
import twiscode.masakuuser.Adapter.AdapterCheckout;
import twiscode.masakuuser.Adapter.AdapterMenu;
import twiscode.masakuuser.Control.JSONControl;
import twiscode.masakuuser.Model.ModelCart;
import twiscode.masakuuser.Model.ModelMenu;
import twiscode.masakuuser.Model.ModelUser;
import twiscode.masakuuser.R;
import twiscode.masakuuser.Utilities.ApplicationData;
import twiscode.masakuuser.Utilities.ApplicationManager;
import twiscode.masakuuser.Utilities.DialogManager;
import twiscode.masakuuser.Utilities.NetworkManager;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by TwisCode-02 on 10/26/2015.
 */
public class ActivityCheckout extends AppCompatActivity {

    Activity act;
    ApplicationManager applicationManager;
    private EditText txtKode, txtNote;
    private ImageView btnBack;
    private ListView mListView;
    private TextView txtSubtotal, txtTip, txtDelivery, txtTotal, txtDiskon, noData, txtAlamat;
    private Button btnKonfirmasi;
    AdapterCheckout mAdapter;
    private List<ModelCart> LIST_MENU = new ArrayList<>();
    SegmentedGroup segmented;
    NiceSpinner paySpiner;
    int delivery = 0;
    int subtotal = 0;
    int tip = 0;
    int total = 0;
    int diskon = 0;
    private DecimalFormat decimalFormat;
    private ProgressBar progress;
    private BroadcastReceiver updateCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        act = this;
        applicationManager = new ApplicationManager(act);
        progress = (ProgressBar) findViewById(R.id.progress);
        btnBack = (ImageView) findViewById(R.id.btnBack);
        mListView = (ListView) findViewById(R.id.listCheckout);
        noData = (TextView) findViewById(R.id.noData);
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
        otherSymbols.setDecimalSeparator(',');
        otherSymbols.setGroupingSeparator('.');
        decimalFormat = new DecimalFormat();
        decimalFormat.setDecimalFormatSymbols(otherSymbols);

        DummyData();

        View header = getLayoutInflater().inflate(R.layout.layout_header_checkout, null);
        txtKode = (EditText) header.findViewById(R.id.kodePromoCheckout);
        txtNote = (EditText) header.findViewById(R.id.noteCheckout);
        txtAlamat = (TextView) header.findViewById(R.id.alamatCheckout);
        mListView.addHeaderView(header);
        View footer = getLayoutInflater().inflate(R.layout.layout_footer_checkout, null);
        txtSubtotal = (TextView) footer.findViewById(R.id.subtotalCheckout);
        txtDelivery = (TextView) footer.findViewById(R.id.deliveryCheckout);
        txtTip = (TextView) footer.findViewById(R.id.tipCheckout);
        txtDiskon = (TextView) footer.findViewById(R.id.diskonCheckout);
        txtTotal = (TextView) footer.findViewById(R.id.totalCheckout);
        btnKonfirmasi = (Button) footer.findViewById(R.id.btnKonfirmasi);

        txtDiskon.setText("Rp. " + decimalFormat.format(diskon));
        txtSubtotal.setText("Rp. " + decimalFormat.format(subtotal));
        txtDelivery.setText("Rp. " + decimalFormat.format(delivery));
        txtTip.setText("Rp. " + decimalFormat.format(tip));
        txtTotal.setText("Rp. " + decimalFormat.format(total));

        String alamat = ApplicationData.location;
        if (alamat != "") {
            txtAlamat.setText(alamat);
        }
        paySpiner = (NiceSpinner) footer.findViewById(R.id.paySpinner);
        segmented = (SegmentedGroup) footer.findViewById(R.id.segmented);
        segmented.setTintColor(Color.parseColor("#D02D2E"));
        segmented.check(R.id.button23);
        List<String> dataPay = new LinkedList<>(Arrays.asList("Cash"));
        paySpiner.attachDataSource(dataPay);
        mListView.addFooterView(footer);
        mAdapter = new AdapterCheckout(this, LIST_MENU);
        mListView.setAdapter(mAdapter);
        mListView.setScrollingCacheEnabled(false);
        segmented.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.button21:
                        tip = 0;
                        total = subtotal + tip + delivery - diskon;
                        txtTip.setText("Rp. " + decimalFormat.format(tip));
                        txtTotal.setText("Rp. " + decimalFormat.format(total));
                        return;
                    case R.id.button22:
                        tip = (int) Math.round(subtotal * 0.05);
                        total = subtotal + tip + delivery - diskon;
                        txtTip.setText("Rp. " + decimalFormat.format(tip));
                        txtTotal.setText("Rp. " + decimalFormat.format(total));
                        return;
                    case R.id.button23:
                        tip = (int) Math.round(subtotal * 0.1);
                        total = subtotal + tip + delivery - diskon;
                        txtTip.setText("Rp. " + decimalFormat.format(tip));
                        txtTotal.setText("Rp. " + decimalFormat.format(total));
                        return;
                    case R.id.button24:
                        tip = (int) Math.round(subtotal * 0.15);
                        total = subtotal + tip + delivery - diskon;
                        txtTip.setText("Rp. " + decimalFormat.format(tip));
                        txtTotal.setText("Rp. " + decimalFormat.format(total));
                        return;
                    case R.id.button25:
                        tip = (int) Math.round(subtotal * 0.2);
                        total = subtotal + tip + delivery - diskon;
                        txtTip.setText("Rp. " + decimalFormat.format(tip));
                        txtTotal.setText("Rp. " + decimalFormat.format(total));
                        return;
                    default:
                        // Nothing to do
                }

            }
        });

        txtAlamat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), ActivityChangeLocation.class);
                startActivity(i);
                finish();
            }
        });

        txtKode.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            Log.d("kupon", txtKode.getText().toString());
                            new CalculatePrice(ActivityCheckout.this).execute(txtKode.getText().toString());
                            return true;
                        }
                        return false;
                    }
                });

        /*
        if(ApplicationData.cart.size() > 0){
            mListView.setVisibility(View.VISIBLE);
            noData.setVisibility(View.GONE);
        }
        else {
            mListView.setVisibility(View.GONE);
            noData.setVisibility(View.VISIBLE);
        }
        */

        new CalculatePrice(ActivityCheckout.this).execute(txtKode.getText().toString());

        btnKonfirmasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtAlamat.getText().toString() == "") {
                    DialogManager.showDialog(act, "Mohon Maaf", "Isi alamat anda");
                } else {
                    try {
                        final Context ctx = ActivityCheckout.this;
                        new MaterialDialog.Builder(ctx)
                                .title("Anda yakin untuk order?")
                                .positiveText("OK")
                                .callback(new MaterialDialog.ButtonCallback() {
                                    @Override
                                    public void onPositive(MaterialDialog dialog) {
                                        new CheckOut(ActivityCheckout.this).execute(
                                                txtKode.getText().toString(),
                                                txtAlamat.getText().toString(),
                                                txtNote.getText().toString()
                                        );
                                        dialog.dismiss();
                                    }
                                })
                                .negativeText("Tidak")
                                .cancelable(false)
                                .typeface("GothamRnd-Medium.otf", "Gotham.ttf")
                                .show();
                    } catch (Exception e) {

                    }
                }

            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        updateCart = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Extract data included in the Intent
                Log.d("", "broadcast updateCart");
                String message = intent.getStringExtra("message");
                if (message.equals("true")) {
                    if(ApplicationData.cart.size() > 0){
                        LIST_MENU = new ArrayList<ModelCart>(ApplicationData.cart.values());
                        if (LIST_MENU.size() > 0) {
                            subtotal = 0;
                            for (int i = 0; i < LIST_MENU.size(); i++) {
                                subtotal = subtotal + (LIST_MENU.get(i).getJumlah() * LIST_MENU.get(i).getHarga());
                            }
                            tip = (int) Math.round(subtotal / 10);
                        }
                        total = subtotal + tip + delivery - diskon;
                        txtDiskon.setText("Rp. " + decimalFormat.format(diskon));
                        txtSubtotal.setText("Rp. " + decimalFormat.format(subtotal));
                        txtDelivery.setText("Rp. " + decimalFormat.format(delivery));
                        txtTip.setText("Rp. " + decimalFormat.format(tip));
                        txtTotal.setText("Rp. " + decimalFormat.format(total));
                    }
                    else{
                        onBackPressed();
                    }



                }
                else if(message.equals("delete")){
                    if(ApplicationData.cart.size() > 0){
                        LIST_MENU = new ArrayList<ModelCart>(ApplicationData.cart.values());
                        if (LIST_MENU.size() > 0) {
                            subtotal = 0;
                            for (int i = 0; i < LIST_MENU.size(); i++) {
                                subtotal = subtotal + (LIST_MENU.get(i).getJumlah() * LIST_MENU.get(i).getHarga());
                            }
                            tip = (int) Math.round(subtotal / 10);
                        }
                        total = subtotal + tip + delivery - diskon;
                        txtDiskon.setText("Rp. " + decimalFormat.format(diskon));
                        txtSubtotal.setText("Rp. " + decimalFormat.format(subtotal));
                        txtDelivery.setText("Rp. " + decimalFormat.format(delivery));
                        txtTip.setText("Rp. " + decimalFormat.format(tip));
                        txtTotal.setText("Rp. " + decimalFormat.format(total));
                        mAdapter = new AdapterCheckout(ActivityCheckout.this, LIST_MENU);
                        mListView.setAdapter(mAdapter);
                    }
                    else{
                        ApplicationData.cart = new HashMap<>();
                        finish();
                    }
                }


            }
        };
    }

    private void DummyData() {

        LIST_MENU = new ArrayList<ModelCart>();
        /*
        ModelCart modelDeliver0 = new ModelCart("1","Pecel Mak Yem", "5", "50.000");
        LIST_MENU.add(modelDeliver0);
        ModelCart modelDeliver1 = new ModelCart("2","Soto Spesial Bu Winda", "4", "60.000");
        LIST_MENU.add(modelDeliver1);
        */
        LIST_MENU = new ArrayList<ModelCart>(ApplicationData.cart.values());
        if (LIST_MENU.size() > 0) {
            for (int i = 0; i < LIST_MENU.size(); i++) {
                subtotal = subtotal + (LIST_MENU.get(i).getJumlah() * LIST_MENU.get(i).getHarga());
            }
            tip = (int) Math.round(subtotal / 10);
        }
        total = subtotal + tip + delivery - diskon;
    }

    private class CalculatePrice extends AsyncTask<String, Void, String> {
        private Activity activity;
        private Context context;
        private Resources resources;
        private ProgressDialog progressDialog;

        public CalculatePrice(Activity activity) {
            super();
            this.activity = activity;
            this.context = activity.getApplicationContext();
            this.resources = activity.getResources();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*
            progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage("Loading. . .");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
            */
            progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                String kode = params[0];
                JSONControl jsControl = new JSONControl();
                List<ModelCart> cart = new ArrayList<ModelCart>(ApplicationData.cart.values());
                JSONObject response = jsControl.calculatePrice(kode, applicationManager.getUserToken(), cart);
                Log.d("json response", response.toString());
                try {
                    JSONArray transaksi = response.getJSONArray("transactions");
                    if (transaksi.length() > 0) {
                        for (int i = 0; i < transaksi.length(); i++) {
                            String discountPrice = transaksi.getJSONObject(i).getString("discountPrice");
                            diskon = Integer.parseInt(discountPrice);
                            String shippingPrice = transaksi.getJSONObject(i).getString("shippingPrice");
                            delivery = Integer.parseInt(shippingPrice);
                        }
                        return "OK";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return "FAIL";

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progress.setVisibility(View.GONE);
            switch (result) {
                case "FAIL":
                    if (delivery == 0) {
                        delivery = ApplicationData.def_delivery;
                    }
                    diskon = 0;
                    total = subtotal + tip + delivery - diskon;
                    txtDiskon.setText("Rp. " + decimalFormat.format(diskon));
                    txtDelivery.setText("Rp. " + decimalFormat.format(delivery));
                    txtTotal.setText("Rp. " + decimalFormat.format(total));
                    break;
                case "OK":
                    total = subtotal + tip + delivery - diskon;
                    txtDiskon.setText("Rp. " + decimalFormat.format(diskon));
                    txtDelivery.setText("Rp. " + decimalFormat.format(delivery));
                    txtTotal.setText("Rp. " + decimalFormat.format(total));
                    break;
            }
            //progressDialog.dismiss();
            if (ApplicationData.cart.size() > 0) {
                mListView.setVisibility(View.VISIBLE);
                noData.setVisibility(View.GONE);
            } else {
                mListView.setVisibility(View.GONE);
                noData.setVisibility(View.VISIBLE);
            }

        }


    }


    private class CheckOut extends AsyncTask<String, Void, String> {
        private Activity activity;
        private Context context;
        private Resources resources;
        private ProgressDialog progressDialog;

        public CheckOut(Activity activity) {
            super();
            this.activity = activity;
            this.context = activity.getApplicationContext();
            this.resources = activity.getResources();
        }

        @Override
        protected void onPreExecute() {
            /*
            super.onPreExecute();
            progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage("Loading. . .");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
            */
            progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                String kode = params[0];
                String address = params[1];
                String note = params[2];

                JSONControl jsControl = new JSONControl();
                List<ModelCart> cart = new ArrayList<ModelCart>(ApplicationData.cart.values());
                JSONObject response = jsControl.checkOut(kode, address, note, ApplicationData.posFrom, applicationManager.getUserToken(), cart);
                Log.d("json response checkout", response.toString());
                try {
                    return "OK";
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return "FAIL";

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progress.setVisibility(View.GONE);
            switch (result) {
                case "FAIL":
                    break;
                case "OK":
                    if (NetworkManager.getInstance(ActivityCheckout.this).isConnectedInternet()) {
                        try {
                            final Context ctx = ActivityCheckout.this;
                            new MaterialDialog.Builder(ctx)
                                    .title("Terima kasih")
                                    .content("Pesanan Anda akan segera kami proses")
                                    .positiveText("OK")
                                    .callback(new MaterialDialog.ButtonCallback() {
                                        @Override
                                        public void onPositive(MaterialDialog dialog) {
                                            if (NetworkManager.getInstance(ActivityCheckout.this).isConnectedInternet()) {
                                                ApplicationData.cart = new HashMap<String, ModelCart>();
                                                Intent j = new Intent(getBaseContext(), ActivityHome.class);
                                                startActivity(j);
                                                finish();
                                            } else {
                                                DialogManager.showDialog(ActivityCheckout.this, "Mohon Maaf", "Tidak ada koneksi internet!");
                                            }
                                            dialog.dismiss();
                                        }
                                    })
                                    .cancelable(false)
                                    .typeface("GothamRnd-Medium.otf", "Gotham.ttf")
                                    .show();
                        } catch (Exception e) {

                        }
                    } else {
                        DialogManager.showDialog(ActivityCheckout.this, "Mohon Maaf", "Tidak ada koneksi internet!");
                    }
                    break;
            }
            //progressDialog.dismiss();

        }


    }

    private void SendBroadcast(String typeBroadcast, String type) {
        Intent intent = new Intent(typeBroadcast);
        // add data
        intent.putExtra("message", type);
        LocalBroadcastManager.getInstance(act).sendBroadcast(intent);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(ActivityCheckout.this).registerReceiver(updateCart,
                new IntentFilter("updateCart"));


    }


}
