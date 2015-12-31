package twiscode.masakuuser.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import twiscode.masakuuser.R;


/**
 * Created by Unity on 01/09/2015.
 */
public class FragmentTutorial_1 extends Fragment {
    private static final String ARG_COLOR = "color";
    ProgressBar progressBar;
    private String mColor;

    public static FragmentTutorial_1 newInstance(String param1) {
        FragmentTutorial_1 fragment = new FragmentTutorial_1();
        Bundle args = new Bundle();
        args.putString(ARG_COLOR, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentTutorial_1() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColor = getArguments().getString(ARG_COLOR);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = null;
        try {
            v = inflater.inflate(R.layout.tutorial_slider_fragment_1, container, false);
        } catch (Exception e) {

        }


        return v;
    }
}
