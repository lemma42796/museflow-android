package com.ixuea.courses.mymusic.component.comment.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.ixuea.courses.mymusic.R;

/**
 * 评论更多对话框
 */
public class CommentMoreDialogFragment extends DialogFragment {
    private DialogInterface.OnClickListener onClickListener;

    public static void showDialog(FragmentManager fragmentManager, DialogInterface.OnClickListener onClickListener) {
        CommentMoreDialogFragment fragment = newInstance();
        fragment.onClickListener = onClickListener;
        fragment.show(fragmentManager, "CommentMoreDialogFragment");
    }

    public static CommentMoreDialogFragment newInstance() {

        Bundle args = new Bundle();

        CommentMoreDialogFragment fragment = new CommentMoreDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setItems(R.array.dialog_comment_more, onClickListener)
                .create();
    }
}
