package twiscode.masakuuser.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.flurry.android.FlurryAgent;

import java.util.HashMap;
import java.util.Map;

import twiscode.masakuuser.Activity.ActivityAbout;
import twiscode.masakuuser.Activity.ActivityContactUs;
import twiscode.masakuuser.Activity.ActivityFAQ;
import twiscode.masakuuser.Activity.ActivityFAQDetail;
import twiscode.masakuuser.Activity.ActivityRegister;
import twiscode.masakuuser.Activity.ActivityTerms;
import twiscode.masakuuser.Activity.ActivityTutorialBantuan;
import twiscode.masakuuser.R;
import twiscode.masakuuser.Utilities.ApplicationData;
import twiscode.masakuuser.Utilities.DataFragmentHelper;
import twiscode.masakuuser.Utilities.DialogManager;
import twiscode.masakuuser.Utilities.PersistenceDataHelper;

public class FragmentBantuan extends Fragment {

	public static final String ARG_PAGE = "ARG_PAGE";

	private int mPage;
	private LinearLayout btnAbout, btnContactUs, btnFAQ, btnTerms,btnTutor;
	private RecyclerView recyclerView;
	private DataFragmentHelper datafragmentHelper = PersistenceDataHelper.GetInstance().FragmentHelper;

	Map<String, String> flurryParams = new HashMap<String,String>();


	public static FragmentBantuan newInstance(int page) {
		Bundle args = new Bundle();
		args.putInt(ARG_PAGE, page);
		FragmentBantuan fragment = new FragmentBantuan();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.activity_bantuan, container, false);
		btnAbout = (LinearLayout) rootView.findViewById(R.id.btnAbout);
		btnContactUs = (LinearLayout) rootView.findViewById(R.id.btnContactUs);
		btnFAQ = (LinearLayout) rootView.findViewById(R.id.btnFAQ);
		btnTerms = (LinearLayout) rootView.findViewById(R.id.btnTerms);
		btnTutor = (LinearLayout) rootView.findViewById(R.id.btnTutorial);

		btnAbout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(getActivity(), ActivityAbout.class);
				startActivity(i);
			}
		});
		btnContactUs.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(getActivity(), ActivityContactUs.class);
				startActivity(i);
			}
		});
		btnFAQ.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(getActivity(), ActivityFAQ.class);
				startActivity(i);
			}
		});
		btnTerms.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(getActivity(), ActivityTerms.class);
				startActivity(i);
			}
		});
		btnTutor.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(getActivity(), ActivityTutorialBantuan.class);
				startActivity(i);
			}
		});


		return rootView;
	}

	public void onStart() {
		super.onStart();
		FlurryAgent.logEvent("HELP", flurryParams, true);
	}

	public void onStop() {
		super.onStop();
		FlurryAgent.endTimedEvent("HELP");
	}


}
