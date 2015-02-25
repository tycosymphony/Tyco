package com.poc.tycolibrary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DriverFragment extends Fragment {
    DriverFragmentNotify mCallBack;

    public interface DriverFragmentNotify {
        public void notifyDelete();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Retrieving the currently selected item number
        final int position = getArguments().getInt("position");

        // List of rivers
        String[] route = getResources().getStringArray(R.array.route);
        String[] startTime = getResources().getStringArray(R.array.startTime);
        String[] start = getResources().getStringArray(R.array.start);
        String[] destination = getResources().getStringArray(R.array.destination);
        String[] employee_count = getResources().getStringArray(R.array.employee_count);

        // Creating view corresponding to the fragment
        View v = inflater.inflate(R.layout.routedetails, container, false);

        // Getting reference to the TextView of the Fragment
        TextView routeTxt = (TextView) v.findViewById(R.id.routeTxt);
        TextView timeTxt = (TextView) v.findViewById(R.id.timeTxt);
        TextView startTxt = (TextView) v.findViewById(R.id.startTxt);
        TextView endTxt = (TextView) v.findViewById(R.id.endTxt);
        TextView totalEmpTxt = (TextView) v.findViewById(R.id.totalEmpTxt);

        // Setting currently selected route name in the TextView
        routeTxt.setText(route[position]);
        timeTxt.setText(startTime[position]);
        startTxt.setText(start[position]);
        endTxt.setText(destination[position]);
        totalEmpTxt.setText(employee_count[position]);

        Button startRouteBtn = (Button) v.findViewById(R.id.startRouteBtn);
        startRouteBtn.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        Intent mainIntent = new Intent(getActivity(), ShowRoute.class);
                        startActivity(mainIntent);
                    }
                });

        Button closeRouteBtn = (Button) v.findViewById(R.id.closeRouteBtn);
        closeRouteBtn.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        Toast.makeText(getActivity(), "Trip Closed..", Toast.LENGTH_SHORT).show();
                        mCallBack.notifyDelete();
                    }
                });

        Button checkRoute = (Button) v.findViewById(R.id.checkRoute);
        checkRoute.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        Intent mainIntent = new Intent(getActivity(), ShowRoute.class);
                        startActivity(mainIntent);
                    }
                });

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallBack= (DriverRoute)activity;
    }
}