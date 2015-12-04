package twiscode.masakuuser.Fragment;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baoyz.widget.PullRefreshLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import twiscode.masakuuser.Activity.ActivityCheckout;
import twiscode.masakuuser.Adapter.AdapterWishlist;
import twiscode.masakuuser.Control.JSONControl;
import twiscode.masakuuser.Model.ModelCart;
import twiscode.masakuuser.Model.ModelWishlist;
import twiscode.masakuuser.R;
import twiscode.masakuuser.Utilities.ApplicationData;
import twiscode.masakuuser.Utilities.ApplicationManager;

public class FragmentWishlist extends Fragment {

	private TextView countCart;
	private LinearLayout wrapCount;
	private ImageView btnCart;
	public static final String ARG_PAGE = "ARG_PAGE";
	private List<ModelWishlist> LIST_MENU = new ArrayList<>();
	private PullRefreshLayout mSwipeRefreshLayout;
	private ListView mListView;
	private AdapterWishlist mAdapter;
	//private LinearLayout noData;

	int page =1;
	boolean isNodata = false;

	private ProgressBar progress;


	private int mPage;

	private RecyclerView recyclerView;

	private BroadcastReceiver updateCart;

	private HashMap<String,ModelWishlist> wishlistMenus = new HashMap<>();

	ApplicationManager appManager;


	public static FragmentWishlist newInstance(int page) {
		Bundle args = new Bundle();
		args.putInt(ARG_PAGE, page);
		FragmentWishlist fragment = new FragmentWishlist();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		appManager = new ApplicationManager(getActivity());
		View rootView = inflater.inflate(R.layout.activity_all_menus, container, false);
		progress = (ProgressBar) rootView.findViewById(R.id.progress);
		//noData = (LinearLayout) rootView.findViewById(R.id.noData);
		wrapCount = (LinearLayout) rootView.findViewById(R.id.wrapCount);
		countCart = (TextView) rootView.findViewById(R.id.countCart);
		btnCart = (ImageView) rootView.findViewById(R.id.btnCart);
		mListView = (ListView) rootView.findViewById(R.id.list_delivery);
		mSwipeRefreshLayout = (PullRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
		mSwipeRefreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_RING);
		View header = getActivity().getLayoutInflater().inflate(R.layout.layout_header_menu, null);
		//mListView.addHeaderView(header);
		//mAdapter = new AdapterMenuNew(getActivity(), LIST_MENU);
		//mListView.setAdapter(mAdapter);
		mListView.setScrollingCacheEnabled(false);
		mSwipeRefreshLayout.setRefreshing(false);

		btnCart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(getActivity(), ActivityCheckout.class);
				startActivity(i);
				getActivity().finish();
			}
		});



		updateCart = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				// Extract data included in the Intent
				Log.d("", "broadcast updateCart");
				String message = intent.getStringExtra("message");
				if (message.equals("true")) {
					List<ModelCart>list = new ArrayList<ModelCart>(ApplicationData.cart.values());
					if(list.size() > 0){
						int jml = 0;
						for(int i = 0;i<list.size();i++){
							jml = jml + list.get(i).getJumlah();
						}
						countCart.setText(""+jml);
						wrapCount.setVisibility(View.VISIBLE);
					}
					else {
						wrapCount.setVisibility(View.GONE);
					}

				}


			}
		};
		mSwipeRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				isNodata = false;
				DummyData(isNodata);
			}
		});

		DummyData(isNodata);
		/*
		if(LIST_MENU.size() > 0){
			mListView.setVisibility(View.VISIBLE);
			noData.setVisibility(View.GONE);
		}
		else {
			mListView.setVisibility(View.GONE);
			noData.setVisibility(View.VISIBLE);
		}
		*/


		return rootView;
	}

	private void DummyData(boolean isnodata){
		isNodata = isnodata;
		LIST_MENU = new ArrayList<ModelWishlist>();
		new GetWishlist(getActivity()).execute();

	}

	private class GetWishlist extends AsyncTask<String, Void, String> {
		private Activity activity;
		private Context context;
		private Resources resources;


		public GetWishlist(Activity activity) {
			super();
			this.activity = activity;
			this.context = activity.getApplicationContext();
			this.resources = activity.getResources();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if(isNodata){
			}
			else {
				mSwipeRefreshLayout.setRefreshing(true);
			}
		}

		@Override
		protected String doInBackground(String... params) {
			try {

				JSONControl jsControl = new JSONControl();
				JSONObject response = jsControl.getWishlist(appManager.getUserToken());
				Log.d("json response wishlist", response.toString());
				Log.d("user token wishlist", appManager.getUserToken());
				JSONArray menus = response.getJSONArray("menus");
				if(menus.length() > 0){
					for(int i=0;i<menus.length();i++){
						String id = menus.getJSONObject(i).getString("_id");
						String nama = menus.getJSONObject(i).getString("name");
						String foto = menus.getJSONObject(i).getJSONArray("imageUrls").getString(0);
						String price = menus.getJSONObject(i).getString("price");
						String time = "";//menus.getJSONObject(i).getJSONObject("speed").getString("waitingTime");
						String desc = menus.getJSONObject(i).getString("description");
						JSONArray feedback = new JSONArray();//menus.getJSONObject(i).getJSONArray("feedbacks");
						boolean added = false;
						ModelWishlist menu = new ModelWishlist(id,nama,price,foto,time,desc,feedback,added);
						//LIST_MENU.add(menu);
						if(wishlistMenus.size() > 0){
							if(!wishlistMenus.containsKey(id)){
								wishlistMenus.put(id,menu);
							}
						}
						else {
							wishlistMenus.put(id, menu);
						}
						LIST_MENU = new ArrayList<>(wishlistMenus.values());
						ApplicationData.wishlist = wishlistMenus;
					}

					return "OK";
				}


			} catch (Exception e) {
				e.printStackTrace();
			}
			Log.d("json response id 0", "FAIL");
			return "FAIL";

		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);


			//progressDialog.dismiss();
			progress.setVisibility(View.GONE);
			switch (result) {
				case "FAIL":
					//DialogManager.showDialog(activity, "Mohon maaf", "Nomor ponsel Anda belum terdaftar!");
					mSwipeRefreshLayout.setRefreshing(false);
					if(LIST_MENU.size() > 0){

						mListView.setVisibility(View.VISIBLE);
						//noData.setVisibility(View.GONE);
						mSwipeRefreshLayout.setVisibility(View.VISIBLE);
						Log.d("datalist","ada");
					}
					else {
						mListView.setVisibility(View.GONE);
						//noData.setVisibility(View.VISIBLE);
						mSwipeRefreshLayout.setVisibility(View.GONE);
					}
					break;
				case "OK":
					//Intent i = new Intent(getBaseContext(), ActivityHome.class);
					//startActivity(i);
					//finish();
					Log.d("jumlah menu : ",""+LIST_MENU.size());
					mAdapter = new AdapterWishlist(getActivity(), LIST_MENU);
					mListView.setAdapter(mAdapter);
					mSwipeRefreshLayout.setRefreshing(false);
					if(LIST_MENU.size() > 0){

						mListView.setVisibility(View.VISIBLE);
						//noData.setVisibility(View.GONE);
						mSwipeRefreshLayout.setVisibility(View.VISIBLE);
						Log.d("datalist","ada");
					}
					else {
						mListView.setVisibility(View.GONE);
						//noData.setVisibility(View.VISIBLE);
						mSwipeRefreshLayout.setVisibility(View.GONE);
					}
					break;

			}
		}
	}



}
