package com.morrigan.m.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.morrigan.m.about.AboutActivity;
import com.morrigan.m.FeedbackActivity;
import com.morrigan.m.R;
import com.morrigan.m.UserController;
import com.morrigan.m.goal.GoalActivity;
import com.morrigan.m.historyrecord.HisttofyRecordActivity;
import com.morrigan.m.login.LoginActivity;
import com.morrigan.m.personal.PersonalActivity;
import com.squareup.picasso.Picasso;

public class NavigateFragment extends Fragment {

    private NavigateListener listener;
    private TextView nicknameView;
    private ImageView avatarView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_navigate, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        nicknameView = (TextView) view.findViewById(R.id.nickname);
        avatarView = (ImageView) view.findViewById(R.id.avatar);
        view.findViewById(R.id.my_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PersonalActivity.class);
                startActivity(intent);
            }
        });
        view.findViewById(R.id.goal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GoalActivity.class);
                startActivity(intent);
            }
        });
        view.findViewById(R.id.about).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AboutActivity.class);
                startActivity(intent);
            }
        });
        view.findViewById(R.id.feedback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FeedbackActivity.class);
                startActivity(intent);
            }
        });
        view.findViewById(R.id.quit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        view.findViewById(R.id.history).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), HisttofyRecordActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NavigateListener) {
            listener = (NavigateListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        nicknameView.setText(UserController.getInstance().getNickname(getActivity()));
        Picasso.with(getActivity()).load(UserController.getInstance().getImgUrl(getActivity()))
                .placeholder(R.drawable.default_avatar)
                .error(R.drawable.default_avatar)
                .centerCrop().into(avatarView);
    }

    public interface NavigateListener {
    }
}
