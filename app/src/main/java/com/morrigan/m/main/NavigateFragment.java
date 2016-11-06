package com.morrigan.m.main;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.yzeaho.common.ToastUtils;
import com.morrigan.m.FeedbackActivity;
import com.morrigan.m.R;
import com.morrigan.m.UiResult;
import com.morrigan.m.about.AboutActivity;
import com.morrigan.m.c.UserController;
import com.morrigan.m.device.DeviceActivity;
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
        view.findViewById(R.id.device).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DeviceActivity.class);
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
                showQuitDialog();
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

    private void showQuitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("注销用户信息");
        builder.setMessage("注销后此账号将删除所有有关信息，只能通过重新注册才能登录");
        builder.setNegativeButton(R.string.action_cancel, null);
        builder.setPositiveButton(R.string.action_logout, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                quit();
            }
        });
        builder.show();
    }

    private void quit() {
        AsyncTask<Void, Void, UiResult<Void>> task = new AsyncTask<Void, Void, UiResult<Void>>() {

            private ProgressDialog dialog;

            @Override
            protected void onPreExecute() {
                dialog = new ProgressDialog(getActivity());
                dialog.setMessage(getActivity().getString(R.string.changing));
                dialog.setCancelable(false);
                dialog.show();
            }

            @Override
            protected UiResult<Void> doInBackground(Void... voids) {
                return UserController.getInstance().logout(getActivity());
            }

            @Override
            protected void onPostExecute(UiResult<Void> result) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (result.success) {
                    UserController.getInstance().clear(getContext());
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    ToastUtils.show(getContext(), result.message);
                }
            }
        };
        AsyncTaskCompat.executeParallel(task);
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
        String url = UserController.getInstance().getImgUrl(getActivity());
        if (TextUtils.isEmpty(url)) {
            Picasso.with(getActivity()).load(R.drawable.default_avatar).into(avatarView);
        } else {
            Picasso.with(getActivity()).load(url)
                    .placeholder(R.drawable.default_avatar)
                    .error(R.drawable.default_avatar).into(avatarView);
        }
    }

    public interface NavigateListener {
    }
}
